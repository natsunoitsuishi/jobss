package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import springfox.documentation.swagger.schema.ApiModelPropertyPropertyBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service

public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserMapper userMapper;
    private ApiModelPropertyPropertyBuilder apiModelPropertyPropertyBuilder;

    @Override
    public TurnoverReportVO getTurnoverStatistics(
            LocalDate begin,
            LocalDate end
    ) {
        return TurnoverReportVO.builder()
                .dateList(GetStringTimeFromBeginAndEnd(begin, end))
                .turnoverList(
                        StringUtils.join(
                                orderMapper.getsumDailyRevenue(
                                        LocalDateTime.of(begin, LocalTime.MIN),
                                        LocalDateTime.of(end, LocalTime.MAX),
                                        Orders.COMPLETED
                                ), ",")
                )
                .build();
    }

    @Override
    public UserReportVO getUserStatistics(
            LocalDate begin,
            LocalDate end
    ) {
        List <Integer> newUserList = new ArrayList();
        List <Integer> totalUserList = new ArrayList();

        for (LocalDate date : GetListTimeFromBeginAndEnd(begin, end)) {
            Map map = new HashMap();
            map.put("end", LocalDateTime.of(date, LocalTime.MAX));
            Integer totalUser = userMapper.countByMap(map);

            map.put("begin", LocalDateTime.of(date, LocalTime.MIN));
            Integer newUser = userMapper.countByMap(map);

            totalUserList.add(totalUser);
            newUserList.add(newUser);
        }

        return UserReportVO.builder()
                .dateList(GetStringTimeFromBeginAndEnd(begin, end))
                .totalUserList(StringUtils.join(totalUserList,","))
                .newUserList(StringUtils.join(newUserList,","))
                .build();
    }

    @Override
    public OrderReportVO getOrdersStatistics(
            LocalDate begin,
            LocalDate end
    ) {
        List <Integer> orderCountList = new ArrayList();
        List <Integer> validOrderCountList = new ArrayList();
        for (LocalDate date : GetListTimeFromBeginAndEnd(begin, end)) {
            orderCountList.add(
                    getOrderCount(
                        LocalDateTime.of(date, LocalTime.MIN),
                        LocalDateTime.of(date, LocalTime.MAX),
                        null
                    )
            );

            validOrderCountList.add(
                    getOrderCount(
                        LocalDateTime.of(date, LocalTime.MIN),
                        LocalDateTime.of(date, LocalTime.MAX),
                        Orders.COMPLETED
                    )
            );
        }
        return OrderReportVO.builder()
                .dateList(GetStringTimeFromBeginAndEnd(begin, end))
                .orderCountList(StringUtils.join(orderCountList, ","))
                .validOrderCountList(StringUtils.join(validOrderCountList,","))
                .totalOrderCount(orderCountList.stream().mapToInt(Integer::intValue).sum())
                .validOrderCount(validOrderCountList.stream().mapToInt(Integer::intValue).sum())
                .orderCompletionRate(
                        Optional.of(validOrderCountList.stream().mapToDouble(Integer::doubleValue).sum())
                                .filter(validOrderCount -> validOrderCount != 0)
                                .map(validOrderCount -> orderCountList.stream()
                                        .mapToDouble(Integer::doubleValue)
                                        .sum() / validOrderCount
                                )
                                .orElse(0.0)
                )
                .build();
    }

    private Integer getOrderCount(
            LocalDateTime begin,
            LocalDateTime end,
            Integer status
    ) {
        Map map = new HashMap();
        map.put("begin", begin);
        map.put("end", end);
        map.put("status", status);

        return orderMapper.countByMap(map);
    }

    private String GetStringTimeFromBeginAndEnd(
            LocalDate begin,
            LocalDate end
    ) {
        return Stream
                .iterate(begin, localDate -> localDate.plusDays(1))
                .limit(ChronoUnit.DAYS.between(begin, end) + 1)
                .map(LocalDate::toString)
                .collect(Collectors.joining(","));
    }

    private List<LocalDate> GetListTimeFromBeginAndEnd(
            LocalDate begin,
            LocalDate end
    ) {
        return Stream
                .iterate(
                        begin,
                        localDate->!localDate.isAfter(end),
                        localDate->localDate.plusDays(1)
                )
                .toList();
    }
}