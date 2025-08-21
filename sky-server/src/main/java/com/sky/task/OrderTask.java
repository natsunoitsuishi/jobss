package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j

public class OrderTask {

    @Autowired
    private OrderMapper orderMapper;

//    @Scheduled(cron = "1/5 * * * * ?")
    @Scheduled(cron = "0 * * * * ?")
    public void processTimeoutOrder() {
        log.info("TimeoutOrder ====> {}", LocalDateTime.now().toString());

        List<Orders> ordersList = orderMapper.getByStatusAndOrderTimeLT(
                Orders.PENDING_PAYMENT,
                LocalDateTime.now().plusMinutes(-15)
        );
        if (ordersList != null && !ordersList.isEmpty()) {
            for (Orders orders : ordersList) {
                orders.setStatus(Orders.CANCELLED);
                orders.setCancelReason("Caonimasb");
                orders.setCancelTime(LocalDateTime.now());

                orderMapper.update(orders);
            }
        }
    }

//    @Scheduled(cron = "0/5 * * * * ?")
    @Scheduled(cron = "0 0 1 * * ?")
    public void processDeliveryOrder() {
        log.info("DeliveryOrder ====> {}", LocalDateTime.now().toString());

        List<Orders> ordersList = orderMapper.getByStatusAndOrderTimeLT(
                Orders.DELIVERY_IN_PROGRESS,
                LocalDateTime.now().plusMinutes(-60)
        );

        if (ordersList != null && !ordersList.isEmpty()) {
            for (Orders orders : ordersList) {
                orders.setStatus(Orders.COMPLETED);

                orderMapper.update(orders);
            }
        }
    }

}
