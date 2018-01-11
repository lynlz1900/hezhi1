package com.wukong.hezhi.bean;

import java.io.Serializable;

/**
 *
 * @ClassName: OrderStateInfo
 * @Description: TODO(订单状态)
 * @author HuZhiyin
 * @date 2017-7-5 下午3:23:22
 *
 */


public class OrderStateInfo implements Serializable {
    public static final int ALL = 0;//全部
    public static final int WAIT_PAY = 1;//待付款
    public static final int WAIT_SEND = 2;//待发货
    public static final int WAIT_RECIEVE = 3;//待收货
    public static final int FINISH = 4;//已完成
    public static final int WAIT_COMMENT = 5;//待评论

    private int payingConut;//订单待付款总数
    private int deliveringCount;//订单待收款总数
    private int awaitCommentConut;//单待评价总数

    public int getPayingConut() {
        return payingConut;
    }

    public void setPayingConut(int payingConut) {
        this.payingConut = payingConut;
    }

    public int getDeliveringCount() {
        return deliveringCount;
    }

    public void setDeliveringCount(int deliveringCount) {
        this.deliveringCount = deliveringCount;
    }

    public int getAwaitCommentConut() {
        return awaitCommentConut;
    }

    public void setAwaitCommentConut(int awaitCommentConut) {
        this.awaitCommentConut = awaitCommentConut;
    }
}
