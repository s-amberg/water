package ch.ost.mge.amberg.water

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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




@Composable
fun SearchButton(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    var radiusKm: Int by remember { mutableIntStateOf(5) }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Slider(
                value = radiusKm.toFloat(),
                onValueChange = { radiusKm = it.toInt() },
                modifier = Modifier.padding(horizontal = 24.dp),
                valueRange = 1F..25F,
                steps = 5
            )
            Text(text = radiusKm.toString() + stringResource(id = R.string.km),
                color = MaterialTheme.colorScheme.primary)
        }

        Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.Center
        ) {

            FilledTonalButton(
                onClick = { loadWater(context, radiusKm) },
                shape = CircleShape,
                border = BorderStroke(8.dp, MaterialTheme.colorScheme.primary),
                modifier = Modifier
                    .padding(8.dp)
                    .size(300.dp)
                    .shadow(16.dp, CircleShape)
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

}

fun loadWater(context: Context, radiusKm: Int) {
    val intent = Intent(context, PointsActivity::class.java).putExtra("radius", radiusKm)
    startActivity(context, intent, null)
}

@Composable
fun MainContent(modifier: Modifier = Modifier) {

    Layout(
        modifier.fillMaxSize()
    ){padding ->
        Box(
            Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = R.drawable.water),
                contentDescription = "Background",
                contentScale = ContentScale.FillHeight,
                modifier = Modifier.matchParentSize()
            )
            SearchButton(
                Modifier
                    .padding(padding)
                    .matchParentSize()
            )
        }

    }

}

@Composable
private fun Layout(modifier: Modifier = Modifier, content: @Composable (PaddingValues) -> Unit) {
    Layout(modifier, { Text(stringResource(R.string.app_name)) }, null, content)
}



@Preview(showBackground = true)
@Composable
fun MainPreview() {
    ToTheWateringHoleTheme {
        MainContent()
    }
}