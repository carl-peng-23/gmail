package com.atecut.gmall.service;

import com.atecut.gmall.bean.PaymentInfo;

import java.util.Map;

public interface PaymentService {
    /**
     * 保存交易记录
     * @param paymentInfo
     */
    void savePaymentInfo(PaymentInfo paymentInfo);

    /**
     * 根据out_trade_no查询PaymentInfo
     * @param paymentInfo
     * @return
     */
    PaymentInfo getPaymentInfo(PaymentInfo paymentInfo);

    /**
     * 支付成功之后更新订单状态
     * @param out_trade_no
     * @param paymentInfoUpd
     */
    void updatePaymentInfo(String out_trade_no, PaymentInfo paymentInfoUpd);

    /**
     * 根据orderId退款
     * @param orderId
     * @return
     */
    boolean refund(String orderId);

    /**
     * 创建微信支付
     * @param orderId
     * @param total_fee
     * @return
     */
    Map createNative(String orderId, String total_fee);

    /**
     * 发送支付结果
     * @param paymentInfo
     * @param result
     */
    void sendPaymentResult(PaymentInfo paymentInfo, String result);

    /**
     * 查询是否支付成功
     * @param paymentInfoQuery
     * @return
     */
    boolean checkPayment(PaymentInfo paymentInfoQuery);

    /**
     * 发送延迟队列
     * @param outTradeNo
     * @param delaySec
     * @param checkCount
     */
     void sendDelayPaymentResult(String outTradeNo,int delaySec ,int checkCount);
}
