package com.haxon.blog.pages.admin

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.haxon.blog.components.AdminPageLayout
import com.haxon.blog.components.LinkPopup
import com.haxon.blog.components.MessagePopup
import com.haxon.blog.models.Category
import com.haxon.blog.models.ControlStyle
import com.haxon.blog.models.EditorControl
import com.haxon.blog.models.Post
import com.haxon.blog.models.Theme
import com.haxon.blog.navigation.Screen
import com.haxon.blog.styles.EditorKeyStyle
import com.haxon.blog.util.Constants
import com.haxon.blog.util.Id
import com.haxon.blog.util.addPost
import com.haxon.blog.util.applyControlStyle
import com.haxon.blog.util.applyStyle
import com.haxon.blog.util.getEditor
import com.haxon.blog.util.getSelectedText
import com.haxon.blog.util.isUserLoggedIn
import com.haxon.blog.util.noBorder
import com.varabyte.kobweb.browser.file.loadDataUrlFromDisk
import com.varabyte.kobweb.compose.css.Cursor
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.Overflow
import com.varabyte.kobweb.compose.css.Resize
import com.varabyte.kobweb.compose.css.ScrollBehavior
import com.varabyte.kobweb.compose.css.Visibility
import com.varabyte.kobweb.compose.foundation.layout.Arrangement
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.attrsModifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.backgroundColor
import com.varabyte.kobweb.compose.ui.modifiers.borderRadius
import com.varabyte.kobweb.compose.ui.modifiers.classNames
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.cursor
import com.varabyte.kobweb.compose.ui.modifiers.disabled
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxHeight
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxSize
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.fontFamily
import com.varabyte.kobweb.compose.ui.modifiers.fontSize
import com.varabyte.kobweb.compose.ui.modifiers.fontWeight
import com.varabyte.kobweb.compose.ui.modifiers.height
import com.varabyte.kobweb.compose.ui.modifiers.id
import com.varabyte.kobweb.compose.ui.modifiers.margin
import com.varabyte.kobweb.compose.ui.modifiers.maxHeight
import com.varabyte.kobweb.compose.ui.modifiers.maxWidth
import com.varabyte.kobweb.compose.ui.modifiers.onClick
import com.varabyte.kobweb.compose.ui.modifiers.onKeyDown
import com.varabyte.kobweb.compose.ui.modifiers.overflow
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.modifiers.resize
import com.varabyte.kobweb.compose.ui.modifiers.scrollBehavior
import com.varabyte.kobweb.compose.ui.modifiers.visibility
import com.varabyte.kobweb.compose.ui.thenIf
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.silk.components.forms.Switch
import com.varabyte.kobweb.silk.components.forms.SwitchSize
import com.varabyte.kobweb.silk.components.graphics.Image
import com.varabyte.kobweb.silk.components.layout.SimpleGrid
import com.varabyte.kobweb.silk.components.layout.numColumns
import com.varabyte.kobweb.silk.components.style.breakpoint.Breakpoint
import com.varabyte.kobweb.silk.components.style.toModifier
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.theme.breakpoint.rememberBreakpoint
import kotlinx.browser.document
import kotlinx.browser.localStorage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.A
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Input
import org.jetbrains.compose.web.dom.Li
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.dom.TextArea
import org.jetbrains.compose.web.dom.Ul
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLTextAreaElement
import kotlin.js.Date

data class CreatePageUiState(
    val id: String = "",
    val title: String = "",
    val subtitle: String = "",
    val thumbnail: String = "",
    val thumbnailInputDisabled: Boolean = true,
    val content: String = "",
    val category: Category = Category.Programming,
    val main: Boolean = false,
    val popular: Boolean = false,
    val sponsored: Boolean = false,
    val editorVisibility: Boolean = true,
    val messagePopup: Boolean = false,
    val linkPopup: Boolean = false,
    val imagePopup: Boolean = false,
)

@Page("create")
@Composable
fun CreatePage() {
    isUserLoggedIn {
        CreateScreen()
    }
}

@Composable
fun CreateScreen() {
    val scope = rememberCoroutineScope()
    val breakpoint = rememberBreakpoint()
    val context = rememberPageContext()
    var uiState by remember { mutableStateOf(CreatePageUiState()) }

    AdminPageLayout {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .margin(topBottom = 50.px)
                .padding(
                    left = if (breakpoint > Breakpoint.MD) Constants.SIDE_PANEL_WIDTH.px else 0.px
                ),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .maxWidth(700.px),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SimpleGrid(numColumns = numColumns(base = 1, sm = 3)) {
                    Row(
                        modifier = Modifier
                            .margin(
                                right = if (breakpoint < Breakpoint.SM) 0.px else 24.px,
                                bottom = if (breakpoint < Breakpoint.SM) 12.px else 0.px
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Switch(
                            modifier = Modifier.margin(right = 8.px),
                            checked = uiState.popular,
                            onCheckedChange = { uiState = uiState.copy(popular = it) },
                            size = SwitchSize.LG
                        )
                        SpanText(
                            modifier = Modifier
                                .fontSize(14.px)
                                .fontFamily(Constants.FONT_FAMILY)
                                .color(Theme.HalfBlack.rgb),
                            text = "Popular"
                        )
                    }

                    Row(
                        modifier = Modifier
                            .margin(
                                right = if (breakpoint < Breakpoint.SM) 0.px else 24.px,
                                bottom = if (breakpoint < Breakpoint.SM) 12.px else 0.px
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Switch(
                            modifier = Modifier.margin(right = 8.px),
                            checked = uiState.main,
                            onCheckedChange = { uiState = uiState.copy(main = it) },
                            size = SwitchSize.LG
                        )
                        SpanText(
                            modifier = Modifier
                                .fontSize(14.px)
                                .fontFamily(Constants.FONT_FAMILY)
                                .color(Theme.HalfBlack.rgb),
                            text = "Main"
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Switch(
                            modifier = Modifier.margin(right = 8.px),
                            checked = uiState.sponsored,
                            onCheckedChange = { uiState = uiState.copy(sponsored = it) },
                            size = SwitchSize.LG
                        )
                        SpanText(
                            modifier = Modifier
                                .fontSize(14.px)
                                .fontFamily(Constants.FONT_FAMILY)
                                .color(Theme.HalfBlack.rgb),
                            text = "Sponsored"
                        )
                    }
                }

                Input(
                    type = InputType.Text,
                    attrs = Modifier
                        .id(Id.TITLE_INPUT)
                        .fillMaxWidth()
                        .height(54.px)
                        .margin(topBottom = 12.px)
                        .padding(leftRight = 20.px)
                        .backgroundColor(Theme.LightGray.rgb)
                        .borderRadius(r = 4.px)
                        .noBorder()
                        .fontFamily(Constants.FONT_FAMILY)
                        .fontSize(16.px)
                        .toAttrs {
                            attr("placeholder", "Title")
                        }
                )

                Input(
                    type = InputType.Text,
                    attrs = Modifier
                        .id(Id.SUBTITLE_INPUT)
                        .fillMaxWidth()
                        .height(54.px)
                        .margin(bottom = 12.px)
                        .padding(leftRight = 20.px)
                        .backgroundColor(Theme.LightGray.rgb)
                        .borderRadius(r = 4.px)
                        .noBorder()
                        .fontFamily(Constants.FONT_FAMILY)
                        .fontSize(16.px)
                        .toAttrs {
                            attr("placeholder", "Subtitle")
                        }
                )

                CategoryDropDown(
                    selectedCategory = uiState.category,
                    onCategorySelect = { uiState = uiState.copy(category = it) }
                )

                Row(
                    modifier = Modifier.fillMaxWidth().margin(topBottom = 12.px),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Switch(
                        modifier = Modifier.margin(right = 8.px),
                        checked = !uiState.thumbnailInputDisabled,
                        onCheckedChange = { uiState = uiState.copy(thumbnailInputDisabled = !it) },
                        size = SwitchSize.MD
                    )
                    SpanText(
                        modifier = Modifier
                            .fontSize(14.px)
                            .fontFamily(Constants.FONT_FAMILY)
                            .color(Theme.HalfBlack.rgb),
                        text = "Paste an Image URL instead"
                    )
                }

                ThumbnailUploader(
                    thumbnail = uiState.thumbnail,
                    thumbnailInputDisabled = uiState.thumbnailInputDisabled,
                    onThumbnailSelect = { filename, file ->
                        (document.getElementById(Id.THUMBNAIL_INPUT) as HTMLInputElement).value =
                            filename
                        uiState = uiState.copy(thumbnail = file)
                    }
                )

                EditorControls(
                    breakpoint = breakpoint,
                    editorVisibility = uiState.editorVisibility,
                    onEditorVisibilityChange = {
                        uiState = uiState.copy(editorVisibility = !uiState.editorVisibility)
                    },
                    onLinkClick = {
                        uiState = uiState.copy(linkPopup = true)
                    },
                    onImageClick = {
                        uiState = uiState.copy(imagePopup = true)
                    }
                )

                Editor(editorVisibility = uiState.editorVisibility)

                CreateButton(onClick = {
                    uiState = uiState.copy(
                        title = (document.getElementById(Id.TITLE_INPUT) as HTMLInputElement).value,
                        subtitle = (document.getElementById(Id.SUBTITLE_INPUT) as HTMLInputElement).value,
                        content = (document.getElementById(Id.EDITOR) as HTMLTextAreaElement).value,
                    )
                    if (!uiState.thumbnailInputDisabled)
                        uiState =
                            uiState.copy(thumbnail = (document.getElementById(Id.THUMBNAIL_INPUT) as HTMLInputElement).value)
                    if (
                        uiState.title.isNotEmpty() &&
                        uiState.subtitle.isNotEmpty() &&
                        uiState.thumbnail.isNotEmpty() &&
                        uiState.content.isNotEmpty()
                    ) {
                        scope.launch {
                            val result = addPost(
                                Post(
                                    author = localStorage.getItem("username").toString(),
                                    title = uiState.title,
                                    subtitle = uiState.subtitle,
                                    date = Date.now().toLong(),
                                    thumbnail = uiState.thumbnail,
                                    content = uiState.content,
                                    category = uiState.category,
                                    popular = uiState.popular,
                                    main = uiState.main,
                                    sponsored = uiState.sponsored
                                )
                            )
                            if (result) {
                                context.router.navigateTo(Screen.AdminSuccess.route)
                            }
                        }
                    } else {
                        scope.launch {
                            uiState = uiState.copy(messagePopup = true)
                            delay(2000)
                            uiState = uiState.copy(messagePopup = false)
                        }
                    }
                })
            }
        }
    }

    if (uiState.messagePopup) {
        MessagePopup(
            message = "Please fill out all fields",
            onDialogDismiss = {
                uiState = uiState.copy(messagePopup = false)
            }
        )
    }

    if (uiState.linkPopup) {
        LinkPopup(
            editorControl = EditorControl.Link,
            onDialogDismiss = {
                uiState = uiState.copy(linkPopup = false)
            },
            onAddClick = { href, title ->
                applyStyle(
                    ControlStyle.Link(
                        selectedText = getSelectedText(),
                        href = href,
                        title = title
                    )
                )
            }
        )
    }

    if (uiState.imagePopup) {
        LinkPopup(
            editorControl = EditorControl.Image,
            onDialogDismiss = {
                uiState = uiState.copy(imagePopup = false)
            },
            onAddClick = { imageUrl, description ->
                applyStyle(
                    ControlStyle.Image(
                        selectedText = getSelectedText(),
                        imageUrl = imageUrl,
                        alt = description
                    )
                )
            }
        )
    }
}

@Composable
fun CategoryDropDown(
    selectedCategory: Category,
    onCategorySelect: (Category) -> Unit
) {
    Box(
        modifier = Modifier
            .classNames("dropdown")
            .fillMaxWidth()
            .height(54.px)
            .backgroundColor(Theme.LightGray.rgb)
            .cursor(Cursor.Pointer)
            .attrsModifier {
                attr("data-bs-toggle", "dropdown")
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(leftRight = 20.px),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            SpanText(
                modifier = Modifier
                    .fillMaxWidth()
                    .fontSize(16.px)
                    .fontFamily(Constants.FONT_FAMILY),
                text = selectedCategory.name
            )
            Box(
                modifier = Modifier
                    .classNames("dropdown-toggle")
            )
        }
        Ul(
            attrs = Modifier
                .fillMaxWidth()
                .classNames("dropdown-menu")
                .toAttrs()
        ) {
            Category.entries.forEach { category ->
                Li {
                    A(
                        attrs = Modifier
                            .classNames("dropdown-item")
                            .color(Colors.Black)
                            .fontFamily(Constants.FONT_FAMILY)
                            .fontSize(16.px)
                            .onClick { onCategorySelect(category) }
                            .toAttrs()
                    ) {
                        Text(
                            value = category.name
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ThumbnailUploader(
    thumbnail: String,
    thumbnailInputDisabled: Boolean,
    onThumbnailSelect: (String, String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .margin(bottom = 20.px)
            .height(54.px)
    ) {
        Input(
            type = InputType.Text,
            attrs = Modifier
                .id(Id.THUMBNAIL_INPUT)
                .fillMaxSize()
                .margin(right = 12.px)
                .padding(leftRight = 20.px)
                .backgroundColor(Theme.LightGray.rgb)
                .borderRadius(r = 4.px)
                .noBorder()
                .fontFamily(Constants.FONT_FAMILY)
                .fontSize(16.px)
                .thenIf(
                    condition = thumbnailInputDisabled,
                    other = Modifier.disabled()
                )
                .toAttrs {
                    attr("placeholder", "Thumbnail")
                    attr("value", thumbnail)
                }
        )
        Button(
            attrs = Modifier
                .onClick {
                    document.loadDataUrlFromDisk(
                        accept = "image/png, image/jpeg",
                        onLoad = {
                            onThumbnailSelect(filename, it)
                        }
                    )
                }
                .fillMaxHeight()
                .padding(leftRight = 24.px)
                .backgroundColor(
                    if (!thumbnailInputDisabled)
                        Theme.Gray.rgb
                    else
                        Theme.Primary.rgb
                )
                .color(
                    if (!thumbnailInputDisabled)
                        Theme.DarkGray.rgb
                    else
                        Colors.White
                )
                .borderRadius(r = 4.px)
                .noBorder()
                .fontFamily(Constants.FONT_FAMILY)
                .fontWeight(FontWeight.Medium)
                .fontSize(14.px)
                .thenIf(
                    condition = !thumbnailInputDisabled,
                    other = Modifier.disabled()
                )
                .toAttrs()
        ) {
            SpanText(text = "Upload")
        }
    }
}

@Composable
fun EditorControls(
    breakpoint: Breakpoint,
    editorVisibility: Boolean,
    onLinkClick: () -> Unit,
    onImageClick: () -> Unit,
    onEditorVisibilityChange: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        SimpleGrid(
            modifier = Modifier.fillMaxWidth(),
            numColumns = numColumns(base = 1, sm = 2)
        ) {
            Row(
                modifier = Modifier
                    .backgroundColor(Theme.LightGray.rgb)
                    .borderRadius(r = 4.px)
                    .height(54.px)
            ) {
                EditorControl.entries.forEach {
                    EditorControlView(
                        control = it,
                        onClick = {
                            applyControlStyle(
                                editorControl = it,
                                onLinkClick = onLinkClick,
                                onImageClick = onImageClick
                            )
                        }
                    )
                }
            }

            Box(
                contentAlignment = Alignment.CenterEnd
            ) {
                Button(
                    attrs = Modifier
                        .height(54.px)
                        .thenIf(
                            condition = breakpoint < Breakpoint.SM,
                            other = Modifier.fillMaxWidth()
                        )
                        .margin(topBottom = if (breakpoint < Breakpoint.SM) 12.px else 0.px)
                        .padding(leftRight = 24.px)
                        .borderRadius(r = 4.px)
                        .backgroundColor(
                            if (editorVisibility) Theme.LightGray.rgb else Theme.Primary.rgb
                        )
                        .color(
                            if (editorVisibility) Theme.DarkGray.rgb else Colors.White
                        )
                        .noBorder()
                        .onClick {
                            onEditorVisibilityChange()
                            document.getElementById(Id.EDITOR_PREVIEW)?.innerHTML = getEditor().value
                            js("hljs.highlightAll()") as Unit
                        }
                        .toAttrs()
                ) {
                    SpanText(
                        modifier = Modifier
                            .fontFamily(Constants.FONT_FAMILY)
                            .fontWeight(FontWeight.Medium)
                            .fontSize(14.px),
                        text = "Preview"
                    )
                }
            }
        }
    }
}

@Composable
fun EditorControlView(
    control: EditorControl,
    onClick: () -> Unit
) {
    Box(
        modifier = EditorKeyStyle.toModifier()
            .fillMaxHeight()
            .padding(leftRight = 12.px)
            .borderRadius(r = 4.px)
            .cursor(Cursor.Pointer)
            .onClick {
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Image(
            src = control.icon,
            description = "${control.name} Icon"
        )
    }
}

@Composable
fun Editor(editorVisibility: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        TextArea(
            attrs = Modifier
                .id(Id.EDITOR)
                .fillMaxWidth()
                .height(400.px)
                .maxHeight(400.px)
                .resize(Resize.None)
                .margin(top = 8.px)
                .padding(all = 20.px)
                .backgroundColor(Theme.LightGray.rgb)
                .borderRadius(r = 4.px)
                .noBorder()
                .visibility(
                    if (editorVisibility) Visibility.Visible else Visibility.Hidden
                )
                .onKeyDown {
                    if (it.code == "Enter" && it.shiftKey) {
                        applyStyle(
                            controlStyle = ControlStyle.Break(
                                selectedText = getSelectedText()
                            )
                        )
                    }
                }
                .fontFamily(Constants.FONT_FAMILY)
                .fontSize(16.px)
                .toAttrs {
                    attr("placeholder", "Type here...")
                }
        )

        Div(
            attrs = Modifier
                .id(Id.EDITOR_PREVIEW)
                .fillMaxWidth()
                .height(400.px)
                .maxHeight(400.px)
                .resize(Resize.None)
                .margin(top = 8.px)
                .padding(all = 20.px)
                .backgroundColor(Theme.LightGray.rgb)
                .borderRadius(r = 4.px)
                .noBorder()
                .visibility(
                    if (editorVisibility) Visibility.Hidden else Visibility.Visible
                )
                .overflow(Overflow.Auto)
                .scrollBehavior(ScrollBehavior.Smooth)
                .toAttrs()
        )
    }
}

@Composable
fun CreateButton(onClick: () -> Unit) {
    Button(
        attrs = Modifier
            .onClick { onClick() }
            .fillMaxWidth()
            .height(54.px)
            .margin(top = 24.px)
            .backgroundColor(Theme.Primary.rgb)
            .color(Colors.White)
            .borderRadius(r = 4.px)
            .noBorder()
            .fontFamily(Constants.FONT_FAMILY)
            .fontSize(16.px)
            .toAttrs()
    ) {
        SpanText(text = "Create")
    }
}