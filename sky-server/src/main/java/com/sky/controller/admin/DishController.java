package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/dish")
@Api(tags = "dish Port")
@Slf4j

public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping
    @ApiOperation(value = "new Dish")
    public Result save (@RequestBody DishDTO dishDTO,
                        @RequestHeader(value = "Content-Type", required = false) String header) {
        log.info("RequestHeader :{}", header);
        log.info("DishDTO : {}", dishDTO);

        dishService.saveWithFlavor(dishDTO);

        cleanCache("dish_" + dishDTO.getCategoryId());

        return Result.success();
    }

    @GetMapping("/page")
    @ApiOperation(value = "new page")
    public Result<PageResult> page (DishPageQueryDTO dishPageQueryDTO) {
        log.info("new page :{}", dishPageQueryDTO);
        PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    @DeleteMapping
    @ApiOperation(value = "deleteByIds")
    public Result deleteByIds(@RequestParam List<Long> ids) {
        log.info("deleteByIds :{}", ids);
        dishService.deleteBatch(ids);

        cleanCache("dish_*");

        return Result.success();
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "getById")
    public Result<DishVO> getById(@PathVariable Long id) {
        log.info("getById : {}", id);
        DishVO dishVO = dishService.getByIdWithFlavor (id);
        return Result.success(dishVO);
    }

    @PutMapping
    @ApiOperation(value = "update")
    public Result update (@RequestBody DishDTO dishDTO) {
        log.info("update :{}", dishDTO);
        dishService.updateWithFlavor (dishDTO);

        cleanCache("dish_*");

        return Result.success();
    }

    private void cleanCache(String pattern) {
        redisTemplate.delete(redisTemplate.keys(pattern));
    }
}

