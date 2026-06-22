package per.goweii.wanandroid.utils.tts

import android.annotation.SuppressLint
import android.content.Context
import per.goweii.wanandroid.R

class TtsManager(private val context: Context) {
    companion object {
        @SuppressLint("StaticFieldLeak")
        private var instance: TtsManager? = null

        @JvmStatic
        fun getInstance(context: Context): TtsManager {
            if (instance == null) {
                synchronized(TtsManager::class) {
                    if (instance == null) {
                        instance = TtsManager(context.applicationContext)
                    }
                }
            }
            return instance!!
        }
    }

    private val notification = TtsNotification(context)
    private val playback: MutableList<TtsSource> = arrayListOf()
    private var playable: TtsPlayable? = null

    val isSpeaking: Boolean
        get() = playable?.state?.let {
            it == TtsPlayable.TtsState.THINKING || it == TtsPlayable.TtsState.SPEAKING
        } ?: false

    fun addSource(source: TtsSource) {
        if (!playback.contains(source)) {
            playback.add(source)
            if (playable != null) {
                notification.update(
                    playbackIndex = playback.indexOf(playable!!.source) + 1,
                    playableCount = playback.size,
                )
            }
        }
    }

    fun removeSource(source: TtsSource) {
        playback.remove(source)
        if (playable != null) {
            notification.update(
                playbackIndex = playback.indexOf(playable!!.source) + 1,
                playableCount = playback.size,
            )
        }
    }

    fun clearPlayback() {
        playback.clear()
        notification.cancel()
    }

    fun playSource(source: TtsSource) {
        if (!playback.contains(source)) {
            playback.add(source)
        }
        if (playable?.source != source) {
            playable?.stop()
            playable = null
        }
        if (playable == null) {
            playable = TtsPlayable(
                source = source,
                onPlayStateChangeListener = object : TtsPlayable.OnPlayStateChangeListener {
                    override fun onIdle() {
                        notification.show(source)
                        notification.update(
                            isPlaying = false,
                            playbackIndex = playback.indexOf(source) + 1,
                            playableCount = playback.size,
                            currentSpeakText = source.url,
                        )
                    }

                    override fun onThinking() {
                        notification.update(
                            isPlaying = true,
                            currentSpeakText = context.getString(R.string.thinking),
                        )
                    }

                    override fun onSpeaking() {
                        notification.update(
                            isPlaying = true,
                        )
                    }

                    override fun onStopped(completed: Boolean) {
                        notification.update(
                            isPlaying = false,
                            currentSpeakText = source.url,
                        )
                        if (completed) {
                            val index = playback.indexOf(source)
                            if (index < playback.lastIndex) {
                                playSource(playback[index + 1])
                            }
                        }
                    }

                    override fun onReleased() {
                        playable = null
                        notification.update(
                            isPlaying = false,
                            currentSpeakText = source.url,
                        )
                    }

                    override fun onErrored() {
                        notification.update(
                            isPlaying = false,
                            currentSpeakText = source.url,
                        )
                    }
                },
                onSpeakTextChangeListener = object : TtsPlayable.OnSpeakTextChangeListener {
                    override fun onSpeakStart(
                        text: String,
                        index: Int,
                        total: Int
                    ) {
                        notification.update(
                            currentSpeakText = text,
                        )
                    }

                    override fun onSpeakDone(
                        text: String,
                        index: Int,
                        total: Int
                    ) {
                        notification.update(
                            currentSpeakText = text,
                        )
                    }

                    override fun onSpeakError(
                        text: String,
                        index: Int,
                        total: Int
                    ) {
                        notification.update(
                            currentSpeakText = text,
                        )
                    }
                },
            )
            playable!!.start()
        }
    }

    fun pause() {
        playable?.stop()
        notification.update(
            isPlaying = false,
            currentSpeakText = playable?.source?.url ?: "",
        )
    }

    fun resume() {
        playable?.start()
    }

    fun prev() {
        if (playback.isEmpty()) {
            return
        }
        if (playable != null) {
            val index = playback.indexOf(playable!!.source)
            if (index == -1 || index == 0) {
                playSource(playback.last())
            } else {
                playSource(playback[index - 1])
            }
        }
    }

    fun next() {
        if (playback.isEmpty()) {
            return
        }
        if (playable != null) {
            val index = playback.indexOf(playable!!.source)
            if (index == -1 || index == playback.lastIndex) {
                playSource(playback.first())
            } else {
                playSource(playback[index + 1])
            }
        }
    }

    fun stop() {
        playable?.stop()
    }

    fun release() {
        playable?.release()
        playable = null
        instance = null
    }
}