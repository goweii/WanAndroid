package per.goweii.wanandroid.utils.biometric

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.FragmentActivity
import javax.crypto.Cipher

class BiometricHelper(
        private val activity: FragmentActivity
) {
    private val biometricPrompt = BiometricPrompt(activity, AuthCallback())
    private var keyAlias = "keyForLogin"

    var onAuthSuccess: ((cipher: Cipher) -> Unit)? = null
    var onAuthFailure: (() -> Unit)? = null
    var onAuthError: ((errorCode: Int, errString: CharSequence) -> Unit)? = null

    @RequiresApi(Build.VERSION_CODES.M)
    fun canAuth(): Boolean {
        val biometricManager = BiometricManager.from(activity)
        val canAuthenticate = biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK)
        return canAuthenticate == BiometricManager.BIOMETRIC_SUCCESS
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun authForEncode(
            title: String = "开启生物认证登录",
            cancelBtn: String = "取消"
    ) {
        val promptInfo = BiometricPrompt.PromptInfo.Builder().apply {
            setTitle(title)
            setNegativeButtonText(cancelBtn)
            setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
            setConfirmationRequired(false)
        }.build()
        val cipher = CipherUtils.getEncryptCipher(keyAlias)
        val cryptoObject = BiometricPrompt.CryptoObject(cipher)
        biometricPrompt.authenticate(promptInfo, cryptoObject)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun authForDecode(
            iv: ByteArray,
            title: String = "使用生物认证登录",
            cancelBtn: String = "取消"
    ) {
        val promptInfo = BiometricPrompt.PromptInfo.Builder().apply {
            setTitle(title)
            setNegativeButtonText(cancelBtn)
            setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
            setConfirmationRequired(false)
        }.build()
        val cipher = CipherUtils.getDecryptCipher(keyAlias, iv)
        val cryptoObject = BiometricPrompt.CryptoObject(cipher)
        biometricPrompt.authenticate(promptInfo, cryptoObject)
    }

    private inner class AuthCallback : BiometricPrompt.AuthenticationCallback() {
        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
            super.onAuthenticationSucceeded(result)
            val cipher = result.cryptoObject?.cipher ?: throw RuntimeException("cipher is null")
            onAuthSuccess?.invoke(cipher)
        }

        override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
            super.onAuthenticationError(errorCode, errString)
            onAuthError?.invoke(errorCode, errString)
        }

        override fun onAuthenticationFailed() {
            super.onAuthenticationFailed()
            onAuthFailure?.invoke()
        }
    }
}