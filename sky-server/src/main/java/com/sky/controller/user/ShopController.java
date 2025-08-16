package com.sky.controller.user;

import com.sky.constant.ShopStatusConstant;
import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController("userShopController")
@RequestMapping("/user/shop")
@Api(tags = "about shop")
@Slf4j

public class ShopController {

    public static final String KEY = "SHOP_STATUS";

    @Autowired
    private RedisTemplate redisTemplate;

    @GetMapping("/status")
    @ApiOperation(value = "getShopStatus")
    public Result<Integer> getStatus () {
        String status = (String) redisTemplate.opsForValue().get(KEY);
        log.info("setShopStatus {}", status.equals(ShopStatusConstant.SHOP_OPEN) ? "on" : "off");
        return Result.success(Integer.valueOf(status));
    }
}
