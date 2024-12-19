package com.phonecompany.billing.telephonebillcalculator;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class TelephoneBillCalculatorImpl implements TelephoneBillCalculator {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    @Override
    public BigDecimal calculate(String phoneLog) {
        if (phoneLog == null || phoneLog.isEmpty()) {
            return BigDecimal.ZERO;
        }

        String[] calls = phoneLog.split("\n");
        Map<String, Integer> callDurations = new HashMap<>();
        Map<String, BigDecimal> callCosts = new HashMap<>();

        for (String call : calls) {
            String[] parts = call.split(",");
            String phoneNumber = parts[0];
            LocalDateTime startTime = LocalDateTime.parse(parts[1], FORMATTER);
            LocalDateTime endTime = LocalDateTime.parse(parts[2], FORMATTER);

            int totalMinutes = calculateMinutes(startTime, endTime);
            BigDecimal cost = calculateCost(startTime, totalMinutes);

            callDurations.put(phoneNumber, callDurations.getOrDefault(phoneNumber, 0) + totalMinutes);
            callCosts.put(phoneNumber, callCosts.getOrDefault(phoneNumber, BigDecimal.ZERO).add(cost));
        }

        String mostCalledNumber = getMostCalledNumber(callDurations);
        callCosts.remove(mostCalledNumber);

        return callCosts.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private int calculateMinutes(LocalDateTime startTime, LocalDateTime endTime) {
        return (int) Math.ceil((double) Duration.between(startTime, endTime).getSeconds() / 60);
    }

    private BigDecimal calculateCost(LocalDateTime startTime, int totalMinutes) {
        BigDecimal cost = BigDecimal.ZERO;
        for (int minute = 0; minute < totalMinutes; minute++) {
            LocalDateTime currentMinute = startTime.plusMinutes(minute);
            if (minute < 5) {
                cost = cost.add(getMinuteRate(currentMinute));
            } else {
                cost = cost.add(BigDecimal.valueOf(0.20));
            }
        }
        return cost;
    }

    private BigDecimal getMinuteRate(LocalDateTime time) {
        LocalTime startTime = LocalTime.of(8, 0);
        LocalTime endTime = LocalTime.of(16, 0);
        LocalTime checkTime = LocalTime.of(time.getHour(), 0);

        if ((checkTime.equals(startTime) || checkTime.isAfter(startTime)) && checkTime.isBefore(endTime)) {
            return BigDecimal.valueOf(1.00);
        } else {
            return BigDecimal.valueOf(0.50);
        }
    }

    private String getMostCalledNumber(Map<String, Integer> callDurations) {
        return callDurations.entrySet()
                .stream()
                .max(Comparator.comparingInt(Map.Entry<String, Integer>::getValue)
                        .thenComparing(Map.Entry::getKey))
                .map(Map.Entry::getKey)
                .orElse("");
    }
}
