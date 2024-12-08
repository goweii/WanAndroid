package per.goweii.wanandroid.utils.web

import com.tencent.smtt.sdk.ValueCallback
import per.goweii.basic.utils.LogUtils
import per.goweii.wanandroid.utils.web.view.X5WebView
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock

object WebScrollableUtils {
    const val DIRECTION_UP = 1
    const val DIRECTION_DOWN = 2
    const val DIRECTION_LEFT = 4
    const val DIRECTION_RIGHT = 8

    fun getScrollableDirections(webView: X5WebView, x: Float, y: Float): Int {
        if (true) return 0

        val density: Float = webView.resources.displayMetrics.density
        val wx: Float = (x + webView.webScrollX) / density
        val wy: Float = (y + webView.webScrollY) / density
        val js = """javascript:(
            function getScrollableDirections() {
                let touchX = $wx;
                let touchY = $wy;
                
                let directions = 0;

                // 检查触摸点是否在可滚动的元素内
                const elements = document.querySelectorAll('*'); // 获取页面中的所有元素
                
                for (var i = 0; i < elements.length; i++) {
                    var element = elements[i];
                    
                    // 获取元素的位置和大小
                    var rect = element.getBoundingClientRect();
    
                    // 检查触摸点是否在当前元素内
                    if (touchX >= rect.left && touchX <= rect.right && touchY >= rect.top && touchY <= rect.bottom) {
                        // 垂直滚动判断
                        if (element.scrollHeight > element.clientHeight) {
                            if (element.scrollTop > 0) {
                                console.log('可以向上滚动:', directions);
                                directions |= $DIRECTION_UP; // 可以向上滚动
                            }
                            if (element.scrollTop + element.clientHeight < element.scrollHeight) {
                                console.log('可以向下滚动:', directions);
                                directions |= $DIRECTION_DOWN; // 可以向下滚动
                            }
                        }
            
                        // 水平滚动判断
                        if (element.scrollWidth > element.clientWidth) {
                            if (element.scrollLeft > 0 || element.scrollWidth > element.clientWidth) {
                                console.log('可以向左滚动:', directions);
                                directions |= $DIRECTION_LEFT; // 可以向左滚动
                            }
                            if (element.scrollLeft + element.clientWidth < element.scrollWidth) {
                                console.log('可以向右滚动:', directions);
                                directions |= $DIRECTION_RIGHT; // 可以向右滚动
                            }
                        }
                    }
                }
                
                console.log('touchX:', touchX, 'touchY:', touchY, 'directions:', directions);
                
                return directions;
            }
        )()""".trimIndent()

        val l = ReentrantLock()
        val c = l.newCondition()

        var directions = 0

        webView.evaluateJavascript(js, ValueCallback { s ->
            l.lock()
            try {
                directions = Integer.parseInt(s.trim())
                LogUtils.d("WebScrollableUtils", "可滚动方向: $directions")
                c.signalAll()
            } catch (e: NumberFormatException) {
                LogUtils.e("WebScrollableUtils", "Failed to parse directions: $s")
            } finally {
                l.unlock()
            }
        })

        l.lock()
        try {
            c.await(500, TimeUnit.MILLISECONDS)
        } catch (ignore: InterruptedException) {
        } finally {
            l.unlock()
        }

        return directions
    }
}
