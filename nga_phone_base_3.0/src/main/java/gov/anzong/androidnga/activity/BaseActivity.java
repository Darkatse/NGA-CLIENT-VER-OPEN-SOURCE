package gov.anzong.androidnga.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.justwen.androidnga.cloud.CloudServerManager;

import gov.anzong.androidnga.R;
import gov.anzong.androidnga.base.util.PreferenceUtils;
import gov.anzong.androidnga.common.PreferenceKey;
import sp.phone.common.NotificationController;
import sp.phone.common.PhoneConfiguration;
import sp.phone.theme.ThemeManager;

/**
 * Created by liuboyu on 16/6/28.
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected PhoneConfiguration mConfig;

    private boolean mToolbarEnabled;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        mConfig = PhoneConfiguration.getInstance();
        updateThemeUi();
        super.onCreate(savedInstanceState);
        ThemeManager.getInstance().initializeWebTheme(this);


        /********************设置开始*****************************/
        Window window = getWindow();
        //        请求进行全屏布局

        // SYSTEM_UI_FLAG_LAYOUT_STABLE
            //*** Tells the system that the window wishes the content to
            //*** be laid out at the most extreme scenario. See the docs for
            //*** more information on the specifics
        //SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            //*** Tells the system that the window wishes the content to
            //*** be laid out as if the navigation bar was hidden
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setNavigationBarColor(Color.TRANSPARENT);

        // 在安卓10以上禁用系统栏视觉保护。
        // 当设置了 导航栏 背景为透明时，NavigationBarContrastEnforced 如果为true，则系统会自动绘制一个半透明背景
        // 状态栏的StatusBarContrast 效果同理，但是值默认为false，因此不用设置
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.setNavigationBarContrastEnforced(false);

            
        // 获取状态栏的高度
        int statusBarHeight = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        }

        // 给根视图添加一个顶部的padding
        View rootView = findViewById(android.R.id.content);
        rootView.setPadding(0, statusBarHeight, 0, 0);
        }

        /********************设置结束*****************************/

        
        // try {
        //     if (ThemeManager.getInstance().isNightMode()) {
        //         getWindow().setNavigationBarColor(ContextUtils.getColor(R.color.background_color));
        //     }
        // } catch (Exception e) {
        //     NLog.e("set navigation bar color exception occur: " + e);
        // }
    }

    protected void setToolbarEnabled(boolean enabled) {
        mToolbarEnabled = enabled;
    }

    public void setupToolbar(Toolbar toolbar) {
        if (toolbar != null && getSupportActionBar() == null) {
            setSupportActionBar(toolbar);
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setHomeButtonEnabled(true);
            }
        }
    }

    public void setupToolbar() {
        setupToolbar((Toolbar) findViewById(R.id.toolbar));
    }

    public void setupActionBar() {
        if (mToolbarEnabled) {
            setupToolbar();
        } else {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setHomeButtonEnabled(true);
            }
        }
    }

    protected void updateThemeUi() {
        ThemeManager tm = ThemeManager.getInstance();
        setTheme(tm.getTheme(mToolbarEnabled));
    }

    @Deprecated
    public void setupActionBar(Toolbar toolbar) {
        if (toolbar != null && getSupportActionBar() == null) {
            setSupportActionBar(toolbar);
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setHomeButtonEnabled(true);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                return super.onOptionsItemSelected(item);

        }
        return true;

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        try {
            return super.dispatchTouchEvent(ev);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        try {
            return super.dispatchKeyEvent(event);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onResume() {
        checkUpgrade();
        NotificationController.getInstance().checkNotificationDelay();
        super.onResume();
    }

    private void checkUpgrade() {
        if (PreferenceUtils.getData(PreferenceKey.KEY_CHECK_UPGRADE_STATE, true)) {
            long time = PreferenceUtils.getData(PreferenceKey.KEY_CHECK_UPGRADE_TIME, 0L);
            if (System.currentTimeMillis() - time > 1000 * 60 * 60 * 24) {
                CloudServerManager.checkUpgrade();
                PreferenceUtils.putData(PreferenceKey.KEY_CHECK_UPGRADE_TIME, System.currentTimeMillis());
            }
        }
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode, @Nullable Bundle options) {
        try {
            super.startActivityForResult(intent, requestCode, options);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }
}
