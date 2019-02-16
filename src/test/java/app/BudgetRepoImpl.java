package app;

import app.repository.IBudgetRepo;
import java.util.ArrayList;
import java.util.List;

public class BudgetRepoImpl implements IBudgetRepo {

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
}
