package ch.ost.mge.amberg.water

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rxjava3.subscribeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.ost.mge.amberg.water.models.OSMNode
import ch.ost.mge.amberg.water.models.OSMTag
import ch.ost.mge.amberg.water.ui.theme.ToTheWateringHoleTheme

class PointsActivity : ComponentActivity() {

    private val pointService by lazy { PointService() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ToTheWateringHoleTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Wells()
                }
            }
        }
    }

    private fun directionFromCurrentMap(destinationLatitude: String, destinationLongitude: String) {
        // Create a Uri from an intent string. Open map using intent to show direction from current location (latitude, longitude) to specific location (latitude, longitude)
        val mapUri = Uri.parse("https://maps.google.com/maps?daddr=$destinationLatitude,$destinationLongitude")
        val intent = Intent(Intent.ACTION_VIEW, mapUri)
        startActivity(intent)
    }


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun PointView(point: OSMNode) {
        ListItem(
            colors = ListItemDefaults.colors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                headlineColor = MaterialTheme.colorScheme.onSecondaryContainer),
            headlineText = { Text(text = point.title()) },
            supportingText = { Text(text = point.body()) },
            trailingContent = {
                FilledTonalIconButton(
                    onClick = { directionFromCurrentMap(point.long.toString(), point.lat.toString()) },
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        contentColor = MaterialTheme.colorScheme.secondary
                    )

                ) {
                    Icon(
                        Icons.Filled.LocationOn,
                        contentDescription = null
                    )
                }
            }
        )
    }

    @Composable
    fun Wells(defaultPoints: List<OSMNode> = listOf<OSMNode>()) {

        val points = pointService.getPoints().subscribeAsState(defaultPoints)
        val pointState by remember(points){ points }

            Layout(
                modifier = Modifier.fillMaxWidth()
            ) {padding ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(padding)
                        .offset(y = 5.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(pointState) { point ->
                        PointView(point)
                    }
                }
            }
    }

    @Preview(showBackground = true)
    @Composable
    fun WellsPreview() {

        ToTheWateringHoleTheme {
            // A surface container using the 'background' color from the theme
            Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                Wells((0..10).map{
                    OSMNode("node", it, it *2.0, it.toDouble(), "",
                        OSMTag("", drinking_water = true, name = "point${it}", amenity = "drinking_water", man_made = null))
                })
            }
        }
    }
    @Composable
    private fun Layout(modifier: Modifier = Modifier, content: @Composable (PaddingValues) -> Unit) {
        Layout(modifier, {
            Text(
                text = stringResource(R.string.search_title),
                style = MaterialTheme.typography.titleLarge,

                )
        }, {
            OutlinedIconButton(
                onClick = { finish() },
                colors = IconButtonDefaults.outlinedIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            ) {
                Icon(Icons.Filled.ArrowBack, "back",)
            }
        },  content)
    }
}

