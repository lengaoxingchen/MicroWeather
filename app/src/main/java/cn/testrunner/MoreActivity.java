package cn.testrunner;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import cn.testrunner.db.DBManager;

public class MoreActivity extends AppCompatActivity implements View.OnClickListener {
    TextView bgTv, cacheTv, versionTv, shareTv;
    RadioGroup exbgRg;
    ImageView backIv;
    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);

        bgTv = findViewById(R.id.more_tv_exchargebg);
        cacheTv = findViewById(R.id.more_tv_cache);
        versionTv = findViewById(R.id.more_tv_version);
        shareTv = findViewById(R.id.more_tv_share);
        exbgRg = findViewById(R.id.more_rg);
        backIv = findViewById(R.id.more_iv_back);

        //设置点击监听事件
        bgTv.setOnClickListener(this);
        cacheTv.setOnClickListener(this);
        shareTv.setOnClickListener(this);
        backIv.setOnClickListener(this);

        pref = getSharedPreferences("bg_pref", MODE_PRIVATE);

        String versionName = getVersionName();
        versionTv.setText("当前版本:  V" + versionName);

        setRGListener();

    }

    private void setRGListener() {
        //设置改变背景图片的单选按钮的监听
        exbgRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                //获取目前的默认壁纸
                int bg = pref.getInt("bg", 0);
                SharedPreferences.Editor editor = pref.edit();
                Intent intent = new Intent(MoreActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                switch (checkedId) {
                    case R.id.more_rb_green:
                        if (bg == 0) {
                            Toast.makeText(MoreActivity.this, "您选择的背景为当前背景,无需改变!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        editor.putInt("bg",0);
                        editor.apply();
                        break;
                    case R.id.more_rb_pink:
                        if (bg == 1) {
                            Toast.makeText(MoreActivity.this, "您选择的背景为当前背景,无需改变!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        editor.putInt("bg",1);
                        editor.apply();
                        break;
                    case R.id.more_rb_blue:
                        if (bg == 2) {
                            Toast.makeText(MoreActivity.this, "您选择的背景为当前背景,无需改变!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        editor.putInt("bg",2);
                        editor.apply();
                        break;
                }
                startActivity(intent);
            }
        });
    }

    private String getVersionName() {
        /*
        获取应用的版本名称
         */

        PackageManager manager = getPackageManager();
        String versionName = null;
        try {
            PackageInfo packageInfo = manager.getPackageInfo(getPackageName(), 0);
            versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.more_tv_exchargebg:
                if (exbgRg.getVisibility() == View.VISIBLE) {
                    exbgRg.setVisibility(View.GONE);
                } else {
                    exbgRg.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.more_tv_cache:
                clearCache();
                break;
            case R.id.more_tv_share:
                shareSoftwareMsg("微天气App是一款超萌超可爱的天气预报软件,画面简约,播报天气情况精准,快来下载吧!");
                break;
            case R.id.more_iv_back:
                finish();
                break;
        }
    }

    private void shareSoftwareMsg(String s) {
        /* 分享软件的函数 */
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, s);
        startActivity(Intent.createChooser(intent, "微天气"));

    }

    private void clearCache() {
        /*清除缓存的函数 */
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示信息").setMessage("确定要删除所有缓存么?");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DBManager.deleteAllInfo();
                Toast.makeText(MoreActivity.this, "已清除全部缓存!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MoreActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }).setNegativeButton("取消", null);
        builder.create().show();
    }
}
