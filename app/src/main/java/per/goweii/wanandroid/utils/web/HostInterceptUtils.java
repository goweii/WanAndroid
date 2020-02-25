package per.goweii.wanandroid.utils.web;

import android.text.TextUtils;

import java.util.List;

import per.goweii.wanandroid.module.mine.model.HostEntity;
import per.goweii.wanandroid.utils.SettingUtils;

/**
 * @author CuiZhen
 * @date 2019/9/4
 * GitHub: https://github.com/goweii
 */
public class HostInterceptUtils {

    public static final int TYPE_NOTHING = 0;
    public static final int TYPE_ONLY_WHITE = 1;
    public static final int TYPE_INTERCEPT_BLACK = 2;

    public static String getName(int type) {
        String name = "";
        switch (type) {
            default:
                break;
            case TYPE_NOTHING:
                name = "不拦截";
                break;
            case TYPE_ONLY_WHITE:
                name = "仅白名单";
                break;
            case TYPE_INTERCEPT_BLACK:
                name = "拦截黑名单";
                break;
        }
        return name;
    }

    public static boolean isWhiteHost(String host) {
        List<HostEntity> list = SettingUtils.getInstance().getHostWhiteIntercept();
        for (HostEntity entity : list) {
            if (TextUtils.equals(entity.getHost(), host)) {
                return entity.isEnable();
            }
        }
        return false;
    }

    public static boolean isBlackHost(String host) {
        List<HostEntity> list = SettingUtils.getInstance().getHostBlackIntercept();
        for (HostEntity entity : list) {
            if (TextUtils.equals(entity.getHost(), host)) {
                return entity.isEnable();
            }
        }
        return false;
    }

    public static final String[] WHITE_HOST = new String[]{
            "www.wanandroid.com",
            "study.163.com",
            "juejin.im",
            "www.jianshu.com",
            "mp.weixin.qq.com",
            "blog.csdn.net",
            "github.com",
            "gitee.com",
            "www.oschina.net",
            "developers.google.cn",
            "stackoverflow.com",
            "www.androidweekly.cn",
            "hm.baidu.com",
            "www.github.com",
            "www.csdn.net",
            "toutiao.io",
            "segmentfault.com",
            "www.androiddevtools.cn",
            "developers.googleblog.cn",
            "gank.io",
            "a.codekk.com",
            "xiaozhuanlan.com",
            "www.androidos.net.cn",
            "design.1sters.com",
            "leetcode.com",
            "dl.google.com",
            "jcenter.bintray.com",
            "mvnrepository.com",
            "maven.aliyun.com",
            "tech.meituan.com",
            "joyrun.github.io",
            "kaolamobile.github.io",
            "tech.youzan.com",
            "zhuanlan.zhihu.com",
            "www.apkbus.com",
            "www.chinagdg.com",
            "www.v2ex.com",
            "bbs.51cto.com",
            "www.cnblogs.com",
            "www.ruanyifeng.com",
            "hukai.me",
            "droidyue.com",
            "kymjs.com",
            "weishu.me",
            "gityuan.com",
            "www.gcssloop.com",
            "www.wjdiankong.cn",
            "hencoder.com",
            "www.jowanxu.top",
            "guolei1130.github.io",
            "yifeng.studio",
            "prototypez.github.io",
            "www.androidperformance.com",
            "toughcoder.net",
            "wl9739.github.io",
            "www.androidblog.cn",
            "yrom.net",
            "kaedea.com",
            "yifeiyuan.me",
            "pqpo.me",
            "yutiantina.github.io",
            "blog.imallen.wang",
            "wossoneri.github.io",
            "www.iconfont.cn",
            "tinypng.com",
            "translate.google.cn",
            "www.pdfpai.com",
            "ezgif.com",
            "www.aigei.com",
            "www.imooc.com",
            "www.jikexueyuan.com",
            "ke.qq.com",
            "www.maiziedu.com",
            "mooc.study.163.com",
            "www.nowcoder.com",
            "luo.apkbus.com",
            "mars.apkbus.com",
            "gitbook.cn",
            "androidxref.com",
            "www.upyun.com",
            "www.bmob.cn",
            "fir.im",
            "www.rongcloud.cn",
            "www.easemob.com",
            "www.xfyun.cn",
            "www.faceplusplus.com.cn",
            "www.qiniu.com",
            "www.pgyer.com",
            "bugly.qq.com",
            "developer.baidu.com",
            "www.juhe.cn",
            "dev.mi.com",
            "www.jiguang.cn",
            "tutucloud.com",
            "xg.qq.com",
            "www.umeng.com",
            "readhub.me",
            "wwv.cyzone.cn",
            "36kr.com",
            "www.pingwest.com",
            "sspai.com",
            "www.pmtown.com",
            "www.zhaopin.com",
            "www.lagou.com",
            "cn.100offer.com",
            "www.neitui.me",
            "www.zhipin.com",
            "jiagu.360.cn",
            "dun.163.com",
            "www.ijiami.cn",
            "pay.weixin.qq.com",
            "open.alipay.com",
            "developer.huawei.com",
            "push.baidu.com",
            "sharesdk.mob.com",
            "mobile.umeng.com",
            "lbs.amap.com",
            "lbsyun.baidu.com",
            "lbs.qq.com",
            "cloud.tencent.com",
            "cloud.baidu.com",
            "baichuan.taobao.com",
            "leancloud.cn",
            "x5.tencent.com",
            "baozoumanhua.com",
            "www.chuangkit.com",
            "unsplash.com",
            "iconstore.co",
            "www.uplabs.com",
            "www.lottiefiles.com",
            "cn.data.cmcm.com",
            "mtj.baidu.com",
            "qmuiteam.com",
            "dev.360.cn",
            "tucao.qq.com",
            "docs.qq.com",
            "yuque.com",
            "flutter.link",
            "www.beian.miit.gov.cn",
            "www.baidu.com",
            "www.google.com",
            "www.sogou.com",
            "www.so.com",
            "cn.bing.com",
            "weixin.sogou.com",
            "sug.so.360.cn"
    };

    public static final String[] BLACK_HOST = new String[]{
            "www.taobao.com",
            "www.jd.com",
            "yun.tuisnake.com"
    };
}
