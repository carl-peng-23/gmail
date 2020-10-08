package com.atecut.gmall.service;

import com.atecut.gmall.bean.OrderInfo;
import com.atecut.gmall.bean.enums.ProcessStatus;

public interface OrderService {
    /*
        保存订单
     */
    String saveOrder(OrderInfo orderInfo);

    /*
        生成流水号
     */
    String getTradeNo(String userId);

    /*
        比较流水号
     */
    boolean checkTradeCode(String userId,String tradeCodeNo);

    /*
        删除流水号
     */
    void  delTradeCode(String userId);

    /*
        查询是否有足够的库存
     */
    boolean checkStock(String skuId, Integer skuNum);

    /*
        通过订单号查询订单详情
     */
    OrderInfo getOrderInfo(String orderId);

    /*
        更新orderInfo表中订单状态
     */
    void updateOrderStatus(String orderId, ProcessStatus paid);

    /*
        发送消息给库存
     */
    void sendOrderStatus(String orderId);
}
