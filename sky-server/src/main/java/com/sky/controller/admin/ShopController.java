package com.sky.controller.admin;

import com.sky.constant.ShopStatusConstant;
import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.quartz.QuartzTransactionManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.spi.schema.EnumTypeDeterminer;

import java.util.concurrent.TimeUnit;

@RestController("adminShopController")
@RequestMapping("/admin/shop")
@Api(tags = "about shop")
@Slf4j

public class ShopController {

    public static final String KEY = "SHOP_STATUS";

    @Autowired
    private RedisTemplate redisTemplate;

    @PutMapping("/{status}")
    @ApiOperation(value = "setShopStatus")
    public Result setStatus (@PathVariable Integer status){
        log.info("setShopStatus {}", status == Integer.valueOf(ShopStatusConstant.SHOP_OPEN) ? "on" : "off");

        redisTemplate.opsForValue().set(KEY, String.valueOf(status));

        return Result.success();
    }

    @GetMapping("/status")
    @ApiOperation(value = "getShopStatus")
    public Result<Integer> getStatus () {
        String status = (String) redisTemplate.opsForValue().get(KEY);
        log.info("setShopStatus {}", status.equals(ShopStatusConstant.SHOP_OPEN) ? "on" : "off");
        return Result.success(Integer.valueOf(status));
    }
}
