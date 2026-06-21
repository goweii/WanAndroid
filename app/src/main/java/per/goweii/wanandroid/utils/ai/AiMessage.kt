package per.goweii.wanandroid.utils.ai

sealed class AiMessage(
    val role: String,
) {
    open fun toMap(): MutableMap<String, String> {
        return mutableMapOf("role" to role)
    }
}

data class UserMessage(
    val content: String,
) : AiMessage(role = "user") {
    override fun toMap(): MutableMap<String, String> {
        return super.toMap().apply {
            this["content"] = content
        }
    }
}

data class SystemMessage(
    val content: String,
) : AiMessage(role = "system") {
    override fun toMap(): MutableMap<String, String> {
        return super.toMap().apply {
            this["content"] = content
        }
    }
}

data class AssistantMessage(
    val content: String,
) : AiMessage(role = "assistant") {
    override fun toMap(): MutableMap<String, String> {
        return super.toMap().apply {
            this["content"] = content
        }
    }
}