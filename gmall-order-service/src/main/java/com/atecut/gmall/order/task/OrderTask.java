package com.atecut.gmall.order.task;

import com.atecut.gmall.bean.OrderInfo;
import com.atecut.gmall.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@EnableScheduling
public class OrderTask {

    @Autowired
    OrderService orderService;

    // cron表示任务启动规则、每分钟的第五秒执行该方法
    @Scheduled(cron = "5 * * * * ?")
    public void test01() {
        System.out.println(Thread.currentThread().getName() + "--------0001----------");
    }

    // 每隔20秒执行一次
    @Scheduled(cron = "0/20 * * * * ?")
    public  void checkOrder(){
        System.out.println("开始处理过期订单");
        long starttime = System.currentTimeMillis();
        List<OrderInfo> expiredOrderList = orderService.getExpiredOrderList();
        for (OrderInfo orderInfo : expiredOrderList) {
            // 处理未完成订单
            orderService.execExpiredOrder(orderInfo);
        }
        long costtime = System.currentTimeMillis() - starttime;
        System.out.println("一共处理"+expiredOrderList.size()+"个订单 共消耗"+costtime+"毫秒");
    }

}
