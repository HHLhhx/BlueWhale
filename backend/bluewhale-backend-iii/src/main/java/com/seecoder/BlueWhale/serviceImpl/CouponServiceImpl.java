package com.seecoder.BlueWhale.serviceImpl;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import cn.hutool.core.bean.BeanUtil;
import com.seecoder.BlueWhale.enums.GetCouponEnum;
import com.seecoder.BlueWhale.enums.RoleEnum;
import com.seecoder.BlueWhale.exception.BlueWhaleException;
import com.seecoder.BlueWhale.po.Coupon;
import com.seecoder.BlueWhale.po.CouponSet;
import com.seecoder.BlueWhale.po.User;
import com.seecoder.BlueWhale.repository.CouponRepository;
import com.seecoder.BlueWhale.repository.CouponSetRepository;
import com.seecoder.BlueWhale.util.SecurityUtil;
import com.seecoder.BlueWhale.vo.CouponVO;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import com.seecoder.BlueWhale.service.CouponService;
import com.seecoder.BlueWhale.vo.CouponSetVO;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class CouponServiceImpl implements CouponService {

    @Autowired
    SecurityUtil securityUtil;

    @Autowired
    CouponRepository couponRepository;

    @Autowired
    CouponSetRepository couponSetRepository;

    @Autowired
    RedissonClient redissonClient;

    @Resource
    RedisTemplate<String, Object> redisTemplate;

    private static final Logger logger = LoggerFactory.getLogger(CouponServiceImpl.class);

    @Override
    public Boolean create(CouponSetVO couponSet) {
        User user = securityUtil.getCurrentUser();
        couponSet.setCreateTime(Date.from(LocalDateTime.now().atZone(ZoneId.of("Asia/Shanghai")).toInstant()));
        // System.err.println(couponSet.getCreateTime());
        couponSet.setSentNum(0);
        couponSet.setIsGlobal(user.getRole() != RoleEnum.STAFF);
        if (user.getRole() == RoleEnum.STAFF)
            couponSet.setStoreId(user.getStoreId());
        couponSetRepository.save(couponSet.toPO());
        redisTemplate.opsForValue().set("coupon:stock:" + couponSet.getId(), couponSet.getTotalNum());
        logger.info("{} create couponSet({}) for store {}", user.getName(), couponSet.getCouponType(), user.getStoreId());
        return true;
    }

    @Override
    public List<Integer> getAllCouponSet() {
        RoleEnum role = securityUtil.getCurrentUser().getRole();
        switch (role) {
            case STAFF:
                return couponSetRepository.findAllIdByStoreId(securityUtil.getCurrentUser().getStoreId());
            case CUSTOMER:
                // fallthrough
            case CEO:
            case MANAGER:
                return couponSetRepository.findAllId();
            default:
                return new ArrayList<>();
        }
    }

    @Override
    public List<CouponVO> getAllCoupon(GetCouponEnum getType) {
        switch (getType) {
            case ALL:
                return couponRepository.findAllByUid(securityUtil.getCurrentUser().getId()).stream()
                        .map(Coupon::toVO).collect(Collectors.toList());
            case UNUSED:
                return couponRepository.findAllByUid(securityUtil.getCurrentUser().getId()).stream()
                        .filter((Coupon coupon) -> {
                            CouponSet couponSet = couponSetRepository.findById(coupon.getSetId()).orElseThrow(BlueWhaleException::couponSetNotExist);
                            return Date.from(LocalDateTime.now().atZone(ZoneId.of("Asia/Shanghai")).toInstant()).before(couponSet.getExpireTime()) && !coupon.getHasUsed();
                        }).map(Coupon::toVO).collect(Collectors.toList());
            case USED:
                return couponRepository.findAllByUid(securityUtil.getCurrentUser().getId()).stream()
                        .filter(Coupon::getHasUsed).map(Coupon::toVO).collect(Collectors.toList());
            case EXPIRED:
                return couponRepository.findAllByUid(securityUtil.getCurrentUser().getId()).stream()
                        .filter((Coupon coupon) -> {
                            CouponSet couponSet = couponSetRepository.findById(coupon.getSetId()).orElseThrow(BlueWhaleException::couponSetNotExist);
                            return Date.from(LocalDateTime.now().atZone(ZoneId.of("Asia/Shanghai")).toInstant()).after(couponSet.getExpireTime()) && !coupon.getHasUsed();
                        }).map(Coupon::toVO).collect(Collectors.toList());
            default:
                return new ArrayList<>();
        }
    }

//    private static final DefaultRedisScript<Long> COUPON_SCRIPT;
//
//    static {
//        COUPON_SCRIPT = new DefaultRedisScript<>();
//        COUPON_SCRIPT.setLocation(new ClassPathResource("coupon.lua"));
//        COUPON_SCRIPT.setResultType(Long.class);
//    }
//
//    private static final ExecutorService COUPON_ORDER_EXECUTOR = Executors.newSingleThreadExecutor();
//
//    private class CouponOrderHandler implements Runnable {
//        @Override
//        public void run() {
//            while (true) {
//                try {
//                    List<MapRecord<String, Object, Object>> list = redisTemplate.opsForStream().read(
//                            Consumer.from("g1", "c1"),
//                            StreamReadOptions.empty().count(1).block(Duration.ofSeconds(2)),
//                            StreamOffset.create("stream.orders", ReadOffset.lastConsumed())
//                    );
//                    if (list == null || list.isEmpty()) {
//                        continue;
//                    }
//                    MapRecord<String, Object, Object> record = list.get(0);
//                    Map<Object, Object> value = record.getValue();
//                    int uid = (int) value.get("uid");
//                    int setId = (int) value.get("set_id");
//
//                    String key = "couponset:lock:" + setId;
//                    RLock lock = redissonClient.getLock(key);
//                    try {
//                        Coupon coupon = new Coupon();
//                        coupon.setUid(uid);
//                        coupon.setSetId(setId);
//                        coupon.setHasUsed(false);
//                        couponRepository.save(coupon);
//
//                        boolean locked = lock.tryLock(500, TimeUnit.MILLISECONDS);
//                        if (!locked) {
//                            throw BlueWhaleException.acquireCouponFailed();
//                        }
//                        CouponSet couponSet = couponSetRepository.findById(setId).orElse(null);
//                        couponSet.setSentNum(couponSet.getSentNum() + 1);
//                        couponSetRepository.save(couponSet);
//                        logger.info("{} acquire coupon(set:{})", securityUtil.getCurrentUser().getName(), setId);
//                    } catch (InterruptedException e) {
//                        throw new RuntimeException(e);
//                    } finally {
//                        lock.unlock();
//                    }
//
//                    redisTemplate.opsForStream().acknowledge("s1", "g1", record.getId());
//                } catch (Exception e) {
//                    handlePendingList();
//                }
//            }
//        }
//
//        private void handlePendingList() {
//            while (true) {
//                try {
//                    List<MapRecord<String, Object, Object>> list = redisTemplate.opsForStream().read(
//                            Consumer.from("g1", "c1"),
//                            StreamReadOptions.empty().count(1).block(Duration.ofSeconds(2)),
//                            StreamOffset.create("stream.orders", ReadOffset.lastConsumed())
//                    );
//                    if (list == null || list.isEmpty()) {
//                        break;
//                    }
//                    MapRecord<String, Object, Object> record = list.get(0);
//                    Map<Object, Object> value = record.getValue();
//                    int uid = (int) value.get("uid");
//                    int setId = (int) value.get("set_id");
//
//                    String key = "couponset:lock:" + setId;
//                    RLock lock = redissonClient.getLock(key);
//                    try {
//                        Coupon coupon = new Coupon();
//                        coupon.setUid(uid);
//                        coupon.setSetId(setId);
//                        coupon.setHasUsed(false);
//                        couponRepository.save(coupon);
//
//                        boolean locked = lock.tryLock(500, TimeUnit.MILLISECONDS);
//                        if (!locked) {
//                            throw BlueWhaleException.acquireCouponFailed();
//                        }
//                        CouponSet couponSet = couponSetRepository.findById(setId).orElse(null);
//                        couponSet.setSentNum(couponSet.getSentNum() + 1);
//                        couponSetRepository.save(couponSet);
//                        logger.info("{} acquire coupon(set:{})", securityUtil.getCurrentUser().getName(), setId);
//                    } catch (InterruptedException e) {
//                        throw new RuntimeException(e);
//                    } finally {
//                        lock.unlock();
//                    }
//                    redisTemplate.opsForStream().acknowledge("s1", "g1", record.getId());
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
//
//    @PostConstruct
//    private void init() {
//        COUPON_ORDER_EXECUTOR.submit(new CouponOrderHandler());
//    }

    @Override
    public CouponVO acquire(Integer setId) {
//        Integer id = securityUtil.getCurrentUser().getId();
//        Long result = redisTemplate.execute(
//                COUPON_SCRIPT,
//                Collections.emptyList(),
//                setId.toString(), id.toString()
//        );
//        int r = result.intValue();
//        if (r == 1) {
//            throw BlueWhaleException.couponSetAllSent();
//        }
//        if (r == 2) {
//            throw BlueWhaleException.holdCouponAlready();
//        }
//        CouponVO couponVO = new CouponVO();
//        couponVO.setUid(id);
//        couponVO.setSetId(setId);
//        return couponVO;
        String key = "couponset:lock:" + setId;
        RLock lock = redissonClient.getLock(key);
        try {
            boolean locked = lock.tryLock(500, TimeUnit.MILLISECONDS);
            if (!locked) {
                throw BlueWhaleException.acquireCouponFailed();
            }

            CouponSet couponSet = couponSetRepository.findById(setId).orElse(null);
            Integer uid = securityUtil.getCurrentUser().getId();

            if (couponSet == null)
                throw BlueWhaleException.couponSetNotExist();

            if (couponSet.getSentNum() >= couponSet.getTotalNum())
                throw BlueWhaleException.couponSetAllSent();

            if (!couponRepository.findAllByUidAndSetId(uid, couponSet.getId()).isEmpty())
                throw BlueWhaleException.holdCouponAlready();

            Coupon coupon = new Coupon();
            coupon.setSetId(couponSet.getId());
            coupon.setUid(uid);
            coupon.setHasUsed(false);
            coupon = couponRepository.save(coupon);

            couponSet.setSentNum(couponSet.getSentNum() + 1);
            couponSetRepository.save(couponSet);
            logger.info("{} acquire coupon(set:{})", securityUtil.getCurrentUser().getName(), setId);

            return coupon.toVO();

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Boolean check(Integer setId) {
        Coupon coupon = couponRepository.findBySetIdAndUid(setId, securityUtil.getCurrentUser().getId());
        return (coupon != null);
    }

    @Override
    public CouponSetVO getCouponSetById(Integer setId) {
        CouponSet couponSet = couponSetRepository.findById(setId).orElseThrow(BlueWhaleException::couponSetNotExist);
        return couponSet.toVO();
    }

    @Override
    public Boolean isValid(Integer setId) {
        CouponSet couponSet = couponSetRepository.findById(setId).orElse(null);
        if (couponSet == null)
            throw BlueWhaleException.couponSetNotExist();
        return (couponSet.getExpireTime().compareTo(Date.from(LocalDateTime.now().atZone(ZoneId.of("Asia/Shanghai")).toInstant())) >= 0);
    }
}
