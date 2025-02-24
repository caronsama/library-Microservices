package com.caron;

import java.util.concurrent.atomic.AtomicInteger;

public class LoginUser {

    private static AtomicInteger visitCount = new AtomicInteger(0);

    public static void addVisitCount() {
        visitCount.incrementAndGet();
    }

    public static int getVisitCount() {
        return visitCount.get();
    }

    public static void setZero() {
        visitCount.set(0);
    }
}
