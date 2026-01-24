package kz.hashiroii.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kz.hashiroii.data.datasource.MockSubscriptionDataSource
import kz.hashiroii.data.network.NetworkMonitor
import kz.hashiroii.data.repository.CurrencyRepositoryImpl
import kz.hashiroii.data.repository.NotificationRepositoryImpl
import kz.hashiroii.data.repository.PreferencesRepositoryImpl
import kz.hashiroii.data.service.AppNameResolver
import kz.hashiroii.data.service.ServiceRecognizer
import kz.hashiroii.data.service.SubscriptionDetectionService
import kz.hashiroii.domain.repository.CurrencyRepository
import kz.hashiroii.domain.repository.NotificationRepository
import kz.hashiroii.domain.repository.PreferencesRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideServiceRecognizer(
        appNameResolver: AppNameResolver
    ): ServiceRecognizer {
        return ServiceRecognizer(appNameResolver)
    }

    @Provides
    @Singleton
    fun provideNotificationRepository(
        subscriptionDetectionService: SubscriptionDetectionService,
        mockDataSource: MockSubscriptionDataSource
    ): NotificationRepository {
        return NotificationRepositoryImpl(subscriptionDetectionService, mockDataSource)
    }

    @Provides
    @Singleton
    fun provideNetworkMonitor(): NetworkMonitor {
        return NetworkMonitor()
    }

    @Provides
    @Singleton
    fun provideCurrencyRepository(): CurrencyRepository {
        return CurrencyRepositoryImpl()
    }

    @Provides
    @Singleton
    fun providePreferencesRepository(
        preferencesRepositoryImpl: PreferencesRepositoryImpl
    ): PreferencesRepository {
        return preferencesRepositoryImpl
    }
}
