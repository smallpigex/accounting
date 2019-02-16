package app;

import app.repository.IBudgetRepo;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Accounting {

    private IBudgetRepo budget;

    public Accounting(IBudgetRepo budget) {
        this.budget = budget;
    }

    private double getBudgetAmountWhenSameMonth(LocalDate start, LocalDate end) {
        Budget budget = findBudget(start);
        return budget.getAmountByDuration(start, end);
    }

    private boolean isCrossMonth(LocalDate start, LocalDate end) {
        return start.getMonthValue() != end.getMonthValue();
    }

    private double getBudgetAmountWhenCrossMonth(LocalDate start, LocalDate end) {
        List<Budget> budgets = findBudgetBetween(start, end);
        int interval = budgets.stream().mapToInt(budget -> budget.amount).sum();
        return getStartMonthAmount(start) + getEndMonthAmount(end) + interval;
    }

    private double getEndMonthAmount(LocalDate date) {
        Budget budget = findBudget(date);
        return budget.getAmountByEndDate(date);
    }

    private double getStartMonthAmount(LocalDate date) {
        Budget budget = findBudget(date);
        return budget.getAmountByStartDate(date);
    }

    private Budget findBudget(LocalDate date) {
        List<Budget> list = budget.getAll();
        for (Budget budget : list) {
            if (budget.isSameYearMonth(date)) {
                return budget;
            }
        }
        return new Budget("", 0);
    }

    private List<Budget> findBudgetBetween(LocalDate start, LocalDate end) {
        List<Budget> result = new ArrayList<>();
        List<Budget> list = budget.getAll();
        for (Budget budget : list) {
            if (budget.isBetween(start, end)) {
                result.add(budget);
            }
        }
        return result;
    }

    public double totalAmount(LocalDate start, LocalDate end) {
        if (start.isAfter(end)) {
            // 開始大於結束當錯誤輸入回傳0
            return 0;
        }

        return isCrossMonth(start, end) ?
            getBudgetAmountWhenCrossMonth(start, end) : getBudgetAmountWhenSameMonth(start, end);
    }
}
