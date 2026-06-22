package per.goweii.wanandroid.utils.tts

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View
import android.widget.RemoteViews
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import per.goweii.wanandroid.R
import per.goweii.wanandroid.module.main.activity.RouterActivity

class TtsNotification(val context: Context) {
    private val manager = NotificationManagerCompat.from(context)

    private var source: TtsSource? = null
    private var coverBitmap: Bitmap? = null
    private var isPlaying: Boolean = false
    private var currentSpeakText: String? = null
    private var playbackIndex: Int = 0
    private var playableCount: Int = 0

    fun show(source: TtsSource) {
        createNotificationChannel()
        this.source = source
        update()
        if (source.cover?.isNotEmpty() == true && hasPermission()) {
            loadCover()
        }
    }

    @SuppressLint("MissingPermission")
    fun update(
        source: TtsSource? = null,
        isPlaying: Boolean? = null,
        currentSpeakText: String? = null,
        playbackIndex: Int? = null,
        playableCount: Int? = null,
    ) {
        this.source = source ?: this.source
        this.isPlaying = isPlaying ?: this.isPlaying
        this.currentSpeakText = currentSpeakText ?: this.currentSpeakText
        this.playbackIndex = playbackIndex ?: this.playbackIndex
        this.playableCount = playableCount ?: this.playableCount
        if (this.source != null && hasPermission()) {
            val notification = buildNotification()
            manager.notify(R.string.tts_notification_channel_id, notification)
        }
    }

    fun cancel() {
        manager.cancel(R.string.tts_notification_channel_id)
        coverBitmap?.recycle()
        source = null
        coverBitmap = null
        isPlaying = false
        currentSpeakText = null
        playbackIndex = 0
        playableCount = 0
    }

    private fun hasPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannelCompat.Builder(
            context.getString(R.string.tts_notification_channel_id),
            NotificationManagerCompat.IMPORTANCE_DEFAULT,
        ).setName(context.getString(R.string.tts_notification_channel_name))
            .setDescription(context.getString(R.string.tts_notification_channel_desc))
            .setShowBadge(false)
            .setVibrationEnabled(false)
            .setLightsEnabled(false)
            .build()
        manager.createNotificationChannel(channel)
    }

    private fun loadCover() {
        Glide.with(context)
            .asBitmap()
            .load(source?.cover)
            .override(200)
            .centerCrop()
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(
                    bitmap: Bitmap,
                    transition: Transition<in Bitmap>?
                ) {
                    coverBitmap = bitmap
                    update()
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    coverBitmap = null
                    update()
                }
            })
    }

    private fun buildNotification(): Notification {
        val intent = Intent(context, RouterActivity::class.java).apply {
            action = Intent.ACTION_VIEW
            data = source?.url?.toUri()
        }
        val openAppIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val playIntent = PendingIntent.getBroadcast(
            context,
            R.id.iv_play,
            Intent(context, TtsNotificationReceiver::class.java).apply {
                action = TtsNotificationReceiver.ACTION_PLAY(context)
            },
            PendingIntent.FLAG_UPDATE_CURRENT or
                    PendingIntent.FLAG_IMMUTABLE
        )

        val pauseIntent = PendingIntent.getBroadcast(
            context,
            R.id.iv_pause,
            Intent(context, TtsNotificationReceiver::class.java).apply {
                action = TtsNotificationReceiver.ACTION_PAUSE(context)
            },
            PendingIntent.FLAG_UPDATE_CURRENT or
                    PendingIntent.FLAG_IMMUTABLE
        )

        val prevIntent = PendingIntent.getBroadcast(
            context,
            R.id.iv_prev,
            Intent(context, TtsNotificationReceiver::class.java).apply {
                action = TtsNotificationReceiver.ACTION_PREV(context)
            },
            PendingIntent.FLAG_UPDATE_CURRENT or
                    PendingIntent.FLAG_IMMUTABLE
        )

        val nextIntent = PendingIntent.getBroadcast(
            context,
            R.id.iv_next,
            Intent(context, TtsNotificationReceiver::class.java).apply {
                action = TtsNotificationReceiver.ACTION_NEXT(context)
            },
            PendingIntent.FLAG_UPDATE_CURRENT or
                    PendingIntent.FLAG_IMMUTABLE
        )

        val smallView =
            RemoteViews(context.packageName, R.layout.layout_tts_notification_small).apply {
                if (coverBitmap?.isRecycled == false) {
                    setImageViewBitmap(R.id.iv_cover, coverBitmap!!)
                } else {
                    setImageViewResource(R.id.iv_cover, R.drawable.logo_512)
                }
                setTextViewText(R.id.tv_title, source?.title?.trim())
                setTextViewText(R.id.tv_desc, currentSpeakText?.trim())
                if (isPlaying) {
                    setViewVisibility(R.id.iv_play, View.GONE)
                    setViewVisibility(R.id.iv_pause, View.VISIBLE)
                } else {
                    setViewVisibility(R.id.iv_play, View.VISIBLE)
                    setViewVisibility(R.id.iv_pause, View.GONE)
                }
                setOnClickPendingIntent(R.id.iv_play, playIntent)
                setOnClickPendingIntent(R.id.iv_pause, pauseIntent)
            }

        val bigView =
            RemoteViews(context.packageName, R.layout.layout_tts_notification_big).apply {
                if (coverBitmap?.isRecycled == false) {
                    setImageViewBitmap(R.id.iv_cover, coverBitmap!!)
                } else {
                    setImageViewResource(R.id.iv_cover, R.drawable.logo_512)
                }
                setTextViewText(R.id.tv_title, source?.title?.trim())
                setTextViewText(R.id.tv_desc, currentSpeakText?.trim())
                if (isPlaying) {
                    setViewVisibility(R.id.iv_play, View.GONE)
                    setViewVisibility(R.id.iv_pause, View.VISIBLE)
                } else {
                    setViewVisibility(R.id.iv_play, View.VISIBLE)
                    setViewVisibility(R.id.iv_pause, View.GONE)
                }
                if (playbackIndex > 0 && playableCount > 0) {
                    setTextViewText(R.id.tv_index, "${playbackIndex}/${playableCount}")
                }
                setOnClickPendingIntent(R.id.iv_play, playIntent)
                setOnClickPendingIntent(R.id.iv_pause, pauseIntent)
                setOnClickPendingIntent(R.id.iv_prev, prevIntent)
                setOnClickPendingIntent(R.id.iv_next, nextIntent)
            }

        return NotificationCompat.Builder(
            context,
            context.getString(R.string.tts_notification_channel_id)
        ).setContentIntent(openAppIntent)
            .setSmallIcon(R.drawable.ic_icon)
            .setBadgeIconType(NotificationCompat.BADGE_ICON_NONE)
            .setCustomContentView(smallView)
            .setCustomBigContentView(bigView)
            .setShowWhen(false)
            .setAllowSystemGeneratedContextualActions(false)
            .setOngoing(isPlaying)
            .build()
    }
}