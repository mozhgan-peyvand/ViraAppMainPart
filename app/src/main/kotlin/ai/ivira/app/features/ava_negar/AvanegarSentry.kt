package ai.ivira.app.features.ava_negar

import ai.ivira.app.utils.common.sentry.sentryEvent
import ai.ivira.app.utils.common.sentry.sentryMessage
import ai.ivira.app.utils.data.exceptions.TrackingFailedException
import io.sentry.Breadcrumb
import io.sentry.Sentry
import io.sentry.SentryLevel.INFO

object AvanegarSentry {
    const val CAT_VOICE_RECORD = "recordVoice"

    fun unableToGetAudioDuration(absolutePath: String) {
        val event = sentryEvent {
            message = sentryMessage("Unable to Read file duration!")
            setModules(emptyMap())
        }

        Sentry.captureEvent(event) { scope ->
            scope.setContexts("filename", absolutePath)
        }
    }

    fun queueIsFull(hasError: Boolean) {
        val event = sentryEvent {
            message = sentryMessage("Queue is full")
            setModules(emptyMap())
        }
        Sentry.captureEvent(event) { scope ->
            scope.setContexts("hasError", hasError)
        }
    }

    fun convertToTextRecorded() {
        val event = sentryEvent {
            message = sentryMessage("Convert Record to text")
            setModules(emptyMap())
        }
        Sentry.captureEvent(event)
    }

    fun catchServerException(exception: Throwable, hasNetwork: Boolean, hasVpn: Boolean) {
        Sentry.captureException(exception) { scope ->
            scope.setContexts(
                "network",
                buildMap {
                    put("hasNetwork", "$hasNetwork")
                    put("hasVpn", "$hasVpn")
                }
            )
        }
    }

    fun catchTrackException(
        fileDuration: Long,
        processTime: Int?,
        createdTime: Long,
        lastFailedTime: Long?
    ) {
        Sentry.captureException(TrackingFailedException()) { scope ->
            scope.setContexts(
                "trackInfo",
                buildMap {
                    put("fileDuration", "$fileDuration")
                    put("processTime", "$processTime")
                    put("createdTime", "$createdTime")
                    put("lastFailedTime", "$lastFailedTime")
                }
            )
        }
    }

    fun breadCrumbVoiceStarted(willRecord: Boolean, hasPaused: Boolean) {
        val breadcrumb = Breadcrumb()
        breadcrumb.message = "Status Changed"
        breadcrumb.category = CAT_VOICE_RECORD
        breadcrumb.level = INFO
        breadcrumb.setData("willRecord", willRecord)
        breadcrumb.setData("hasPaused", hasPaused)
        Sentry.addBreadcrumb(breadcrumb)
    }

    fun breadCrumbVoiceFinish(isStopped: Boolean, name: String) {
        val breadcrumb = Breadcrumb()
        breadcrumb.message = "Finished"
        breadcrumb.category = CAT_VOICE_RECORD
        breadcrumb.level = INFO
        breadcrumb.setData("isStopped", isStopped)
        breadcrumb.setData("name", name)
        Sentry.addBreadcrumb(breadcrumb)
    }
}