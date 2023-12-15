package eu.kanade.tachiyomi.ui.reader.setting

import cafe.adriel.voyager.core.model.ScreenModel
<<<<<<< HEAD
import eu.kanade.presentation.util.ioCoroutineScope
import eu.kanade.tachiyomi.ui.reader.ReaderViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
=======
import eu.kanade.tachiyomi.util.preference.toggle
import tachiyomi.core.preference.Preference
>>>>>>> parent of 05ce223db (Merge branch 'tachiyomiorg:master' into master)
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

class ReaderSettingsScreenModel(
    val preferences: ReaderPreferences = Injekt.get(),
) : ScreenModel {

<<<<<<< HEAD
    val viewerFlow = readerState
        .map { it.viewer }
        .distinctUntilChanged()
        .stateIn(ioCoroutineScope, SharingStarted.Lazily, null)

    val mangaFlow = readerState
        .map { it.manga }
        .distinctUntilChanged()
        .stateIn(ioCoroutineScope, SharingStarted.Lazily, null)
=======
    fun togglePreference(preference: (ReaderPreferences) -> Preference<Boolean>) {
        preference(preferences).toggle()
    }
>>>>>>> parent of 05ce223db (Merge branch 'tachiyomiorg:master' into master)
}
