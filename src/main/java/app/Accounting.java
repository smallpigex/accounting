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

        if(start.isAfter(end)) {
            return 0;
        }

        if (start.getMonthValue() != end.getMonthValue()) {

            int startAmount = getStartMonthAmount(start);

            int intervalMonths = Period.between(start, end).getMonths();
            int intervalAmount = 0;
            LocalDate intervalDate = start;
            if(intervalMonths>1){
                for(int i =start.getMonthValue();i<end.getMonthValue()-1;i++){
                    intervalDate = intervalDate.plusMonths(1);
                    intervalAmount += getFullMonthBudget(intervalDate).amount;
                }
            }

            int endAmount = getEndMonthAmount(end);

            return startAmount + endAmount + intervalAmount;
        }


        // same month.
        Budget monthBudget = getFullMonthBudget(start);
        if (monthBudget == null) {
            return 0;
        }
        if (start.equals(end)) {
            return getSingleDayBudget(start);
        }



        Period p = Period.between(start, end);

        if (p.getDays() == 1) {
            return (getSingleDayBudget(start)) * (p.getDays() + 1);
        }
        return monthBudget.amount;
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
