package app;

import app.repository.IBudgetRepo;
import java.time.LocalDate;
import java.time.Period;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Accounting {

    public Accounting(IBudgetRepo budget) {
        this.budget = budget;
    }

    private IBudgetRepo budget;

    private YearMonth getYearMonth(String yearMonth) {
        return YearMonth.parse(yearMonth, DateTimeFormatter.ofPattern("yyyyMM"));
    }

    private Budget getFullMonthBudget(LocalDate date) {
        List<Budget> list = budget.getAll();
        int month = date.getMonthValue();
        for (Budget budget : list) {
            YearMonth ym = getYearMonth(budget.yearMonth);
            if (month == ym.getMonthValue()) {
                return budget;
            }
        }
        return null;
    }

    public double totalAmount(LocalDate start, LocalDate end) {
        return isCrossMonth(start, end) ?
            getCrossMonthBudget(start, end) : getSameMonth(start, end);
    }

    private double getSameMonth(LocalDate start, LocalDate end) {
        Budget monthBudget = getFullMonthBudget(start);
        if (monthBudget == null) {
            return 0;
        }
        if (isSameDate(start, end)) {
            return getSingleDayBudget(start);
        }
        Period p = Period.between(start, end);
        if (p.getDays() == 1) {
            return (getSingleDayBudget(start)) * (p.getDays() + 1);
        }
        return monthBudget.amount;
    }

    private boolean isSameDate(LocalDate start, LocalDate end) {
        return start.equals(end);
    }

    private boolean isCrossMonth(LocalDate start, LocalDate end) {
        return start.getMonthValue() != end.getMonthValue();
    }

    private double getCrossMonthBudget(LocalDate start, LocalDate end) {
        if (start.isAfter(end)) {
            return 0;
        }
        int intervalAmount = 0;
        if (Period.between(start, end).getMonths() > 1) {
            LocalDate intervalDate = start;
            for (int i = start.getMonthValue(); i < end.getMonthValue() - 1; i++) {
                intervalAmount += getFullMonthBudget(intervalDate.plusMonths(1)).amount;
            }
        }
        return getStartMonthAmount(start) + getEndMonthAmount(end) + intervalAmount;
    }

    private int getEndMonthAmount(LocalDate date) {
        int daysInEnd = date.getDayOfMonth();
        return getSingleDayBudget(date) * daysInEnd;
    }

    private int getStartMonthAmount(LocalDate date) {
        int daysInStart = date.lengthOfMonth() - date.getDayOfMonth() + 1;
        return getSingleDayBudget(date) * daysInStart;

    }

    private int getSingleDayBudget(LocalDate date) {
        return getFullMonthBudget(date).amount / date.lengthOfMonth();
    }
}
