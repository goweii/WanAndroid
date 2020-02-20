package per.goweii.wanandroid.http;

import java.util.List;

import io.reactivex.Observable;
import per.goweii.rxhttp.request.Api;
import per.goweii.rxhttp.request.base.BaseBean;
import per.goweii.wanandroid.module.home.model.BannerBean;
import per.goweii.wanandroid.module.home.model.HotKeyBean;
import per.goweii.wanandroid.module.login.model.LoginBean;
import per.goweii.wanandroid.module.main.model.ArticleBean;
import per.goweii.wanandroid.module.main.model.ArticleListBean;
import per.goweii.wanandroid.module.main.model.ChapterBean;
import per.goweii.wanandroid.module.main.model.CollectionLinkBean;
import per.goweii.wanandroid.module.main.model.ConfigBean;
import per.goweii.wanandroid.module.main.model.JinrishiciBean;
import per.goweii.wanandroid.module.main.model.UpdateBean;
import per.goweii.wanandroid.module.main.model.UsefulWebBean;
import per.goweii.wanandroid.module.main.model.UserPageBean;
import per.goweii.wanandroid.module.mine.model.AboutMeBean;
import per.goweii.wanandroid.module.mine.model.CoinRankBean;
import per.goweii.wanandroid.module.mine.model.CoinRecordBean;
import per.goweii.wanandroid.module.mine.model.UserInfoBean;
import per.goweii.wanandroid.module.navigation.model.NaviBean;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * @author CuiZhen
 * @date 2019/5/7
 * GitHub: https://github.com/goweii
 */
public class WanApi extends Api {

    public static ApiService api() {
        return api(ApiService.class);
    }

    public static class ApiCode {
        public static final int ERROR = 1000;

        public static final int SUCCESS = 0;

        public static final int FAILED_NO_CACHE = -9000;  //没有缓存

        public static final int FAILED_NOT_LOGIN = -1001; //请先登录
    }

    public interface ApiService {

        @GET("https://v2.jinrishici.com/token")
        Observable<WanResponse<String>> getJinrishiciToken();

        @GET("https://v2.jinrishici.com/sentence")
        Observable<WanResponse<JinrishiciBean>> getJinrishici(@retrofit2.http.Header("Token") String token);

        @GET("https://gitee.com/goweii/WanAndroidServer/raw/master/update/update.json")
        Observable<WanResponse<UpdateBean>> update();

        @GET("https://gitee.com/goweii/WanAndroidServer/raw/master/about/about_me.json")
        Observable<WanResponse<AboutMeBean>> getAboutMe();

        @GET("https://gitee.com/goweii/WanAndroidServer/raw/master/config/config.json")
        Observable<WanResponse<ConfigBean>> getConfig();

        /**
         * 登录
         * 方法： POST
         * 参数：
         * username，password
         * 登录后会在cookie中返回账号密码，只要在客户端做cookie持久化存储即可自动登录验证。
         */
        @FormUrlEncoded
        @POST("user/login")
        Observable<WanResponse<LoginBean>> login(@Field("username") String username,
                                                 @Field("password") String password);

        /**
         * 注册
         * 方法： POST
         * 参数：
         * username，password,repassword
         */
        @FormUrlEncoded
        @POST("user/register")
        Observable<WanResponse<LoginBean>> register(@Field("username") String username,
                                                    @Field("password") String password,
                                                    @Field("repassword") String repassword);

        /**
         * 退出
         * 方法： GET
         * 访问了 logout 后，服务端会让客户端清除 Cookie（即cookie max-Age=0），
         * 如果客户端 Cookie 实现合理，可以实现自动清理，如果本地做了用户账号密码和保存，及时清理。
         */
        @GET("user/logout/json")
        Observable<WanResponse<BaseBean>> logout();

        /**
         * 获取公众号列表
         * 方法： GET
         */
        @GET("wxarticle/chapters/json")
        Observable<WanResponse<List<ChapterBean>>> getWxArticleChapters();

        /**
         * 查看某个公众号历史数据
         * 方法：GET
         * 参数：
         * 公众号 ID：拼接在 url 中，eg:405
         * 公众号页码：拼接在 url 中，eg:1
         */
        @GET("wxarticle/list/{id}/{page}/json")
        Observable<WanResponse<ArticleListBean>> getWxArticleList(@Path("id") int id,
                                                                  @Path("page") int page);

        /**
         * 在某个公众号中搜索历史文章
         * 方法：GET
         * 参数：
         * k : 字符串，eg:Java
         * 公众号 ID：拼接在 url 中，eg:405
         * 公众号页码：拼接在 url 中，eg:1
         */
        @GET("wxarticle/list/{id}/{page}/json")
        Observable<WanResponse<ArticleListBean>> getWxArticleList(@Path("id") int id,
                                                                  @Path("page") int page,
                                                                  @Query("k") String key);

        /**
         * 项目分类
         * 方法： GET
         */
        @GET("project/tree/json")
        Observable<WanResponse<List<ChapterBean>>> getProjectChapters();

        /**
         * 项目列表数据
         * 方法：GET
         * 参数：
         * cid 分类的id，上面项目分类接口
         * 页码：拼接在链接中，从1开始。
         */
        @GET("project/list/{page}/json")
        Observable<WanResponse<ArticleListBean>> getProjectArticleList(@Path("page") int page,
                                                                       @Query("cid") int id);

        /**
         * 置顶文章
         * 方法：GET
         */
        @GET("article/top/json")
        Observable<WanResponse<List<ArticleBean>>> getTopArticleList();

        /**
         * 首页文章列表
         * 方法：GET
         * 参数：页码，拼接在连接中，从0开始。
         */
        @GET("article/list/{page}/json")
        Observable<WanResponse<ArticleListBean>> getArticleList(@Path("page") int page);

        /**
         * 首页banner
         */
        @GET("banner/json")
        Observable<WanResponse<List<BannerBean>>> getBanner();

        /**
         * 常用网站
         */
        @GET("friend/json")
        Observable<WanResponse<List<UsefulWebBean>>> getUsefulWebList();

        /**
         * 搜索热词
         */
        @GET("hotkey/json")
        Observable<WanResponse<List<HotKeyBean>>> getHotKeyList();

        /**
         * 搜索
         * 方法：POST
         * 参数：
         * 页码：拼接在链接上，从0开始。
         * k ： 搜索关键词
         * 支持多个关键词，用空格隔开
         */
        @FormUrlEncoded
        @POST("article/query/{page}/json")
        Observable<WanResponse<ArticleListBean>> search(@Path("page") int page,
                                                        @Field("k") String key);

        /**
         * 搜索热词
         */
        @GET("navi/json")
        Observable<WanResponse<List<NaviBean>>> getNaviList();

        /**
         * 体系数据
         */
        @GET("tree/json")
        Observable<WanResponse<List<ChapterBean>>> getKnowledgeList();

        /**
         * 知识体系下的文章
         * 方法：GET
         * 参数：
         * cid 分类的id，上述二级目录的id
         * 页码：拼接在链接上，从0开始。
         */
        @GET("article/list/{page}/json")
        Observable<WanResponse<ArticleListBean>> getKnowledgeArticleList(@Path("page") int page,
                                                                         @Query("cid") int id);

        /**
         * 收藏文章列表
         * 方法：GET
         * 参数： 页码：拼接在链接中，从0开始。
         */
        @GET("lg/collect/list/{page}/json")
        Observable<WanResponse<ArticleListBean>> getCollectArticleList(@Path("page") int page);

        /**
         * 收藏网站列表
         * 方法：GET
         */
        @GET("lg/collect/usertools/json")
        Observable<WanResponse<List<CollectionLinkBean>>> getCollectLinkList();

        /**
         * 收藏站内文章
         * 方法：POST
         * 参数： 文章id，拼接在链接中。
         */
        @POST("lg/collect/{id}/json")
        Observable<WanResponse<BaseBean>> collect(@Path("id") int id);

        /**
         * 收藏站外文章
         * 方法：POST
         * 参数：
         * title，author，link
         */
        @FormUrlEncoded
        @POST("lg/collect/add/json")
        Observable<WanResponse<ArticleBean>> collect(@Field("title") String title,
                                                     @Field("author") String author,
                                                     @Field("link") String link);

        /**
         * 收藏网址
         * 方法：POST
         * 参数：
         * name,link
         */
        @FormUrlEncoded
        @POST("lg/collect/addtool/json")
        Observable<WanResponse<CollectionLinkBean>> collect(@Field("name") String name,
                                                            @Field("link") String link);

        /**
         * 取消收藏 文章列表
         * 方法：POST
         * 参数：
         * id:拼接在链接上 id传入的是列表中文章的id。
         */
        @POST("lg/uncollect_originId/{id}/json")
        Observable<WanResponse<BaseBean>> uncollect(@Path("id") int id);

        /**
         * 删除收藏网站
         * 方法：POST
         * 参数：
         * id
         */
        @FormUrlEncoded
        @POST("lg/collect/deletetool/json")
        Observable<WanResponse<BaseBean>> uncollectLink(@Field("id") int id);

        /**
         * 编辑收藏网站
         * 方法：POST
         * 参数：
         * id,name,link
         */
        @FormUrlEncoded
        @POST("lg/collect/updatetool/json")
        Observable<WanResponse<CollectionLinkBean>> updateCollectLink(@Field("id") int id,
                                                                      @Field("name") String name,
                                                                      @Field("link") String link);

        /**
         * 取消收藏 我的收藏页面（该页面包含自己录入的内容）
         * 方法：POST
         * 参数：
         * id:拼接在链接上
         * originId:列表页下发，无则为-1
         * originId 代表的是你收藏之前的那篇文章本身的id； 但是收藏支持主动添加，这种情况下，没有originId则为-1
         */
        @FormUrlEncoded
        @POST("lg/uncollect/{id}/json")
        Observable<WanResponse<BaseBean>> uncollect(@Path("id") int id,
                                                    @Field("originId") int originId);

        /**
         * 获取个人积分
         */
        @GET("lg/coin/getcount/json")
        Observable<WanResponse<Integer>> getCoin();

        /**
         * 获取个人积分
         */
        @GET("lg/coin/userinfo/json")
        Observable<WanResponse<UserInfoBean>> getUserInfo();

        /**
         * 获取个人积分获取列表
         * page 1开始
         */
        @GET("lg/coin/list/{page}/json")
        Observable<WanResponse<CoinRecordBean>> getCoinRecordList(@Path("page") int page);

        /**
         * 积分排行榜接口
         * page 1开始
         */
        @GET("coin/rank/{page}/json")
        Observable<WanResponse<CoinRankBean>> getCoinRankList(@Path("page") int page);

        /**
         * 广场列表数据
         * 可能出现返回列表数据<每页数据，因为有自见的文章被过滤掉了。
         * page 0开始
         */
        @GET("user_article/list/{page}/json")
        Observable<WanResponse<ArticleListBean>> getUserArticleList(@Path("page") int page);

        /**
         * 分享人对应列表数据
         * page 从1开始
         */
        @GET("user/{userId}/share_articles/{page}/json")
        Observable<WanResponse<UserPageBean>> getUserPage(@Path("userId") int userId,
                                                          @Path("page") int page);

        /**
         * 自己的分享的文章列表
         * 页码，从1开始
         */
        @GET("user/lg/private_articles/{page}/json")
        Observable<WanResponse<UserPageBean>> getMineShareArticleList(@Path("page") int page);

        /**
         * 删除自己分享的文章
         * 文章id，拼接在链接上
         */
        @POST("lg/user_article/delete/{id}/json")
        Observable<WanResponse<BaseBean>> deleteMineShareArticle(@Path("id") int id);

        /**
         * 分享文章
         * 注意需要登录后查看，如果为CSDN，简书等链接会直接通过审核，在对外的分享文章列表中展示。
         * title
         * link
         */
        @FormUrlEncoded
        @POST("lg/user_article/add/json")
        Observable<WanResponse<BaseBean>> shareArticle(@Field("title") String title,
                                                       @Field("link") String link);
    }

}
