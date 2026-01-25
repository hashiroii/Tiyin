package kz.hashiroii.tiyin

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kz.hashiroii.domain.usecase.preferences.GetLanguageUseCase
import javax.inject.Inject

@HiltAndroidApp
class TiyinApplication : Application() {

    @Inject
    lateinit var getLanguageUseCase: GetLanguageUseCase

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onCreate() {
        super.onCreate()
        applicationScope.launch {
            val languageCode = getLanguageUseCase().first()
            if (languageCode.isNotEmpty() && languageCode != "System") {
                val localeList = LocaleListCompat.forLanguageTags(languageCode)
                AppCompatDelegate.setApplicationLocales(localeList)
            }
        }
    }
}