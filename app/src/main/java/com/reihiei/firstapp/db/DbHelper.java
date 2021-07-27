package com.reihiei.firstapp.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DbHelper extends SQLiteOpenHelper {

    private String create_bill_db =
            "create table bill_table " +
                    "( " +
                    "type integer default 0," +
                    "year integer default 1900," +
                    "month integer default 1," +
                    "day integer default 1," +
                    "remark text," +
                    "in_money text default 0," +
                    "out_money text default 0," +
                    "classify integer default 0," +
                    "addtime text," +
                    "primary key(addtime)" +
                    ")";

    private String create_manage_db =
            "create table manage_table " +
                    "( " +
                    "year integer default 1900," +
                    "month integer default 1," +
                    "day integer default 1," +
                    "name text," +
                    "money text default 0," +
                    "classify integer default 0," +
                    "addtime text," +
                    "channel text," +
                    "eventid integer," +
                    "primary key(addtime)" +
                    ")";

    private String create_manage_new_db =
            "create table manage_new_table " +
                    "( " +
                    "year integer default 1900," +
                    "month integer default 1," +
                    "day integer default 1," +
                    "productid int," +
                    "money text default 0," +
                    "classify integer default 0," +
                    "addtime text," +
                    "channelid int," +
                    "eventid integer," +
                    "shuhui integer default 0," +
                    "primary key(addtime)," +
                    "FOREIGN KEY (productid) REFERENCES product_table (id),"+
                    "FOREIGN KEY (channelid) REFERENCES channel_table (id)"+
                    ")";

    private String create_product_db =
            "create table product_table " +
                    "( " +
                    "id integer PRIMARY KEY AUTOINCREMENT," +
                    "nameP text default 0," +
                    "typeP integer default 0" +
                    ")";

    private String create_channel_db =
            "create table channel_table " +
                    "( " +
                    "id integer PRIMARY KEY AUTOINCREMENT," +
                    "nameC text default 0" +
                    ")";

    private String create_tag_db =
            "create table tag_table " +
                    "( " +
                    "type integer default 0," +
                    "name text," +
                    "id text default '#'," +
                    "primary key(id)" +
                    ")";

    private String create_mention_db =
            "create table mention_table " +
                    "( " +
                    "dtstart long," +
                    "dtend long,"+
                    "title text," +
                    "descripe text," +
                    "eventid integer," +
                    "times text,"+
                    "primary key(eventid)" +
                    ")";

    public DbHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(create_bill_db);
        db.execSQL(create_manage_db);
        db.execSQL(create_tag_db);
        db.execSQL(create_mention_db);
        db.execSQL(create_channel_db);
        db.execSQL(create_product_db);
        db.execSQL(create_manage_new_db);
    }

    private String custListAddRow(int num) {
        String moveCustListDataSqlAdd = "insert into manage_table select *";
        for(int i = 0;i<num;i++){
            moveCustListDataSqlAdd += ",''";
        }
        moveCustListDataSqlAdd += " from manage_table_temp";
        return moveCustListDataSqlAdd;
    }

    private final String createManageListTempSql = "alter table manage_table rename to manage_table_temp";
    private final String dropManageListTempSql = "drop table manage_table_temp";


    public void updateManageListTable(SQLiteDatabase db,String moveSql){
        db.execSQL(createManageListTempSql);
        db.execSQL(create_manage_db);
        db.execSQL(moveSql);
        db.execSQL(dropManageListTempSql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        switch (oldVersion) {
            case 1://1->2->3
                updateManageListTable(db,custListAddRow(1));
                db.execSQL(create_channel_db);
                db.execSQL(create_product_db);
                db.execSQL(create_manage_new_db);
                break;
            case 2://2->3
                db.execSQL(create_channel_db);
                db.execSQL(create_product_db);
                db.execSQL(create_manage_new_db);
                break;

        }
    }
}
