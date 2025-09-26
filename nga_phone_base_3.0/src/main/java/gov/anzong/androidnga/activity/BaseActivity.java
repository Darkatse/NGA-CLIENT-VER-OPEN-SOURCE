package gov.anzong.androidnga.activity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.justwen.androidnga.cloud.CloudServerManager;

import gov.anzong.androidnga.R;
import gov.anzong.androidnga.base.util.PreferenceUtils;
import gov.anzong.androidnga.common.PreferenceKey;
import sp.phone.common.NotificationController;
import sp.phone.common.PhoneConfiguration;
import sp.phone.theme.ThemeManager;
import gov.anzong.androidnga.common.util.NLog;

/**
 * Created by liuboyu on 16/6/28.
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected PhoneConfiguration mConfig;

    private boolean mToolbarEnabled;

    private boolean mComposeEnabled;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        mConfig = PhoneConfiguration.getInstance();
        updateThemeUi();

        // 1：在 super.onCreate 之前调用，实现真正的边到边布局
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        super.onCreate(savedInstanceState);
        ThemeManager.getInstance().initializeWebTheme(this);

        // --------------- 统一的沉浸式设置开始 -----------------------
        Window window = getWindow();
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // 2：将系统栏背景设置为透明
        int primaryColor = ThemeManager.getInstance().getPrimaryColor(this);
        window.setStatusBarColor(primaryColor);
        window.setNavigationBarColor(Color.TRANSPARENT);

        // 禁用系统对比度保护
        window.setNavigationBarContrastEnforced(false);
        // 状态栏的默认为false，一般不用设置
        // window.setStatusBarContrastEnforced(false);

        // 3：为 View 体系的 UI 添加 Insets 监听，以处理边距
        // 将监听器附加到根内容视图上
        if (!mComposeEnabled) {
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
                Insets statusBarsInsets = insets.getInsets(WindowInsetsCompat.Type.statusBars());
                Insets navigationBarsInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars());

                // 处理顶部的 AppBar
                View appBar = v.findViewById(R.id.appbar);
                if (appBar != null) {
                    appBar.setPadding(
                            appBar.getPaddingLeft(),
                            statusBarsInsets.top,
                            appBar.getPaddingRight(),
                            appBar.getPaddingBottom()
                    );
                }
//                // 处理底部的SwipeRefresh
//                View mainContent = v.findViewById(R.id.swipe_refresh);
//                if (mainContent != null) {
//                    // 为内容区域设置底部 padding，防止列表的最后一项被导航栏遮挡
//                    mainContent.setPadding(
//                            mainContent.getPaddingLeft(),
//                            mainContent.getPaddingTop(),
//                            mainContent.getPaddingRight(),
//                            navigationBarsInsets.bottom
//                    );
//                }

                // 告诉系统我们已经处理了 Insets，系统无需再进行默认处理
                return WindowInsetsCompat.CONSUMED;
            });
        }


        // ------------------ 设置结束 ------------------------------
    }

    public void setComposeEnabled(boolean composeEnabled) {
        mComposeEnabled = composeEnabled;
    }

    // Android15上开启EdgeToEdge后adjustResize会失效，这里临时做下兼容
    protected void compatActivityAdjustResize(Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            return;
        }
        View content = ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);
        final Rect r = new Rect();
        content.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            content.getWindowVisibleDisplayFrame(r);
            int screenHeight = content.getRootView().getHeight();
            int statusBarHeight = 0;
            int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                statusBarHeight = getResources().getDimensionPixelSize(resourceId);
            }
            int keyboardHeight = screenHeight - r.bottom - statusBarHeight;
            if (keyboardHeight > screenHeight / 4) { // 键盘高度超过屏幕1/4
                content.setPadding(0, 0, 0, keyboardHeight);
            } else {
                content.setPadding(0, 0, 0, 0);
            }
        });
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
    public void setContentView(View view) {
        super.setContentView(view);
//        view.setFitsSystemWindows(!mToolbarEnabled);
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
