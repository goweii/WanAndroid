package per.goweii.wanandroid.utils.web

import com.tencent.smtt.sdk.WebView

/**
 * @author CuiZhen
 * @date 2020/3/14
 */
data class HitResult(
        private val result: WebView.HitTestResult
) {

    fun getType(): Type {
        return when (result.type) {
            WebView.HitTestResult.ANCHOR_TYPE -> Type.ANCHOR_TYPE
            WebView.HitTestResult.PHONE_TYPE -> Type.PHONE_TYPE
            WebView.HitTestResult.GEO_TYPE -> Type.GEO_TYPE
            WebView.HitTestResult.EMAIL_TYPE -> Type.EMAIL_TYPE
            WebView.HitTestResult.IMAGE_TYPE -> Type.IMAGE_TYPE
            WebView.HitTestResult.IMAGE_ANCHOR_TYPE -> Type.IMAGE_ANCHOR_TYPE
            WebView.HitTestResult.SRC_ANCHOR_TYPE -> Type.SRC_ANCHOR_TYPE
            WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE -> Type.SRC_IMAGE_ANCHOR_TYPE
            WebView.HitTestResult.EDIT_TEXT_TYPE -> Type.EDIT_TEXT_TYPE
            WebView.HitTestResult.UNKNOWN_TYPE -> Type.UNKNOWN_TYPE
            else -> Type.UNKNOWN_TYPE
        }
    }

    fun getResult(): String {
        return result.extra
    }

    enum class Type {
        UNKNOWN_TYPE,
        ANCHOR_TYPE,
        PHONE_TYPE,
        GEO_TYPE,
        EMAIL_TYPE,
        IMAGE_TYPE,
        IMAGE_ANCHOR_TYPE,
        SRC_ANCHOR_TYPE,
        SRC_IMAGE_ANCHOR_TYPE,
        EDIT_TEXT_TYPE
    }
}