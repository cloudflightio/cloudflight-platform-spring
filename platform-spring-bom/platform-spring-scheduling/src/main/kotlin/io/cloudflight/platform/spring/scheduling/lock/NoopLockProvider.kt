package io.cloudflight.platform.spring.scheduling.lock

import net.javacrumbs.shedlock.core.LockConfiguration
import net.javacrumbs.shedlock.core.LockProvider
import net.javacrumbs.shedlock.core.SimpleLock
import java.util.*

/**
 * @author Harald Radi (harald.radi@catalysts.cc)
 * @version 1.0
 */
class NoopLockProvider : LockProvider {
    override fun lock(lockConfiguration: LockConfiguration): Optional<SimpleLock> {
        return Optional.of(NoopLock(lockConfiguration))
    }
}
