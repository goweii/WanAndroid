package per.goweii.wanandroid.module.mine.model;

import per.goweii.rxhttp.request.base.BaseBean;

/**
 * @author CuiZhen
 * @date 2019/5/23
 * GitHub: https://github.com/goweii
 */
public class AboutMeBean extends BaseBean {
    /**
     * icon : https://avatars3.githubusercontent.com/u/5456892
     * name : Goweii
     * desc : 不相信自己的人没有努力的价值
     * github : https://github.com/goweii
     * jianshu : https://www.jianshu.com/u/78fecab193fa
     * qq : 302833254
     * qq_group : 147715512
     * qq_qrcode : https://raw.githubusercontent.com/goweii/WanAndroidServer/master/about/qq_qrcode.png
     * wx_qrcode : https://raw.githubusercontent.com/goweii/WanAndroidServer/master/about/wx_qrcode.png
     */

    private String icon;
    private String name;
    private String desc;
    private String github;
    private String jianshu;
    private String qq;
    private String qq_group;
    private String qq_group_key;
    private String qq_qrcode;
    private String wx_qrcode;

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getGithub() {
        return github;
    }

    public void setGithub(String github) {
        this.github = github;
    }

    public String getJianshu() {
        return jianshu;
    }

    public void setJianshu(String jianshu) {
        this.jianshu = jianshu;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getQq_group() {
        return qq_group;
    }

    public void setQq_group(String qq_group) {
        this.qq_group = qq_group;
    }

    public String getQq_group_key() {
        return qq_group_key;
    }

    public void setQq_group_key(String qq_group_key) {
        this.qq_group_key = qq_group_key;
    }

    public String getQq_qrcode() {
        return qq_qrcode;
    }

    public void setQq_qrcode(String qq_qrcode) {
        this.qq_qrcode = qq_qrcode;
    }

    public String getWx_qrcode() {
        return wx_qrcode;
    }

    public void setWx_qrcode(String wx_qrcode) {
        this.wx_qrcode = wx_qrcode;
    }
}
