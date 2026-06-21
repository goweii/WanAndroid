package per.goweii.wanandroid.utils.ai

import com.google.gson.Gson
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import okhttp3.*
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener
import okhttp3.sse.EventSources
import per.goweii.basic.utils.LogUtils
import per.goweii.wanandroid.utils.SettingUtils
import java.util.concurrent.TimeUnit

/**
 * AI 客户端，基于 OkHttp SSE 实现流式调用
 */
object AiClient {
    private const val TAG = "AiClient"

    private val gson = Gson()
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val mediaType = MediaType.get("application/json; charset=utf-8")

    @JvmStatic
    fun stream(messages: List<AiMessage>): Flow<String> = callbackFlow {
        val settings = SettingUtils.getInstance()
        val aiProvider = AiProvider.findProvider(settings.aiProvider)
            ?: AiProvider.providers.first()
        val aiModel = aiProvider.findModel(settings.aiModel)
            ?: aiProvider.models.first()

        val request = Request.Builder()
            .url("${aiProvider.baseUrl}/chat/completions")
            .addHeader("Authorization", "Bearer ${settings.aiApiKey}")
            .addHeader("Accept", "text/event-stream")
            .post(
                RequestBody.create(
                    mediaType,
                    gson.toJson(
                        mapOf(
                            "model" to aiModel.id,
                            "stream" to true,
                            "messages" to messages.map { it.toMap() }
                        )
                    ))
            )
            .build()

        LogUtils.d(TAG, "Url: ${request.url()}")

        val factory = EventSources.createFactory(client)
        val eventSource = factory.newEventSource(request, object : EventSourceListener() {
            override fun onEvent(
                eventSource: EventSource,
                id: String?,
                type: String?,
                data: String
            ) {
                LogUtils.d(TAG, "onEvent: id=$id, type=$type, data=$data")
                if (data == "[DONE]") {
                    close()
                    return
                }
                try {
                    val response = gson.fromJson(data, ChatResponse::class.java)
                    val content = response.choices?.firstOrNull()?.delta?.content
                    if (content != null) {
                        trySend(content)
                    }
                } catch (e: Exception) {
                    // Ignore malformed JSON
                }
            }

            override fun onFailure(eventSource: EventSource, t: Throwable?, response: Response?) {
                LogUtils.d(TAG, "onFailure: ${t.toString()}")
                close(t ?: Exception("SSE failure"))
            }

            override fun onClosed(eventSource: EventSource) {
                LogUtils.d(TAG, "onClosed")
                close()
            }
        })

        awaitClose {
            eventSource.cancel()
        }
    }

    private data class ChatResponse(
        val choices: List<Choice>?
    )

    private data class Choice(
        val delta: Delta?
    )

    private data class Delta(
        val content: String?
    )
}
