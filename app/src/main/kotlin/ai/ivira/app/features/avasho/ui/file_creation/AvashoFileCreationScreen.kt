package ai.ivira.app.features.avasho.ui.file_creation

import ai.ivira.app.R
import ai.ivira.app.R.string
import ai.ivira.app.utils.ui.safeClick
import ai.ivira.app.utils.ui.theme.Color_BG
import ai.ivira.app.utils.ui.theme.Color_BG_Bottom_Sheet
import ai.ivira.app.utils.ui.theme.Color_Primary_200
import ai.ivira.app.utils.ui.theme.Color_Surface_Container_High
import ai.ivira.app.utils.ui.theme.Color_Text_3
import ai.ivira.app.utils.ui.theme.Color_White
import ai.ivira.app.utils.ui.theme.ViraTheme
import ai.ivira.app.utils.ui.theme.labelMedium
import ai.ivira.app.utils.ui.widgets.ViraIcon
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.IconButton
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue.Hidden
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection.Rtl
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch

private const val CHAR_COUNT = 2500

@Composable
fun AvashoFileCreationScreen(
    navController: NavHostController,
    viewModel: AvashoFileCreationViewModel = hiltViewModel()
) {
    val scrollState = rememberScrollState()
    val focusRequester = remember { FocusRequester() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(focusRequester) {
        focusRequester.requestFocus()
    }
    val fileName = rememberSaveable { mutableStateOf("") }
    val bottomSheetState = rememberModalBottomSheetState(
        initialValue = Hidden,
        skipHalfExpanded = true,
        confirmValueChange = { false }
    )

    Scaffold(backgroundColor = Color_BG) { padding ->
        ModalBottomSheetLayout(
            sheetState = bottomSheetState,
            sheetShape = RoundedCornerShape(topEnd = 16.dp, topStart = 16.dp),
            sheetBackgroundColor = Color_BG_Bottom_Sheet,
            scrimColor = Color.Black.copy(alpha = 0.5f),
            sheetContent = {
                SelectSpeachBottomSheet(
                    fileName = fileName.value,
                    uploadFileAction = { nameFile, selectedItem ->
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set(
                                SpeechResult.FILE_NAME,
                                SpeechResult(
                                    fileName = nameFile,
                                    text = viewModel.textBody.value,
                                    speakerType = selectedItem.value
                                )

                            )
                        navController.popBackStack()
                    }
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color_BG)
            ) {
                TopAppBar(
                    onBackAction = {
                        // todo should handle the situation when the textField is not empty
                        navController.navigateUp()
                    }
                )

                Body(
                    text = viewModel.textBody.value,
                    focusRequester = focusRequester,
                    onTextChange = {
                        viewModel.addTextToList(it)
                    },
                    scrollState = scrollState,
                    modifier = Modifier.weight(1f)
                )

                Button(
                    contentPadding = PaddingValues(vertical = 12.dp),
                    enabled = viewModel.textBody.value.isNotEmpty(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    onClick = {
                        safeClick {
                            coroutineScope.launch {
                                if (!bottomSheetState.isVisible) {
                                    bottomSheetState.show()
                                } else {
                                    bottomSheetState.hide()
                                }
                            }
                        }
                    }
                ) {
                    Text(
                        text = stringResource(id = string.lbl_convert_to_sound),
                        style = MaterialTheme.typography.button
                    )
                }
            }
        }
    }
}

@Composable
private fun TopAppBar(
    onBackAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = {
                safeClick {
                    onBackAction()
                }
            }
        ) {
            ViraIcon(
                drawable = R.drawable.ic_arrow_forward,
                modifier = Modifier.padding(8.dp),
                contentDescription = stringResource(id = R.string.desc_back)
            )
        }

        Spacer(modifier = Modifier.size(8.dp))

        Text(
            text = stringResource(id = R.string.lbl_file_creation),
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.subtitle2,
            color = Color_White
        )
    }
}

@Composable
private fun Body(
    text: String,
    focusRequester: FocusRequester,
    onTextChange: (String) -> Unit,
    scrollState: ScrollState,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            TextField(
                value = text,
                textStyle = MaterialTheme.typography.body1,
                placeholder = {
                    Text(
                        text = stringResource(id = R.string.lbl_type_text_or_import_file),
                        style = MaterialTheme.typography.body1,
                        color = Color_Text_3
                    )
                },
                onValueChange = {
                    val generatedText = if (it.length <= CHAR_COUNT) {
                        it
                    } else {
                        it.substring(0 ..< CHAR_COUNT)
                    }

                    onTextChange(generatedText)
                },
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    backgroundColor = Color.Transparent
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
            )

            Spacer(modifier = Modifier.size(52.dp))
        }
        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .background(
                    color = Color_Surface_Container_High,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(12.dp)
        ) {
            CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.labelMedium) {
                Text(
                    text = stringResource(id = R.string.lbl_character),
                    color = Color_Primary_200
                )

                Spacer(modifier = Modifier.size(2.dp))

                Text(
                    text = buildString {
                        append(text.length)
                        append("/")
                        append(CHAR_COUNT)
                    },
                    color = Color_Primary_200
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xffffff)
@Composable
private fun AvashoFileCreationPreview() {
    ViraTheme {
        CompositionLocalProvider(LocalLayoutDirection provides Rtl) {
            AvashoFileCreationScreen(
                rememberNavController()
            )
        }
    }
}