package com.wukong.hezhi.function.cashcase;

/**
 * Created by wanjian on 2017/2/15.
 */

@SuppressWarnings("serial")
final class QuitCockroachException extends RuntimeException {
    public QuitCockroachException(String message) {
        super(message);
    }
}
