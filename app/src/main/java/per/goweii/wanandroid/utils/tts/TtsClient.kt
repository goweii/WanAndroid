package per.goweii.wanandroid.utils.tts

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import per.goweii.basic.utils.LogUtils
import per.goweii.basic.utils.Utils
import java.util.Locale
import java.util.UUID

object TtsClient {
    private const val TAG = "TtsClient"

    private var textToSpeech: TextToSpeech

    private val buffer = StringBuilder()

    val isSpeaking: Boolean
        get() = textToSpeech.isSpeaking

    private val textCache = mutableMapOf<String, String>()

    private val stateChangeListeners = arrayListOf<OnTtsStateChangeListener>()

    private var currentSpeakId = -1

    init {
        textToSpeech = TextToSpeech(Utils.getAppContext()) { status ->
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.language = Locale.CHINESE
                textToSpeech.setSpeechRate(1.0f)
                textToSpeech.setPitch(1.0f)
            }
        }
        textToSpeech.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            // 开始朗读
            override fun onStart(id: String?) {
                val index = id?.toIntOrNull() ?: -1
                currentSpeakId = index
                val text = id?.let { textCache[it] }
                LogUtils.d(TAG, "onStart: id=${id}, text=${text}")
                if (text != null) {
                    stateChangeListeners.forEach { it.onStart(id, text) }
                }
            }

            // 朗读正常结束
            override fun onDone(id: String?) {
                val text = id?.let { textCache[it] }
                LogUtils.d(TAG, "onDone: id=${id}, text=${text}")
                if (text != null) {
                    stateChangeListeners.forEach { it.onDone(id, text) }
                }
                if (buffer.isNotEmpty()) {
                    val text = buffer.toString()
                    buffer.clear()
                    speakNow(text, null)
                }
            }

            // 朗读被中断/出错
            @Deprecated("Deprecated in Java")
            override fun onError(id: String?) {
                val index = id?.toIntOrNull() ?: -1
                val text = id?.let { textCache[it] }
                LogUtils.d(TAG, "onError: id=${id}, text=${text}")
                if (text != null) {
                    stateChangeListeners.forEach { it.onError(id, text) }
                }
            }

            // 朗读被中断/出错
            override fun onError(id: String?, errorCode: Int) {
                val index = id?.toIntOrNull() ?: -1
                val text = id?.let { textCache[it] }
                LogUtils.d(TAG, "onError: id=${id}, text=${text}")
                if (text != null) {
                    stateChangeListeners.forEach { it.onError(id, text) }
                }
            }
        })
    }

    fun addStateChangeListener(listener: OnTtsStateChangeListener) {
        stateChangeListeners.add(listener)
    }

    fun removeStateChangeListener(listener: OnTtsStateChangeListener) {
        stateChangeListeners.remove(listener)
    }

    // 流式逐段播报
    // textChunk == null 表示结束添加文本
    fun speak(
        textChunk: String?,
        id: String? = null,
        useBuffer: Boolean = false,
    ) {
        if (textChunk == null) {
            if (buffer.isNotEmpty()) {
                val text = buffer.toString()
                buffer.clear()
                speakNow(text, id)
            }
            return
        }
        if (useBuffer) {
            buffer.append(textChunk)
            if (buffer.contains("。")
                || buffer.contains("！")
                || buffer.contains("？")
                || buffer.contains("；")
                || buffer.contains("\\n")
            ) {
                val text = buffer.toString()
                buffer.clear()
                speakNow(text, id)
            }
        } else {
            speakNow(textChunk, id)
        }
    }

    fun stop() {
        buffer.clear()
        currentSpeakId = -1
        textCache.clear()
        textToSpeech.stop()
    }

    private fun speakNow(text: String, id: String?) {
        val id = id ?: UUID.randomUUID().toString()
        textCache[id] = text
        val params = Bundle().apply {
            putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, id)
        }
        textToSpeech.speak(text, TextToSpeech.QUEUE_ADD, params, id)
    }

    interface OnTtsStateChangeListener {
        fun onStart(id: String, text: String)
        fun onDone(id: String, text: String)
        fun onError(id: String, text: String)
    }
}