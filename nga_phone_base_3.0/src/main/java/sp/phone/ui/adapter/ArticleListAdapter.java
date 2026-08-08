package sp.phone.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.launcher.ARouter;

import java.text.MessageFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import gov.anzong.androidnga.R;
import gov.anzong.androidnga.arouter.ARouterConstants;
import gov.anzong.androidnga.base.util.ContextUtils;
import gov.anzong.androidnga.base.util.DeviceUtils;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import sp.phone.common.PhoneConfiguration;
import sp.phone.common.UserManagerImpl;

import com.client.androidnga.core.data.model.ClientModel;
import com.client.androidnga.core.data.model.ThreadInfo;
import com.client.androidnga.core.data.bean.ThreadPostBean;
import sp.phone.rxjava.BaseSubscriber;
import sp.phone.rxjava.RxUtils;
import sp.phone.theme.ThemeManager;
import sp.phone.ui.fragment.dialog.AvatarDialogFragment;
import sp.phone.ui.fragment.dialog.BaseDialogFragment;
import sp.phone.util.ActivityUtils;
import sp.phone.util.FunctionUtils;
import sp.phone.util.ImageUtils;
import sp.phone.util.StringUtils;
import sp.phone.view.webview.LocalWebView;

/**
 * 帖子详情列表Adapter
 */
public class ArticleListAdapter extends RecyclerView.Adapter<ArticleListAdapter.ArticleViewHolder> {

    private static final int VIEW_TYPE_WEB_VIEW = 0;

    private static final int VIEW_TYPE_NATIVE_VIEW = 1;

    private Context mContext;

    private FragmentManager mFragmentManager;

    private ThreadInfo mData;

    private LayoutInflater mLayoutInflater;

    private ThemeManager mThemeManager = ThemeManager.getInstance();

    private LocalWebView[] mLocalWebViews = new LocalWebView[20];

    private String mTopicOwner;

    private View.OnClickListener mOnClientClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            ThreadPostBean row = (ThreadPostBean) v.getTag();
            ClientModel clientModel = row.threadPostInfo.clientModel;
            if (clientModel == null) {
                return;
            }
            String deviceInfo = "发送自" + clientModel.getModelName();
            ActivityUtils.showToast(deviceInfo);

        }
    };

    private View.OnClickListener mOnReplyClickListener = new View.OnClickListener() {

        private Intent getReplyIntent(ThreadPostBean row) {
            Intent intent = new Intent();
            StringBuilder postPrefix = new StringBuilder();
            String mention = null;

            final String quote_regex = "\\[quote\\]([\\s\\S])*\\[/quote\\]";
            final String replay_regex = "\\[b\\]Reply to \\[pid=\\d+,\\d+,\\d+\\]Reply\\[/pid\\] Post by .+?\\[/b\\]";
            String content = row.content;
            final String name = row.author;
            final String uid = String.valueOf(row.authorid);
            int page = (row.lou + 20) / 20;// 以楼数计算page
            content = content.replaceAll(quote_regex, "");
            content = content.replaceAll(replay_regex, "");
            final String postTime = row.postdate;
            final String tidStr = String.valueOf(row.tid);
            content = FunctionUtils.checkContent(content);
            content = StringUtils.unEscapeHtml(content);
            if (row.pid != 0 || row.lou == 0) {
                mention = name;
                postPrefix.append("[quote][pid=");
                postPrefix.append(row.pid);
                postPrefix.append(',');
                postPrefix.append(tidStr);
                postPrefix.append(",");
                if (page > 0)
                    postPrefix.append(page);
                postPrefix.append("]");// Topic
                postPrefix.append("Reply");
                if (row.getISANONYMOUS()) {// 是匿名的人
                    postPrefix.append("[/pid] [b]Post by [uid=");
                    postPrefix.append("-1");
                    postPrefix.append("]");
                    postPrefix.append(name);
                    postPrefix.append("[/uid][color=gray](");
                    postPrefix.append(row.lou);
                    postPrefix.append("楼)[/color] (");
                } else {
                    postPrefix.append("[/pid] [b]Post by [uid=");
                    postPrefix.append(uid);
                    postPrefix.append("]");
                    postPrefix.append(name);
                    postPrefix.append("[/uid] (");
                }
                postPrefix.append(postTime);
                postPrefix.append("):[/b]\n");
                postPrefix.append(content);
                postPrefix.append("[/quote]\n");
            }
            if (!StringUtils.isEmpty(mention))
                intent.putExtra("mention", mention);
            intent.putExtra("prefix",
                    StringUtils.removeBrTag(postPrefix.toString()));
            intent.putExtra("tid", tidStr);
            intent.putExtra("action", "reply");

            if (UserManagerImpl.getInstance().hasValidUser()) {// 登入了才能发
                intent.setClass(
                        ContextUtils.getContext(),
                        PhoneConfiguration.getInstance().postActivityClass);
            } else {
                ActivityUtils.startLoginActivity(mContext);
            }
            return intent;
        }

        @Override
        public void onClick(View view) {

            ThreadPostBean row = (ThreadPostBean) view.getTag();

            Observable.create((ObservableOnSubscribe<Intent>) emitter -> {
                emitter.onNext(getReplyIntent(row));
                emitter.onComplete();

            }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new BaseSubscriber<Intent>() {
                @Override
                public void onNext(@io.reactivex.annotations.NonNull Intent intent) {
                    try {
                        view.setEnabled(true);
                        ((Activity) view.getContext()).startActivityForResult(intent, ActivityUtils.REQUEST_CODE_TOPIC_POST);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    super.onNext(intent);
                }
            });
        }
    };

    private View.OnClickListener mOnProfileClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ThreadPostBean row = (ThreadPostBean) view.getTag();

            if (row.getISANONYMOUS()) {
                ActivityUtils.showToast("这白痴匿名了,神马都看不到");
            } else if (row.author != null){
                ARouter.getInstance()
                        .build(ARouterConstants.ACTIVITY_PROFILE)
                        .withString("mode", "uid")
                        .withString("uid", String.valueOf(row.authorid))
                        .navigation();
            }
        }
    };

    private View.OnClickListener mOnAvatarClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ThreadPostBean row = (ThreadPostBean) view.getTag();
            if (row.getISANONYMOUS()) {
                ActivityUtils.showToast("这白痴匿名了,神马都看不到");
            } else {
                Bundle bundle = new Bundle();
                bundle.putString("name", row.author);
                bundle.putString("url", FunctionUtils.parseAvatarUrl(row.js_escap_avatar));
                BaseDialogFragment.show(mFragmentManager, bundle, AvatarDialogFragment.class);
                //FunctionUtils.Create_Avatar_Dialog(row, view.getContext(), null);
            }
        }
    };

    private View.OnClickListener mSupportListener;
    private View.OnClickListener mOpposeListener;
    private View.OnClickListener mMenuTogglerListener;

    private boolean mWifiConnected;

    public class ArticleViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_nickName)
        TextView nickNameTV;

        LocalWebView contentTV;

        @BindView(R.id.wv_container)
        FrameLayout contentContainer;

        @BindView(R.id.tv_floor)
        TextView floorTv;

        @BindView(R.id.tv_post_time)
        TextView postTimeTv;

        @BindView(R.id.iv_support)
        ImageView supportBtn;

        @BindView(R.id.iv_oppose)
        ImageView opposeBtn;

        @BindView(R.id.iv_reply)
        ImageView replyBtn;

        @BindView(R.id.iv_avatar)
        ImageView avatarIv;

        @BindView(R.id.iv_client)
        ImageView clientIv;

        @BindView(R.id.tv_score)
        TextView scoreTv;

        @BindView(R.id.iv_more)
        ImageView menuIv;

        @BindView(R.id.fl_avatar)
        FrameLayout avatarPanel;

        @BindView(R.id.tv_detail)
        TextView detailTv;

        @BindView(R.id.tv_content)
        TextView contentTextView;

        public ArticleViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public ArticleListAdapter(Context context, FragmentManager fm) {
        mContext = context;
        mFragmentManager = fm;
        mLayoutInflater = LayoutInflater.from(mContext);
        mWifiConnected = DeviceUtils.isWifiConnected(context);
    }

    public void setTopicOwner(String topicOwner) {
        mTopicOwner = topicOwner;
    }

    public void setData(ThreadInfo data) {
        mData = data;
    }

    public void setSupportListener(View.OnClickListener listener) {
        mSupportListener = listener;
    }

    public void setOpposeListener(View.OnClickListener listener) {
        mOpposeListener = listener;
    }

    public void setMenuTogglerListener(View.OnClickListener menuTogglerListener) {
        mMenuTogglerListener = menuTogglerListener;
    }

    @Override
    public int getItemViewType(int position) {
        ThreadPostBean row = mData.rowList.get(position);
        return TextUtils.isEmpty(row.threadPostInfo.formatHtml) ? VIEW_TYPE_NATIVE_VIEW : VIEW_TYPE_WEB_VIEW;
    }

    @Override
    public ArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.fragment_article_list_item, parent, false);
        ArticleViewHolder viewHolder = new ArticleViewHolder(view);
        ViewGroup.LayoutParams lp = viewHolder.avatarIv.getLayoutParams();
        lp.width = lp.height = PhoneConfiguration.getInstance().getAvatarSize();
        if (viewType == VIEW_TYPE_WEB_VIEW) {
            viewHolder.contentTextView.setVisibility(View.GONE);
            // viewHolder.contentTV.setVisibility(View.VISIBLE);
        } else {
            viewHolder.contentTextView.setVisibility(View.VISIBLE);
            //  viewHolder.contentTV.setVisibility(View.GONE);
        }
        RxUtils.clicks(viewHolder.nickNameTV, mOnProfileClickListener);
        RxUtils.clicks(viewHolder.supportBtn, mSupportListener);
        RxUtils.clicks(viewHolder.opposeBtn, mOpposeListener);
        RxUtils.clicks(viewHolder.replyBtn, mOnReplyClickListener);
        RxUtils.clicks(viewHolder.clientIv, mOnClientClickListener);
        RxUtils.clicks(viewHolder.menuIv, mMenuTogglerListener);
        RxUtils.clicks(viewHolder.avatarPanel, mOnAvatarClickListener);
        viewHolder.contentTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, PhoneConfiguration.getInstance().getTopicContentSize());
        // viewHolder.contentTV.setTextSize(PhoneConfiguration.getInstance().getTopicContentSize());
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ArticleViewHolder holder, final int position) {

        final ThreadPostBean row = mData.rowList.get(position);

        if (row == null) {
            return;
        }

        if (!PhoneConfiguration.getInstance().useSolidColorBackground()) {
            holder.itemView.setBackgroundResource(ThemeManager.getInstance().getBackgroundColor(position));
        }

        holder.supportBtn.setTag(row);
        holder.opposeBtn.setTag(row);
        holder.replyBtn.setTag(row);
        holder.nickNameTV.setTag(row);
        holder.menuIv.setTag(row);
        holder.avatarPanel.setTag(row);

        onBindAvatarView(holder.avatarIv, row);
        onBindDeviceType(holder.clientIv, row);
        onBindContentView(holder, row, position);

        int fgColor = mThemeManager.getAccentColor(mContext);
        FunctionUtils.handleNickName(row, fgColor, holder.nickNameTV, mTopicOwner, mContext);

        holder.floorTv.setText(MessageFormat.format("[{0} 楼]", String.valueOf(row.lou)));
        holder.postTimeTv.setText(row.postdate);
        holder.scoreTv.setText(MessageFormat.format("{0}", row.score));

        holder.detailTv.setText(String.format("级别：%s   威望：%s   发帖：%s", row.getMemberGroup(), row.getReputation(), row.getPostCount()));

    }

    private LocalWebView createLocalWebView() {
        LocalWebView localWebView = new LocalWebView(mContext);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMarginStart(mContext.getResources().getDimensionPixelSize(R.dimen.material_standard_half));
        lp.setMarginEnd(mContext.getResources().getDimensionPixelSize(R.dimen.material_standard_half));
        localWebView.setLayoutParams(lp);
        return localWebView;
    }

    private void onBindContentView(ArticleViewHolder holder, ThreadPostBean row, int position) {
        String html = row.threadPostInfo.formatHtml;
        if (html != null) {
            if (mLocalWebViews != null) {
                LocalWebView localWebView = mLocalWebViews[position];
                if (localWebView == null) {
                    localWebView = createLocalWebView();
                    mLocalWebViews[position] = localWebView;
                }
                if (localWebView != holder.contentTV) {
                    holder.contentContainer.removeView(holder.contentTV);
                    if (localWebView.getParent() != null) {
                        ((ViewGroup) localWebView.getParent()).removeView(localWebView);
                    }
                    holder.contentTV = localWebView;
                    holder.contentContainer.addView(localWebView);
                }
            } else if (holder.contentTV == null) {
                holder.contentTV = createLocalWebView();
                holder.contentContainer.addView(holder.contentTV);
            }
            holder.contentTV.getWebViewClientEx().setImgUrls(row.getImageUrls());
            holder.contentTV.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);
        } else {
            holder.contentTextView.setText(row.content);
        }
    }

    private void onBindDeviceType(ImageView clientBtn, ThreadPostBean row) {
        ClientModel clientModel = row.threadPostInfo.clientModel;
        if (clientModel == null) {
            clientBtn.setVisibility(View.GONE);
            clientBtn.setTag(null);
            return;
        } else if (clientModel == ClientModel.IOS) {
            clientBtn.setImageResource(R.drawable.ic_apple_12dp);
        } else if (clientModel == ClientModel.ANDROID) {
            clientBtn.setImageResource(R.drawable.ic_android_12dp);
        } else if (clientModel == ClientModel.WP) {
            clientBtn.setImageResource(R.drawable.ic_windows_12dp);
        } else {
            clientBtn.setImageResource(R.drawable.ic_smartphone_12dp);
        }
        clientBtn.setTag(row);
        clientBtn.setVisibility(View.VISIBLE);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.rowNum;
    }

    private void onBindAvatarView(ImageView avatarIv, ThreadPostBean row) {
        final String avatarUrl = FunctionUtils.parseAvatarUrl(row.js_escap_avatar);
        final boolean downImg = PhoneConfiguration.getInstance().isAvatarLoadEnabled(mWifiConnected);

        ImageUtils.loadRoundCornerAvatar(avatarIv, avatarUrl, !downImg);
    }

}