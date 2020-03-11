package per.goweii.wanandroid.utils.wanpwd;

import android.support.annotation.Nullable;

import per.goweii.wanandroid.utils.router.Router;

/**
 * @author CuiZhen
 * @date 2019/12/28
 * GitHub: https://github.com/goweii
 */
public class FestivalWanPwd implements IWanPwd {

    private final String content;
    private String mShowText;
    private String mBtnText;
    private Runnable mRunnable;

    public FestivalWanPwd(String content) {
        this.content = content;
        parse();
    }

    @Nullable
    @Override
    public Runnable getRunnable() {
        return mRunnable;
    }

    @Override
    public String getShowText() {
        return mShowText;
    }

    @Override
    public String getBtnText() {
        return mBtnText;
    }

    private void parse() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < content.length(); i++) {
            char c = content.charAt(i);
            if (c >= '0' && c <= '9') {
                stringBuilder.append(c);
            }
        }
        if (stringBuilder.length() != 8) {
            return;
        }
        try {
            String s = stringBuilder.toString();
            int y = Integer.parseInt(s.substring(0, 4));
            int m = Integer.parseInt(s.substring(4, 6));
            int d = Integer.parseInt(s.substring(6, 8));
            StringBuilder sbShowText = new StringBuilder("8大传统节日你还记得哪些？\n");
            if (m == 12 && d == 30) {
                sbShowText.append("【除夕·农历十二月三十】\n\n");
                sbShowText.append("乾坤空落落，岁月去堂堂；\n");
                sbShowText.append("末路惊风雨，穷边饱雪霜。\n");
                sbShowText.append("命随年欲尽，身与世俱忘；\n");
                sbShowText.append("无复屠苏梦，挑灯夜未央。\n");
            } else if (m == 1 && d == 1) {
                sbShowText.append("【春节·农历正月初一】\n\n");
                sbShowText.append("爆竹声中一岁除，\n");
                sbShowText.append("春风送暖入屠苏。\n");
                sbShowText.append("千门万户曈曈日，\n");
                sbShowText.append("总把新桃换旧符。\n");
            } else if (m == 1 && d == 15) {
                sbShowText.append("【元宵节·农历正月十五】\n\n");
                sbShowText.append("去年元夜时，花市灯如昼。\n");
                sbShowText.append("月到柳梢头，人约黄昏后。\n");
                sbShowText.append("今年元夜时，月与灯依旧。\n");
                sbShowText.append("不见去年人，泪湿春衫袖。\n");
            } else if (m == 3 && d == 3) {
                sbShowText.append("【上巳节·农历三月初三】\n\n");
                sbShowText.append("巳日帝城春，倾都祓禊晨。\n");
                sbShowText.append("停车须傍水，奏乐要惊尘。\n");
                sbShowText.append("弱柳障行骑，浮桥拥看人。\n");
                sbShowText.append("犹言日尚早，更向九龙津。\n");
            } else if (m == 5 && d == 5) {
                sbShowText.append("【端午节·农历五月初五】\n\n");
                sbShowText.append("节分端午自谁言，\n");
                sbShowText.append("万古传闻为屈原；\n");
                sbShowText.append("堪笑楚江空渺渺，\n");
                sbShowText.append("不能洗得直臣冤。\n");
            } else if (m == 7 && d == 7) {
                sbShowText.append("【七夕节·农历七月初七】\n\n");
                sbShowText.append("络角星河菡萏天，\n");
                sbShowText.append("一家欢笑设红筵。\n");
                sbShowText.append("应倾谢女珠玑箧，\n");
                sbShowText.append("尽写檀郎锦绣篇。\n");
                sbShowText.append("香帐簇成排窈窕，\n");
                sbShowText.append("金针穿罢拜婵娟。\n");
                sbShowText.append("铜壶漏报天将晓，\n");
                sbShowText.append("惆怅佳期又一年。\n");
            } else if (m == 8 && d == 15) {
                sbShowText.append("【中秋节·农历八月十五】\n\n");
                sbShowText.append("暮云收尽溢清寒，\n");
                sbShowText.append("银汉无声转玉盘。\n");
                sbShowText.append("此生此夜不长好，\n");
                sbShowText.append("明月明年何处看。\n");
            } else if (m == 9 && d == 9) {
                sbShowText.append("【重阳节·农历九月初九】\n\n");
                sbShowText.append("独在异乡为异客，\n");
                sbShowText.append("每逢佳节倍思亲。\n");
                sbShowText.append("遥知兄弟登高处，\n");
                sbShowText.append("遍插茱萸少一人。\n");
            } else {
                mShowText = "你发现了一个节日口令！\n但是我不知道这是什么节日！\n"
                        + "【农历" + m + "月" + d + "日】";
                mBtnText = "我去查查";
                mRunnable = new Runnable() {
                    @Override
                    public void run() {
                        Router.router("https://www.baidu.com/s?wd=" + "农历" + m + "月" + d + "日");
                    }
                };
            }
            if (mShowText == null && mBtnText == null) {
                mShowText = sbShowText
                        .append("\n祝你快乐每一天！")
                        .toString();
                mBtnText = "我知道了";
            }
        } catch (Exception ignore) {
            mShowText = "你发现了一个节日口令，但是好像没有这个节日！";
            mBtnText = "关闭";
        }
    }
}
