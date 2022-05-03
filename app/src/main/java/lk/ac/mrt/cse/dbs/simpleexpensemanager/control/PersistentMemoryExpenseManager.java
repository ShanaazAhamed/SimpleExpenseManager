package lk.ac.mrt.cse.dbs.simpleexpensemanager.control;

import android.content.Context;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.control.exception.ExpenseManagerException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistentMemoryAccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistentTransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.ui.MainActivity;


public class PersistentMemoryExpenseManager extends ExpenseManager {
    public PersistentMemoryExpenseManager(Context context) {
        setup(context);
    }

    @Override
    public void setup(){
    }

    @Override
    public void setup(Context context) {
        setTransactionsDAO(new PersistentTransactionDAO(context));
        setAccountsDAO(new PersistentMemoryAccountDAO(context));
    }
}
