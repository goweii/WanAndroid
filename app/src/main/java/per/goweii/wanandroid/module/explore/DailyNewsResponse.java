package per.goweii.wanandroid.module.explore;

import java.util.List;

import per.goweii.rxhttp.request.base.BaseResponse;

public class DailyNewsResponse implements BaseResponse<List<DailyNewsBean>> {
    private String status;
    private List<DailyNewsBean> data;
    private String msg;
    private String date;

    @Override
    public int getCode() {
        return Integer.parseInt(status);
    }

    @Override
    public void setCode(int code) {
        status = String.valueOf(code);
    }

    @Override
    public List<DailyNewsBean> getData() {
        return data;
    }

    @Override
    public void setData(List<DailyNewsBean> data) {
        this.data = data;
    }

    @Override
    public String getMsg() {
        return msg;
    }

    @Override
    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
