package com.seecoder.BlueWhale.serviceImpl;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.seecoder.BlueWhale.enums.CouponTypeEnum;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.seecoder.BlueWhale.annotation.Access;
import com.seecoder.BlueWhale.service.CouponService;
import com.seecoder.BlueWhale.vo.CouponSetVO;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;

@Service
public class CouponServiceImpl implements CouponService {
    @Autowired
    SecurityUtil securityUtil;

    @Autowired
    CouponRepository couponRepository;

    @Autowired
    CouponSetRepository couponSetRepository;

    private static final Logger logger = LoggerFactory.getLogger(CouponServiceImpl.class);
    @Override
    public Boolean create(CouponSetVO couponSet) {
        User user = securityUtil.getCurrentUser();
        couponSet.setCreateTime(Date.from(LocalDateTime.now().atZone(ZoneId.of("Asia/Shanghai")).toInstant()));
        // System.err.println(couponSet.getCreateTime());
        couponSet.setSentNum(0);
        couponSet.setIsGlobal((user.getRole() == RoleEnum.STAFF) ? false : true);
        if (user.getRole() == RoleEnum.STAFF)
            couponSet.setStoreId(user.getStoreId());
        couponSetRepository.save(couponSet.toPO());
        logger.info(String.format("%s create couponSet(%s) for store %d", user.getName(), couponSet.getCouponType(), user.getStoreId()));
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

    @Override
    public synchronized CouponVO acquire(Integer setId) {
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
        logger.info(String.format("%s acquire coupon(set:%d)", securityUtil.getCurrentUser().getName(), setId));

        return coupon.toVO();
    }

    @Override
    public Boolean check(Integer setId) {
        CouponSet couponSet = couponSetRepository.findById(setId).orElseThrow(BlueWhaleException::couponSetNotExist);
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
