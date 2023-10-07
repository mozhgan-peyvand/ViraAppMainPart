package ai.ivira.app.utils.common.event

import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ViraPublisher @Inject constructor() {
    val events = MutableSharedFlow<ViraEvent>(extraBufferCapacity = 1)

    fun publishEvent(iaEvent: ViraEvent) {
        events.tryEmit(iaEvent)
    }
}