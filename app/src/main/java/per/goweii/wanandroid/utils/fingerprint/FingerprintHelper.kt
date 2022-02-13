package per.goweii.wanandroid.utils.fingerprint

import android.app.Activity
import android.app.KeyguardManager
import android.hardware.biometrics.BiometricPrompt
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.annotation.RequiresApi
import androidx.core.hardware.fingerprint.FingerprintManagerCompat
import androidx.core.os.CancellationSignal
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator

class FingerprintHelper(
        private val activity: Activity
) {
    private val fingerprintManager = FingerprintManagerCompat.from(activity)

    private var onCancel: (() -> Unit)? = null

    /** 检查指纹识别支持状态 */
    @RequiresApi(Build.VERSION_CODES.M)
    fun checkBiometric(): String {
        // 获取锁屏管理
        val km = activity.getSystemService(KeyguardManager::class.java)
        return when {
            !fingerprintManager.isHardwareDetected -> {
                "不支持指纹"
            }
            !km.isKeyguardSecure -> {
                "未设置锁屏"
            }
            !fingerprintManager.hasEnrolledFingerprints() -> {
                "未注册有效指纹"
            }
            else -> {
                ""
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun authenticate() {
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun authenticateM() {
        val cancellationSignal = CancellationSignal()
        cancellationSignal.setOnCancelListener {
            // 取消回调
            onCancel?.invoke()
        }
        val cryptoObject = FingerprintManagerCompat.CryptoObject(loadCipher())
        fingerprintManager.authenticate(
                cryptoObject, // 包装了 Cipher 对象的 FingerprintManagerCompat.CryptoObject 对象，用于加解密
                0, // 可选 flag，建议为 0
                cancellationSignal, // CancellationSignal 对象，用于取消指纹认证，可空，但不建议为 null
                object : FingerprintManagerCompat.AuthenticationCallback() {
                    override fun onAuthenticationError(errMsgId: Int, errString: CharSequence?) {
                        super.onAuthenticationError(errMsgId, errString)
                    }

                    override fun onAuthenticationHelp(helpMsgId: Int, helpString: CharSequence?) {
                        super.onAuthenticationHelp(helpMsgId, helpString)
                    }

                    override fun onAuthenticationSucceeded(result: FingerprintManagerCompat.AuthenticationResult?) {
                        super.onAuthenticationSucceeded(result)
                    }

                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()
                    }
                }, // 认证回调接口
                Handler(Looper.getMainLooper()) // 回调所在 Handler，一般为 null
        )
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun authenticateQ() {
        val cancellationSignal = android.os.CancellationSignal()
        cancellationSignal.setOnCancelListener {
            // 取消回调
            onCancel?.invoke()
        }
        // 生成认证对象
        val prompt = with(BiometricPrompt.Builder(activity)) {
            setTitle("")
            setSubtitle("")
            setDescription("")
            setNegativeButton("取消", activity.mainExecutor, { dialog, _ ->
                // 取消回调
                dialog?.dismiss()
                cancellationSignal.cancel()
            })
            build()
        }
        val cryptoObject = BiometricPrompt.CryptoObject(loadCipher())
        prompt.authenticate(
                cryptoObject, // 包装了 Cipher 对象的 BiometricPrompt.CryptoObject 对象，用于加解密
                cancellationSignal, // CancellationSignal 对象，用于取消指纹认证，不能为空
                activity.mainExecutor, // 回调 Executor，不能为空，可使用 activity.mainExecutor
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
                        super.onAuthenticationError(errorCode, errString)
                    }

                    override fun onAuthenticationHelp(helpCode: Int, helpString: CharSequence?) {
                        super.onAuthenticationHelp(helpCode, helpString)
                    }

                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult?) {
                        super.onAuthenticationSucceeded(result)
                    }

                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()
                    }
                }// 认证回调
        )
    }

    private var keyAlias = "keyAlias"

    /** 获取 Cipher 对象 */
    @RequiresApi(Build.VERSION_CODES.M)
    private fun loadCipher(): Cipher {
        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)
        // keyAlias 为密钥别名，可自己定义，加密解密要一致
        if (!keyStore.containsAlias(keyAlias)) {
            // 不包含改别名，重新生成
            // 秘钥生成器
            val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
            val builder = KeyGenParameterSpec.Builder(
                    keyAlias,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(false)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
            keyGenerator.init(builder.build())
            keyGenerator.generateKey()
        }
        // 根据别名获取密钥
        val key = keyStore.getKey(keyAlias, null)
        val cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
                + KeyProperties.BLOCK_MODE_CBC + "/"
                + KeyProperties.ENCRYPTION_PADDING_PKCS7)
        // 开启登录时用于加密，使用 Cipher.ENCRYPT_MODE 初始化
        cipher.init(Cipher.ENCRYPT_MODE, key)
        return cipher
    }
}