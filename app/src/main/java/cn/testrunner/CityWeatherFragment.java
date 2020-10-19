package cn.testrunner;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import cn.testrunner.bean.WeatherBean;
import cn.testrunner.db.DBManager;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CityWeatherFragment extends BaseFragment implements View.OnClickListener {
    TextView tempTv, cityTv, conditionTv, windTv, tempRangeTv, dateTv, clothIndexTv, carIndexTv, coldIndexTv, sportIndexTv, raysIndexTv;
    ImageView dayTv;
    LinearLayout futureLayout;
    String url1 = "http://api.map.baidu.com/telematics/v3/weather?location=";
    String url2 = "&output=json&ak=FkPhtMBK0HTIQNh7gG4cNUttSTyr0nzo";
    private List<WeatherBean.ResultsBean.IndexBean> indexList;
    String city;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_city_weather, container, false);
        initView(view);

        //可以通过activity传值获取到当前fragment加载的是那个地方的天气情况
        Bundle bundle = getArguments();
        city = bundle.getString("city");
        String url = url1 + city + url2;

        //调用父类获取数据的方法
        loadData(url);
        return view;
    }

    @Override
    public void onSuccess(String result) {
        //解析并展示数据
        Log.d("request success", result);
        parseShowData(result);

        //更新数据
        int i = DBManager.updateInfoByCity(city, result);
        if (i <= 0) {
            //更新数据库失败,说明没有这个城市信息,增驾这个城市记录
            DBManager.addCityInfo(city, result);
        }
    }

    @Override
    public void onError(Throwable ex, boolean isOnCallback) {
        Log.d("request Error", ex.getMessage());
        //数据库当中查找上一次信息显示在Fragment中
        String s = DBManager.queryInfoByCity(city);
        if (!TextUtils.isEmpty(s)) {
            parseShowData(s);
        }
    }

    private void parseShowData(String result) {
        //使用给送解析数据
        WeatherBean weatherBean = new Gson().fromJson(result, WeatherBean.class);
        WeatherBean.ResultsBean resultsBean = weatherBean.getResults().get(0);
        //获取指数数据信息集合列表
        indexList = resultsBean.getIndex();
        if (indexList.isEmpty()) {
            WeatherBean.ResultsBean.IndexBean indexBean1 = new WeatherBean.ResultsBean.IndexBean();
            indexBean1.setDes("天气温度较低");
            indexBean1.setTipt("穿衣指数");
            indexBean1.setTitle("穿衣");
            indexBean1.setZs("较冷");
            indexList.add(indexBean1);
            WeatherBean.ResultsBean.IndexBean indexBean2 = new WeatherBean.ResultsBean.IndexBean();
            indexBean2.setDes("天气温度较低");
            indexBean2.setTipt("洗车指数");
            indexBean2.setTitle("穿衣");
            indexBean2.setZs("较冷");
            indexList.add(indexBean2);
            WeatherBean.ResultsBean.IndexBean indexBean3 = new WeatherBean.ResultsBean.IndexBean();
            indexBean3.setDes("天气温度较低");
            indexBean3.setTipt("感冒指数");
            indexBean3.setTitle("穿衣");
            indexBean3.setZs("较冷");
            indexList.add(indexBean3);
            WeatherBean.ResultsBean.IndexBean indexBean4 = new WeatherBean.ResultsBean.IndexBean();
            indexBean4.setDes("天气温度较低");
            indexBean4.setTipt("运动指数");
            indexBean4.setTitle("穿衣");
            indexBean4.setZs("较冷");
            indexList.add(indexBean4);
            WeatherBean.ResultsBean.IndexBean indexBean5 = new WeatherBean.ResultsBean.IndexBean();
            indexBean5.setDes("天气温度较低");
            indexBean5.setTipt("紫外线指数");
            indexBean5.setTitle("穿衣");
            indexBean5.setZs("较冷");
            indexList.add(indexBean5);
        }
        //设置TextView
        dateTv.setText(weatherBean.getDate());
        cityTv.setText(resultsBean.getCurrentCity());
        //获取今日天气情况
        WeatherBean.ResultsBean.WeatherDataBean todayBean = resultsBean.getWeather_data().get(0);
        windTv.setText(todayBean.getWind());
        tempRangeTv.setText(todayBean.getTemperature());
        conditionTv.setText(todayBean.getWeather());
        //获取实时天气温度情况,需要处理字符串
        String[] split = todayBean.getDate().split("：");
        String todayTemp = split[1].replace(")", "");
        tempTv.setText(todayTemp);

        //设置显示的天气情况
        Picasso.with(getActivity()).load(todayBean.getDayPictureUrl()).into(dayTv);

        //获取未来三天的天气情况,加载到layout中
        List<WeatherBean.ResultsBean.WeatherDataBean> futureList = resultsBean.getWeather_data();
        futureList.remove(0);
        for (WeatherBean.ResultsBean.WeatherDataBean dataBean : futureList) {
            View itemView = LayoutInflater.from(getActivity()).inflate(R.layout.item_main_center, null);
            itemView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            futureLayout.addView(itemView);
            TextView idateTv = itemView.findViewById(R.id.item_center_tv_date);
            TextView iconTv = itemView.findViewById(R.id.item_center_tv_con);
            TextView itempRangeTv = itemView.findViewById(R.id.item_center_tv_temp);
            ImageView iIv = itemView.findViewById(R.id.item_center_iv);
            idateTv.setText(dataBean.getDate());
            iconTv.setText(dataBean.getWeather());
            itempRangeTv.setText(dataBean.getTemperature());
            Picasso.with(getActivity()).load(dataBean.getDayPictureUrl()).into(iIv);
        }
    }

    private void initView(View view) {
        //用于初始化控件操作
        tempTv = view.findViewById(R.id.frag_tv_current_temp);
        cityTv = view.findViewById(R.id.frag_tv_city);
        conditionTv = view.findViewById(R.id.frag_tv_condition);
        windTv = view.findViewById(R.id.frag_tv_wind);
        tempRangeTv = view.findViewById(R.id.frag_tv_temp_range);
        dateTv = view.findViewById(R.id.frag_tv_date);
        clothIndexTv = view.findViewById(R.id.frag_index_tv_cold);
        carIndexTv = view.findViewById(R.id.frag_index_tv_wash_car);
        coldIndexTv = view.findViewById(R.id.frag_index_tv_dress);
        sportIndexTv = view.findViewById(R.id.frag_index_tv_sport);
        raysIndexTv = view.findViewById(R.id.frag_index_tv_rays);
        dayTv = view.findViewById(R.id.frag_iv_today);
        futureLayout = view.findViewById(R.id.frag_center_layout);
        //设置点击事件监听
        clothIndexTv.setOnClickListener(this);
        sportIndexTv.setOnClickListener(this);
        raysIndexTv.setOnClickListener(this);
        coldIndexTv.setOnClickListener(this);
        carIndexTv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        switch (v.getId()) {
            case R.id.frag_index_tv_cold:
                WeatherBean.ResultsBean.IndexBean indexBean = indexList.get(2);
                builder.setTitle(indexBean.getTitle());
                String msg = indexBean.getZs() + "\n" + indexBean.getDes();
                builder.setMessage(msg);
                builder.setPositiveButton("确定", null);
                break;
            case R.id.frag_index_tv_wash_car:
                indexBean = indexList.get(1);
                builder.setTitle(indexBean.getTitle());
                msg = indexBean.getZs() + "\n" + indexBean.getDes();
                builder.setMessage(msg);
                builder.setPositiveButton("确定", null);
                break;
            case R.id.frag_index_tv_dress:
                indexBean = indexList.get(0);
                builder.setTitle(indexBean.getTitle());
                msg = indexBean.getZs() + "\n" + indexBean.getDes();
                builder.setMessage(msg);
                builder.setPositiveButton("确定", null);
                break;
            case R.id.frag_index_tv_sport:
                indexBean = indexList.get(3);
                builder.setTitle(indexBean.getTitle());
                msg = indexBean.getZs() + "\n" + indexBean.getDes();
                builder.setMessage(msg);
                builder.setPositiveButton("确定", null);
                break;
            case R.id.frag_index_tv_rays:
                indexBean = indexList.get(4);
                builder.setTitle(indexBean.getTitle());
                msg = indexBean.getZs() + "\n" + indexBean.getDes();
                builder.setMessage(msg);
                builder.setPositiveButton("确定", null);
                break;
        }
        builder.create().show();
    }
}
