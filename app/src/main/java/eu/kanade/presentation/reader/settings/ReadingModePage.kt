package eu.kanade.presentation.reader.settings

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
<<<<<<< HEAD
import eu.kanade.domain.manga.model.orientationType
import eu.kanade.domain.manga.model.readingModeType
=======
import eu.kanade.presentation.util.collectAsState
>>>>>>> parent of 05ce223db (Merge branch 'tachiyomiorg:master' into master)
import eu.kanade.tachiyomi.R
import eu.kanade.tachiyomi.ui.reader.setting.ReaderPreferences
import eu.kanade.tachiyomi.ui.reader.setting.ReaderSettingsScreenModel
import eu.kanade.tachiyomi.util.system.isReleaseBuildType
import tachiyomi.presentation.core.components.CheckboxItem
import tachiyomi.presentation.core.components.HeadingItem
import tachiyomi.presentation.core.components.SliderItem
import tachiyomi.presentation.core.util.collectAsState
import java.text.NumberFormat

<<<<<<< HEAD
private val readingModeOptions = ReadingModeType.entries.map { it.stringRes to it }
private val orientationTypeOptions = OrientationType.entries.map { it.stringRes to it }
private val tappingInvertModeOptions = ReaderPreferences.TappingInvertMode.entries.map { it.titleResId to it }

=======
>>>>>>> parent of 05ce223db (Merge branch 'tachiyomiorg:master' into master)
@Composable
internal fun ColumnScope.ReadingModePage(screenModel: ReaderSettingsScreenModel) {
    HeadingItem(R.string.pref_category_for_this_series)

    // Reading mode
    // Rotation type

    // if (pager)
    PagerViewerSettings(screenModel)

    WebtoonViewerSettings(screenModel)
}

@Composable
private fun ColumnScope.PagerViewerSettings(screenModel: ReaderSettingsScreenModel) {
    HeadingItem(R.string.pager_viewer)

    // Tap zones
    // Invert tap zones
    // Scale type
    // Zoom start position

    CheckboxItem(
        label = stringResource(R.string.pref_crop_borders),
        pref = screenModel.preferences.cropBorders(),
    )

    CheckboxItem(
        label = stringResource(R.string.pref_landscape_zoom),
        pref = screenModel.preferences.landscapeZoom(),
    )

    CheckboxItem(
        label = stringResource(R.string.pref_navigate_pan),
        pref = screenModel.preferences.navigateToPan(),
    )

    val dualPageSplitPaged by screenModel.preferences.dualPageSplitPaged().collectAsState()
    CheckboxItem(
        label = stringResource(R.string.pref_dual_page_split),
        pref = screenModel.preferences.dualPageSplitPaged(),
    )

    if (dualPageSplitPaged) {
        CheckboxItem(
            label = stringResource(R.string.pref_dual_page_invert),
            pref = screenModel.preferences.dualPageInvertPaged(),
        )
    }

    val dualPageRotateToFit by screenModel.preferences.dualPageRotateToFit().collectAsState()
    CheckboxItem(
        label = stringResource(R.string.pref_page_rotate),
        pref = screenModel.preferences.dualPageRotateToFit(),
    )

    if (dualPageRotateToFit) {
        CheckboxItem(
            label = stringResource(R.string.pref_page_rotate_invert),
            pref = screenModel.preferences.dualPageRotateToFitInvert(),
        )
    }
}

@Composable
private fun ColumnScope.WebtoonViewerSettings(screenModel: ReaderSettingsScreenModel) {
    val numberFormat = remember { NumberFormat.getPercentInstance() }

    HeadingItem(R.string.webtoon_viewer)

    // TODO: Tap zones
    // TODO: Invert tap zones

    val webtoonSidePadding by screenModel.preferences.webtoonSidePadding().collectAsState()
    SliderItem(
        label = stringResource(R.string.pref_webtoon_side_padding),
        min = ReaderPreferences.WEBTOON_PADDING_MIN,
        max = ReaderPreferences.WEBTOON_PADDING_MAX,
        value = webtoonSidePadding,
        valueText = numberFormat.format(webtoonSidePadding / 100f),
        onChange = {
            screenModel.preferences.webtoonSidePadding().set(it)
        },
    )

    CheckboxItem(
        label = stringResource(R.string.pref_crop_borders),
        pref = screenModel.preferences.cropBordersWebtoon(),
    )

    val dualPageSplitWebtoon by screenModel.preferences.dualPageSplitWebtoon().collectAsState()
    CheckboxItem(
        label = stringResource(R.string.pref_dual_page_split),
        pref = screenModel.preferences.dualPageSplitWebtoon(),
    )

    if (dualPageSplitWebtoon) {
        CheckboxItem(
            label = stringResource(R.string.pref_dual_page_invert),
            pref = screenModel.preferences.dualPageInvertWebtoon(),
        )
    }

    if (!isReleaseBuildType) {
        CheckboxItem(
            label = stringResource(R.string.pref_long_strip_split),
            pref = screenModel.preferences.longStripSplitWebtoon(),
        )
    }

    CheckboxItem(
        label = stringResource(R.string.pref_double_tap_zoom),
        pref = screenModel.preferences.webtoonDoubleTapZoomEnabled(),
    )
}
