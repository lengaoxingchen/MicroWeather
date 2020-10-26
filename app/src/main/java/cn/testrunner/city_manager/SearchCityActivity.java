package cn.testrunner.city_manager;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;
import android.os.Bundle;
import cn.testrunner.MainActivity;
import cn.testrunner.R;
import cn.testrunner.base.BaseActivity;
import cn.testrunner.bean.WeatherBean;
import com.google.gson.Gson;

public class SearchCityActivity extends BaseActivity implements View.OnClickListener {
    EditText searchEt;
    ImageView submitIv;
    GridView searchGv;


    String[] hotCities = {
            "北京", "上海", "广州", "深圳", "珠海", "佛山", "南京", "苏州", "厦门", "长沙", "成都", "福州", "杭州", "武汉", "青岛", "西安", "太原",
            "沈阳", "重庆", "天津", "南宁"
    };
    private String city;

    String url1 = "http://api.map.baidu.com/telematics/v3/weather?location=";
    String url2 = "&output=json&ak=FkPhtMBK0HTIQNh7gG4cNUttSTyr0nzo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_city);

        searchEt = findViewById(R.id.search_et);
        submitIv = findViewById(R.id.search_iv_submit);
        searchGv = findViewById(R.id.search_gv);

        submitIv.setOnClickListener(this);

        //设置适配器
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.item_hotcity, hotCities);
        searchGv.setAdapter(adapter);

        setListener();

    }

    //设置监听事件
    private void setListener() {
        searchGv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                city = hotCities[position];
                String url = url1 + city + url2;
                loadData(url);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_iv_submit:
                city = searchEt.getText().toString();
                if (!TextUtils.isEmpty(city)) {
                    //判断是否能找到这个城市
                    String url = url1 + city + url2;
                    loadData(url);

                } else {
                    Toast.makeText(this, "输入内容不能为空", Toast.LENGTH_SHORT).show();
                }
                break;
        }

    }

    @Override
    public void onSuccess(String result) {
        WeatherBean weatherBean = new Gson().fromJson(result, WeatherBean.class);
        if (weatherBean.getError() == 0) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("city", city);
            startActivity(intent);
        } else {
            Toast.makeText(this, "暂时未收录此城市天气信息", Toast.LENGTH_SHORT).show();
        }
    }
}
