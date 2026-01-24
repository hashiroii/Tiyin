package kz.hashiroii.tiyin.di

import kz.hashiroii.tiyin.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    @Provides
    @Singleton
    @Named("logo_dev_api_key")
    fun provideLogoDevApiKey(): String {
        return BuildConfig.LOGO_DEV_API_KEY
    }
    
    @Provides
    @Singleton
    @Named("is_debug")
    fun provideIsDebug(): Boolean {
        return BuildConfig.DEBUG
    }
}
