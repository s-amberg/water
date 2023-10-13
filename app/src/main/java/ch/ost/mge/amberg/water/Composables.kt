package ch.ost.mge.amberg.water

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Layout(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit,
    navigationIcon: (@Composable () -> Unit)? = null,
    content: @Composable (PaddingValues) -> Unit) {

    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    title()
                },
                navigationIcon = { if (navigationIcon != null) { navigationIcon() } }
            )
        }
    ) {padding -> content(padding) }
}