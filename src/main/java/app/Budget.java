package app;

import java.time.LocalDate;
import java.time.Period;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

public class Budget {

    public String yearMonth;
    public int amount;

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMM");

    public Budget(String yearMonth, int amount) {
        this.yearMonth = yearMonth;
        this.amount = amount;
    }

    YearMonth getYearMonth() {
        return YearMonth.parse(this.yearMonth, this.dateTimeFormatter);
    }

    public boolean isSameYearMonth(LocalDate target) {
        return getYearMonth()
            .equals(YearMonth.of(target.getYear(), target.getMonthValue()));
    }

    private double getDailyAmount() {
        if (this.amount == 0) {
            return 0;
        }
        return (double) amount / getYearMonth().lengthOfMonth();
    }

    public double getAmountByDuration(LocalDate startDate, LocalDate endDate) {
        Period p = Period.between(startDate, endDate);
        return getAmountByNumberOfDay(p.getDays() + 1);
    }

    private double getAmountByNumberOfDay(int times) {
        return (double) Math.round(getDailyAmount() * times * 100) / 100;
    }

    public double getAmountByStartDate(LocalDate start) {
        int numberOfDay = start.lengthOfMonth() - start.getDayOfMonth() + 1;
        return getAmountByNumberOfDay(numberOfDay);
    }

    public double getAmountByEndDate(LocalDate end) {
        return getAmountByNumberOfDay(end.getDayOfMonth());
    }

    public boolean isBetween(LocalDate start, LocalDate end) {
        YearMonth yearMonth = getYearMonth();
        return yearMonth.isAfter(YearMonth.of(start.getYear(), start.getMonth()))
            && yearMonth.isBefore(YearMonth.of(end.getYear(), end.getMonth()));
    }
}
