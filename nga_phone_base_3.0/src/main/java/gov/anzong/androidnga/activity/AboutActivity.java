package gov.anzong.androidnga.activity;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.danielstone.materialaboutlibrary.MaterialAboutActivity;
import com.danielstone.materialaboutlibrary.items.MaterialAboutActionItem;
import com.danielstone.materialaboutlibrary.model.MaterialAboutCard;
import com.danielstone.materialaboutlibrary.model.MaterialAboutList;

import gov.anzong.androidnga.BuildConfig;
import gov.anzong.androidnga.R;
import gov.anzong.androidnga.debug.Debugger;
import sp.phone.theme.ThemeManager;
import sp.phone.ui.fragment.dialog.VersionUpgradeDialogFragment;
import sp.phone.util.FunctionUtils;

public class AboutActivity extends MaterialAboutActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ThemeManager.getInstance().applyAboutTheme(this);

        getWindow().setNavigationBarColor(Color.TRANSPARENT);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);

        super.onCreate(savedInstanceState);        

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

    @NonNull
    @Override
    protected MaterialAboutList getMaterialAboutList(@NonNull Context context) {
        return new MaterialAboutList(buildAppCard(), buildDevelopCard(), buildExtraCard());
    }

    private MaterialAboutCard buildAppCard() {
        MaterialAboutCard.Builder builder = new MaterialAboutCard.Builder();
        builder.addItem(new MaterialAboutActionItem.Builder()
                .text(R.string.start_title)
                .icon(R.mipmap.ic_launcher)
                .setOnClickAction(() -> new VersionUpgradeDialogFragment().show(getSupportFragmentManager(), null))
                .build());

        builder.addItem(new MaterialAboutActionItem.Builder()
                .text("版本")
                .subText(BuildConfig.VERSION_NAME)
                .icon(R.drawable.ic_about)
                .setOnClickAction(() -> {
                    try {
                        String url = "market://details?id=" + getPackageName();
                        Intent intent = new Intent("android.intent.action.VIEW");
                        intent.setData(Uri.parse(url));
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        //FunctionUtils.openUrlByDefaultBrowser(AboutActivity.this, "https://www.coolapk.com/apk/gov.anzong.androidnga");
                    }
                })
                .build());

        builder.addItem(new MaterialAboutActionItem.Builder()
                .text("License")
                .subText("GNU GPL v2,开放源代码许可")
                .setOnClickAction(() -> {
                    Intent intent = new Intent(AboutActivity.this, WebViewerActivity.class);
                    intent.putExtra("path", "file:///android_asset/OSLICENSE.TXT");
                    startActivity(intent);

                })
                .icon(R.drawable.ic_license)
                .build());

        builder.addItem(new MaterialAboutActionItem.Builder()
                .text("检测更新")
                .setOnClickAction(() -> {
                    Intent intent = new Intent(AboutActivity.this, WebViewerActivity.class);
                    intent.putExtra("path", "https://github.com/Justwen/NGA-CLIENT-VER-OPEN-SOURCE/releases");
                    startActivity(intent);

                })
                .icon(R.drawable.ic_update_24dp)
                .build());

        return builder.build();
    }

    private MaterialAboutCard buildDevelopCard() {
        MaterialAboutCard.Builder builder = new MaterialAboutCard.Builder();
        builder.title("开发团队");
        builder.addItem(new MaterialAboutActionItem.Builder()
                .text("代码")
                .subText("[@竹井詩織里]/[@cfan8]/[@jjimmys]\n[@Moandor]/[@Elrond]/[@Justwen]")
                .setOnLongClickAction(Debugger::toggleDebugMode)
                .icon(R.drawable.ic_code)
                .build());

        builder.addItem(new MaterialAboutActionItem.Builder()
                .text("美工")
                .subText("[@那个惩戒骑]/[@从来不卖萌]")
                .icon(R.drawable.ic_color_lens)
                .build());

        builder.addItem(new MaterialAboutActionItem.Builder()
                .text("Github")
                .subText("bug & 建议")
                .setOnClickAction(() -> FunctionUtils.openUrlByDefaultBrowser(AboutActivity.this, "https://github.com/Justwen/NGA-CLIENT-VER-OPEN-SOURCE/issues"))
                .icon(R.drawable.ic_github)
                .build());

        return builder.build();
    }


    private MaterialAboutCard buildExtraCard() {
        MaterialAboutCard.Builder builder = new MaterialAboutCard.Builder();
        builder.title("赞美片总!感谢[@force0119]");
        builder.addItem(new MaterialAboutActionItem.Builder()
                .text("客户端吐槽QQ群,欢迎加入捡肥皂")
                .subText("1065310118")
                .setOnClickAction(() -> FunctionUtils.copyToClipboard(AboutActivity.this, "1065310118"))
                .icon(R.drawable.ic_qq)
                .build());
        builder.addItem(new MaterialAboutActionItem.Builder()
                .text("客户端问题反馈群，请勿开车！")
                .subText("1077054628")
                .setOnClickAction(() -> FunctionUtils.copyToClipboard(AboutActivity.this, "1077054628"))
                .icon(R.drawable.ic_qq)
                .build());

        return builder.build();
    }

    @Nullable
    @Override
    protected CharSequence getActivityTitle() {
        return getString(R.string.title_about);
    }
}
