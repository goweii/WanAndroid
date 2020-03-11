package per.goweii.wanandroid.http;

import per.goweii.rxhttp.request.base.BaseResponse;

/**
 * @author CuiZhen
 * @date 2019/5/8
 * GitHub: https://github.com/goweii
 */
public class WanResponse<E> implements BaseResponse<E> {

    private int errorCode;
    private String errorMsg;
    private E data;

    @Override
    public int getCode() {
        return errorCode;
    }

    @Override
    public void setCode(int code) {
        errorCode = code;
    }

    @Override
    public E getData() {
        return data;
    }

    @Override
    public void setData(E data) {
        this.data = data;
    }

    @Override
    public String getMsg() {
        return errorMsg;
    }

    @Override
    public void setMsg(String msg) {
        errorMsg = msg;
    }
}
