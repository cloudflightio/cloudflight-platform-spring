package io.cloudflight.platform.spring.jpa.autoconfigure

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.transaction.support.AbstractPlatformTransactionManager

@ConfigurationProperties("cloudflight.spring.tx")
class TransactionProperties(

    /**
     * If true, participating transcation definitions will be checked if they match with the outside transaction.
     * This is especially important if if you open readOnly transaction and then would continue with non-read-only
     * transactions - you could unknowingly lose data then
     *
     * @see [AbstractPlatformTransactionManager.setValidateExistingTransaction]
     */
    val validateExistingTransaction: Boolean = true
)
