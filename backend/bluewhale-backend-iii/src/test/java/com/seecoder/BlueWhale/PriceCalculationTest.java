package com.seecoder.BlueWhale;

import com.seecoder.BlueWhale.enums.CouponTypeEnum;
import com.seecoder.BlueWhale.po.CouponSet;
import com.seecoder.BlueWhale.po.User;
import com.seecoder.BlueWhale.serviceImpl.strategy.FullReductionCouponCalculateStrategy;
import com.seecoder.BlueWhale.serviceImpl.strategy.SpecialCouponCalculateStrategy;
import com.seecoder.BlueWhale.util.TokenUtil;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

class PriceCalculationTest {
    @Test
    void priceFullReductionCoupon() {
        CouponSet set = new CouponSet();
        set.setCouponType(CouponTypeEnum.FULL_REDUCTION);
        set.setFulfillPrice(100.0);
        set.setReducePrice(20.0);
        FullReductionCouponCalculateStrategy fullReduction = new FullReductionCouponCalculateStrategy(set);

        // positive
        Assertions.assertEquals(fullReduction.calculate(100.0), 80.0);
        Assertions.assertEquals(fullReduction.calculate(130.0), 110.0);
        Assertions.assertEquals(fullReduction.calculate(101.0), 81.0);

        // negative
        Assertions.assertEquals(fullReduction.calculate(90.0), 90.0);
        Assertions.assertEquals(fullReduction.calculate(-100.0), -100.0); // Nonsense. But show insight.
    }

    @Test
    void priceSpecialCoupon() {
        SpecialCouponCalculateStrategy special = new SpecialCouponCalculateStrategy();

        Assertions.assertEquals(special.calculate(0.0), 0.0);
        Assertions.assertEquals(special.calculate(100.0), 95.0);
        Assertions.assertEquals(special.calculate(150.0), 140.0);
        Assertions.assertEquals(special.calculate(200.0), 185.0);
        Assertions.assertEquals(special.calculate(300.0), 270);
        Assertions.assertEquals(special.calculate(400.0), 350);
        Assertions.assertEquals(special.calculate(500.0), 425);
        Assertions.assertEquals(special.calculate(1000.0), 775.0);
    }

}
