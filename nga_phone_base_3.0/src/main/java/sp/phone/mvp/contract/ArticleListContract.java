package sp.phone.mvp.contract;

import android.content.Intent;
import android.os.Bundle;

import java.util.Map;

import gov.anzong.androidnga.http.OnHttpCallBack;
import com.client.androidnga.core.data.model.ThreadInfo;
import com.client.androidnga.core.data.bean.ThreadPostBean;
import sp.phone.param.ArticleListParam;

/**
 *
 * @author Justwen
 * @date 2017/11/22
 */

public interface ArticleListContract {

    interface Presenter {

        void loadPage(ArticleListParam param);

        void banThisSB(ThreadPostBean row);

        void postComment(ArticleListParam param, ThreadPostBean row);

        void postSupportTask(int tid, int pid);

        void postOpposeTask(int tid, int pid);

        void quote(ArticleListParam param, ThreadPostBean row);

        void cachePage();

        void loadCachePage();
    }

    interface View {

        void setRefreshing(boolean refreshing);

        boolean isRefreshing();

        void hideLoadingView();

        void setData(ThreadInfo data);

        void startPostActivity(Intent intent);

        void showPostCommentDialog(String prefix, Bundle bundle);

    }

    interface Model {

        void loadPage(ArticleListParam param, OnHttpCallBack<ThreadInfo> callBack);

        void loadPage(ArticleListParam param, Map<String, String> header, OnHttpCallBack<ThreadInfo> callBack);

        void cachePage(ArticleListParam param, String rawData);

        void loadCachePage(ArticleListParam param, OnHttpCallBack<ThreadInfo> callBack);
    }
}
