package ch.ost.mge.amberg.water

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
        val radiusKm = intent.getIntExtra("radius", 5)

        setContent {
            ToTheWateringHoleTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Wells(radiusKm, null)
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
            supportingText = { Text(text = point.body() + " ${point.lon} | ${point.lat}") },
            trailingContent = {
                FilledTonalIconButton(
                    onClick = { directionFromCurrentMap(point.lon.toString(), point.lat.toString()) },
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
    fun PointList(points: List<OSMNode>, padding: PaddingValues) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding)
                .offset(y = 5.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(points) { point ->
                PointView(point)
            }
        }
    }

    @Composable
    fun Wells(radius: Int, defaultPoints: List<OSMNode>?) {

        var points by remember{mutableStateOf(defaultPoints?:listOf<OSMNode>())}

        val defaultError = stringResource(id = R.string.list_empty)
        var error by remember{ mutableStateOf(defaultError) }

        pointService.getPoints(radius.toFloat(), { points = it }, {error = it.message ?: defaultError})

        Layout(
            modifier = Modifier.fillMaxWidth()
        ) {padding ->

            if(points.isNotEmpty())
                PointList(points, padding)
            else
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ){ Text(text = stringResource(id = R.string.list_empty)) }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun WellsPreview() {

        ToTheWateringHoleTheme {
            // A surface container using the 'background' color from the theme
            Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                Wells(5, (0L..10L).map{
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

