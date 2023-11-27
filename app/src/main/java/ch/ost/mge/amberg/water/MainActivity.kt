package ch.ost.mge.amberg.water

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
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
    var useOverpass by remember { mutableStateOf(true) }


    Column(
        modifier = modifier
            .padding(bottom = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ){
        Column(horizontalAlignment = Alignment.CenterHorizontally){
            Slider(
                value = radiusKm.toFloat(),
                onValueChange = { radiusKm = it.toInt() },
                modifier = Modifier.padding(horizontal = 24.dp),
                valueRange = 1F..25F,
                steps = 5
            )
            Text(text = radiusKm.toString() + stringResource(id = R.string.km), color = MaterialTheme.colorScheme.primary)
        }

        Column(
            modifier = Modifier
                .weight(1F),
            verticalArrangement = Arrangement.Center
        ) {

            FilledTonalButton(
                onClick = { loadWater(context, radiusKm, useOverpass) },
                shape = CircleShape,
                border = BorderStroke(8.dp, MaterialTheme.colorScheme.primary),
                colors = ButtonDefaults.filledTonalButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
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

        Row(
            horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .shadow(16.dp, shape = CircleShape)
                .clip(shape = CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(horizontal = 8.dp)

        ){
            Switch(
                checked = useOverpass,
                onCheckedChange = { useOverpass = it },
                colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.primaryContainer))
            Text(
                modifier = Modifier.padding(horizontal = 8.dp),
                text = if(useOverpass) stringResource(id = R.string.overpass) else stringResource(id = R.string.osm))
        }
    }
}

fun loadWater(context: Context, radiusKm: Int, useOverpass: Boolean) {
    val intent = Intent(context, PointsActivity::class.java)
        .putExtra("radius", radiusKm)
        .putExtra("useOverpass", useOverpass)
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