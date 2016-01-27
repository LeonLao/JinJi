package com.example.jinji;

import android.content.Context;
import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by jack on 2016/1/7.
 */
public class GetDataTask extends AsyncTask<String,Void,String>{
    Context context;
    private LinearLayout mainlayout;
    TextView city,updatetime,temperature,weather,tips,riqis,textView3,textView4;
    private ListView listView;
    private MyAdapter adapter = new MyAdapter();
    private String[] weilaidates= new String[7];//未来几天
    private String[] gaowens = new String[7];//最高温度
    private String[] diwens = new String[7];//最低温度
    private int[] weatherIDs = new int[7];//天气ID
    private int[] weatherPICs = new int[7];//对应天气图

    //接口地址
    private static final String URL= "http://op.juhe.cn/onebox/weather/query";
    //APIKEY
    private static final String KEY="c9f6af6efa1077151f52aacd2b596441";


    public GetDataTask(Context context,TextView city,TextView updatetime,
                       TextView temperature,TextView weather,ListView listView,
                       TextView tips,TextView riqis,TextView textView3,TextView textView4,LinearLayout mainlayout){
        super();
        this.context = context;
        this.city = city;
        this.updatetime = updatetime;
        this.temperature = temperature;
        this.weather = weather;
        this.listView = listView;
        this.tips = tips;
        this.riqis = riqis;
        this.textView3 = textView3;
        this.textView4 = textView4;
        this.mainlayout = mainlayout;
    }

//    public GetDataTask(Context context,TextView city){
//        super();
//        this.context = context;
//        this.city = city;
//        this.updatetime = updatetime;
//        this.temperature = temperature;
//        this.weather = weather;
//    }

    @Override
    protected String doInBackground(String... params) {
        String cityname = params[0];

        ArrayList<NameValuePair> headerList = new ArrayList<NameValuePair>();
        headerList.add(new BasicNameValuePair("Content-Type", "text/html; charset=utf-8"));

        String targetUrl = URL;

        ArrayList<NameValuePair> paramList = new ArrayList<NameValuePair>();
        paramList.add(new BasicNameValuePair("key",KEY));
        paramList.add(new BasicNameValuePair("dtype","json"));
        paramList.add(new BasicNameValuePair("cityname",cityname));

        for (int i = 0 ;i<paramList.size();i++){

            NameValuePair nowPair = paramList.get(i);
            String value = nowPair.getValue();
            try {
                value = URLEncoder.encode(value,"UTF-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (i ==0){
                targetUrl +=("?"+nowPair.getName()+"="+value);
            }else {
                targetUrl +=("&"+nowPair.getName()+"="+value);
            }
        }

        HttpGet httpRequest =  new HttpGet(targetUrl);
        try {
            for (int i=0;i<headerList.size();i++){
                httpRequest.addHeader(headerList.get(i).getName(),
                        headerList.get(i).getValue());
            }

            HttpClient httpClient = new DefaultHttpClient();
            HttpResponse httpResponse = httpClient.execute(httpRequest);

            if (httpResponse.getStatusLine().getStatusCode() ==200){
                String strResult = EntityUtils.toString(httpResponse.getEntity());
                return  strResult;
            }else {
                return "请求出错";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {

        if (result!=null){
            try {
                JSONObject jsonObject = new JSONObject(result);
                int resultCode = jsonObject.getInt("error_code");
                String resulttype = jsonObject.getString("reason");

                if (resultCode ==0){
                    JSONObject resultJsonObject = jsonObject.getJSONObject("result").getJSONObject("data").getJSONObject("realtime");
                    JSONArray weatherArray = jsonObject.getJSONObject("result").getJSONObject("data").getJSONArray("weather");
                    JSONObject tipsObject = jsonObject.getJSONObject("result").getJSONObject("data").getJSONObject("life");

                    String city_name = resultJsonObject.getString("city_name");
                           // +resultJsonObject.getString("moon")+ "\n";
                    String update_time = resultJsonObject.getString("time");
                    String now_temperature = resultJsonObject.getJSONObject("weather").getString("temperature");
                    String now_weather = resultJsonObject.getJSONObject("weather").getString("info");
                    String now_htmidity = resultJsonObject.getJSONObject("weather").getString("humidity");
                    int now_weaID = resultJsonObject.getJSONObject("weather").getInt("img");
                    String now_windspeed = resultJsonObject.getJSONObject("wind").getString("windspeed");
                    String now_direct = resultJsonObject.getJSONObject("wind").getString("direct");
                    String now_power = resultJsonObject.getJSONObject("wind").getString("power");


                    city.setText(city_name);
                    updatetime.setText("发布时间："+update_time);
                    temperature.setText(now_temperature+"°c/");
                    weather.setText(now_weather);
                    textView3.setText("湿度:"+now_htmidity+"　　"+"风向："+now_direct);
                    textView4.setText("风速:"+now_windspeed+"　　"+"风力："+now_power);

                    //未来一周
                    for (int i=0;i<7;i++){
                        weilaidates[i] = weatherArray.getJSONObject(i).getString("week");
                    }

                    //白天温度、天气ID
                    for(int i=0;i<7;i++){
                        JSONObject dayinfo = weatherArray.getJSONObject(i).getJSONObject("info");
                        gaowens[i] = dayinfo.getJSONArray("day").getString(2);
                        weatherIDs[i] = dayinfo.getJSONArray("day").getInt(0);
                    }

                    //获取对应天气状态图
                    for (int i=0;i<7;i++){
                        weatherPICs[i] = getPIC(weatherIDs[i]);
                    }

                    //夜间温度
                    for (int i=0;i<7;i++){
                        JSONObject nightinfo = weatherArray.getJSONObject(i).getJSONObject("info");
                        diwens[i] = nightinfo.getJSONArray("night").getString(2);
                    }


                    listView.setAdapter(adapter);

                    //建议的内容
                    String riqi = tipsObject.getString("date");//日期
                    riqis.setText("日期："+riqi+ "\n");

                    //主体内容
                    JSONObject suggest = tipsObject.getJSONObject("info");

                    String suggestion = context.getString(R.string.kongtiao)+":"+"\n"+" \u3000\u3000 "+suggest.getJSONArray("kongtiao").getString(0)
                            +","+suggest.getJSONArray("kongtiao").getString(1)+ "\n"+ "\n"
                            +context.getString(R.string.yundong)+":"+"\n"+" \u3000\u3000 "+suggest.getJSONArray("yundong").getString(0)
                            +","+suggest.getJSONArray("yundong").getString(1)+ "\n"+ "\n"
                            +context.getString(R.string.ziwaixian)+":"+"\n"+" \u3000\u3000 "+suggest.getJSONArray("ziwaixian").getString(0)
                            + ","+suggest.getJSONArray("ziwaixian").getString(1)+"\n"+ "\n"
                            +context.getString(R.string.ganmao)+":"+"\n"+" \u3000\u3000 "+suggest.getJSONArray("ganmao").getString(0)
                            + ","+suggest.getJSONArray("ganmao").getString(1)+"\n"+ "\n"
                            +context.getString(R.string.xiche)+":"+"\n"+" \u3000\u3000 "+suggest.getJSONArray("xiche").getString(0)
                            +","+suggest.getJSONArray("xiche").getString(1)+ "\n"+ "\n"
                            +context.getString(R.string.wuran)+":"+"\n"+" \u3000\u3000 "+suggest.getJSONArray("wuran").getString(0)
                            + ","+suggest.getJSONArray("wuran").getString(1)+"\n"+ "\n"
                            +context.getString(R.string.chuanyi)+":"+"\n"+" \u3000\u3000 "+suggest.getJSONArray("chuanyi").getString(0)
                            + ","+suggest.getJSONArray("chuanyi").getString(1);

                    tips.setText(suggestion);

                    setBG(now_weaID);


                }else {
                    Toast.makeText(context,"查询失败,网络不可用或没有该城市信息"+resulttype,Toast.LENGTH_LONG).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else {
            Toast.makeText(context,"查询失败，未找到该城市信息",Toast.LENGTH_SHORT).show();
        }

    }

    private void setBG(int id) {
        if (id ==0){
            mainlayout.setBackgroundResource(R.mipmap.sun);
        }
        else if (id ==1){
            mainlayout.setBackgroundResource(R.mipmap.suncloud);
        }
        else if (id ==2){
            mainlayout.setBackgroundResource(R.mipmap.cloud);
        }
        else if (id ==3||id>=6&&id<=12||id==19||id>=21&&id<=25){
            mainlayout.setBackgroundResource(R.mipmap.rain);
        }
        else if (id ==4||id ==5){
            mainlayout.setBackgroundResource(R.mipmap.thunderstorm);
        }
        else if (id>=13&&id<=17||id>=26&&id<=28){
            mainlayout.setBackgroundResource(R.mipmap.snow);
        }
        else if (id==18){
            mainlayout.setBackgroundResource(R.mipmap.fog);
        }
        else {
            mainlayout.setBackgroundResource(R.mipmap.haze);
        }
    }


    private class MyAdapter extends BaseAdapter{
        public MyAdapter(){
            super();
        }

        @Override
        public int getCount() {
            return 7;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null){
                convertView = View.inflate(context,R.layout.list_item,null);
                viewHolder = new ViewHolder();
                viewHolder.riqi = (TextView)convertView.findViewById(R.id.week);
                viewHolder.diwen = (TextView)convertView.findViewById(R.id.diwen);
                viewHolder.gaowen = (TextView)convertView.findViewById(R.id.gaowen);
                viewHolder.imageView = (ImageView)convertView.findViewById(R.id.weather);
                convertView.setTag(viewHolder);
            }else {
                viewHolder = (ViewHolder)convertView.getTag();
            }
            viewHolder.riqi.setText("星期"+weilaidates[position]);
            viewHolder.gaowen.setText(gaowens[position]);
            viewHolder.diwen.setText(diwens[position]);
            viewHolder.imageView.setImageResource(weatherPICs[position]);

            return convertView;
        }
    }

    private class ViewHolder{
        TextView riqi,gaowen,diwen;
        ImageView imageView;
    }


    //匹配天气图
    private int getPIC(int id){
        int weapic = 0;
        for (int i= 0;i<MY_Weather.length;i++){
            if (id==MY_Weather[i][0]){
                weapic =MY_Weather[i][1];
            }
        }
        return weapic;
    }

    //匹配天气状态表
    private final int[][] MY_Weather={
            //{天气ID，图ID}
            {0,R.mipmap.w00},
            {1,R.mipmap.w01},
            {2,R.mipmap.w02},
            {3,R.mipmap.w03},
            {4,R.mipmap.w04},
            {5,R.mipmap.w05},
            {6,R.mipmap.w06},
            {7,R.mipmap.w07},
            {8,R.mipmap.w08},
            {9,R.mipmap.w09},
            {10,R.mipmap.w10},
            {11,R.mipmap.w11},
            {12,R.mipmap.w12},
            {13,R.mipmap.w13},
            {14,R.mipmap.w14},
            {15,R.mipmap.w15},
            {16,R.mipmap.w16},
            {17,R.mipmap.w17},
            {18,R.mipmap.w18},
            {19,R.mipmap.w19},
            {20,R.mipmap.w20},
            {21,R.mipmap.w21},
            {22,R.mipmap.w22},
            {23,R.mipmap.w23},
            {24,R.mipmap.w24},
            {25,R.mipmap.w25},
            {26,R.mipmap.w26},
            {27,R.mipmap.w27},
            {28,R.mipmap.w28},
            {29,R.mipmap.w29},
            {30,R.mipmap.w30},
            {31,R.mipmap.w31},
            {53,R.mipmap.w53}
    };

}
