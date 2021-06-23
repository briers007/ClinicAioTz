package com.minorfish.clinicwaste;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Author: Administrator
 * Date: 2017/11/10
 */

public class Test {
    public static void main(String[] args){
        BigDecimal bd1 = new BigDecimal(1.1f);
        BigDecimal bd2 = new BigDecimal(1.32f);
        System.out.println(bd1.add(bd2).floatValue());
        System.out.println(bd1.add(bd2).doubleValue());
        System.out.println(bd1.add(bd2).intValue());
        System.out.println(bd1.add(bd2).setScale(1, RoundingMode.HALF_UP).floatValue());
    }
}
