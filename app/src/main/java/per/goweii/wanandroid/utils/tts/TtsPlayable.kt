package per.goweii.wanandroid.utils.tts

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import per.goweii.wanandroid.utils.SettingUtils
import per.goweii.wanandroid.utils.ai.AiClient
import per.goweii.wanandroid.utils.ai.AssistantMessage
import per.goweii.wanandroid.utils.ai.UserMessage
import per.goweii.wanandroid.utils.web.utils.WebReadability

class TtsPlayable(
    val source: TtsSource,
    val onPlayStateChangeListener: OnPlayStateChangeListener,
    val onSpeakTextChangeListener: OnSpeakTextChangeListener,
) {
    private val prompt = """
你是一个专业的AI助理，我会给你提供一个网页 HTML 源码，你需要帮我总结并生成一份纯文本格式文章总结。

你需要：
- 使用中文
- 尽量保持原文输出，不要过于压缩总结
- 只输出文章总结，不要输出任何无关的内容
- 不要输出类似“根据所提供的HTML源码”等前缀回复
- 纯文本格式，方便朗读

你不能：
- 不要压缩过多
- 不能输出富文本合适或者markdown格式
- 如果文章包含代码块，你不能输出大面积代码
""".trim()

    private val scope = MainScope()

    private val listener = object : TtsClient.OnTtsStateChangeListener {
        override fun onStart(id: String, text: String) {
            state = TtsState.SPEAKING
            index = id.toInt()
            onSpeakTextChangeListener.onSpeakStart(text, index, cache.size)
        }

        override fun onDone(id: String, text: String) {
            index = id.toInt()
            onSpeakTextChangeListener.onSpeakDone(text, index, cache.size)
            if (index == cache.lastIndex && job?.isActive != true) {
                state = TtsState.STOPPED
                onPlayStateChangeListener.onStopped(true)
            }
        }

        override fun onError(id: String, text: String) {
            index = id.toInt()
            onSpeakTextChangeListener.onSpeakError(text, index, cache.size)
            if (index == cache.lastIndex && job?.isActive != true) {
                state = TtsState.STOPPED
                onPlayStateChangeListener.onStopped(true)
            }
        }
    }

    var state: TtsState = TtsState.IDLE
        private set

    private var job: Job? = null

    private val cache = arrayListOf<String>()
    private var index: Int = -1
    private val buffer = StringBuilder()

    fun start() {
        if (state == TtsState.RECYCLED) return
        if (state == TtsState.THINKING) return
        if (state == TtsState.SPEAKING) return

        if (state == TtsState.IDLE) {
            onPlayStateChangeListener.onIdle()
            TtsClient.addStateChangeListener(listener)
            startNewJob()
            return
        }

        if (state == TtsState.ERROR) {
            startNewJob()
            return
        }

        if (state == TtsState.STOPPED) {
            if (cache.isNotEmpty()) {
                state = TtsState.SPEAKING
                onPlayStateChangeListener.onSpeaking()
                index = index.coerceIn(cache.indices).takeIf { it != cache.lastIndex } ?: 0
                for (i in index..cache.lastIndex) {
                    TtsClient.speak(cache[i], useBuffer = false, id = i.toString())
                }
            } else {
                state = TtsState.THINKING
                onPlayStateChangeListener.onThinking()
            }
            return
        }
    }

    fun stop() {
        if (state == TtsState.RECYCLED) return
        if (state == TtsState.IDLE) return
        if (state == TtsState.STOPPED) return
        state = TtsState.STOPPED
        onPlayStateChangeListener.onStopped(false)
        TtsClient.stop()
    }

    fun release() {
        if (state == TtsState.RECYCLED) return
        stop()
        TtsClient.removeStateChangeListener(listener)
        scope.cancel()
        state = TtsState.RECYCLED
        onPlayStateChangeListener.onReleased()
    }

    private fun startNewJob() {
        job?.cancel()
        job = scope.launch {
            TtsClient.stop()
            if (SettingUtils.getInstance().isAiEnabled && SettingUtils.getInstance().aiApiKey.isNotEmpty()) {
                startByAI()
            } else {
                startByLocal()
            }
        }.apply {
            invokeOnCompletion {
                job = null
            }
        }
    }

    private suspend fun startByLocal() = withContext(Dispatchers.Default) {
        state = TtsState.THINKING
        onPlayStateChangeListener.onThinking()

        try {
            val readability = WebReadability(source.url, source.html, true)
            val md = readability.toMarkdown()
            buffer.append(md)
            processBuffer(isEnd = true)
            if (state == TtsState.RECYCLED) return@withContext
            if (state == TtsState.STOPPED) return@withContext
            if (state != TtsState.SPEAKING) {
                state = TtsState.SPEAKING
                onPlayStateChangeListener.onSpeaking()
            }
            cache.forEachIndexed { index, string ->
                TtsClient.speak(string, useBuffer = false, id = index.toString())
            }
        } catch (_: Exception) {
            state = TtsState.ERROR
            onPlayStateChangeListener.onErrored()
        }
    }

    private suspend fun startByAI() {
        state = TtsState.THINKING
        onPlayStateChangeListener.onThinking()

        val readability = WebReadability(source.url, source.html, false)
        val md = readability.toMarkdown()
        val flow = AiClient.stream(
            listOf(
                AssistantMessage(prompt),
                UserMessage(md),
            )
        )
        flow.catch {
            state = TtsState.ERROR
            onPlayStateChangeListener.onErrored()
        }.collect { chunk ->
            if (chunk.isEmpty()) return@collect
            buffer.append(chunk)
            val oldSize = cache.size
            processBuffer()
            val newSize = cache.size
            if (state == TtsState.RECYCLED) return@collect
            if (state == TtsState.STOPPED) return@collect
            if (state != TtsState.SPEAKING) {
                state = TtsState.SPEAKING
                onPlayStateChangeListener.onSpeaking()
            }
            for (i in oldSize until newSize) {
                TtsClient.speak(cache[i], useBuffer = false, id = i.toString())
            }
        }
        val oldSize = cache.size
        processBuffer(isEnd = true)
        val newSize = cache.size
        kotlin.run {
            if (state == TtsState.RECYCLED) return@run
            if (state == TtsState.STOPPED) return@run
            for (i in oldSize until newSize) {
                TtsClient.speak(cache[i], useBuffer = false, id = i.toString())
            }
            TtsClient.speak(null, useBuffer = false)
        }
    }

    private fun processBuffer(isEnd: Boolean = false) {
        if (buffer.isEmpty()) return
        val lines = buffer.lines()
        buffer.clear()
        if (lines.isEmpty()) return

        fun String.texts(): List<String> {
            return split("(?<=[，。！？；：])".toRegex())
                .filter { it.isNotBlank() }
        }

        lines
            .take(lines.size - 1)
            .forEach { line ->
                line.texts()
                    .forEach { text ->
                        cache.add(text)
                    }
            }

        val line = lines.last()
        if (line.isEmpty()) return
        val texts = line.texts()
        if (texts.isEmpty()) return
        texts
            .take(texts.size - 1)
            .forEach { text ->
                cache.add(text)
            }
        val text = texts.last()
        if (isEnd) {
            cache.add(text)
        } else {
            buffer.append(text)
        }
    }

    enum class TtsState {
        IDLE,
        THINKING,
        SPEAKING,
        STOPPED,
        ERROR,
        RECYCLED,
    }

    interface OnPlayStateChangeListener {
        fun onIdle()
        fun onThinking()
        fun onSpeaking()
        fun onStopped(completed: Boolean)
        fun onReleased()
        fun onErrored()
    }

    interface OnSpeakTextChangeListener {
        fun onSpeakStart(text: String, index: Int, total: Int)
        fun onSpeakDone(text: String, index: Int, total: Int)
        fun onSpeakError(text: String, index: Int, total: Int)
    }
}