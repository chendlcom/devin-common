package com.quanzikong.common.to;

import java.io.Serializable;

/**
 * @author Devin on 2018-06-27 19:05.
 */
public class TestPojo implements Serializable {
    private int one;
    private String two;

    public int getOne() {
        return one;
    }

    public void setOne(int one) {
        this.one = one;
    }

    public String getTwo() {
        return two;
    }

    public void setTwo(String two) {
        this.two = two;
    }
}
