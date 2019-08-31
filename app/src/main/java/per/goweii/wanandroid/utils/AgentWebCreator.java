package per.goweii.wanandroid.utils;

import android.app.Activity;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.just.agentweb.AgentWeb;
import com.just.agentweb.DefaultWebClient;
import com.just.agentweb.WebChromeClient;
import com.just.agentweb.WebViewClient;

import per.goweii.basic.utils.ResUtils;
import per.goweii.wanandroid.R;

/**
 * @author CuiZhen
 * @date 2019/8/31
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public class AgentWebCreator {

    public static AgentWeb create(Activity activity,
                                  FrameLayout container,
                                  String url,
                                  WebChromeClient webChromeClient,
                                  WebViewClient webViewClient) {
        return AgentWeb.with(activity)
                .setAgentWebParent(container, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
                .useDefaultIndicator(ResUtils.getColor(R.color.accent), 1)
                .interceptUnkownUrl()
                .setSecurityType(AgentWeb.SecurityType.STRICT_CHECK)
                .setMainFrameErrorView(R.layout.layout_agent_web_error, R.id.iv_404)
                .setOpenOtherPageWays(DefaultWebClient.OpenOtherPageWays.ASK)
                .setWebChromeClient(webChromeClient == null ? new WebChromeClient() : webChromeClient)
                .setWebViewClient(webViewClient == null ? new WebViewClient() : webViewClient)
                .createAgentWeb()
                .ready()
                .go(url);
    }

    public static AgentWeb create(Activity activity, FrameLayout container, String url) {
        return create(activity, container, url, null, null);
    }

}
