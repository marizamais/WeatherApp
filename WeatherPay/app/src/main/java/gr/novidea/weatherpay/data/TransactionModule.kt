package gr.novidea.weatherpay.data

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TransactionModule {
    @Binds
    @Singleton
    abstract fun bindTransactionManager(transactionManager: TransactionManager): ITransaction
}