package per.goweii.wanandroid.module.main.model;

import java.util.List;

import per.goweii.rxhttp.request.base.BaseBean;

/**
 * @author CuiZhen
 * @date 2019/11/10
 * GitHub: https://github.com/goweii
 */
public class JinrishiciBean extends BaseBean {
    /**
     * id : 5b8b9572e116fb3714e70966
     * content : 竹里缲丝挑网车，青蝉独噪日光斜。
     * popularity : 2720
     * origin : {"title":"南园十三首","dynasty":"唐代","author":"李贺","content":["花枝草蔓眼中开，小白长红越女腮。","可怜日暮嫣香落，嫁与春风不用媒。","宫北田塍晓气酣，黄桑饮露窣宫帘。","长腰健妇偷攀折，将喂吴王八茧蚕。","竹里缲丝挑网车，青蝉独噪日光斜。","桃胶迎夏香琥珀，自课越佣能种瓜。","三十未有二十余，白日长饥小甲蔬。","桥头长老相哀念，因遗戎韬一卷书。","男儿何不带吴钩，收取关山五十州。","请君暂上凌烟阁，若个书生万户侯？","寻章摘句老雕虫，晓月当帘挂玉弓。","不见年年辽海上，文章何处哭秋风？","长卿牢落悲空舍，曼倩诙谐取自容。","见买若耶溪水剑，明朝归去事猿公。","春水初生乳燕飞，黄蜂小尾扑花归。","窗含远色通书幌，鱼拥香钩近石矶。","泉沙软卧鸳鸯暖，曲岸回篙舴艋迟。","泻酒木栏椒叶盖，病容扶起种菱丝。","边让今朝忆蔡邕，无心裁曲卧春风。","舍南有竹堪书字，老去溪头作钓翁。","长峦谷口倚嵇家，白昼千峰老翠华。","自履藤鞋收石蜜，手牵苔絮长莼花。","松溪黑水新龙卵，桂洞生硝旧马牙。","谁遣虞卿裁道帔，轻绡一匹染朝霞。","小树开朝径，长茸湿夜烟。","柳花惊雪浦，麦雨涨溪田。","古刹疏钟度，遥岚破月悬。","沙头敲石火，烧竹照渔船。"],"translate":null}
     * matchTags : ["白天"]
     * recommendedReason :
     * cacheAt : 2019-11-10T15:24:20.765760
     */

    private String id;
    private String content;
    private int popularity;
    private OriginBean origin;
    private String recommendedReason;
    private String cacheAt;
    private List<String> matchTags;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getPopularity() {
        return popularity;
    }

    public void setPopularity(int popularity) {
        this.popularity = popularity;
    }

    public OriginBean getOrigin() {
        return origin;
    }

    public void setOrigin(OriginBean origin) {
        this.origin = origin;
    }

    public String getRecommendedReason() {
        return recommendedReason;
    }

    public void setRecommendedReason(String recommendedReason) {
        this.recommendedReason = recommendedReason;
    }

    public String getCacheAt() {
        return cacheAt;
    }

    public void setCacheAt(String cacheAt) {
        this.cacheAt = cacheAt;
    }

    public List<String> getMatchTags() {
        return matchTags;
    }

    public void setMatchTags(List<String> matchTags) {
        this.matchTags = matchTags;
    }

    public static class OriginBean {
        /**
         * title : 南园十三首
         * dynasty : 唐代
         * author : 李贺
         * content : ["花枝草蔓眼中开，小白长红越女腮。","可怜日暮嫣香落，嫁与春风不用媒。","宫北田塍晓气酣，黄桑饮露窣宫帘。","长腰健妇偷攀折，将喂吴王八茧蚕。","竹里缲丝挑网车，青蝉独噪日光斜。","桃胶迎夏香琥珀，自课越佣能种瓜。","三十未有二十余，白日长饥小甲蔬。","桥头长老相哀念，因遗戎韬一卷书。","男儿何不带吴钩，收取关山五十州。","请君暂上凌烟阁，若个书生万户侯？","寻章摘句老雕虫，晓月当帘挂玉弓。","不见年年辽海上，文章何处哭秋风？","长卿牢落悲空舍，曼倩诙谐取自容。","见买若耶溪水剑，明朝归去事猿公。","春水初生乳燕飞，黄蜂小尾扑花归。","窗含远色通书幌，鱼拥香钩近石矶。","泉沙软卧鸳鸯暖，曲岸回篙舴艋迟。","泻酒木栏椒叶盖，病容扶起种菱丝。","边让今朝忆蔡邕，无心裁曲卧春风。","舍南有竹堪书字，老去溪头作钓翁。","长峦谷口倚嵇家，白昼千峰老翠华。","自履藤鞋收石蜜，手牵苔絮长莼花。","松溪黑水新龙卵，桂洞生硝旧马牙。","谁遣虞卿裁道帔，轻绡一匹染朝霞。","小树开朝径，长茸湿夜烟。","柳花惊雪浦，麦雨涨溪田。","古刹疏钟度，遥岚破月悬。","沙头敲石火，烧竹照渔船。"]
         * translate : null
         */

        private String title;
        private String dynasty;
        private String author;
        private Object translate;
        private List<String> content;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDynasty() {
            return dynasty;
        }

        public void setDynasty(String dynasty) {
            this.dynasty = dynasty;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public Object getTranslate() {
            return translate;
        }

        public void setTranslate(Object translate) {
            this.translate = translate;
        }

        public List<String> getContent() {
            return content;
        }

        public void setContent(List<String> content) {
            this.content = content;
        }
    }
}
