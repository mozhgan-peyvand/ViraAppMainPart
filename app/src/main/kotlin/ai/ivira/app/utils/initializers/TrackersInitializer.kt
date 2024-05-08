package ai.ivira.app.utils.initializers

import ai.ivira.app.features.ava_negar.data.AvanegarTracker
import ai.ivira.app.features.avasho.data.AvashoTracker
import ai.ivira.app.features.hamahang.data.HamahangTracker
import ai.ivira.app.features.imazh.data.ImazhTracker
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrackersInitializer @Inject constructor(
    private val avanegarTracker: AvanegarTracker,
    private val avashoTracker: AvashoTracker,
    private val imazhTracker: ImazhTracker,
    private val hamahangTracker: HamahangTracker
) {
    fun init() {
        avanegarTracker.init()
        avashoTracker.init()
        imazhTracker.init()
        hamahangTracker.init()
    }
}