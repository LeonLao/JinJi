package com.example.jinji;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by jack on 2016/1/5.
 */
public class dbHelper extends SQLiteOpenHelper{
    //数据库名
    private final static String DATABASE_NAME = "jinji_db";
    //版本号
    private final static int DATABASE_VERSION  = 1;
    //表名字
    private final static  String TABLE_NAME = "city_save";
    private final static  String FIELD_ID = "_id";
    final static String FIELD_TITLE = "_title";

    // 构造函数，调用父类SQLiteOpenHelper的构造函数
	/* 对于抽象类SQLiteOpenHelper的继承，需要重写：1）constructor，2）onCreate()和onUpgrade()  * */

    /** step 1 :重写构造函数中，继承super的构造函数，创建database
     * 第一个参数 为当前环境
     * 第二个参数 String name为数据库文件，如果数据存放在内存 ，则为null，
     * 第三个参数 为SQLiteDatabase.CursorFactory  factory，存放cursor，缺省设置为null
     * 第四个参数 为int version数据库的版本，从1开始，如果版本旧，则通过onUpgrade()进行更新，如果版本新则通过onDowngrade()进行发布。
     * 例如，我要更改mytable表格，增加一列，或者修改初始化的数据，或者程序变得复杂，我需要增加一个表，这时我需要在版本的数字增加，
     * 在加载时，才会对 SQLite中的数据库个更新，这点非常重要，同时参见onUpgrade()的说明 **/

    public dbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建表
        String sql = "Create table "+TABLE_NAME+"("+FIELD_ID+" integer primary key autoincrement,"
                +FIELD_TITLE+" text );";
        //执行sql语句
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 调用时间：如果DATABASE_VERSION值被改为别的数,系统发现现有数据库版本不同,即会调用onUpgrade

        // onUpgrade方法的三个参数，一个 SQLiteDatabase对象，一个旧的版本号和一个新的版本号
        // 这样就可以把一个数据库从旧的模型转变到新的模型
        // 这个方法中主要完成更改数据库版本的操作

        String sql = "DROP TABLE IF EXISTS"+TABLE_NAME;
        db.execSQL(sql);
        onCreate(db);
    }
        public Cursor select(){
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.query(TABLE_NAME,null,null,null,null,null,"_id desc");
            return cursor;
        }
        public long insert(String Title){
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(FIELD_TITLE,Title);
            long row = db.insert(TABLE_NAME,null,cv);
            return row;

            //RAW方式。
            //db.execSQL("INSERT INTO mytable(Name,Weight) VALUES ('Test1',1.0);");
        }
        public void delete(int id){
            SQLiteDatabase db = this.getWritableDatabase();
            String where = FIELD_ID+"=?";
            String[] whereValue = {Integer.toString(id)};
            db.delete(TABLE_NAME,where,whereValue);
            //RAW方式
            //db.execSQL("DELETE FROM mytable WHERE Name='Test1';");
    }



}
