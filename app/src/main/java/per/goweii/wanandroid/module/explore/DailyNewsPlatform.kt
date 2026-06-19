package per.goweii.wanandroid.module.explore

enum class DailyNewsPlatform(
    val platformName: String,
    val platformCode: String,
    val contentCategory: String,
) {
    BAIDU("百度热搜", "baidu", "社会热点、娱乐、事件"),
    SSPAI("少数派", "shaoshupai", "科技、数码、生活方式"),
    WEIBO("微博热搜", "weibo", "社交媒体热点、娱乐、事件"),
    ZHIHU("知乎热榜", "zhihu", "问答、深度内容、社会热点"),
    TSKR("36氪", "36kr", "科技创业、商业资讯"),
    FTPOJIE("吾爱破解", "52pojie", "技术、软件、安全"),
    BILIBILI("哔哩哔哩", "bilibili", "视频、动漫、游戏、生活"),
    DOUBAN("豆瓣", "douban", "书影音、文化、讨论"),
    HUPU("虎扑", "hupu", "体育、游戏、数码"),
    TIEBA("百度贴吧", "tieba", "兴趣社区、话题讨论"),
    JUEJIN("掘金", "juejin", "编程、技术文章"),
    DOUYIN("抖音", "douyin", "短视频热点、娱乐"),
    VTEX("V2EX", "v2ex", "技术、编程、创意"),
    JINRITOUTIAO("今日头条", "jinritoutiao", "新闻、热点事件"),
    STACKOVERFLOW("Stack Overflow", "stackoverflow", "编程问答、技术讨论"),
    GITHUB("GitHub Trending", "github", "开源项目、编程语言"),
    HACKERNEWS("Hacker News", "hackernews", "科技新闻、创业、编程"),
    SINA_FINANCE("新浪财经", "sina_finance", "财经新闻、股市资讯"),
    EASTMONEY("东方财富", "eastmoney", "财经资讯、投资理财"),
    XUEQIU("雪球", "xueqiu", "股票投资、财经社区"),
    CLS("财联社", "cls", "财经快讯、市场动态"),
    TENXUNWANG("腾讯网", "tenxunwang", "综合新闻、娱乐、科技"),
    ;

    companion object {
        val _code2Platform: Map<String, DailyNewsPlatform> by lazy {
            DailyNewsPlatform.entries.associateBy { it.platformCode }
        }

        @JvmStatic
        fun fromCode(value: String): DailyNewsPlatform {
            return _code2Platform[value] ?: JUEJIN
        }

        @JvmStatic
        fun fromCodeOrNull(value: String): DailyNewsPlatform? {
            return _code2Platform[value]
        }
    }
}