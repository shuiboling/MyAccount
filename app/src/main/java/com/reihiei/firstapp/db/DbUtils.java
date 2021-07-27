package com.reihiei.firstapp.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.reihiei.firstapp.bean.AccountBean;
import com.reihiei.firstapp.bean.AccountBillResp;
import com.reihiei.firstapp.bean.AnalyseManageBean;
import com.reihiei.firstapp.bean.AnalyseManageBeanResp;
import com.reihiei.firstapp.bean.ManageBean;
import com.reihiei.firstapp.bean.ManageBeanResp;
import com.reihiei.firstapp.bean.ManageProductBean;
import com.reihiei.firstapp.bean.TagBean;

import java.util.ArrayList;
import java.util.List;

public class DbUtils {
    private Context context;
    private static DbUtils dbUtils = null;
    private DbHelper dbHelper;
    private String dbName = "account";
    private int version = 3;
    private SQLiteDatabase db;

    private DbUtils(Context context) {
        this.context = context;
        init();
    }

    private void init() {
        if (dbHelper == null) {
            dbHelper = new DbHelper(context, dbName, null, version);
        }
    }

    public static DbUtils getInstance(Context context) {
        if (dbUtils == null) {
            synchronized (context) {
                dbUtils = new DbUtils(context);
            }
        }
        return dbUtils;
    }

    public void closeDb() {
        if (dbHelper != null) {
            dbHelper.close();
        }
    }

    //增
    public void addAccount(AccountBean accountBean) {
        db = dbHelper.getWritableDatabase();

        db.execSQL("insert into bill_table values(?,?,?,?,?,?,?,?,?)",
                new Object[]{
                        accountBean.getType(),
                        accountBean.getYear(),
                        accountBean.getMonth(),
                        accountBean.getDay(),
                        accountBean.getRemark(),
                        accountBean.getInMoney(),
                        accountBean.getOutMoney(),
                        accountBean.getClassify(),
                        accountBean.getAddTime()
                });
    }

    //删
    public void deleteByTime(String time) {
        db = dbHelper.getWritableDatabase();
        db.execSQL("delete from bill_table where addtime = ?", new String[]{time});
    }

    public void deleteByClass(String classify) {
        db = dbHelper.getWritableDatabase();
        db.execSQL("delete from bill_table where classify = ?", new String[]{classify});
    }
    //改
//    public void updateByTime(String time){
//        db = dbHelper.getWritableDatabase();
//        db.execSQL("update ");
//    }

    //查（按指定年月）
    public AccountBillResp queryByMonth(int year, int month, int type) {

        db = dbHelper.getReadableDatabase();

        String query;
        String[] param;

        if (type != -1) {
            query = "select * from bill_table where year = ? and month = ? and type = ? order by day DESC";
            param = new String[]{year + "", month + "", type + ""};
        } else {
            query = "select * from bill_table where year = ? and month = ? order by day DESC";
            param = new String[]{year + "", month + ""};
        }
        Cursor cursor = db.rawQuery(query, param);

        List<AccountBean> list = new ArrayList<>();
        AccountBillResp accountBillResp = new AccountBillResp();
//        BigDecimal sumIn=new BigDecimal(0) ;
//        BigDecimal sumOut=new BigDecimal(0) ;
        float sumIn=0,sumOut=0;

        while (cursor.moveToNext()) {
            AccountBean bean = new AccountBean();
            bean.setType(cursor.getInt(cursor.getColumnIndex("type")));
            bean.setYear(cursor.getInt(cursor.getColumnIndex("year")));
            bean.setMonth(cursor.getInt(cursor.getColumnIndex("month")));
            bean.setDay(cursor.getInt(cursor.getColumnIndex("day")));
            bean.setRemark(cursor.getString(cursor.getColumnIndex("remark")));
            bean.setInMoney(cursor.getString(cursor.getColumnIndex("in_money")));
            bean.setOutMoney(cursor.getString(cursor.getColumnIndex("out_money")));
            bean.setClassify(cursor.getInt(cursor.getColumnIndex("classify")));
            bean.setAddTime(cursor.getString(cursor.getColumnIndex("addtime")));

            sumIn += Float.valueOf(cursor.getString(cursor.getColumnIndex("in_money")));
            sumOut += Float.valueOf(cursor.getString(cursor.getColumnIndex("out_money")));
//            BigDecimal in = new BigDecimal(cursor.getString(cursor.getColumnIndex("in_money")));
//            sumIn = sumIn.add(in).setScale(2,RoundingMode.HALF_UP);
//            BigDecimal out = new BigDecimal(cursor.getString(cursor.getColumnIndex("out_money")));
//            sumOut = sumOut.add(out).setScale(3);

            list.add(bean);
        }
        cursor.close();

        accountBillResp.setList(list);
        accountBillResp.setSumIn(String.format("%.2f",sumIn));
        accountBillResp.setSumOut(String.format("%.2f",sumOut));

        return accountBillResp;

    }

    //查支出（按指定年月）
    public float queryOutcomeByMonth(int year, int month) {
        db = dbHelper.getReadableDatabase();

        String query = "select sum(out_money) from bill_table where year = ? and month = ? and type = 0";
        Cursor cursorCount = db.rawQuery(query, new String[]{year + "", month + ""});
        cursorCount.moveToFirst();
        float totalCount = cursorCount.getFloat(0);
        cursorCount.close();

        return totalCount;

    }

    //按年查收入和支出
    public List<AccountBean> queryInAndOutByYear() {
        List<AccountBean> list = new ArrayList<>();

        db = dbHelper.getReadableDatabase();

        String query = "select sum(in_money),sum(out_money),year from bill_table group by year order by year desc";
        Cursor cursor = db.rawQuery(query, null);

        float sumIn = 0, sumOut = 0;

        while (cursor.moveToNext()) {
            AccountBean accountBean = new AccountBean();
            accountBean.setInMoney(cursor.getString(cursor.getColumnIndex("sum(in_money)")));
            accountBean.setOutMoney(cursor.getString(cursor.getColumnIndex("sum(out_money)")));
            accountBean.setYear(cursor.getInt(cursor.getColumnIndex("year")));
            accountBean.setType(0);
            list.add(accountBean);

            sumIn += Float.valueOf(cursor.getString(cursor.getColumnIndex("sum(in_money)")));
            sumOut += Float.valueOf(cursor.getString(cursor.getColumnIndex("sum(out_money)")));
        }

        AccountBean accountBean = new AccountBean();
        accountBean.setInMoney(String.format("%.2f",sumIn));
        accountBean.setOutMoney(String.format("%.2f",sumOut));
        accountBean.setYear(-1);
        accountBean.setType(0);
        list.add(0, accountBean);

        cursor.close();

        return list;

    }

    //按月查收入和支出
    public List<AccountBean> queryInAndOutByMonth(int year) {
        List<AccountBean> list = new ArrayList<>();

        db = dbHelper.getReadableDatabase();

        String query = "select sum(in_money),sum(out_money),month from bill_table where year = ? group by month order by month desc";
        Cursor cursor = db.rawQuery(query, new String[]{year + ""});

        while (cursor.moveToNext()) {

            AccountBean accountBean = new AccountBean();
            accountBean.setInMoney(cursor.getString(cursor.getColumnIndex("sum(in_money)")));
            accountBean.setOutMoney(cursor.getString(cursor.getColumnIndex("sum(out_money)")));
            accountBean.setMonth(cursor.getInt(cursor.getColumnIndex("month")));
            accountBean.setType(1);
            accountBean.setYear(year);
            list.add(accountBean);
        }

        cursor.close();

        return list;

    }

    //按月查各分类支出
    public List<AccountBean> queryClassifyOutByMonth(int year, int month, int type) {
        List<AccountBean> list = new ArrayList<>();

        db = dbHelper.getReadableDatabase();
        String query = "";
        if (type == 0) {
            query = "select sum(out_money),classify from bill_table where year = ? and month = ? and type = 0 group by classify order by sum(out_money) DESC";

        } else {
            query = "select sum(in_money),classify from bill_table where year = ? and month = ? and type = 1 group by classify order by sum(in_money) DESC";

        }
        Cursor cursor = db.rawQuery(query, new String[]{year + "", month + ""});

        while (cursor.moveToNext()) {

            AccountBean accountBean = new AccountBean();
            if (type == 0) {
                accountBean.setOutMoney(cursor.getString(cursor.getColumnIndex("sum(out_money)")));
            } else {
                accountBean.setInMoney(cursor.getString(cursor.getColumnIndex("sum(in_money)")));
            }
            accountBean.setClassify(cursor.getInt(cursor.getColumnIndex("classify")));
            list.add(accountBean);
        }

        cursor.close();

        return list;

    }

    //查有记录的年份
    public int[] queryYear() {
        db = dbHelper.getReadableDatabase();

        String query = "select distinct year from bill_table order by year";
        Cursor cursor = db.rawQuery(query, null);
        int totalCount = cursor.getCount();
        if (totalCount <= 0) {
            return new int[]{};
        }
        int[] result = new int[totalCount];
        int i = 0;
        while (cursor.moveToNext()) {
            result[i] = cursor.getInt(cursor.getColumnIndex("year"));
            i++;
        }

        cursor.close();

        return result;

    }

    /*------------------理财表------------------*/

    //增
    // TODO: 7/23/21  
    public void addManage(ManageBean manageBean) {
        db = dbHelper.getWritableDatabase();

        db.execSQL("insert into manage_new_table values(?,?,?,?,?,?,?,?,?,?)",
                new Object[]{
                        manageBean.getYear(),
                        manageBean.getMonth(),
                        manageBean.getDay(),
                        manageBean.getProductid(),
                        manageBean.getMoney(),
                        manageBean.getClassify(),
                        manageBean.getAddTime(),
                        manageBean.getChannelid(),
                        -1,
                        manageBean.getShuhui()
                });
    }

    //删
    public void deleteByTimeManage(String time) {
        db = dbHelper.getWritableDatabase();
        db.execSQL("delete from manage_new_table where addtime = ?", new String[]{time});
    }

    // TODO: 7/23/21  inner join
    //查（按指定年月）
    public ManageBeanResp queryByMonthMange(int year, int month) {

        db = dbHelper.getReadableDatabase();

        String query;
        String[] param;

        query = "select x.*,y.nameP,y.typeP ,z.nameC from " +
                "manage_new_table x inner join product_table y on x.productid=y.id " +
                "inner join channel_table z on x.channelid = z.id " +
                "where x.year = ? and x.month = ? order by x.classify,x.day";

        param = new String[]{year + "", month + ""};

        Cursor cursor = db.rawQuery(query, param);

        List<ManageBean> list = new ArrayList<>();
        ManageBeanResp manageBeanResp = new ManageBeanResp();
        float sum = 0;

        while (cursor.moveToNext()) {
            ManageBean bean = new ManageBean();
            bean.setYear(cursor.getInt(cursor.getColumnIndex("year")));
            bean.setMonth(cursor.getInt(cursor.getColumnIndex("month")));
            bean.setDay(cursor.getInt(cursor.getColumnIndex("day")));
            bean.setNameP(cursor.getString(cursor.getColumnIndex("nameP")));
            bean.setTypeP(cursor.getInt(cursor.getColumnIndex("typeP")));
            bean.setMoney(cursor.getString(cursor.getColumnIndex("money")));
            bean.setNameC(cursor.getString(cursor.getColumnIndex("nameC")));
            bean.setClassify(cursor.getInt(cursor.getColumnIndex("classify")));
            bean.setAddTime(cursor.getString(cursor.getColumnIndex("addtime")));
            bean.setEventId(cursor.getLong(cursor.getColumnIndex("eventid")));
            bean.setShuhui(cursor.getInt(cursor.getColumnIndex("shuhui")));

            sum += Float.valueOf(cursor.getString(cursor.getColumnIndex("money")));

            list.add(bean);
        }
        cursor.close();

        manageBeanResp.setList(list);
        manageBeanResp.setSum(String.format("%.2f",sum));

        return manageBeanResp;

    }


    //查（按指定年月类型）
    public AnalyseManageBeanResp queryByMonthMangeClassify(int year, int month) {

        db = dbHelper.getReadableDatabase();

        String query;
        String[] param;

        query = "select count(1), sum(money), classify from manage_new_table where year = ? and month = ? group by classify";
        param = new String[]{year + "", month + ""};

        Cursor cursor = db.rawQuery(query, param);

        List<AnalyseManageBean> list = new ArrayList<>();
        float sum = 0;

        while (cursor.moveToNext()) {

            AnalyseManageBean bean = new AnalyseManageBean();
            bean.setCount(cursor.getInt(cursor.getColumnIndex("count(1)")));
            bean.setClassify(cursor.getInt(cursor.getColumnIndex("classify")));
            String sumMoney = cursor.getString(cursor.getColumnIndex("sum(money)"));
            bean.setSumMoney(sumMoney);
            list.add(bean);

            sum += Float.valueOf(sumMoney);

        }

        AnalyseManageBeanResp resp = new AnalyseManageBeanResp(list,String.format("%.2f",sum));

        cursor.close();

        return resp;

    }

    public void updateEventId(long evenId,String addtime){
        db = dbHelper.getWritableDatabase();
        db.execSQL("update manage_new_table set eventid = ? where addtime = ?", new Object[]{evenId,addtime});
    }

    public void updateShuHui(int shuhui,String addtime){
        db = dbHelper.getWritableDatabase();
        db.execSQL("update manage_new_table set shuhui = ? where addtime = ?", new Object[]{shuhui,addtime});
    }

    /*------------------标签表------------------*/

    //增
    public void addTag(TagBean tagBean) {
        db = dbHelper.getWritableDatabase();
        int id = queryTagNum() + 1;
        db.execSQL("insert into tag_table values(?,?,?)",
                new Object[]{
                        tagBean.getType(),
                        tagBean.getName(),
                        "10" + id,
                });
    }

    //删
    public void deleteTagById(String id) {
        db = dbHelper.getWritableDatabase();
        db.execSQL("delete from tag_table where id = ?", new String[]{id});
    }

    public void delAll() {
        db = dbHelper.getWritableDatabase();
        db.execSQL("delete from tag_table", new String[]{});
    }

    //查数量
    public int queryTagNum() {

        int num = 0;
        db = dbHelper.getReadableDatabase();
        String query = "select count(1) from tag_table";
        Cursor cursor = db.rawQuery(query, new String[]{});
        cursor.moveToFirst();
        num = cursor.getInt(0);
        cursor.close();
        return num;
    }

    //按名字查
    public boolean queryTagByName(String name, int type) {

        db = dbHelper.getReadableDatabase();
        String query = "select * from tag_table where name = ? and type = ?";
        Cursor cursor = db.rawQuery(query, new String[]{name, type + ""});
        cursor.moveToFirst();
        int num = cursor.getCount();
        cursor.close();

        if (num == 0) {
            return false;
        }
        return true;
    }

    //查（按type）
    public List<TagBean> queryByType(int type) {

        db = dbHelper.getReadableDatabase();

        String query = "select * from tag_table where type = ? ";
        String[] param = new String[]{type + ""};

        Cursor cursor = db.rawQuery(query, param);

        List<TagBean> list = new ArrayList<>();
        TagBean tagBean = new TagBean();

        while (cursor.moveToNext()) {
            TagBean bean = new TagBean();
            bean.setType(cursor.getInt(cursor.getColumnIndex("type")));
            bean.setName(cursor.getString(cursor.getColumnIndex("name")));
            bean.setId(cursor.getString(cursor.getColumnIndex("id")));

            list.add(bean);
        }
        cursor.close();

        return list;

    }

    //查（按id）
    public TagBean queryTagById(String id) {

        db = dbHelper.getReadableDatabase();

        String query = "select * from tag_table where id = ? ";
        String[] param = new String[]{id};

        Cursor cursor = db.rawQuery(query, param);

        TagBean tagBean = new TagBean();

        while (cursor.moveToNext()) {
            tagBean.setType(cursor.getInt(cursor.getColumnIndex("type")));
            tagBean.setName(cursor.getString(cursor.getColumnIndex("name")));
            tagBean.setId(cursor.getString(cursor.getColumnIndex("id")));

        }
        cursor.close();

        return tagBean;

    }

    //改
    public void updateTagById(String id, String name) {
        db = dbHelper.getWritableDatabase();
        db.execSQL("update tag_table set name = ? where id = ?", new Object[]{name, id});
    }

    //----------------提醒事项-------------------
    public void addMention(TagBean tagBean) {
        db = dbHelper.getWritableDatabase();
        int id = queryTagNum() + 1;
        db.execSQL("insert into tag_table values(?,?,?)",
                new Object[]{
                        tagBean.getType(),
                        tagBean.getName(),
                        "10" + id,
                });
    }

    //删
    public void deleteMentionById(String id) {
        db = dbHelper.getWritableDatabase();
        db.execSQL("delete from tag_table where id = ?", new String[]{id});
    }

    //--------------------理财产品---------------
    public void addMP(ManageProductBean bean) {
        db = dbHelper.getWritableDatabase();
        db.execSQL("insert into product_table values(?,?,?)",
                new Object[]{
                        null,
                        bean.getName(),
                        bean.getType()
                });
    }

    public List<ManageProductBean> queryMP(int type){
        db = dbHelper.getReadableDatabase();

        String query;
        if (type == 0){
            query = "select * from product_table where typeP = 0";
        } else {
            query = "select * from product_table where typeP != 0";

        }
        Cursor cursor = db.rawQuery(query,new String[]{});

        List<ManageProductBean> list = new ArrayList<>();

        while (cursor.moveToNext()) {
            ManageProductBean bean = new ManageProductBean();
            bean.setId(cursor.getInt(cursor.getColumnIndex("id")));
            bean.setType(cursor.getInt(cursor.getColumnIndex("typeP")));
            bean.setName(cursor.getString(cursor.getColumnIndex("nameP")));

            list.add(bean);
        }
        cursor.close();

        return list;
    }

    public boolean queryMPByName(String name, int type) {
        db = dbHelper.getReadableDatabase();
        String query = "select * from product_table where nameP = ? and typeP = ?";
        Cursor cursor = db.rawQuery(query, new String[]{name, type + ""});
        cursor.moveToFirst();
        int num = cursor.getCount();
        cursor.close();

        if (num == 0) {
            return false;
        }
        return true;
    }

    public int queryMPIdByName(String name){
        db = dbHelper.getReadableDatabase();
        String query = "select id from product_table where nameP like ? ";
        Cursor cursor = db.rawQuery(query, new String[]{"%"+name+"%"});
        cursor.moveToFirst();
        int id = cursor.getInt(cursor.getColumnIndex("id"));
        return id;
    }

    public void updateMPById(int id, String name, int type) {
        db = dbHelper.getWritableDatabase();
        db.execSQL("update product_table set nameP = ? , type = ? where id = ?", new Object[]{name,type,id});
    }

    //删
    public void deleteProductById(int id) {
        db = dbHelper.getWritableDatabase();
        db.execSQL("delete from product_table where id = ?", new Object[]{id});
    }

    //----------------理财渠道-----------------
    public int queryMCIdByName(String name){
        db = dbHelper.getReadableDatabase();
        String query = "select id from channel_table where nameC like ? ";
        Cursor cursor = db.rawQuery(query, new String[]{"%"+name+"%"});
        cursor.moveToFirst();
        int id = cursor.getInt(cursor.getColumnIndex("id"));
        return id;
    }

    public List<ManageProductBean> queryMC() {
        db = dbHelper.getReadableDatabase();

        String query = "select * from channel_table";
        Cursor cursor = db.rawQuery(query,new String[]{});

        List<ManageProductBean> list = new ArrayList<>();

        while (cursor.moveToNext()) {
            ManageProductBean bean = new ManageProductBean();
            bean.setId(cursor.getInt(cursor.getColumnIndex("id")));
            bean.setName(cursor.getString(cursor.getColumnIndex("nameC")));
            bean.setType(-1);
            list.add(bean);
        }
        cursor.close();

        return list;
    }

    public boolean queryMCByName(String name) {
        db = dbHelper.getReadableDatabase();
        String query = "select * from channel_table where nameC = ?";
        Cursor cursor = db.rawQuery(query, new String[]{name});
        cursor.moveToFirst();
        int num = cursor.getCount();
        cursor.close();

        if (num == 0) {
            return false;
        }
        return true;
    }

    public void updateMCById(int id, String name) {
        db = dbHelper.getWritableDatabase();
        db.execSQL("update channel_table set nameC = ? where id = ?", new Object[]{name,id});
    }

    public void addMC(ManageProductBean bean) {
        db = dbHelper.getWritableDatabase();
        db.execSQL("insert into channel_table values(?,?)",
                new Object[]{
                        null,
                        bean.getName()
                });
    }

    public void deleteChannelById(int id) {
        db = dbHelper.getWritableDatabase();
        db.execSQL("delete from channel_table where id = ?", new Object[]{id});
    }

    //将数据从manage_table导到manage_new_table
    public void removeData() {
        db = dbHelper.getWritableDatabase();
        String query = "select * from manage_table ";

        Cursor cursor = db.rawQuery(query, new String[]{});

        List<ManageBean> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            ManageBean bean = new ManageBean();
            bean.setYear(cursor.getInt(cursor.getColumnIndex("year")));
            bean.setMonth(cursor.getInt(cursor.getColumnIndex("month")));
            bean.setDay(cursor.getInt(cursor.getColumnIndex("day")));
            bean.setNameP(cursor.getString(cursor.getColumnIndex("name")));
            bean.setMoney(cursor.getString(cursor.getColumnIndex("money")));
            bean.setNameC(cursor.getString(cursor.getColumnIndex("channel")));
            bean.setClassify(cursor.getInt(cursor.getColumnIndex("classify")));
            bean.setAddTime(cursor.getString(cursor.getColumnIndex("addtime")));
            bean.setEventId(cursor.getLong(cursor.getColumnIndex("eventid")));
            bean.setShuhui(0);
            list.add(bean);
        }
        cursor.close();

        for (ManageBean item:list){
            int idP = queryMPIdByName(item.getNameP());
            item.setProductid(idP);
            int idC = queryMCIdByName(item.getNameC().substring(0,1));
            item.setChannelid(idC);
            addManage(item);
        }
    }
}
