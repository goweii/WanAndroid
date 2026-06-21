package per.goweii.wanandroid.utils.tts

data class TtsSource(
    val url: String,
    val cover: String?,
    val title: String?,
    val html: String,
) {
    override fun hashCode(): Int {
        return url.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as TtsSource
        return url == other.url
    }
}
