package eu.kanade.presentation.reader.settings

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import eu.kanade.tachiyomi.R
import eu.kanade.tachiyomi.ui.reader.setting.ReaderSettingsScreenModel
import tachiyomi.presentation.core.components.CheckboxItem
<<<<<<< HEAD
import tachiyomi.presentation.core.components.SettingsChipRow
import tachiyomi.presentation.core.util.collectAsState

private val themes = listOf(
    R.string.black_background to 1,
    R.string.gray_background to 2,
    R.string.white_background to 0,
    R.string.automatic_background to 3,
)
=======
import tachiyomi.presentation.core.components.HeadingItem
import tachiyomi.presentation.core.components.RadioItem
>>>>>>> parent of 05ce223db (Merge branch 'tachiyomiorg:master' into master)

@Composable
internal fun ColumnScope.GeneralPage(screenModel: ReaderSettingsScreenModel) {
    // TODO: show this in a nicer way
    HeadingItem(R.string.pref_reader_theme)
    val readerTheme by screenModel.preferences.readerTheme().collectAsState()
    listOf(
        R.string.black_background to 1,
        R.string.gray_background to 2,
        R.string.white_background to 0,
        R.string.automatic_background to 3,
    ).map { (titleRes, theme) ->
        RadioItem(
            label = stringResource(titleRes),
            selected = readerTheme == theme,
            onClick = { screenModel.preferences.readerTheme().set(theme) },
        )
    }

    CheckboxItem(
        label = stringResource(R.string.pref_show_page_number),
        pref = screenModel.preferences.showPageNumber(),
    )

    CheckboxItem(
        label = stringResource(R.string.pref_fullscreen),
        pref = screenModel.preferences.fullscreen(),
    )

    // TODO: hide if there's no cutout
    CheckboxItem(
        label = stringResource(R.string.pref_cutout_short),
        pref = screenModel.preferences.cutoutShort(),
    )

    CheckboxItem(
        label = stringResource(R.string.pref_keep_screen_on),
        pref = screenModel.preferences.keepScreenOn(),
    )

    CheckboxItem(
        label = stringResource(R.string.pref_read_with_long_tap),
        pref = screenModel.preferences.readWithLongTap(),
    )

    CheckboxItem(
        label = stringResource(R.string.pref_always_show_chapter_transition),
        pref = screenModel.preferences.alwaysShowChapterTransition(),
    )

    CheckboxItem(
        label = stringResource(R.string.pref_page_transitions),
        pref = screenModel.preferences.pageTransitions(),
    )
}
