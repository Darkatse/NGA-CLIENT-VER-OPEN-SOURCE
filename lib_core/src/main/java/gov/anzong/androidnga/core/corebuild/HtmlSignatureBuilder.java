package gov.anzong.androidnga.core.corebuild;

import android.text.TextUtils;

import com.client.androidnga.core.data.html.HtmlData;
import gov.anzong.androidnga.core.decode.ForumDecoder;

/**
 *
 * @author Justwen
 * @date 2018/8/28
 */
public class HtmlSignatureBuilder implements IHtmlBuild {

    private static final String HTML_VOTE = "<br/></br>签名<hr/><br/>%s";

    @Override
    public CharSequence build(HtmlData htmlData) {
        if (TextUtils.isEmpty(htmlData.getSignature())) {
            return "";
        } else {
            return String.format(HTML_VOTE, ForumDecoder.decode(htmlData.getSignature(), htmlData));
        }
    }
}
