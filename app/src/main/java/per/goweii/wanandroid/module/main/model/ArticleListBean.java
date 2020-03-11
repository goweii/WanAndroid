package per.goweii.wanandroid.module.main.model;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.util.ArrayList;
import java.util.List;

import per.goweii.rxhttp.request.base.BaseBean;

/**
 * @author CuiZhen
 * @date 2019/5/12
 * GitHub: https://github.com/goweii
 */
public class ArticleListBean extends BaseBean {
    /**
     * curPage : 2
     * datas : [{"apkLink":"","author":"玉刚说","chapterId":410,"chapterName":"玉刚说","collect":false,"courseId":13,"desc":"","envelopePic":"","fresh":false,"id":8367,"link":"https://mp.weixin.qq.com/s/uI7Fej1_qSJOJnzQ6offpw","niceDate":"2019-05-06","origin":"","prefix":"","projectLink":"","publishTime":1557072000000,"superChapterId":408,"superChapterName":"公众号","tags":[{"name":"公众号","url":"/wxarticle/list/410/1"}],"title":"深扒 EventBus：register","type":0,"userId":-1,"visible":1,"zan":0},{"apkLink":"","author":"鸿洋","chapterId":408,"chapterName":"鸿洋","collect":false,"courseId":13,"desc":"","envelopePic":"","fresh":false,"id":8371,"link":"https://mp.weixin.qq.com/s/8ceAAAt8FEL0Se-8--7CfQ","niceDate":"2019-05-06","origin":"","prefix":"","projectLink":"","publishTime":1557072000000,"superChapterId":408,"superChapterName":"公众号","tags":[{"name":"公众号","url":"/wxarticle/list/408/1"}],"title":"我在一个群分享Android 好像被我分享得没人说话了... 2期","type":0,"userId":-1,"visible":1,"zan":0},{"apkLink":"","author":"三好码农","chapterId":77,"chapterName":"响应式编程","collect":false,"courseId":13,"desc":"","envelopePic":"","fresh":false,"id":8359,"link":"https://juejin.im/post/5b66eeaa6fb9a04fd93e5369","niceDate":"2019-05-05","origin":"","prefix":"","projectLink":"","publishTime":1557063177000,"superChapterId":184,"superChapterName":"热门专题","tags":[],"title":"RxJava2源码解读之 Map、FlatMap","type":0,"userId":-1,"visible":1,"zan":0},{"apkLink":"","author":"玉刚说","chapterId":410,"chapterName":"玉刚说","collect":false,"courseId":13,"desc":"","envelopePic":"","fresh":false,"id":8366,"link":"https://mp.weixin.qq.com/s/MRv1aMJD8XtTEP0keoqerA","niceDate":"2019-05-05","origin":"","prefix":"","projectLink":"","publishTime":1556985600000,"superChapterId":408,"superChapterName":"公众号","tags":[{"name":"公众号","url":"/wxarticle/list/410/1"}],"title":"从零开始实现一个 mini-Retrofit 框架","type":0,"userId":-1,"visible":1,"zan":0},{"apkLink":"","author":"code小生","chapterId":414,"chapterName":"code小生","collect":false,"courseId":13,"desc":"","envelopePic":"","fresh":false,"id":8368,"link":"https://mp.weixin.qq.com/s/_FeFjeCkpPeiPq1I6VoRFw","niceDate":"2019-05-05","origin":"","prefix":"","projectLink":"","publishTime":1556985600000,"superChapterId":408,"superChapterName":"公众号","tags":[{"name":"公众号","url":"/wxarticle/list/414/1"}],"title":"Android 中的红点提示怎么统一实现","type":0,"userId":-1,"visible":1,"zan":0},{"apkLink":"","author":"鸿洋","chapterId":408,"chapterName":"鸿洋","collect":false,"courseId":13,"desc":"","envelopePic":"","fresh":false,"id":8370,"link":"https://mp.weixin.qq.com/s/0EprsJ7sXKmphghMsU3aGw","niceDate":"2019-05-05","origin":"","prefix":"","projectLink":"","publishTime":1556985600000,"superChapterId":408,"superChapterName":"公众号","tags":[{"name":"公众号","url":"/wxarticle/list/408/1"}],"title":"微信自研APM利器Matrix 卡顿分析工具之Trace Canary","type":0,"userId":-1,"visible":1,"zan":0},{"apkLink":"","author":"郭霖","chapterId":409,"chapterName":"郭霖","collect":false,"courseId":13,"desc":"","envelopePic":"","fresh":false,"id":8373,"link":"https://mp.weixin.qq.com/s/5CeZ6NHF6dm3qN6RgzaGDQ","niceDate":"2019-05-05","origin":"","prefix":"","projectLink":"","publishTime":1556985600000,"superChapterId":408,"superChapterName":"公众号","tags":[{"name":"公众号","url":"/wxarticle/list/409/1"}],"title":"程序媛说源码：AsyncTask在子线程创建与调用的那些事儿","type":0,"userId":-1,"visible":1,"zan":0},{"apkLink":"","author":"承香墨影","chapterId":411,"chapterName":"承香墨影","collect":false,"courseId":13,"desc":"","envelopePic":"","fresh":false,"id":8375,"link":"https://mp.weixin.qq.com/s/JndDaQTkmbo05ciT1OVW1w","niceDate":"2019-05-05","origin":"","prefix":"","projectLink":"","publishTime":1556985600000,"superChapterId":408,"superChapterName":"公众号","tags":[{"name":"公众号","url":"/wxarticle/list/411/1"}],"title":"&quot;&quot;转 Int，{} 转 List，还有什么奇葩的 JSON 要容错？| 实战","type":0,"userId":-1,"visible":1,"zan":0},{"apkLink":"","author":"飞吧小蚊子","chapterId":443,"chapterName":"Android 10.0","collect":false,"courseId":13,"desc":"","envelopePic":"","fresh":false,"id":8358,"link":"https://www.jianshu.com/p/77f319ea53aa","niceDate":"2019-05-04","origin":"","prefix":"","projectLink":"","publishTime":1556976828000,"superChapterId":183,"superChapterName":"5.+高新技术","tags":[],"title":"Android Q适配（1）-------图标篇","type":0,"userId":-1,"visible":1,"zan":0},{"apkLink":"","author":"蒙伟","chapterId":31,"chapterName":"Dialog","collect":false,"courseId":13,"desc":"","envelopePic":"","fresh":false,"id":8357,"link":"https://www.jianshu.com/p/5f8e74726eee","niceDate":"2019-05-04","origin":"","prefix":"","projectLink":"","publishTime":1556976695000,"superChapterId":30,"superChapterName":"用户交互","tags":[],"title":"Android带进场退场动画的dialog对话框","type":0,"userId":-1,"visible":1,"zan":0},{"apkLink":"","author":"TeaOf","chapterId":99,"chapterName":"具体案例","collect":false,"courseId":13,"desc":"","envelopePic":"","fresh":false,"id":8356,"link":"https://www.jianshu.com/p/f144d6645877","niceDate":"2019-05-04","origin":"","prefix":"","projectLink":"","publishTime":1556976665000,"superChapterId":94,"superChapterName":"自定义控件","tags":[],"title":"仿写一个QQ空间图片预览Dialog","type":0,"userId":-1,"visible":1,"zan":0},{"apkLink":"","author":"sollian","chapterId":100,"chapterName":"RecyclerView","collect":false,"courseId":13,"desc":"","envelopePic":"","fresh":false,"id":8355,"link":"https://www.jianshu.com/p/4f66c2c71d8c","niceDate":"2019-05-04","origin":"","prefix":"","projectLink":"","publishTime":1556976626000,"superChapterId":183,"superChapterName":"5.+高新技术","tags":[],"title":"RecyclerView#Adapter使用中的两个陷阱","type":0,"userId":-1,"visible":1,"zan":0},{"apkLink":"","author":"Peakmain","chapterId":78,"chapterName":"性能优化","collect":false,"courseId":13,"desc":"","envelopePic":"","fresh":false,"id":8354,"link":"https://www.jianshu.com/p/6d5cddd56d94","niceDate":"2019-05-04","origin":"","prefix":"","projectLink":"","publishTime":1556976553000,"superChapterId":184,"superChapterName":"热门专题","tags":[],"title":"新闻类App （MVP + RxJava + Retrofit+Dagger+ARouter）性能优化之启动优化","type":0,"userId":-1,"visible":1,"zan":0},{"apkLink":"","author":"猛猛的小盆友","chapterId":99,"chapterName":"具体案例","collect":false,"courseId":13,"desc":"","envelopePic":"","fresh":false,"id":8353,"link":"https://www.jianshu.com/p/ec96396a470b","niceDate":"2019-05-04","origin":"","prefix":"","projectLink":"","publishTime":1556976514000,"superChapterId":94,"superChapterName":"自定义控件","tags":[],"title":"灵魂画师，Android绘制流程&mdash;&mdash;Android高级UI","type":0,"userId":-1,"visible":1,"zan":0},{"apkLink":"","author":"Mr_villain","chapterId":332,"chapterName":"嵌套滑动","collect":false,"courseId":13,"desc":"","envelopePic":"","fresh":false,"id":8352,"link":"https://www.jianshu.com/p/20efb9f65494","niceDate":"2019-05-04","origin":"","prefix":"","projectLink":"","publishTime":1556976465000,"superChapterId":183,"superChapterName":"5.+高新技术","tags":[],"title":"Android嵌套滑动机制实战演练","type":0,"userId":-1,"visible":1,"zan":0},{"apkLink":"","author":"流水潺湲","chapterId":89,"chapterName":"app缓存相关","collect":false,"courseId":13,"desc":"","envelopePic":"","fresh":false,"id":8351,"link":"https://www.jianshu.com/p/41b98118decc","niceDate":"2019-05-04","origin":"","prefix":"","projectLink":"","publishTime":1556976402000,"superChapterId":89,"superChapterName":"数据存储","tags":[],"title":"Android缓存机制&mdash;&mdash;一般存储实现","type":0,"userId":-1,"visible":1,"zan":0},{"apkLink":"","author":"都有米 ","chapterId":134,"chapterName":"SurfaceView","collect":false,"courseId":13,"desc":"","envelopePic":"","fresh":false,"id":8350,"link":"https://www.jianshu.com/p/05a8f7e1dd3d","niceDate":"2019-05-04","origin":"","prefix":"","projectLink":"","publishTime":1556976373000,"superChapterId":94,"superChapterName":"自定义控件","tags":[],"title":"为啥从SurfaceView中获取不到图片？","type":0,"userId":-1,"visible":1,"zan":0},{"apkLink":"","author":"若丨寒","chapterId":304,"chapterName":"基础源码","collect":false,"courseId":13,"desc":"","envelopePic":"","fresh":false,"id":8349,"link":"https://www.jianshu.com/p/69dad4eee143","niceDate":"2019-05-04","origin":"","prefix":"","projectLink":"","publishTime":1556976352000,"superChapterId":245,"superChapterName":"Java深入","tags":[],"title":"Java：优雅地处理异常真是一门学问啊！","type":0,"userId":-1,"visible":1,"zan":0},{"apkLink":"","author":"未见哥哥","chapterId":78,"chapterName":"性能优化","collect":false,"courseId":13,"desc":"","envelopePic":"","fresh":false,"id":8348,"link":"https://www.jianshu.com/p/adeeee995bc5","niceDate":"2019-05-04","origin":"","prefix":"","projectLink":"","publishTime":1556976324000,"superChapterId":184,"superChapterName":"热门专题","tags":[],"title":"「性能优化4.0」运行期间检测不合理的图片","type":0,"userId":-1,"visible":1,"zan":0},{"apkLink":"","author":"Keven","chapterId":249,"chapterName":"干货资源","collect":false,"courseId":13,"desc":"","envelopePic":"","fresh":false,"id":8347,"link":"https://juejin.im/post/5cc56099e51d456e8a12f01f","niceDate":"2019-05-04","origin":"","prefix":"","projectLink":"","publishTime":1556975595000,"superChapterId":249,"superChapterName":"干货资源","tags":[],"title":"GitHub 上优质项目整理","type":0,"userId":-1,"visible":1,"zan":0}]
     * offset : 20
     * over : false
     * pageCount : 323
     * size : 20
     * total : 6456
     */

    private int curPage;
    private int offset;
    private boolean over;
    private int pageCount;
    private int size;
    private int total;
    private List<ArticleBean> datas;

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

    public List<ArticleBean> getArticles() {
        return datas;
    }

    public List<MultiItemEntity> getDatas() {
        List<MultiItemEntity> list = new ArrayList<>(datas.size());
        list.addAll(datas);
        return list;
    }

    public void setDatas(List<ArticleBean> datas) {
        this.datas = datas;
    }
}
