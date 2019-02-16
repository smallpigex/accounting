package app;

import app.repository.IBudgetRepo;
import java.time.LocalDate;
import java.time.Period;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Accounting {

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMM");
    private IBudgetRepo budget;

    public Accounting(IBudgetRepo budget) {
        this.budget = budget;
    }

    private YearMonth getYearMonth(String yearMonth) {
        return YearMonth.parse(yearMonth, dateTimeFormatter);
    }

    private boolean isMonthMatch(Budget budget, int month) {
        return month == getYearMonth(budget.yearMonth).getMonthValue();
    }

    private Budget getFullMonthBudget(LocalDate date) {
        List<Budget> list = budget.getAll();
        int month = date.getMonthValue();
        for (Budget budget : list) {
            if (isMonthMatch(budget, month)) {
                return budget;
            }
        }
        return null;
    }

    private double getSameMonth(LocalDate start, LocalDate end) {
        Budget monthBudget = getFullMonthBudget(start);
        if (monthBudget == null) {
            return 0;
        }
        Period p = Period.between(start, end);
        return (getSingleDayBudget(start)) * (p.getDays() + 1);
    }

    private boolean isCrossMonth(LocalDate start, LocalDate end) {
        return start.getMonthValue() != end.getMonthValue();
    }

    private double getCrossMonthBudget(LocalDate start, LocalDate end) {
        int intervalAmount = 0;
        if (Period.between(start, end).getMonths() > 1) {
            LocalDate intervalDate = start;
            for (int i = start.getMonthValue(); i < end.getMonthValue() - 1; i++) {
                intervalAmount += getFullMonthBudget(intervalDate.plusMonths(1)).amount;
            }
        }
        return getStartMonthAmount(start) + getEndMonthAmount(end) + intervalAmount;
    }

    private double getEndMonthAmount(LocalDate date) {
        int daysInEnd = date.getDayOfMonth();
        return getSingleDayBudget(date) * daysInEnd;
    }

    private double getStartMonthAmount(LocalDate date) {
        int daysInStart = date.lengthOfMonth() - date.getDayOfMonth() + 1;
        return getSingleDayBudget(date) * daysInStart;

    }

    private double getSingleDayBudget(LocalDate date) {
        return getFullMonthBudget(date).amount / date.lengthOfMonth();
    }

    public double totalAmount(LocalDate start, LocalDate end) {
        if (start.isAfter(end)) {
            // 開始大於結束當錯誤輸入回傳0
            return 0;
        }
        return isCrossMonth(start, end) ?
                getCrossMonthBudget(start, end) : getSameMonth(start, end);
    }
}
