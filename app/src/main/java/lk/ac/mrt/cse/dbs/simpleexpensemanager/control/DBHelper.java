package lk.ac.mrt.cse.dbs.simpleexpensemanager.control;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;


public class DBHelper extends SQLiteOpenHelper{
    private static final String DB_NAME = "account.db";
    private static final int DB_VERSION = 1;
    private static final String TABLE_ACC = "account";
    private static final String ACC_NO = "accountNo";
    private static final String BANK = "bankName";
    private static final String ACC_HOLDER = "accountHolderName";
    private static final String BAL = "balance";
    private static final String TABLE_TRAN = "transactiontable";
    private static final String ID = "id";
    private static final String DATE = "date";
    private final static  String EXPENSE_TYPE = "type";
    private final static  String AMOUNT = "amount";
    private final Context context;


    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase DB) {
        DB.execSQL("CREATE TABLE " + TABLE_ACC + " ("
                + ACC_NO + " TEXT PRIMARY KEY NOT NULL, "
                + BANK + " TEXT NOT NULL,"
                + ACC_HOLDER + " TEXT NOT NULL,"
                + BAL + " NUMERIC NOT NULL)");
        DB.execSQL("CREATE TABLE IF NOT EXISTS " +
                TABLE_TRAN +
                "(" +
                ID+" INTEGER PRIMARY KEY AUTOINCREMENT," +
                DATE+" TEXT NOT NULL," +
                ACC_NO+" TEXT NOT NULL,"+
                EXPENSE_TYPE+" TEXT NOT NULL," +
                AMOUNT+" NUMERIC NOT NULL," +
                "FOREIGN KEY(" +ACC_NO +") REFERENCES " +TABLE_ACC+"("+ACC_NO+"));");
    }

    @Override
    public void onUpgrade(SQLiteDatabase DB,  int oldVersion, int newVersion) {
        DB.execSQL("DROP TABLE IF EXISTS " + TABLE_ACC);
        DB.execSQL("DROP TABLE IF EXISTS " + TABLE_TRAN + ";");
        onCreate(DB);
    }

    public void addAccount(Account account) {

        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ACC_NO,account.getAccountNo());
        values.put(BANK,account.getBankName());
        values.put(ACC_HOLDER,account.getAccountHolderName());
        values.put(BAL, account.getBalance());
        DB.insert(TABLE_ACC, null, values);
        DB.close();
    }
    public void updateAccount(Account account) {
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ACC_NO,account.getAccountNo());
        values.put(BANK,account.getBankName());
        values.put(ACC_HOLDER,account.getAccountHolderName());
        values.put(BAL, account.getBalance());
        DB.update(TABLE_ACC,values, ACC_NO+"=?", new String[]{account.getAccountNo()});
    }

    public Map<String,Account> getAccounts() {
        Map<String,Account> accounts= new HashMap<>();
        SQLiteDatabase DB = this.getReadableDatabase();
        Cursor cursor = DB.rawQuery("SELECT  * FROM " + TABLE_ACC, null);
        if (cursor.moveToFirst()) {
            do {
                Account account = new Account(cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getDouble(3));
                accounts.put(cursor.getString(0),account);
            } while (cursor.moveToNext());
        }

        if (!cursor.isClosed()) {
            cursor.close();
        }
        DB.close();
        return accounts;
    }

    public void removeAccount(String accountNo) {
        SQLiteDatabase DB  = this.getWritableDatabase();
        DB.delete(TABLE_ACC, ACC_NO+"=?", new String[]{accountNo});
        DB.close();
    }

    public List<Transaction> getTransactions() {
        List<Transaction> transactions= new ArrayList<>();
        SQLiteDatabase DB = this.getReadableDatabase();
        Cursor cursor = DB.rawQuery("SELECT  * FROM " + TABLE_TRAN, null);
        if (cursor.moveToFirst()) {
            do {
                String[] dates = cursor.getString(1).split("-");
                Date date= new Date(Integer.parseInt(dates[2])-1900,Integer.parseInt(dates[1]),Integer.parseInt(dates[0]));
                ExpenseType type = null;
                if (cursor.getString(3).equals("EXPENSE")){
                    type = ExpenseType.EXPENSE;
                }else if (cursor.getString(3).equals("INCOME")){
                    type = ExpenseType.INCOME;
                }
                transactions.add(new Transaction(date,cursor.getString(2),type,cursor.getDouble(4)));
            }while (cursor.moveToNext());
        }

        if (!cursor.isClosed()) {
            cursor.close();
        }
        DB.close();
        return transactions;
    }

    public void addTransaction(Transaction transaction) {
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        Date date = transaction.getDate();
        DateFormat dtFormat = new SimpleDateFormat("dd-MM-yyyy");
        values.put(DATE,dtFormat.format(date));
        values.put(ACC_NO,transaction.getAccountNo());
        String type = "UNKNOWN";
        if (transaction.getExpenseType()==ExpenseType.EXPENSE){
            type = "EXPENSE";
        }else if (transaction.getExpenseType()==ExpenseType.INCOME){
            type = "INCOME";
        }
        values.put(EXPENSE_TYPE,type);
        values.put(AMOUNT,transaction.getAmount());
        DB.insert(TABLE_TRAN,null,values);
    }
}
