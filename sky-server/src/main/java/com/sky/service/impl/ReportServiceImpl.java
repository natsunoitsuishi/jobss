package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.service.ReportService;
import com.sky.vo.TurnoverReportVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service

public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;

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
}
