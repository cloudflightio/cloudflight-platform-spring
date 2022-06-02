package io.cloudflight.platform.spring.scheduling.lock

import net.javacrumbs.shedlock.core.AbstractSimpleLock
import net.javacrumbs.shedlock.core.LockConfiguration
import net.javacrumbs.shedlock.core.SimpleLock
import java.util.*


/**
 * @author Harald Radi (harald.radi@catalysts.cc)
 * @version 1.0
 */
class NoopLock(lockConfiguration: LockConfiguration?) : AbstractSimpleLock(lockConfiguration) {
    @Suppress("EmptyFunctionBlock")
    override fun doUnlock() {
    }

    override fun doExtend(newConfig: LockConfiguration?): Optional<SimpleLock> {
        return Optional.of(this)
    }
}
