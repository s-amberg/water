package ch.ost.mge.amberg.water

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

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
//                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    title()
                },
                navigationIcon = { if (navigationIcon != null) { navigationIcon() } }
            )
        }
    ) {padding -> content(padding) }
}

@Composable
fun StatusDisplay(error: String?, isLoading: Boolean) {
    return  if(isLoading)
                CircularProgressIndicator(
                    modifier = Modifier.width(64.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                )
            else
                Text(
                    text = error ?: stringResource(id = R.string.list_empty)
                )
}
