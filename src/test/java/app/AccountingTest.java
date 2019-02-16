package app;

import static org.junit.Assert.*;

import app.repository.IBudgetRepo;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

public class AccountingTest {


    private Accounting accounting;

    @Test
    public void fullMonth() {
        accounting = new Accounting(new IBudgetRepo() {
            @Override
            public List<Budget> getAll() {
                ArrayList<Budget> list = new ArrayList<>();
                list.add(new Budget("201904",3000));
                return list;
            }
        });

        LocalDate start = LocalDate.of(2019, 4, 1);
        LocalDate end = LocalDate.of(2019, 4, 30);
        assertTrue(3000 == accounting.totalAmount(start, end));
    }

    @Test
    public void OneDay() {
        accounting = new Accounting(new IBudgetRepo() {
            @Override
            public List<Budget> getAll() {
                ArrayList<Budget> list = new ArrayList<>();
                list.add(new Budget("201904",3000));
                return list;
            }
        });
        LocalDate start = LocalDate.of(2019, 4, 1);
        LocalDate end = LocalDate.of(2019, 4, 1);
        amountShouldBe(start, end, 100);
    }

    @Test
    public void Period() {
        accounting = new Accounting(new IBudgetRepo() {
            @Override
            public List<Budget> getAll() {
                ArrayList<Budget> list = new ArrayList<>();
                list.add(new Budget("201904",3000));
                return list;
            }
        });
        LocalDate start = LocalDate.of(2019, 4, 1);
        LocalDate end = LocalDate.of(2019, 4, 2);
        amountShouldBe(start, end, 200);
    }

    @Test
    public void noBudget() {
        accounting = new Accounting(new IBudgetRepo() {
            @Override
            public List<Budget> getAll() {
                return new ArrayList<>();
            }
        });
        LocalDate start = LocalDate.of(2019, 3, 1);
        LocalDate end = LocalDate.of(2019, 3, 10);
        amountShouldBe(start, end, 0);
    }

    @Test
    public void CrossMonths() {
        accounting = new Accounting(new IBudgetRepo() {
            @Override
            public List<Budget> getAll() {
                ArrayList<Budget> list = new ArrayList<>();
                list.add(new Budget("201902",1400));
                list.add(new Budget("201901",3100));
                return list;
            }
        });
        LocalDate start = LocalDate.of(2019, 1, 31);
        LocalDate end = LocalDate.of(2019, 2, 1);
        amountShouldBe(start, end, 150);
    }

    @Test
    public void CrossNoBudgetMonths() {
        accounting = new Accounting(new IBudgetRepo() {
            @Override
            public List<Budget> getAll() {
                ArrayList<Budget> list = new ArrayList<>();
                list.add(new Budget("201902",1400));
                list.add(new Budget("201903",3100));
                list.add(new Budget("201904",3000));
                return list;
            }
        });
        LocalDate start = LocalDate.of(2019, 2, 1);
        LocalDate end = LocalDate.of(2019, 4, 1);
        amountShouldBe(start, end, 4600);
    }

    @Test
    public void CrossYears() {
        accounting = new Accounting(new IBudgetRepo() {
            @Override
            public List<Budget> getAll() {
                ArrayList<Budget> list = new ArrayList<>();
                list.add(new Budget("201901",3100));
                list.add(new Budget("201812",3100));
                return list;
            }
        });
        LocalDate start = LocalDate.of(2018, 12, 31);
        LocalDate end = LocalDate.of(2019, 1, 1);
        amountShouldBe(start, end, 200);
    }

    @Test
    public void ErrorDate() {
        accounting = new Accounting(new IBudgetRepo() {
            @Override
            public List<Budget> getAll() {
                ArrayList<Budget> list = new ArrayList<>();
                list.add(new Budget("201904",3000));
                list.add(new Budget("201902",2800));
                list.add(new Budget("201901",3100));
                list.add(new Budget("201812",3100));
                list.add(new Budget("201912",3100));
                return list;
            }
        });
        LocalDate start = LocalDate.of(2019, 4, 1);
        LocalDate end = LocalDate.of(2019, 2, 1);
        amountShouldBe(start, end, 0);
    }

    private void amountShouldBe(LocalDate start, LocalDate end, int i) {
        assertEquals(i, accounting.totalAmount(start, end), 0.00);
    }


}






