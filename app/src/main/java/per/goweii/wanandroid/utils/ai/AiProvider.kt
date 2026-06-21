package per.goweii.wanandroid.utils.ai

sealed class AiProvider(
    val name: String,
    val id: String,
    val baseUrl: String,
    val models: List<AiModel>,
) {
    companion object {
        @JvmStatic
        val providers: List<AiProvider> by lazy {
            AiProvider::class.sealedSubclasses
                .map { it.objectInstance!! }
                .toList()
        }

        @JvmStatic
        fun findProvider(id: String): AiProvider? {
            return providers.firstOrNull { it.id == id }
        }

        @JvmStatic
        fun getProvider(id: String): AiProvider {
            return providers.firstOrNull { it.id == id } ?: providers.first()
        }
    }

    fun findModel(id: String): AiModel? {
        return models.firstOrNull { it.id == id }
    }

    fun getModel(id: String): AiModel {
        return models.firstOrNull { it.id == id } ?: models.first()
    }
}

object DeepSeek : AiProvider(
    name = "DeepSeek",
    id = "deepseek",
    baseUrl = "https://api.deepseek.com",
    models = listOf(
        AiModel(
            name = "DeepSeek V4 Flash",
            id = "deepseek-v4-flash",
        ),
        AiModel(
            name = "DeepSeek V4 Pro",
            id = "deepseek-v4-pro",
        ),
    ),
)

object Qwen : AiProvider(
    name = "Qwen",
    id = "qwen",
    baseUrl = "https://coding.dashscope.aliyuncs.com/v1",
    models = listOf(
        AiModel(
            name = "Qwen3.6 Flash",
            id = "qwen3.6-flash",
        ),
        AiModel(
            name = "Qwen3.6 Plus",
            id = "qwen3.6-plus",
        ),
        AiModel(
            name = "Qwen3.7 Plus",
            id = "qwen3.7-plus",
        ),
        AiModel(
            name = "Qwen3.7 Max",
            id = "qwen3.7-max",
        ),
    ),
)

data class AiModel(
    val name: String,
    val id: String,
)