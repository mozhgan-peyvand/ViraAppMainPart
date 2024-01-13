package ai.ivira.app.features.imazh.ui.newImageDescriptor.sheets

import ai.ivira.app.R
import ai.ivira.app.features.imazh.ui.newImageDescriptor.component.ImazhKeywordItem
import ai.ivira.app.features.imazh.ui.newImageDescriptor.model.ImazhKeywordView
import ai.ivira.app.utils.ui.preview.ViraDarkPreview
import ai.ivira.app.utils.ui.preview.ViraPreview
import ai.ivira.app.utils.ui.safeClick
import ai.ivira.app.utils.ui.theme.Color_BG_Bottom_Sheet
import ai.ivira.app.utils.ui.theme.Color_On_Surface_Variant
import ai.ivira.app.utils.ui.theme.Color_Primary
import ai.ivira.app.utils.ui.theme.Color_Text_1
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ScrollableTabRow
import androidx.compose.material.Tab
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun ImazhKeywordBottomSheet(
    map: Map<String, Set<ImazhKeywordView>>,
    selectedChips: List<ImazhKeywordView>,
    onAcceptClick: (List<ImazhKeywordView>) -> Unit
) {
    val configuration = LocalConfiguration.current
    val height by remember(configuration.screenHeightDp) {
        mutableStateOf(configuration.screenHeightDp.dp * 0.5f)
    }

    var selected by rememberSaveable(
        stateSaver = listSaver<List<ImazhKeywordView>, Any>(
            save = { list ->
                buildList {
                    list.forEach { keyword ->
                        add(keyword.farsi)
                        add(keyword.english)
                    }
                }
            },
            restore = { list ->
                buildList {
                    var i = 0
                    while (i < list.lastIndex) {
                        add(
                            ImazhKeywordView(
                                farsi = list[i++] as String,
                                english = list[i++] as String
                            )
                        )
                    }
                }
            }
        )
    ) { mutableStateOf(selectedChips) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .background(Color_BG_Bottom_Sheet)
            .padding(horizontal = 16.dp, vertical = 20.dp)
    ) {
        Text(
            text = stringResource(id = R.string.lbl_keyword),
            style = MaterialTheme.typography.h6,
            color = Color_Text_1,
            modifier = Modifier.padding(top = 12.dp)
        )

        Spacer(modifier = Modifier.size(12.dp))

        TabSection(
            mapOfKeywords = map,
            selectedChips = selected.map { it.farsi },
            onChipClick = { value, isSelected ->

                selected = if (isSelected) {
                    selected.filter { it != value }
                } else {
                    selected.plus(value)
                }
            },
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.size(16.dp))

        Button(
            contentPadding = PaddingValues(vertical = 12.dp),
            onClick = {
                safeClick {
                    onAcceptClick(selected)
                }
            }
        ) {
            Text(
                text = stringResource(id = R.string.lbl_accept),
                style = MaterialTheme.typography.button,
                color = Color_Text_1,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun TabSection(
    mapOfKeywords: Map<String, Set<ImazhKeywordView>>,
    selectedChips: List<String>,
    onChipClick: (value: ImazhKeywordView, isSelected: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var tabName by remember { mutableStateOf(mapOfKeywords.keys.first()) }
    val tabIndex by remember(tabName) { mutableIntStateOf(mapOfKeywords.keys.indexOf(tabName)) }
    val scrollState = rememberScrollState()

    Column(modifier = modifier.fillMaxWidth()) {
        ScrollableTabRow(
            backgroundColor = Color_BG_Bottom_Sheet,
            selectedTabIndex = tabIndex,
            edgePadding = 0.dp,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    color = Color_Primary,
                    modifier = Modifier
                        .tabIndicatorOffset(tabPositions[tabIndex])
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                )
            }
        ) {
            mapOfKeywords.keys.forEach {
                Tab(
                    text = { Text(it) },
                    selected = tabName == it,
                    onClick = {
                        tabName = it
                    },
                    selectedContentColor = Color_Primary,
                    unselectedContentColor = Color_On_Surface_Variant,
                    modifier = Modifier.background(Color_BG_Bottom_Sheet)
                )
            }
        }
        FlowRow(
            modifier = Modifier
                .verticalScroll(scrollState)
                .padding(vertical = 16.dp)
        ) {
            key(tabName) {
                mapOfKeywords[tabName]?.forEach { keyword ->
                    ImazhKeywordItem(
                        value = keyword,
                        isSelected = selectedChips.contains(keyword.farsi),
                        onClick = {
                            onChipClick(keyword, selectedChips.contains(keyword.farsi))
                        }
                    )

                    Spacer(modifier = Modifier.size(8.dp))
                }
            }
        }
    }
}

@ViraDarkPreview
@Composable
private fun ImazhKeywordBottomSheetPreview() {
    ViraPreview {
        ImazhKeywordBottomSheet(
            map = mapOf(),
            selectedChips = emptyList(),
            onAcceptClick = {}
        )
    }
}