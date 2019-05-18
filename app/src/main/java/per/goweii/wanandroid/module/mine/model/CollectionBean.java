package per.goweii.wanandroid.module.mine.model;

import java.util.List;

import per.goweii.rxhttp.request.base.BaseBean;

/**
 * @author CuiZhen
 * @date 2019/5/17
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public class CollectionBean extends BaseBean {
    /**
     * curPage : 1
     * datas : [{"author":"承香墨影","chapterId":411,"chapterName":"承香墨影","courseId":13,"desc":"","envelopePic":"","id":57677,"link":"http://mp.weixin.qq.com/s?__biz=MzIxNjc0ODExMA==&mid=2247484864&idx=1&sn=6d36b7d2e505794e7230a819b70bbb58&chksm=97851ce1a0f295f7383e44542b0cbe2f3adaffc9dd3a2fd1a5b68475071cc38c97f880245018&scene=38#wechat_redirect","niceDate":"2019-04-23","origin":"","originId":4723,"publishTime":1555981613000,"title":"聊聊 MD 设计里，阴影的那些事儿（二）","userId":20382,"visible":0,"zan":0},{"author":"承香墨影","chapterId":411,"chapterName":"承香墨影","courseId":13,"desc":"","envelopePic":"","id":57612,"link":"http://mp.weixin.qq.com/s?__biz=MzIxNjc0ODExMA==&mid=2247485209&idx=1&sn=4ad594e5707ea8092f0fc226ce49d62a&chksm=97851e38a0f2972e7b97281d5d1f6254b8867308dd60ed8af39f51a3a1a5ae00433f1e635534&scene=38#wechat_redirect","niceDate":"2019-04-23","origin":"","originId":4960,"publishTime":1555979277000,"title":"彻底理解 Android MD 设计里的 \u201c阴影\u201d","userId":20382,"visible":0,"zan":0},{"author":"玉刚说","chapterId":410,"chapterName":"玉刚说","courseId":13,"desc":"","envelopePic":"","id":55215,"link":"https://mp.weixin.qq.com/s/SFBV00qjncyLoyp46FwuuA","niceDate":"2019-04-02","origin":"","originId":8110,"publishTime":1554178545000,"title":"推荐一个强大的毛玻璃特效开源库","userId":20382,"visible":0,"zan":0},{"author":"RmondJone","chapterId":357,"chapterName":"表格类","courseId":13,"desc":"Android自定义表格，支持锁双向表头，自适应列宽，自适应行高,快速集成。","envelopePic":"http://www.wanandroid.com/blogimgs/44cfdbbd-9128-4cb3-a45f-331c759dbd45.png","id":54711,"link":"http://www.wanandroid.com/blog/show/2135","niceDate":"2019-03-30","origin":"","originId":2935,"publishTime":1553907711000,"title":"Android自定义表格 LockTableView","userId":20382,"visible":0,"zan":0},{"author":"GcsSloop","chapterId":314,"chapterName":"RV列表动效","courseId":13,"desc":"具有分页功能的 Recyclerview 布局管理器，主打分页，可以替代部分场景下的网格布局，线性布局，以及一些简单的ViewPager，但也有一定的局限性，请选择性使用。\r\n\r\n","envelopePic":"http://www.wanandroid.com/blogimgs/d65890f3-0a09-4b9c-93b0-778caa31b2aa.gif","id":54710,"link":"http://www.wanandroid.com/blog/show/2106","niceDate":"2019-03-30","origin":"","originId":2800,"publishTime":1553907234000,"title":"Android 网格分页布局 pager-layoutmanager","userId":20382,"visible":0,"zan":0},{"author":"yangchong211","chapterId":294,"chapterName":"完整项目","courseId":13,"desc":"组件化综合案例，包含的模块：wanAndroid【kotlin】+干货集中营+知乎日报+番茄Todo+微信精选新闻+豆瓣音乐电影小说+小说读书+简易记事本+搞笑视频+经典游戏+其他更多等等","envelopePic":"http://www.wanandroid.com/blogimgs/92f34dfb-a3ae-44fe-ad1a-d1ff22c773e5.png","id":54664,"link":"http://www.wanandroid.com/blog/show/2517","niceDate":"2019-03-29","origin":"","originId":8028,"publishTime":1553861275000,"title":"组件化综合训练案例","userId":20382,"visible":0,"zan":0},{"author":"HokoFly","chapterId":358,"chapterName":"项目基础功能","courseId":13,"desc":"给图片添加模糊效果；动态模糊，对背景的实时模糊","envelopePic":"http://www.wanandroid.com/blogimgs/4f09e637-6e3b-45f0-a88e-ec5bfff4599c.png","id":54459,"link":"http://www.wanandroid.com/blog/show/2345","niceDate":"2019-03-28","origin":"","originId":3389,"publishTime":1553784438000,"title":"动态模糊组件 HokoBlur ","userId":20382,"visible":0,"zan":0}]
     * offset : 0
     * over : true
     * pageCount : 1
     * size : 20
     * total : 7
     */

    private int curPage;
    private int offset;
    private boolean over;
    private int pageCount;
    private int size;
    private int total;
    private List<DatasBean> datas;

    public int getCurPage() {
        return curPage;
    }

    public void setCurPage(int curPage) {
        this.curPage = curPage;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public boolean isOver() {
        return over;
    }

    public void setOver(boolean over) {
        this.over = over;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<DatasBean> getDatas() {
        return datas;
    }

    public void setDatas(List<DatasBean> datas) {
        this.datas = datas;
    }

    public static class DatasBean {
        /**
         * author : 承香墨影
         * chapterId : 411
         * chapterName : 承香墨影
         * courseId : 13
         * desc :
         * envelopePic :
         * id : 57677
         * link : http://mp.weixin.qq.com/s?__biz=MzIxNjc0ODExMA==&mid=2247484864&idx=1&sn=6d36b7d2e505794e7230a819b70bbb58&chksm=97851ce1a0f295f7383e44542b0cbe2f3adaffc9dd3a2fd1a5b68475071cc38c97f880245018&scene=38#wechat_redirect
         * niceDate : 2019-04-23
         * origin :
         * originId : 4723
         * publishTime : 1555981613000
         * title : 聊聊 MD 设计里，阴影的那些事儿（二）
         * userId : 20382
         * visible : 0
         * zan : 0
         */

        private String author;
        private int chapterId;
        private String chapterName;
        private int courseId;
        private String desc;
        private String envelopePic;
        private int id;
        private String link;
        private String niceDate;
        private String origin;
        private int originId;
        private long publishTime;
        private String title;
        private int userId;
        private int visible;
        private int zan;

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public int getChapterId() {
            return chapterId;
        }

        public void setChapterId(int chapterId) {
            this.chapterId = chapterId;
        }

        public String getChapterName() {
            return chapterName;
        }

        public void setChapterName(String chapterName) {
            this.chapterName = chapterName;
        }

        public int getCourseId() {
            return courseId;
        }

        public void setCourseId(int courseId) {
            this.courseId = courseId;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getEnvelopePic() {
            return envelopePic;
        }

        public void setEnvelopePic(String envelopePic) {
            this.envelopePic = envelopePic;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public String getNiceDate() {
            return niceDate;
        }

        public void setNiceDate(String niceDate) {
            this.niceDate = niceDate;
        }

        public String getOrigin() {
            return origin;
        }

        public void setOrigin(String origin) {
            this.origin = origin;
        }

        public int getOriginId() {
            return originId;
        }

        public void setOriginId(int originId) {
            this.originId = originId;
        }

        public long getPublishTime() {
            return publishTime;
        }

        public void setPublishTime(long publishTime) {
            this.publishTime = publishTime;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public int getVisible() {
            return visible;
        }

        public void setVisible(int visible) {
            this.visible = visible;
        }

        public int getZan() {
            return zan;
        }

        public void setZan(int zan) {
            this.zan = zan;
        }
    }
}
