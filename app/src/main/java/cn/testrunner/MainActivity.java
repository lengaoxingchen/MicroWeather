package cn.testrunner;

import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import cn.testrunner.city_manager.CityManagerActivity;
import cn.testrunner.db.DBManager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    ImageView addCityIv, moreIv;
    LinearLayout pointLayout;
    ViewPager mainVp;
    RelativeLayout outLayout;

    //ViewPager的数据源
    List<Fragment> fragmentList = new ArrayList<>();
    //表示选中城市的集合
    List<String> cityList = DBManager.queryAllCityName();  //获取数据库包含的城市信息列表

    //表示ViewPager的页数指数器集合
    List<ImageView> imgList = new ArrayList<>();
    private CityFragmentPagerAdapter adapter;
    private SharedPreferences pref;
    private int bgNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        addCityIv = findViewById(R.id.main_iv_add);
        moreIv = findViewById(R.id.main_iv_more);
        pointLayout = findViewById(R.id.main_layout_point);
        mainVp = findViewById(R.id.main_vp);
        outLayout = findViewById(R.id.main_out_layout);

        //交换壁纸
        exchangeBg();

        addCityIv.setOnClickListener(this);
        moreIv.setOnClickListener(this);

        if (cityList.size() == 0) {
            cityList.add("北京");
        }

        /*
        因为可能搜索界面点击跳转到此界面会传值,所以获取一下
         */
        try {

            Intent intent = getIntent();
            String city = intent.getStringExtra("city");
            if (!cityList.contains(city) && !TextUtils.isEmpty(city)) {
                cityList.add(city);
            }

        } catch (Exception e) {
            Log.i("animee", "程序出问题了");
        }

        //初始化ViewPager页面的方法
        initPager();
        adapter = new CityFragmentPagerAdapter(getSupportFragmentManager(), fragmentList);
        mainVp.setAdapter(adapter);

        //创建小圆点指示器
        initPoint();
        //设置最后一个城市信息
        mainVp.setCurrentItem(fragmentList.size() - 1);

        //设置ViewPager页面监听
        setPagerListener();
    }

    //换壁纸的函数
    public void exchangeBg() {
        pref = getSharedPreferences("bg_pref", MODE_PRIVATE);
        bgNum = pref.getInt("bg", 2);
        switch (bgNum) {
            case 0:
                outLayout.setBackgroundResource(R.mipmap.bg);
                break;
            case 1:
                outLayout.setBackgroundResource(R.mipmap.bg2);
                break;
            case 2:
                outLayout.setBackgroundResource(R.mipmap.bg3);
                break;
        }
    }

    private void setPagerListener() {
        mainVp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                for (ImageView imageView : imgList) {
                    imageView.setImageResource(R.mipmap.a1);
                }
                imgList.get(position).setImageResource(R.mipmap.a2);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initPoint() {
        //创建小圆点 ViewPager页面指示器的函数
        for (int i = 0; i < fragmentList.size(); i++) {
            ImageView pIv = new ImageView(this);
            pIv.setImageResource(R.mipmap.a1);
            pIv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) pIv.getLayoutParams();
            lp.setMargins(0, 0, 20, 0);
            imgList.add(pIv);
            pointLayout.addView(pIv);
        }
        imgList.get(imgList.size() - 1).setImageResource(R.mipmap.a2);
    }

    private void initPager() {
        /*
        创建Fragment对象添加到ViewPager数据源当中
         */
        for (String city : cityList) {
            CityWeatherFragment cwFrag = new CityWeatherFragment();
            Bundle bundle = new Bundle();
            bundle.putString("city", city);
            cwFrag.setArguments(bundle);
            fragmentList.add(cwFrag);
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.main_iv_add:
                intent.setClass(this, CityManagerActivity.class);
                break;
            case R.id.main_iv_more:
                intent.setClass(this, MoreActivity.class);
                break;
            default:
        }
        startActivity(intent);
    }

    /*
    页面重新加载时会调用的函数,这个函数在页面获取焦点之前进行调用,此处完成ViewPager页数的更新
     */
    @Override
    protected void onRestart() {

        super.onRestart();

        //获取数据库方式还剩下的城市集合
        List<String> list = DBManager.queryAllCityName();
        if (list.size() == 0) {
            list.add("北京");
        }
        cityList.clear();  //重新加载之前,清空原来的数据源
        cityList.addAll(list);

        //剩余城市也要创建对应的fragment页面
        fragmentList.clear();
        initPager();
        adapter.notifyDataSetChanged();
        //页面数量发生改变,指示器的数量也会发生变化,重新设置添加指示器
        imgList.clear();
        pointLayout.removeAllViews();  //将布局中所有元素全部移除
        initPoint();
        mainVp.setCurrentItem(fragmentList.size() - 1);
    }
}
