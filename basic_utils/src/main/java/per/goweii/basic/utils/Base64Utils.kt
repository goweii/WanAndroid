package per.goweii.basic.utils

import android.util.Base64
import java.nio.charset.StandardCharsets

object Base64Utils {
    fun encodeToBytes(src: ByteArray?): ByteArray {
        if (src == null || src.isEmpty()) {
            return ByteArray(0)
        }
        return Base64.encode(src, Base64.NO_WRAP)
    }

    fun encodeToBytes(src: String?): ByteArray {
        val bytes = src?.toByteArray(StandardCharsets.UTF_8)
        return encodeToBytes(bytes)
    }

    fun encodeToString(src: ByteArray?): String {
        val bytes = encodeToBytes(src)
        return String(bytes, StandardCharsets.UTF_8)
    }

    fun encodeToString(src: String?): String {
        val bytes = encodeToBytes(src)
        return String(bytes, StandardCharsets.UTF_8)
    }

    fun decodeToBytes(src: ByteArray?): ByteArray {
        if (src == null || src.isEmpty()) {
            return ByteArray(0)
        }
        return Base64.decode(src, Base64.NO_WRAP)
    }

    fun decodeToBytes(src: String?): ByteArray {
        val bytes = src?.toByteArray(StandardCharsets.UTF_8)
        return decodeToBytes(bytes)
    }

    fun decodeToString(src: ByteArray?): String {
        val bytes = decodeToBytes(src)
        return String(bytes, StandardCharsets.UTF_8)
    }

    fun decodeToString(src: String?): String {
        val bytes = decodeToBytes(src)
        return String(bytes, StandardCharsets.UTF_8)
    }
}