package io.cloudflight.platform.spring.jpa.autoconfigure

import com.querydsl.jpa.JPQLQueryFactory
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.context.annotation.Bean
import javax.persistence.EntityManager

@AutoConfiguration
@ConditionalOnClass(value = [JPQLQueryFactory::class])
class QueryDslAutoConfiguration {

    @Bean
    fun jpaQueryFactory(entityManager: EntityManager): JPQLQueryFactory {
        return JPAQueryFactory(entityManager)
    }
}