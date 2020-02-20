package per.goweii.wanandroid.module.main.model;

import per.goweii.rxhttp.request.base.BaseBean;

/**
 * @author CuiZhen
 * @date 2019/10/3
 * GitHub: https://github.com/goweii
 */
public class CoinInfoBean extends BaseBean {
    /**
     * coinCount : 1285
     * rank : 6
     * userId : 2
     * username : x**oyang
     */

    private int coinCount;
    private int rank;
    private int userId;
    private String username;
    public boolean anim;

    public int getCoinCount() {
        return coinCount;
    }

    public void setCoinCount(int coinCount) {
        this.coinCount = coinCount;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
