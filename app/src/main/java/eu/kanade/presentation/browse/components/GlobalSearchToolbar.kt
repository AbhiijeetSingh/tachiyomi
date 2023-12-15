package eu.kanade.presentation.browse.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
<<<<<<< HEAD
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DoneAll
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
=======
>>>>>>> parent of 05ce223db (Merge branch 'tachiyomiorg:master' into master)
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import eu.kanade.presentation.components.SearchToolbar
<<<<<<< HEAD
import eu.kanade.tachiyomi.R
import eu.kanade.tachiyomi.ui.browse.source.globalsearch.SourceFilter
import tachiyomi.presentation.core.components.material.padding
=======
>>>>>>> parent of 05ce223db (Merge branch 'tachiyomiorg:master' into master)

@Composable
fun GlobalSearchToolbar(
    searchQuery: String?,
    progress: Int,
    total: Int,
    navigateUp: () -> Unit,
    onChangeSearchQuery: (String?) -> Unit,
    onSearch: (String) -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
) {
<<<<<<< HEAD
    Column(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
        Box {
            SearchToolbar(
                searchQuery = searchQuery,
                onChangeSearchQuery = onChangeSearchQuery,
                onSearch = onSearch,
                onClickCloseSearch = navigateUp,
                navigateUp = navigateUp,
                scrollBehavior = scrollBehavior,
            )
            if (progress in 1..<total) {
                LinearProgressIndicator(
                    progress = progress / total.toFloat(),
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth(),
                )
            }
        }

        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = MaterialTheme.padding.small),
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.padding.small),
        ) {
            // TODO: make this UX better; it only applies when triggering a new search
            FilterChip(
                selected = sourceFilter == SourceFilter.PinnedOnly,
                onClick = { onChangeSearchFilter(SourceFilter.PinnedOnly) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.PushPin,
                        contentDescription = null,
                        modifier = Modifier
                            .size(FilterChipDefaults.IconSize),
                    )
                },
                label = {
                    Text(text = stringResource(id = R.string.pinned_sources))
                },
            )
            FilterChip(
                selected = sourceFilter == SourceFilter.All,
                onClick = { onChangeSearchFilter(SourceFilter.All) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.DoneAll,
                        contentDescription = null,
                        modifier = Modifier
                            .size(FilterChipDefaults.IconSize),
                    )
                },
                label = {
                    Text(text = stringResource(id = R.string.all))
                },
            )

            VerticalDivider()

            FilterChip(
                selected = onlyShowHasResults,
                onClick = { onToggleResults() },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.FilterList,
                        contentDescription = null,
                        modifier = Modifier
                            .size(FilterChipDefaults.IconSize),
                    )
                },
                label = {
                    Text(text = stringResource(id = R.string.has_results))
                },
            )
        }

        HorizontalDivider()
=======
    Box {
        SearchToolbar(
            searchQuery = searchQuery,
            onChangeSearchQuery = onChangeSearchQuery,
            onSearch = onSearch,
            onClickCloseSearch = navigateUp,
            navigateUp = navigateUp,
            scrollBehavior = scrollBehavior,
        )
        if (progress in 1 until total) {
            LinearProgressIndicator(
                progress = progress / total.toFloat(),
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth(),
            )
        }
>>>>>>> parent of 05ce223db (Merge branch 'tachiyomiorg:master' into master)
    }
}
