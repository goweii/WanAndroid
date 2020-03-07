package per.goweii.wanandroid.utils.web.js

class VConsoleInject : BaseInject("VCONSOLE_INJECT") {

    override fun injectOnProgress(): IntArray = intArrayOf(30, 50, 75)

    override fun loadJsFromAssets(): String? = "js/vconsole.min.js"
    override fun loadJsFromString(): String? = null

    override fun loadJsInitCode(): String? = "var vConsole = new VConsole();"
}