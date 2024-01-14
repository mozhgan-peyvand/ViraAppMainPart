package ai.ivira.app.features.imazh.ui.newImageDescriptor.sheets

import ai.ivira.app.R
import ai.ivira.app.features.imazh.ui.newImageDescriptor.NewImageDescriptorViewModel
import ai.ivira.app.features.imazh.ui.newImageDescriptor.model.ImazhHistoryView
import ai.ivira.app.utils.ui.preview.ViraDarkPreview
import ai.ivira.app.utils.ui.preview.ViraPreview
import ai.ivira.app.utils.ui.safeClick
import ai.ivira.app.utils.ui.theme.Color_BG_Bottom_Sheet
import ai.ivira.app.utils.ui.theme.Color_On_Surface_Variant
import ai.ivira.app.utils.ui.theme.Color_OutLine
import ai.ivira.app.utils.ui.theme.Color_Primary_200
import ai.ivira.app.utils.ui.theme.Color_Text_1
import ai.ivira.app.utils.ui.theme.Color_Text_2
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.RadioButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun HistoryBottomSheet(
    viewModel: NewImageDescriptorViewModel = hiltViewModel(),
    onAcceptClick: (String) -> Unit
) {
    val configuration = LocalConfiguration.current
    val listState = rememberLazyListState()
    var selectedPrompt by rememberSaveable { mutableStateOf("") }

    val height by remember(configuration.screenHeightDp) {
        mutableStateOf(configuration.screenHeightDp.dp * 0.5f)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = height)
            .background(Color_BG_Bottom_Sheet)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = stringResource(id = R.string.lbl_last_prompts),
            style = MaterialTheme.typography.h6,
            color = Color_Text_1,
            modifier = Modifier.padding(top = 12.dp)
        )

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            items(viewModel.historyList.value) { historyItem ->
                HistoryItem(
                    item = historyItem,
                    isSelected = selectedPrompt == historyItem.prompt
                ) {
                    selectedPrompt = historyItem.prompt
                }
            }
        }

        Spacer(modifier = Modifier.size(16.dp))

        Button(
            contentPadding = PaddingValues(vertical = 12.dp),
            enabled = selectedPrompt.isNotEmpty(),
            onClick = {
                safeClick {
                    if (selectedPrompt.isNotEmpty()) {
                        onAcceptClick(selectedPrompt)
                    }
                }
            },
            modifier = Modifier.padding(bottom = 8.dp)
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
private fun HistoryItem(
    item: ImazhHistoryView,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val textModifier by remember(isSelected) {
        mutableStateOf(
            if (isSelected) {
                Modifier
                    .fillMaxWidth()
                    .basicMarquee()
            } else {
                Modifier.fillMaxWidth()
            }
        )
    }

    val textOverFlow by remember(isSelected) {
        mutableStateOf(if (isSelected) TextOverflow.Clip else TextOverflow.Ellipsis)
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable {
                    safeClick(onClick)
                }
                .padding(vertical = 12.dp)

        ) {
            RadioButton(
                selected = isSelected,
                onClick = {
                    safeClick(onClick)
                },
                colors = RadioButtonDefaults.colors(
                    selectedColor = Color_Primary_200,
                    unselectedColor = Color_Primary_200
                ),
                modifier = Modifier.padding(start = 12.dp)
            )

            Spacer(modifier = Modifier.size(12.dp))

            Column {
                Text(
                    text = item.createdAt,
                    style = MaterialTheme.typography.caption,
                    color = Color_On_Surface_Variant
                )

                Text(
                    text = item.prompt,
                    style = MaterialTheme.typography.body1,
                    color = Color_Text_2,
                    maxLines = 1,
                    overflow = textOverFlow,
                    modifier = textModifier
                )
            }
        }

        Divider(
            color = Color_OutLine,
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
        )
    }
}

@ViraDarkPreview
@Composable
private fun HistoryBottomSheetPreview() {
    ViraPreview {
        HistoryBottomSheet(hiltViewModel(), {})
    }
}

@ViraDarkPreview
@Composable
private fun HistoryItemPreview() {
    ViraPreview {
        HistoryItem(
            ImazhHistoryView(
                prompt = "اسب سفید، بالدار، درحال دویدن،  مزرعه، آسمان اسب سفید، بالدار، درحال دویدن،  مزرعه، آسمان",
                createdAt = "۱۴۰۲/۰۸/۱۵"
            ),
            false,
            {}
        )
    }
}