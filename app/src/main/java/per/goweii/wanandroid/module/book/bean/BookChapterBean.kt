package per.goweii.wanandroid.module.book.bean

data class BookIntroBean(
    val img: String,
    val author: String,
    val name: String,
    val desc: String,
    val copyright: Copyright,
    val chapters: List<BookChapterBean>
) {
    data class Copyright(
        val name: String,
        val url: String
    )

    data class BookChapterBean(
        val index: Int,
        val name: String,
        val link: String
    )
}
