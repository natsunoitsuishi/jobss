package com.sky.controller.user;

import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderSubmitVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("userOrderController")
@RequestMapping("/user/order")
@Slf4j
@Api(tags = "C Duan")
public class OrderController {

    @Autowired
    private OrderService orderService;


    @PostMapping("/submit")
    @ApiOperation(value = "user Order")
    public Result<OrderSubmitVO> submit (@RequestBody OrdersSubmitDTO orderSubmitDTO) {
        OrderSubmitVO orderSubmitVO = orderService.submitOrder (orderSubmitDTO);
        return Result.success(orderSubmitVO);
    }

}
