package sp.phone.mvp.model.convert;

import android.text.TextUtils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.client.androidnga.core.data.bean.ThreadAttachBean;
import com.client.androidnga.core.data.bean.ThreadBean;
import com.client.androidnga.core.data.bean.ThreadPostBean;
import com.client.androidnga.core.data.bean.ThreadUserBean;
import com.client.androidnga.core.data.model.ThreadInfo;
import com.client.androidnga.core.data.model.ThreadPostInfo;
import com.client.androidnga.core.parse.ThreadInfoParse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import gov.anzong.androidnga.Utils;
import gov.anzong.androidnga.base.logger.Logger;
import gov.anzong.androidnga.common.util.NLog;
import gov.anzong.androidnga.core.HtmlConvertFactory;
import com.client.androidnga.core.data.html.AttachmentData;
import com.client.androidnga.core.data.html.CommentData;
import com.client.androidnga.core.data.html.HtmlData;
import sp.phone.common.PhoneConfiguration;
import sp.phone.common.UserManagerImpl;
import sp.phone.theme.ThemeManager;
import sp.phone.util.FunctionUtils;

/**
 * Created by Justwen on 2017/12/3.
 */

public class ArticleConvertFactory {

    private static final String TAG = ArticleConvertFactory.class.getSimpleName();

    public static ThreadInfo getArticleInfo(String js) {
        return parseJsonThreadPage(js);
    }

    private static ThreadInfo parseJsonThreadPage(String js) {
        ThreadInfo data = null;
        try {
            ThreadBean threadBean = ThreadInfoParse.INSTANCE.parserThreadBean(js);
            if (threadBean == null) {
                return null;
            }

            int allRows = threadBean.__ROWS;
            data = ThreadInfoParse.INSTANCE.parseThreadInfo(js, threadBean);
            List<ThreadPostBean> rowList = buildThreadRowList(data, threadBean);
            for (ThreadPostBean bean : rowList) {
                data.threadPostList.add(bean.threadPostInfo);
            }
            data.set__ROWS(allRows);
            data.rowNum = data.threadPostList.size();
        } catch (Exception e) {
            NLog.e(TAG, "can not parse :\n" + js);
            Logger.d(e);
        }
        return data;
    }

    private static List<ThreadPostBean> buildThreadRowList(ThreadInfo threadInfo, ThreadBean threadBean) {
        return convertJsObjToList(threadInfo, threadBean, threadBean.__R, threadBean.__R__ROWS);
    }


    private static List<ThreadPostBean> convertJsObjToList(ThreadInfo threadInfo,ThreadBean threadBean, Map<String, ThreadPostBean> rowMap, int count) {
        List<ThreadPostBean> rowList = new ArrayList<>();
        NLog.d("ArticleUtil", "convertJsObjToList");
        for (int i = 0; i < count; i++) {
            ThreadPostBean row = rowMap.get(String.valueOf(i));
            if (row == null) {
                continue;
            }
            row.threadPostInfo = ThreadInfoParse.INSTANCE.parseThreadPostInfo(row);
            buildRowComment(threadInfo, threadBean, row);
            buildRowUserInfo(threadBean, row.threadPostInfo);
            row.threadPostInfo.isComment = isComment(row);
            buildRowContent(threadInfo, row);
            rowList.add(row);
        }
        return rowList;
    }


    private static boolean isComment(ThreadPostBean row) {

        return row.alterinfo == null && row.attachs == null
                && row.comments == null
                && row.threadPostInfo.avatarUrl == null && row.level == null
                && row.threadPostInfo.signature == null;
    }


    private static void buildRowContent(ThreadInfo threadInfo, ThreadPostBean row) {
        if (row.content == null) {
            row.content = row.subject;
            row.subject = null;
        }
        List<String> imageUrls = new ArrayList<>();
        String ngaHtml = HtmlConvertFactory.convert(buildHtmlData(threadInfo, row), imageUrls);
        row.threadPostInfo.imageUrlList.addAll(imageUrls);
        row.threadPostInfo.formatHtml = ngaHtml;
    }

    private static HtmlData buildHtmlData(ThreadInfo threadInfo, ThreadPostBean row) {
        HtmlData htmlData = new HtmlData(row.content);
        ThreadPostInfo postInfo = row.threadPostInfo;
        if (threadInfo.basicInfo != null) {
            htmlData.attachmentHost = threadInfo.basicInfo.attachHost;
        }
        htmlData.setAlertInfo(postInfo.alterInfo);
        htmlData.setDarkMode(ThemeManager.getInstance().isNightMode());
        htmlData.setInBackList(row.threadPostInfo.isBlocked);
        htmlData.setTextSize(PhoneConfiguration.getInstance().getTopicContentSize());
        htmlData.setEmotionSize(PhoneConfiguration.getInstance().getEmoticonSize());
        htmlData.setSignature(PhoneConfiguration.getInstance().isShowSignature() ? postInfo.signature : null);
        htmlData.setVote(postInfo.vote);
        htmlData.setSubject(postInfo.subject);
        htmlData.setShowImage(PhoneConfiguration.getInstance().isImageLoadEnabled());
        htmlData.setNGAHost(Utils.getNGAHost());
        htmlData.pid = String.valueOf(postInfo.pid);
        htmlData.tid = String.valueOf(postInfo.tid);
        htmlData.uid = String.valueOf(postInfo.authorId);
        if (row.attachs != null) {
            List<AttachmentData> attachments = new ArrayList<>();
            for (Map.Entry<String, ThreadAttachBean> entry : row.attachs.entrySet()) {
                AttachmentData data = new AttachmentData();
                data.setAttachUrl(entry.getValue().attachurl);
                data.setThumb(entry.getValue().thumb);
                data.setAttachmentHost(htmlData.attachmentHost);
                attachments.add(data);
            }
            htmlData.setAttachmentList(attachments);
        }

        if (row.comments != null) {
            List<CommentData> comments = new ArrayList<>();
            for (ThreadPostBean value : row.comments) {
                CommentData comment = new CommentData();
                comment.setAuthor(value.threadPostInfo.author);
                comment.setContent(value.threadPostInfo.rawContent);
                comment.setPostTime(value.threadPostInfo.postDate);
                comment.setAvatarUrl(value.threadPostInfo.avatarUrl);
                comments.add(comment);
            }
            htmlData.setCommentList(comments);
        }
        return htmlData;
    }

    //解析贴条
    private static void buildRowComment(ThreadInfo threadInfo,ThreadBean threadBean, ThreadPostBean row) {
        row.comments = convertJsObjToList(threadInfo, threadBean, row.comment, row.comment.size());
    }

    private static void buildRowUserInfo(ThreadBean threadBean, ThreadPostInfo row) {
        if (row.authorId == 0) {
            return;
        }
        int uid = row.authorId;
        row.isBlocked = UserManagerImpl.getInstance().checkBlackList(String.valueOf(uid));
        ThreadInfoParse.INSTANCE.parseUserInfo(threadBean, row);
    }

}

