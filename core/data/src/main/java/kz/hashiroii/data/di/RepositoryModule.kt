package kz.hashiroii.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kz.hashiroii.data.repository.LogoRepositoryImpl
import kz.hashiroii.domain.repository.LogoRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    
    @Binds
    @Singleton
    abstract fun bindLogoRepository(
        logoRepositoryImpl: LogoRepositoryImpl
    ): LogoRepository
}
