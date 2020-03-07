package per.goweii.wanandroid.utils.web.js

class DarkmodeInject : BaseInject("DARKMODE_INJECT") {

    override fun injectOnProgress(): IntArray = intArrayOf()

    override fun loadJsFromAssets(): String? = "js/darkmode.js"
    override fun loadJsFromString(): String? = null

    override fun loadJsInitCode(): String? = "const darkmode = new Darkmode();if(!darkmode.isActivated()){darkmode.toggle();}"

}