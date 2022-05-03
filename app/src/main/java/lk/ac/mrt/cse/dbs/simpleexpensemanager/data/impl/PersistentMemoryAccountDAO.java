package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.control.DBHelper;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

public class PersistentMemoryAccountDAO implements AccountDAO {
    private DBHelper db;

    public PersistentMemoryAccountDAO(Context context) {
        db = new DBHelper(context);
    }

    @Override
    public List<String> getAccountNumbersList() {
        return  new ArrayList<>(this.db.getAccounts().keySet());
    }

    @Override
    public List<Account> getAccountsList() {
        return new ArrayList<>(this.db.getAccounts().values());
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        return this.db.getAccounts().get(accountNo);
    }

    @Override
    public void addAccount(Account account) {
        db.addAccount(account);
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        db.removeAccount(accountNo);
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        if (!db.getAccounts().containsKey(accountNo)) {
            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }
        Account account = db.getAccounts().get(accountNo);

        switch (expenseType) {
            case EXPENSE:
                account.setBalance(account.getBalance() - amount);
                break;
            case INCOME:
                account.setBalance(account.getBalance() + amount);
                break;
        }
        db.updateAccount(account);
    }
}
