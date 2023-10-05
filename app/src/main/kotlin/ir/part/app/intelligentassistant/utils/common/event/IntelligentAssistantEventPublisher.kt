package ir.part.app.intelligentassistant.utils.common.event

import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IntelligentAssistantEventPublisher @Inject constructor() {
    val events = MutableSharedFlow<IntelligentAssistantEvent>(extraBufferCapacity = 1)

    fun publishEvent(iaEvent: IntelligentAssistantEvent) {
        events.tryEmit(iaEvent)
    }
}