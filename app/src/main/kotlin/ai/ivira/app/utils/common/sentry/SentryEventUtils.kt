package ai.ivira.app.utils.common.sentry

import io.sentry.SentryEvent
import io.sentry.protocol.Message

fun sentryEvent(throwable: Throwable? = null, block: SentryEvent.() -> Unit): SentryEvent {
    val event = SentryEvent(throwable)
    event.block()
    return event
}

fun sentryMessage(message: String, block: Message.() -> Unit = {}): Message {
    return Message().apply {
        this.message = message
        block()
    }
}