package ai.ivira.app.utils.ui.sms_retriever

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.auth.api.phone.SmsRetrieverClient
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.suspendCoroutine

@Singleton
class ViraGoogleSmsRetriever @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val _smsResult = MutableSharedFlow<SmsResult>()
    val smsResult = _smsResult.asSharedFlow()

    var isServiceStarted = false

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (SmsRetriever.SMS_RETRIEVED_ACTION == intent.action) {
                intent.extras?.let { extras ->
                    parseOtpMessage(extras)?.let { code ->
                        CoroutineScope(IO).launch {
                            _smsResult.emit(SmsResult.Message(code))
                        }
                    }
                }
            }
        }
    }

    private suspend fun isGoogleApiAvailable(): Boolean {
        return suspendCoroutine { continuation ->
            val client: SmsRetrieverClient = SmsRetriever.getClient(context)
            GoogleApiAvailability.getInstance()
                .checkApiAvailability(client)
                .addOnSuccessListener {
                    continuation.resumeWith(Result.success(true))
                }
                .addOnFailureListener {
                    continuation.resumeWith(Result.failure(it))
                }
        }
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    suspend fun startService(showConsent: Boolean = false) {
        if (isGoogleApiAvailable()) {
            if (!showConsent) {
                if (startForHash()) {
                    context.registerReceiver(
                        receiver,
                        IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
                    )
                    isServiceStarted = true
                }
            } else {
                if (startWithConsent()) {
                    context.registerReceiver(
                        receiver,
                        IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
                    )
                    isServiceStarted = true
                }
            }
        }
    }

    private suspend fun startForHash(): Boolean = suspendCoroutine { continuation ->
        val client: SmsRetrieverClient = SmsRetriever.getClient(context)
        client.startSmsRetriever()
            .addOnSuccessListener {
                continuation.resumeWith(Result.success(true))
            }
            .addOnFailureListener {
                continuation.resumeWith(Result.success(false))
            }
    }

    private suspend fun startWithConsent(): Boolean = suspendCoroutine { continuation ->
        val client: SmsRetrieverClient = SmsRetriever.getClient(context)
        client.startSmsUserConsent(null)
            .addOnSuccessListener {
                continuation.resumeWith(Result.success(true))
            }
            .addOnFailureListener {
                continuation.resumeWith(Result.success(false))
            }
    }

    @Suppress("DEPRECATION")
    fun parseOtpMessage(bundle: Bundle): String? {
        val smsRetrieverStatus = bundle.get(SmsRetriever.EXTRA_STATUS) as Status
        if (smsRetrieverStatus.statusCode == CommonStatusCodes.SUCCESS) {
            val message = bundle.getString(SmsRetriever.EXTRA_SMS_MESSAGE).orEmpty()
            val matcher = "[0-9]{5}".toPattern().matcher(message)
            if (matcher.find()) {
                return matcher.group(0)
            }
        }
        return null
    }

    fun stopService() {
        if (isServiceStarted) {
            isServiceStarted = false
            try {
                context.unregisterReceiver(receiver)
            } catch (e: IllegalArgumentException) {
                Timber.e(e)
            }
        }
    }

    sealed interface SmsResult {
        data class Message(val code: String) : SmsResult
        data class ConsentIntent(val intent: Intent) : SmsResult
    }
}