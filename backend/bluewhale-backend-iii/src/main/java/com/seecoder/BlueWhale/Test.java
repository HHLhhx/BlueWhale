package com.seecoder.BlueWhale;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Test {
    public static void main(String[] args) {
        double number = 3.14159;
        BigDecimal bd = new BigDecimal(number);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        double roundedNumber = bd.doubleValue();
        System.out.println(roundedNumber);
    }
}
