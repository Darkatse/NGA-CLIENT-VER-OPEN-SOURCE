package gov.anzong.androidnga.core;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import gov.anzong.androidnga.common.util.FileUtils;
import gov.anzong.androidnga.core.corebuild.HtmlBuilder;

import com.client.androidnga.core.data.bean.ThreadAttachBean;
import com.client.androidnga.core.data.bean.ThreadPostBean;
import com.client.androidnga.core.data.html.AttachmentData;
import com.client.androidnga.core.data.html.CommentData;
import com.client.androidnga.core.data.html.HtmlData;
import com.client.androidnga.core.data.model.ThreadBasicInfo;
import com.client.androidnga.core.data.model.ThreadInfo;
import com.client.androidnga.core.data.model.ThreadPostInfo;

import gov.anzong.androidnga.core.decode.ForumDecoder;

public class HtmlConvertFactory {

    private volatile static String sHtmlTemplate;

    static {
        sHtmlTemplate = FileUtils.readAssetToString("html/html_template.html");
    }

    public static String convert(ThreadPostInfo row, ThreadBasicInfo basicInfo, IHtmlConfigService config) {
        HtmlData htmlData = new HtmlData(row.rawContent);
        if (basicInfo != null) {
            htmlData.attachmentHost = basicInfo.attachHost;
        }
        htmlData.setAlertInfo(row.alterInfo);
        htmlData.setDarkMode(config.isDarkMode());
        htmlData.setInBackList(row.isBlocked);
     //   htmlData.setTextSize(PhoneConfiguration.getInstance().getTopicContentSize());
        htmlData.setEmotionSize(config.getEmoticonSize());
        htmlData.setSignature(config.isShowSignature() ? row.signature : null);
        htmlData.setVote(row.vote);
        htmlData.setSubject(row.subject);
        htmlData.setShowImage(config.isImageEnabled());
        htmlData.setNGAHost(config.getNGAHost());
        htmlData.pid = String.valueOf(row.pid);
        htmlData.tid = String.valueOf(row.tid);
        htmlData.uid = String.valueOf(row.authorId);
        htmlData.setAttachmentList(row.attachInfo);

        List<CommentData> comments = new ArrayList<>();
        for (ThreadPostInfo value : row.comments) {
            CommentData comment = new CommentData();
            comment.setAuthor(value.author);
            comment.setContent(value.rawContent);
            comment.setPostTime(value.postDate);
            comment.setAvatarUrl(value.avatarUrl);
            comments.add(comment);
        }
        htmlData.setCommentList(comments);
        return convert(htmlData, row.imageUrlList);
    }

    public static String convert(HtmlData htmlData, List<String> images) {

        StringBuilder builder = new StringBuilder();

        if (htmlData.isInBackList()) {
            builder.append("<h5>[屏蔽]</h5>");
        } else if (TextUtils.isEmpty(htmlData.getAlertInfo()) && TextUtils.isEmpty(htmlData.getRawData())) {
            builder.append("<h5>[隐藏]</h5>");
        } else {
            if (!TextUtils.isEmpty(htmlData.getSubject())) {
                builder.append(String.format("<div class='title'>%s</div><br>", htmlData.getSubject()));
            }
            String ngaHtml = ForumDecoder.decode(htmlData.getRawData(), htmlData, images);
            if (TextUtils.isEmpty(ngaHtml)) {
                ngaHtml = htmlData.getAlertInfo();
            }
            builder.append(ngaHtml);
            HtmlBuilder.build(builder, htmlData, images);
        }

        String html = builder.toString();
        String style = htmlData.isDarkMode() ? "style_dark.css" : "style_light.css";
        return String.format(sHtmlTemplate, style, html);
    }


}
