package com.seecoder.BlueWhale.serviceImpl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seecoder.BlueWhale.configure.AlipayTools;
import com.seecoder.BlueWhale.enums.CouponTypeEnum;
import com.seecoder.BlueWhale.enums.InfoEnum;
import com.seecoder.BlueWhale.enums.OrderStateEnum;
import com.seecoder.BlueWhale.exception.BlueWhaleException;
import com.seecoder.BlueWhale.po.*;
import com.seecoder.BlueWhale.repository.*;
import com.seecoder.BlueWhale.service.InfoService;
import com.seecoder.BlueWhale.service.OrderService;
import com.seecoder.BlueWhale.serviceImpl.strategy.*;
import com.seecoder.BlueWhale.util.*;
import com.seecoder.BlueWhale.vo.CommentVO;
import com.seecoder.BlueWhale.vo.CouponVO;
import com.seecoder.BlueWhale.vo.OrderVO;

import java.net.URLEncoder;

import java.util.HashMap;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.time.temporal.ChronoUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private CouponSetRepository couponSetRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private SecurityUtil securityUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AlipayTools alipayTools;

    @Autowired
    private OssUtil ossUtil;

    @Autowired
    private InfoService infoService;

    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    // OrderVO totalPrice can be null
    public Integer create(OrderVO order) {
        Integer productId = order.getProductId();
        updateProductWhenCreate(order, productId);
        order.setOrderState(OrderStateEnum.UNPAID);
        order.setCreateTime(Date.from(LocalDateTime.now().atZone(ZoneId.of("Asia/Shanghai")).toInstant()));
        User user = securityUtil.getCurrentUser();
        if (user == null) {
            throw BlueWhaleException.userNotExist();
        }
        order.setUserPhoneNum(user.getPhone());
        order.setUserId(user.getId());
        order.setHasInfo(false);
        order.setAddress(user.getAddress());
        Order newOrder = orderRepository.save(order.toPO());
        logger.info("Order for product {} in store {} created by user {}", productRepository.findById(order.getProductId()).orElse(null), storeRepository.findById(order.getStoreId()).orElse(null), user.getName());
        return newOrder.getId();
    }

    private synchronized void updateProductWhenCreate(OrderVO order, Integer productId) {
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            throw BlueWhaleException.productNotExists();
        }
        if (product.getStock() - product.getPendingNum() < order.getNum()) {
            throw BlueWhaleException.productLackStock();
        }
        product.setPendingNum(product.getPendingNum() + order.getNum());
        productRepository.save(product);
        Double totalPrice = order.getNum() * product.getPrice();
        order.setTotalPrice(totalPrice);
        order.setTrueTotalPrice(totalPrice);
    }

    @Override
    public Double price(Integer id, List<Integer> couponList) {
        Order order = orderRepository.findById(id).orElse(null);
        if (order == null)
            throw BlueWhaleException.orderNotExist();
        Double currentPrice = order.getTotalPrice();
        List<CouponSet> couponSetList = couponIdsToCouponSet(couponList);
        List<List<CouponSet>> permutations = PermutationUtil.permutationOf(couponSetList);
        for (List<CouponSet> permutation : permutations) {
            Double possiblePrice = order.getTotalPrice();
            for (CouponSet couponSet : permutation)
                possiblePrice = Math.max(0.0, calculate(possiblePrice, couponSet));
            currentPrice = Math.min(currentPrice, possiblePrice);
        }

        logger.info("Price for {} is {}", id, currentPrice);
        logger.info("Number of permutation is {}", permutations.size());
        return currentPrice;
    }

    @Override
    public Boolean validCouponList(Integer id, List<Integer> couponList) {
        if (couponList.isEmpty())
            return true;
        // 我们只允许同一类型的优惠券一起使用
        List<CouponSet> couponSetList = couponIdsToCouponSet(couponList);
        CouponTypeEnum legalType = couponSetList.get(0).getCouponType();
        return couponSetList.stream().allMatch((CouponSet set) -> {
            return set.getCouponType() == legalType;
        });
    }

    private List<CouponSet> couponIdsToCouponSet(List<Integer> couponList) {
        List<CouponSet> couponSetList = new ArrayList<>();
        for (Integer couponId : couponList) {
            Coupon coupon = couponRepository.findById(couponId).orElse(null);
            if (coupon == null)
                throw BlueWhaleException.couponNotExist();
            if (coupon.getHasUsed())
                throw BlueWhaleException.couponHasUsed();

            CouponSet couponSet = couponSetRepository.findById(coupon.getSetId())
                    .orElseThrow(BlueWhaleException::couponSetNotExist);
            couponSetList.add(couponSet);
        }
        return couponSetList;
    }

    private Double calculate(Double originalPrice, CouponSet couponSet) {
        Context context;
        switch (couponSet.getCouponType()) {
            case SPECIAL:
                context = new Context(new SpecialCouponCalculateStrategy());
                break;
            case FULL_REDUCTION:
                context = new Context(new FullReductionCouponCalculateStrategy(couponSet));
                break;
            case VOUCHER:
                context = new Context(new VoucherCouponCalculateStrategy(couponSet));
                break;
            case DIRECT_DISCOUNT:
                context = new Context(new DirectDiscountCouponCalculateStrategy(couponSet));
                break;
            default:
                throw BlueWhaleException.couponSetNotExist();
        }
        return context.calculate(originalPrice);
    }

    public void pay(Integer id, boolean isDirectPay, List<Integer> couponList,
                    javax.servlet.http.HttpServletResponse httpServletResponse) {
        Order order = orderRepository.findById(id).orElse(null);
        if (order == null) {
            throw BlueWhaleException.orderNotExist();
        }

        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", String.valueOf(id));
        String body = "coupon_list:" + couponList;
        // System.out.println(body);
        bizContent.put("total_amount", String.valueOf(price(id, couponList)));
        bizContent.put("subject", "bluewhale pay");
        bizContent.put("body", body);
        String form = "";
        try {
            Map<String, String> returnParams = new HashMap<>();
            Map<String, String> notifyParams = new HashMap<>();
            returnParams.put("url", URLEncoder
                    .encode(isDirectPay ? ("http://localhost:3000/#/productDetail/" + order.getProductId())
                            : "http://localhost:3000/#/allOrder", "UTF-8"));
            notifyParams.put("service", "orderService");

            form = alipayTools.pay(bizContent, notifyParams, returnParams);
            // System.out.println(form);
        } catch (Exception e) {
            throw BlueWhaleException.payError();
        }

        httpServletResponse.setContentType("text/html;charset=utf-8");
        try {
            httpServletResponse.getWriter().write(form);// 直接将完整的表单html输出到页面
            httpServletResponse.getWriter().flush();
            httpServletResponse.getWriter().close();
        } catch (IOException e) {
            throw BlueWhaleException.payError();
        }
    }

    public boolean payNotify(Map<String, String> params) {
        Integer id = Integer.valueOf(params.get("out_trade_no"));
        Double paid = Double.parseDouble(params.get("total_amount"));
        ObjectMapper objectMapper = new ObjectMapper();
        List<Integer> couponList;
        TypeReference<List<Integer>> typeRef = new TypeReference<List<Integer>>() {
        };
        try {
            couponList = objectMapper.readValue(params.get("coupon_list"), typeRef);
        } catch (Exception e) {
            return false;
        }
        ArrayList<Coupon> couponObjList = new ArrayList<Coupon>();
        for (Integer couponId : couponList) {
            Coupon coupon = couponRepository.findById(couponId).orElseThrow(BlueWhaleException::couponNotExist);

            if (coupon.getHasUsed())
                throw BlueWhaleException.couponHasUsed();

            couponObjList.add(coupon);
        }
        Order order = orderRepository.findById(id).orElse(null);
        if (order == null)
            return false;
        order.setTrueTotalPrice(paid);
        try {
            assertOrderState(order, OrderStateEnum.UNPAID);
        } catch (BlueWhaleException e) {
            return false;
        }
        order.setOrderState(OrderStateEnum.UNSEND);
        orderRepository.save(order);
        for (int i = 0; i < couponObjList.size(); i++) {
            Coupon coupon = couponObjList.get(i);
            coupon.setHasUsed(true);
            couponRepository.save(coupon);
        }
        logger.info(String.format("order %d payed", order.getId()));
        return true;
    }

    public List<OrderVO> getOrder() {
        User user = securityUtil.getCurrentUser();
        switch (user.getRole()) {
            case CUSTOMER:
                return orderRepository.findAllByUserId(user.getId()).stream()
                        .filter(order -> !(order.getOrderState().equals(OrderStateEnum.EXPIRED))).map(Order::toVO)
                        .collect(Collectors.toList());
            case STAFF:
                return orderRepository.findAllByStoreId(user.getStoreId()).stream()
                        .filter(order -> !(order.getOrderState().equals(OrderStateEnum.EXPIRED))).map(Order::toVO)
                        .collect(Collectors.toList());
            default: // MANAGER or CEO
                return orderRepository.findAll().stream()
                        .filter(order -> !(order.getOrderState().equals(OrderStateEnum.EXPIRED))).map(Order::toVO)
                        .collect(Collectors.toList());
        }
    }

    public Boolean delivery(Integer id) {
        Order order = orderRepository.findById(id).orElse(null);
        if (order == null) {
            throw BlueWhaleException.orderNotExist();
        }
        assertOrderState(order, OrderStateEnum.UNSEND);
        Integer productId = order.getProductId();
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            throw BlueWhaleException.productNotExists();
        }
        if (product.getPendingNum() < order.getNum()) {
            throw BlueWhaleException.productLackStock();
        }
        order.setOrderState(OrderStateEnum.UNGET);
        product.setPendingNum(product.getPendingNum() - order.getNum());
        product.setStock(product.getStock() - order.getNum());
        product.setSalesAmount(order.getNum());
        orderRepository.save(order);
        productRepository.save(product);
        logger.info("order {} delivered", order.getId());
        return true;
    }

    public Boolean receive(Integer id) {
        Order order = orderRepository.findById(id).orElse(null);
        if (order == null) {
            throw BlueWhaleException.orderNotExist();
        }

        assertOrderState(order, OrderStateEnum.UNGET);
        Integer productId = order.getProductId();
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            throw BlueWhaleException.productNotExists();
        }

        order.setOrderState(OrderStateEnum.UNCOMMENT);
        orderRepository.save(order);
        logger.info("order {} received", order.getId());
        return true;
    }

    public Boolean comment(Integer id, CommentVO comment) {
        Order order = orderRepository.findById(id).orElse(null);
        Integer userId = securityUtil.getCurrentUser().getId();

        if (order == null) {
            throw BlueWhaleException.orderNotExist();
        }
        if (!Objects.equals(order.getUserId(), userId)) {
            throw BlueWhaleException.illegalUserAccess();
        }
        assertOrderState(order, OrderStateEnum.UNCOMMENT);

        order.setOrderState(OrderStateEnum.DONE);
        orderRepository.save(order);

        Comment newComment = comment.toPO();
        newComment.setUserId(userId);
        newComment.setOrderId(id);
        newComment.setStoreId(order.getStoreId());
        newComment.setProductId(order.getProductId());
        newComment
                .setCreateTime(Date.from(LocalDateTime.now().atZone(ZoneId.of("Asia/Shanghai")).toInstant()));
        // 基于订单的评论并不依附于别的评论
        newComment.setCommentOnId(null);
        commentRepository.save(newComment);
        logger.info("order {} commented", order.getId());
        return true;
    }

    private void assertOrderState(Order order, OrderStateEnum orderState) {
        if (order.getOrderState() != orderState)
            throw BlueWhaleException.illegalOrderState();
    }

    public OrderVO getOrderById(Integer id) {
        Optional<Order> order = orderRepository.findById(id);
        if (order.isPresent()) {
            return order.get().toVO();
        } else
            throw BlueWhaleException.orderNotExist();
    }

    @Override
    public List<CouponVO> availableCoupons(Integer id) {
        List<Coupon> coupons = couponRepository.findAllByUid(securityUtil.getCurrentUser().getId());
        Order order = orderRepository.findById(id).orElseThrow(BlueWhaleException::orderNotExist);
        return coupons.stream().filter((Coupon coupon) -> {
            CouponSet set = couponSetRepository.findById(coupon.getSetId())
                    .orElseThrow(BlueWhaleException::couponSetNotExist);
            boolean expired = Date.from(LocalDateTime.now().atZone(ZoneId.of("Asia/Shanghai")).toInstant())
                    .after(set.getExpireTime());
            boolean legal = set.getIsGlobal() || set.getStoreId().equals(order.getStoreId());
            return !expired && legal && !coupon.getHasUsed();
        }).map(Coupon::toVO).collect(Collectors.toList());
    }

    private OrderExcelItem toExcel(Order order) {
        OrderExcelItem item = new OrderExcelItem();
        User customer = userRepository.findById(order.getUserId()).orElseThrow(BlueWhaleException::userNotExist);
        Store store = storeRepository.findById(order.getStoreId()).orElseThrow(BlueWhaleException::storeNotExists);
        Product product = productRepository.findById(order.getProductId())
                .orElseThrow(BlueWhaleException::productNotExists);
        item.setCustomerName(customer.getName());
        item.setStoreName(store.getName());
        item.setDate(order.getCreateTime());
        item.setProductName(product.getName());
        logger.info("store {} to excel by {}", store.getName(), securityUtil.getCurrentUser().getName());
        return item;
    }

    @Override
    public List<OrderExcelItem> getReportData() {
        User user = securityUtil.getCurrentUser();
        switch (user.getRole()) {
            case STAFF:
                return orderRepository.findAllByStoreId(user.getStoreId()).stream().map(this::toExcel)
                        .collect(Collectors.toList());
            case CEO:
                return orderRepository.findAll().stream().map(this::toExcel).collect(Collectors.toList());
            default:
                throw BlueWhaleException.illegalUserAccess();
        }
    }

    @Override
    public String report() throws UnsupportedEncodingException {
        String fileName = URLEncoder.encode("报表", "UTF-8").replaceAll("\\+", "%20");
        List<OrderExcelItem> reportData = getReportData();

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        EasyExcel.write(out, OrderExcelItem.class).sheet("报表").doWrite(reportData);
        return ossUtil.upload(securityUtil.getCurrentUser().getName() + "的报表.xlsx",
                new ByteArrayInputStream(out.toByteArray()));
    }

    @Override
    public Boolean doClean() {
        List<Order> orders = orderRepository.findAllUnpayOrder();
        for (Order order : orders) {
            Date createDate = order.getCreateTime();

            LocalDate currentDate = LocalDate.now();
            LocalDate localCreateDate = createDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            long daysDifference = ChronoUnit.DAYS.between(localCreateDate, currentDate);

            // info
            if (daysDifference >= 7 && !order.getHasInfo()) {
                String mesg = "order for " + productRepository.findById(order.getProductId()).get().getName()
                        + " in store "
                        + storeRepository.findById(order.getStoreId()).orElseThrow(BlueWhaleException::storeNotExists)
                        .getName()
                        + " will be expired in 7 days";
                infoService.addInfo(order.getUserId(), InfoEnum.WARNING, mesg);
                order.setHasInfo(true);
            } else if (daysDifference >= 14) {
                String mesg = "order for " + productRepository.findById(order.getProductId()).get().getName()
                        + " in store "
                        + storeRepository.findById(order.getStoreId()).orElseThrow(BlueWhaleException::storeNotExists)
                        .getName()
                        + " has expired";
                infoService.addInfo(order.getUserId(), InfoEnum.WARNING, mesg);
                order.setHasInfo(true);
                order.setOrderState(OrderStateEnum.EXPIRED);
                logger.info("order {} expired", order.getId());
            }
            orderRepository.save(order);
        }
        return true;
    }

}
