package per.goweii.wanandroid.module.mine.model;

import java.util.List;

import per.goweii.rxhttp.request.base.BaseBean;

/**
 * @author CuiZhen
 * @date 2019/8/31
 * GitHub: https://github.com/goweii
 */
public class CoinRecordBean extends BaseBean {
    /**
     * curPage : 1
     * datas : [{"coinCount":31,"date":1567180996000,"desc":"2019-08-31 00:03:16 签到,积分：10 + 21","id":35124,"type":1,"userId":20382,"userName":"goweii"},{"coinCount":30,"date":1567122917000,"desc":"2019-08-30 07:55:17 签到,积分：10 + 20","id":34399,"type":1,"userId":20382,"userName":"goweii"},{"coinCount":29,"date":1567037296000,"desc":"2019-08-29 08:08:16 签到,积分：10 + 19","id":33693,"type":1,"userId":20382,"userName":"goweii"},{"coinCount":28,"date":1566950895000,"desc":"2019-08-28 08:08:15 签到,积分：10 + 18","id":32973,"type":1,"userId":20382,"userName":"goweii"},{"coinCount":27,"date":1566865666000,"desc":"2019-08-27 08:27:46 签到,积分：10 + 17","id":32272,"type":1,"userId":20382,"userName":"goweii"},{"coinCount":26,"date":1566779329000,"desc":"2019-08-26 08:28:49 签到,积分：10 + 16","id":31593,"type":1,"userId":20382,"userName":"goweii"},{"coinCount":25,"date":1566701830000,"desc":"2019-08-25 10:57:10 签到,积分：10 + 15","id":31390,"type":1,"userId":20382,"userName":"goweii"},{"coinCount":24,"date":1566615064000,"desc":"2019-08-24 10:51:04 签到,积分：10 + 14","id":31117,"type":1,"userId":20382,"userName":"goweii"},{"coinCount":23,"date":1566519830000,"desc":"2019-08-23 08:23:50 签到,积分：10 + 13","id":30404,"type":1,"userId":20382,"userName":"goweii"},{"coinCount":22,"date":1566433133000,"desc":"2019-08-22 08:18:53 签到,积分：10 + 12","id":29802,"type":1,"userId":20382,"userName":"goweii"},{"coinCount":21,"date":1566349229000,"desc":"2019-08-21 09:00:29 签到,积分：10 + 11","id":29359,"type":1,"userId":20382,"userName":"goweii"},{"coinCount":20,"date":1566262893000,"desc":"2019-08-20 09:01:33 签到,积分：10 + 10","id":3377,"type":1,"userId":20382,"userName":"goweii"},{"coinCount":19,"date":1566174931000,"desc":"2019-08-19 08:35:31 签到,积分：10 + 9","id":2880,"type":1,"userId":20382,"userName":"goweii"},{"coinCount":18,"date":1566106309000,"desc":"2019-08-18 13:31:49 签到,积分：10 + 8","id":2724,"type":1,"userId":20382,"userName":"goweii"},{"coinCount":17,"date":1566017466000,"desc":"2019-08-17 12:51:06 签到,积分：10 + 7","id":2562,"type":1,"userId":20382,"userName":"goweii"},{"coinCount":16,"date":1565915218000,"desc":"2019-08-16 08:26:58 签到,积分：10 + 6 ","id":2074,"type":1,"userId":20382,"userName":"goweii"},{"coinCount":15,"date":1565858320000,"desc":"2019-08-15 16:38:40 签到,积分：10 + 5 ","id":1945,"type":1,"userId":20382,"userName":"goweii"},{"coinCount":14,"date":1565742380000,"desc":"2019-08-14 08:26:20 签到,积分：10 + 4 ","id":1189,"type":1,"userId":20382,"userName":"goweii"},{"coinCount":12,"date":1565656114000,"desc":"2019-08-13 08:28:34 签到,积分：10 + 2 ","id":755,"type":1,"userId":20382,"userName":"goweii"},{"coinCount":12,"date":1565656114000,"desc":"2019-08-13 08:28:34 签到,积分：10 + 2 ","id":756,"type":1,"userId":20382,"userName":"goweii"}]
     * offset : 0
     * over : false
     * pageCount : 2
     * size : 20
     * total : 22
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
         * coinCount : 31
         * date : 1567180996000
         * desc : 2019-08-31 00:03:16 签到,积分：10 + 21
         * id : 35124
         * type : 1
         * userId : 20382
         * userName : goweii
         */

        private int coinCount;
        private long date;
        private String desc;
        private int id;
        private int type;
        private int userId;
        private String userName;

        public int getCoinCount() {
            return coinCount;
        }

        public void setCoinCount(int coinCount) {
            this.coinCount = coinCount;
        }

        public long getDate() {
            return date;
        }

        public void setDate(long date) {
            this.date = date;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }
    }
}
