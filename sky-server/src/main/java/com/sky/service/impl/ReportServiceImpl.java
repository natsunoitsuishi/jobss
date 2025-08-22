package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Override
    public TurnoverReportVO getTurnoverStatistics(
            LocalDate begin,
            LocalDate end
    ) {
        return TurnoverReportVO.builder()
                .dateList(
                        Stream
                        .iterate(begin, localDate -> localDate.plusDays(1))
                        .limit(ChronoUnit.DAYS.between(begin, end) + 1)
                        .map(LocalDate::toString)
                        .collect(Collectors.joining(","))
                )
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

        for (LocalDate date :
                Stream
                        .iterate(
                                begin,
                                localDate->!localDate.isAfter(end),
                                localDate->localDate.plusDays(1))
                        .toList()
        ) {
            Map map = new HashMap();
            map.put("end", LocalDateTime.of(date, LocalTime.MAX));
            Integer totalUser = userMapper.countByMap(map);

            map.put("begin", LocalDateTime.of(date, LocalTime.MIN));
            Integer newUser = userMapper.countByMap(map);

            totalUserList.add(totalUser);
            newUserList.add(newUser);
        }

        return UserReportVO.builder()
                .dateList(
                        Stream
                        .iterate(begin, localDate -> localDate.plusDays(1))
                        .limit(ChronoUnit.DAYS.between(begin, end) + 1)
                        .map(LocalDate::toString)
                        .collect(Collectors.joining(","))
                )
                .totalUserList(
                        StringUtils.join(totalUserList,",")
                )
                .newUserList(
                        StringUtils.join(newUserList,",")
                )
                .build();
    }
}
