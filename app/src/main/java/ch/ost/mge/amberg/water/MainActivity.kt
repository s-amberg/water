package ch.ost.mge.amberg.water

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.ost.mge.amberg.water.ui.theme.ToTheWateringHoleTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ToTheWateringHoleTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    MainContent()
                }
            }
        }
    }
}

fun loadWater() {

}


@Composable
fun MainContent(modifier: Modifier = Modifier) {
    Layout(
        modifier
    ){padding ->
        SearchButton()
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Layout(modifier: Modifier = Modifier, content: @Composable (PaddingValues) -> Unit) {
    Scaffold(
        modifier = modifier,
        topBar = {CenterAlignedTopAppBar(
            colors = TopAppBarDefaults.smallTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.primary,
            ),
            title = {
                Text(stringResource(R.string.app_name))
            }
        ) }
    ) {padding -> content(padding) }
}

@Composable
fun SearchButton(modifier: Modifier = Modifier) {

    Column(
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        FilledTonalButton(
            onClick = { loadWater() },
            shape = CircleShape,
            border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
            modifier = Modifier
                .padding(4.dp)
                .size(300.dp)
                .shadow(4.dp, CircleShape)
        ) {
            Text(
                text = stringResource(R.string.search),
                modifier = modifier,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelLarge
            )
        }

    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ToTheWateringHoleTheme {
        MainContent()
    }
}