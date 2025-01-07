package io.cloudflight.platform.spring.jpa.autoconfigure

import org.springframework.boot.autoconfigure.transaction.TransactionManagerCustomizer
import org.springframework.transaction.support.AbstractPlatformTransactionManager

class TransactionCustomizer(private val properties: TransactionProperties) : TransactionManagerCustomizer<AbstractPlatformTransactionManager> {

    override fun customize(transactionManager: AbstractPlatformTransactionManager) {
        // we want to have that validation here in order to ensure that if a read-only transaction is being opened that
        // only readOnly subsequent calls are being done
        transactionManager.isValidateExistingTransaction = properties.validateExistingTransaction
    }
}
