package per.goweii.wanandroid.module.main.activity

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.Process
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_crash.*
import per.goweii.ponyo.crash.Crash
import per.goweii.statusbarcompat.StatusBarCompat
import per.goweii.wanandroid.R
import per.goweii.wanandroid.common.WanApp
import java.io.PrintWriter
import java.io.StringWriter
import kotlin.system.exitProcess

class CrashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.TRANSPARENT
        WanApp.initDarkMode()
        StatusBarCompat.setIconMode(this, !WanApp.isDarkMode())
        setContentView(R.layout.activity_crash)
        tv_copy_log.setOnClickListener {
            val cm = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            cm.setPrimaryClip(ClipData.newPlainText("error", tv_error.text))
            tv_copy_log.text = "已复制"
        }
        btn_exit.setOnClickListener {
            finish()
            Process.killProcess(Process.myPid())
            exitProcess(10)
        }
        btn_restart.setOnClickListener {
            Crash.restartApp(applicationContext)
            finish()
            Process.killProcess(Process.myPid())
        }
        showLog()
    }

    private fun showLog() {
        val e = intent.getSerializableExtra("error") as Throwable
        val stringWriter = StringWriter()
        val printWriter = PrintWriter(stringWriter)
        e.printStackTrace(printWriter)
        tv_error.text = stringWriter.toString().toDBC()
    }

    private fun String.toDBC(): String {
        val c = this.toCharArray()
        for (i in c.indices) {
            if (c[i].toInt() == 12288) {
                c[i] = 32.toChar()
                continue
            }
            if (c[i].toInt() in 65281..65374) c[i] = (c[i] - 65248)
        }
        return String(c)
    }
}