package per.goweii.wanandroid.module.main.model;

import androidx.annotation.Nullable;

import java.util.List;

import per.goweii.rxhttp.request.base.BaseBean;

public class RecommendBean extends BaseBean {
    @Nullable
    private List<BannerBean> bannerList;
    @Nullable
    private ArticleFooter articleFooter;

    @Nullable
    public List<BannerBean> getBannerList() {
        return bannerList;
    }

    public void setBannerList(@Nullable List<BannerBean> bannerList) {
        this.bannerList = bannerList;
    }

    @Nullable
    public ArticleFooter getArticleFooter() {
        return articleFooter;
    }

    public void setArticleFooter(@Nullable ArticleFooter articleFooter) {
        this.articleFooter = articleFooter;
    }

    public static class BannerBean {
        @Nullable
        private String title;
        @Nullable
        private String url;
        @Nullable
        private String route;

        @Nullable
        public String getTitle() {
            return title;
        }

        public void setTitle(@Nullable String title) {
            this.title = title;
        }

        @Nullable
        public String getUrl() {
            return url;
        }

        public void setUrl(@Nullable String url) {
            this.url = url;
        }

        @Nullable
        public String getRoute() {
            return route;
        }

        public void setRoute(@Nullable String route) {
            this.route = route;
        }
    }

    public static class ArticleFooter {
        @Nullable
        private String title;
        @Nullable
        private String url;
        @Nullable
        private String route;

        @Nullable
        public String getTitle() {
            return title;
        }

        public void setTitle(@Nullable String title) {
            this.title = title;
        }

        @Nullable
        public String getUrl() {
            return url;
        }

        public void setUrl(@Nullable String url) {
            this.url = url;
        }

        @Nullable
        public String getRoute() {
            return route;
        }

        public void setRoute(@Nullable String route) {
            this.route = route;
        }
    }
}
