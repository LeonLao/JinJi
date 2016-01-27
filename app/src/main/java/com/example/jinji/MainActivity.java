package com.example.jinji;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import javax.xml.transform.sax.TemplatesHandler;

import service.CityService;


public class MainActivity extends Activity {

   // private Button testbutton;
    private LinearLayout mainlayout;
    private long exitTime = 0;

    GetDataTask getDataTask;

    private dbHelper db;
    private Cursor cursor;
    private int _id;
    private int dataCount = 0;

    private final static String  DATABASE_NAME ="jinji_db";
    private final static int DATABASE_VERSION = 1;

    private static final String TAG = "MainActivity";
    private ImageView imageView;
    private TextView city,updatetime,temperature,weather,tips,riqi,weektitle,textView3,textView4;
    private ListView weilai,citylist;
    private Button add;
    private DrawerLayout mDrawerLayout = null;
    //private CityAdapter cityAdapter = new CityAdapter();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainlayout = (LinearLayout)findViewById(R.id.mainlayout);
        imageView = (ImageView)findViewById(R.id.imageView);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        city = (TextView)findViewById(R.id.city);
        updatetime = (TextView)findViewById(R.id.release);
        temperature = (TextView)findViewById(R.id.temperature);
        weather = (TextView)findViewById(R.id.tianqi);
        tips = (TextView)findViewById(R.id.tips);
        riqi = (TextView)findViewById(R.id.riqi);
        weektitle =(TextView)findViewById(R.id.weektitle);
        textView3 = (TextView)findViewById(R.id.text3);
        textView4 = (TextView)findViewById(R.id.text4);
        weilai = (ListView)findViewById(R.id.weilai);
        citylist = (ListView)findViewById(R.id.city_list);
        add = (Button)findViewById(R.id.add);

        String getcname = CityService.getcity(this);
        if (getcname!=null){
            city.setText(getcname);
            getDataTask = new GetDataTask(MainActivity.this,city,updatetime,
                    temperature,weather,weilai,tips,riqi,textView3,textView4,mainlayout);
            getDataTask.execute(getcname);
        }else {
            city.setText("广州");
            getDataTask = new GetDataTask(MainActivity.this,city,updatetime,
                    temperature,weather,weilai,tips,riqi,textView3,textView4,mainlayout);
            getDataTask.execute("广州");
        }



        weektitle.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        riqi.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);


//        testbutton = (Button)findViewById(R.id.testbutton);
//        testbutton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int hangshu = cursor.getCount();
//                cursor.moveToPosition(3);//行数从0开始，数据每列的位置从0开始,listvie的从0开始
//                String mingzi = cursor.getString(1);
//                tips.setText(hangshu+""+mingzi);
//                getDataTask = new GetDataTask(MainActivity.this,tips);
//                getDataTask.execute("广州");
//            }
//        });


        db = new dbHelper(this,DATABASE_NAME,null,DATABASE_VERSION);
        cursor = db.select();

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(MainActivity.this,R.layout.addcity_item,cursor,
                new String[]{dbHelper.FIELD_TITLE},new int[]{R.id.addcity_item});
        citylist.setAdapter(adapter);

        citylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//              int dianji = position;
//                tips.setText(dianji+"");
                //获取点击item的id，listitem从0开始,先移cursor到点击行数，再获取某列内容
                cursor.moveToPosition(position);
                String citymingzi = cursor.getString(1);
                getDataTask = new GetDataTask(MainActivity.this,city,updatetime,
                        temperature,weather,weilai,tips,riqi,textView3,textView4,mainlayout);
                getDataTask.execute(citymingzi);
                mDrawerLayout.closeDrawers();

            }
        });
        //列表长按事件
        citylist.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                cursor.moveToPosition(position);
                int selectID = cursor.getInt(0);
                String deName = cursor.getString(1);
                DeleteCity(selectID,deName);
//                db.delete(selectID);
//                cursor.requery();
//                citylist.invalidateViews();
                return false;
            }
        });

        //String s = cursor.getString(0);
//        int i = cursor.getCount();
        //tips.setText(s);
        //获得数据总行数
       // getDataCount();

//        tips.setText(dataCount+"9");

//        for (int i = 1; i <= dataCount; i++) {
//            String s = cursor.getString(i);
//            test = s+ "\n";
//        }



//        for (cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext()){
//            String s = cursor.getString(1);
//            test = test+s;
//        }
//        tips.setText(test);



        city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(Gravity.LEFT);
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(Gravity.LEFT);
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCity();
            }
        });
    }

    private void DeleteCity(final int selectID,String DATABASE_NAME) {
        final AlertDialog.Builder deleteCity = new AlertDialog.Builder(MainActivity.this);
        deleteCity.setTitle("删除该城市");
        deleteCity.setMessage("确认后将从城市列表中移除城市:"+DATABASE_NAME);
        deleteCity.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i(TAG, "删除城市名");
                db.delete(selectID);
                cursor.requery();
                citylist.invalidateViews();
            }
        });

        deleteCity.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        deleteCity.create().show();


    }

//    private void getDataCount() {
//        dataCount = cursor.getCount();
//        // return dataCount;
//    }

    private void addCity() {
        final AlertDialog.Builder addcity = new AlertDialog.Builder(MainActivity.this);
        final View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.edittext_item,null);

        addcity.setTitle("输入城市名");
        addcity.setView(view);

        addcity.setPositiveButton("添加", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText editText = (EditText) view.findViewById(R.id.edit_city);
                String cityname = editText.getText().toString().trim();
                if (TextUtils.isEmpty(cityname)) {
                    Toast.makeText(MainActivity.this, "你没有添加城市", Toast.LENGTH_SHORT).show();
                    return;
                } else {

                    //保存城市名到列表
                    boolean  result = CheckCityName(cityname);
                    if (result){
                        Log.i(TAG, "需要保存城市名");
                        db.insert(cityname);
                        cursor.requery();
                        citylist.invalidateViews();
                    }else {
                        Toast.makeText(MainActivity.this,"已添加过该城市",Toast.LENGTH_LONG).show();
                    }

//
                    //_id = 0;
//                    boolean result = CityService.saveCityName(MainActivity.this,cityname);
//                    if(result){
//
//                        Toast.makeText(MainActivity.this,"已添加",Toast.LENGTH_SHORT).show();
//                    }

                    //根据城市名进行获取数据
                }
            }
        });


        addcity.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        addcity.create().show();

    }

    private boolean CheckCityName(String city) {
        for (cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext()) {
            String s = cursor.getString(1);
            if (s.equals(city)){
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次退出程序",
                        Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                //保存城市名
                String cname = city.getText().toString();
                boolean re = CityService.saveCityName(MainActivity.this,cname);
                if (re){
                    //Toast.makeText(MainActivity.this,"保存成功",Toast.LENGTH_LONG).show();
                    Log.i(TAG,"保存成功啦啦啦啦啦啦啦啦啦");
                }

                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
