package com.haxon.blog.pages.admin

import androidx.compose.runtime.Composable
import com.haxon.blog.components.AdminPageLayout
import com.haxon.blog.util.isUserLoggedIn
import com.varabyte.kobweb.core.Page

@Page("create")
@Composable
fun CreatePage() {
    isUserLoggedIn {
        CreateScreen()
    }
}

@Composable
fun CreateScreen() {
    AdminPageLayout {

    }
}