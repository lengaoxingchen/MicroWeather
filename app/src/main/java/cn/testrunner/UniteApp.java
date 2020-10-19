package cn.testrunner;

import android.app.Application;
import cn.testrunner.db.DBManager;
import org.xutils.x;

public class UniteApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
        DBManager.initDB(this);
    }
}
