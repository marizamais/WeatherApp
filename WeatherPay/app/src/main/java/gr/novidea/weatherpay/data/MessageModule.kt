package gr.novidea.weatherpay.data

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class MessageModule {
    @Binds
    @Singleton
    abstract fun bindMessageManager(messageManager: MessageManager): IMessage
}