package ch.ost.mge.amberg.water

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
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


fun loadWater(context: Context) {
    val intent = Intent(context, PointsActivity::class.java)
    startActivity(context, intent, null)
}

@Composable
fun MainContent(modifier: Modifier = Modifier) {
    Layout(
        modifier
    ){padding ->
        SearchButton(Modifier.padding(padding))
    }

}

@Composable
private fun Layout(modifier: Modifier = Modifier, content: @Composable (PaddingValues) -> Unit) {
    Layout(modifier, { Text(stringResource(R.string.app_name)) }, null, content)
}

@Composable
fun SearchButton(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        FilledTonalButton(
            onClick = { loadWater(context) },
            shape = CircleShape,
            border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
            modifier = Modifier
                .padding(4.dp)
                .size(300.dp)
                .shadow(4.dp, CircleShape)
        ) {
            Text(
                text = stringResource(R.string.search),
                modifier = Modifier,
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