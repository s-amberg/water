package ch.ost.mge.amberg.water

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import ch.ost.mge.amberg.water.models.DEFAULT_POINT
import ch.ost.mge.amberg.water.models.OSMNode
import ch.ost.mge.amberg.water.models.OSMTag
import ch.ost.mge.amberg.water.models.Point
import ch.ost.mge.amberg.water.ui.theme.ToTheWateringHoleTheme

class PointsActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val radiusKm = intent.getIntExtra("radius", 5)
        val useOverpass = intent.getBooleanExtra("useOverpass", true)

        setContent {
            ToTheWateringHoleTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    PermissionRequesterView(rationaleTitle = stringResource(id = R.string.request_permission), rationale = stringResource(id = R.string.permission_rationale)){
                        Wells(it, radiusKm, useOverpass, null)
                    }
                }
            }
        }
    }
}

private fun pinLocationMap(context: Context, latitude: String, longitude: String) {
    // Create a Uri from an intent string. Open map using intent to pin a specific location (latitude, longitude)
    val mapUri = Uri.parse("https://maps.google.com/maps/search/$latitude,$longitude")
    val intent = Intent(Intent.ACTION_VIEW, mapUri)
    startActivity(context, intent, null)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PointView(point: OSMNode) {
    val context = LocalContext.current
    ListItem(
        colors = ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            headlineColor = MaterialTheme.colorScheme.onSecondaryContainer),
        headlineText = { Text(text = point.title()) },
        supportingText = { Text(text = point.body()) },
        leadingContent = {Text(
            modifier = Modifier.width(IntrinsicSize.Min),
            text = String.format("%.2f", point.distance) + stringResource(id = R.string.km),
            maxLines = 2,
            overflow = TextOverflow.Visible,
            softWrap = true
        )},
        trailingContent = {
            FilledTonalIconButton(
                onClick = { pinLocationMap(context, point.lat.toString(), point.lon.toString()) },
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
        items(points, key = {it.id}) { point ->
            PointView(point)
        }
    }
}

@Composable
fun Wells(currentLocation: Point, radius: Int, useOverpass: Boolean, defaultPoints: List<OSMNode>?) {

    val pointService by lazy{PointService()}
    var points by remember{mutableStateOf(defaultPoints?:listOf<OSMNode>())}

    val defaultError = stringResource(id = R.string.list_empty)
    var error: String? by remember{ mutableStateOf(null) }
    var isLoading by remember { mutableStateOf(true) }

    pointService.getPoints(radius.toFloat(), currentLocation, useOverpass, {
        isLoading = false
        points = it
        error = null
    }, {
        isLoading = false
        error = it.message ?: defaultError
    })

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
            ){ StatusDisplay(error, isLoading)}
    }
}

@Composable
private fun Layout(modifier: Modifier = Modifier, content: @Composable (PaddingValues) -> Unit) {
    val activity = (LocalContext.current as? Activity)

    Layout(modifier, {
        Text(
            text = stringResource(R.string.search_title),
            style = MaterialTheme.typography.titleLarge,

            )
    }, {
        OutlinedIconButton(
            onClick = { activity?.finish() },
            colors = IconButtonDefaults.outlinedIconButtonColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        ) {
            Icon(Icons.Filled.ArrowBack, "back",)
        }
    },  content)
}

@Preview(showBackground = true)
@Composable
fun WellsPreview() {

    ToTheWateringHoleTheme {
        // A surface container using the 'background' color from the theme
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            Wells(DEFAULT_POINT, 5, true, (0L..10L).map{
                OSMNode("node", it, it *2.0, it.toDouble(),
                    OSMTag("", amenity = "drinking_water","", drinking_water = true, name = "point${it}", null, null, man_made = null, null, null, null),
                    location = DEFAULT_POINT
                )
            })
        }
    }
}
