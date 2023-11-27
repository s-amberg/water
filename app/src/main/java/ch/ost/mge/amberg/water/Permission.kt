package ch.ost.mge.amberg.water

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.location.Location
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import ch.ost.mge.amberg.water.models.DEFAULT_POINT
import ch.ost.mge.amberg.water.models.Point
import ch.ost.mge.amberg.water.models.toPoint
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource


@Composable
fun PermissionRequesterView(rationaleTitle: String, rationale: String, content: @Composable (location: Point)->Unit) {
    val context = (LocalContext.current)
    val x = LocationServices.getFusedLocationProviderClient(context)
    var currentLocation: Point? by remember{ mutableStateOf(null) }


    fun areLocationPermissionsAlreadyGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    var locationPermissionsGranted by remember{ mutableStateOf(areLocationPermissionsAlreadyGranted()) }
    //only set to true after permission was request and denied
    var locationPermissionsDenied by remember{ mutableStateOf(false) }
    var shouldShowPermissionRationale by remember {
        mutableStateOf(
            ActivityCompat.shouldShowRequestPermissionRationale(
                context as Activity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    fun decideCurrentPermissionStatus(locationPermissionsGranted: Boolean,
                                      shouldShowPermissionRationale: Boolean): String {
        return if (locationPermissionsGranted) "Granted"
        else if (shouldShowPermissionRationale) "Rejected"
        else "Denied"
    }

    var currentPermissionsStatus by remember {
        mutableStateOf(decideCurrentPermissionStatus(locationPermissionsGranted, shouldShowPermissionRationale))
    }

    val locationPermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            locationPermissionsGranted = permissions.values.reduce { acc, isPermissionGranted ->
                acc || isPermissionGranted
            }

            if (!locationPermissionsGranted) {
                shouldShowPermissionRationale = ActivityCompat.shouldShowRequestPermissionRationale(context as Activity, Manifest.permission.ACCESS_COARSE_LOCATION)
            }
            currentPermissionsStatus = decideCurrentPermissionStatus(locationPermissionsGranted, shouldShowPermissionRationale)
            locationPermissionsDenied = currentPermissionsStatus == "Denied"
        })

    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(key1 = lifecycleOwner, effect = {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START &&!locationPermissionsGranted && !shouldShowPermissionRationale) {
                locationPermissionLauncher.launch(locationPermissions)
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    })

    data class RationaleState(
        val title: String,
        val rationale: String,
        val onRationaleReply: (proceed: Boolean) -> Unit,
    )

    @Composable
    fun PermissionRationaleDialog(rationaleState: RationaleState) {
        AlertDialog(onDismissRequest = { rationaleState.onRationaleReply(false) }, title = {
            Text(text = rationaleState.title)
        }, text = {
            Text(text = rationaleState.rationale)
        }, confirmButton = {
            TextButton(onClick = { rationaleState.onRationaleReply(true) }) { Text("Continue") }
        }, dismissButton = {
            TextButton(onClick = { rationaleState.onRationaleReply(false) }) { Text("Dismiss") }
        })
    }

    @Composable
    fun LocationPermissions(title: String, rationale: String) {
        PermissionRationaleDialog(rationaleState = RationaleState(
            title = title,
            rationale = rationale,
            onRationaleReply = { proceed ->
                if (proceed) {
                    locationPermissionLauncher.launch(locationPermissions)
                }
                shouldShowPermissionRationale = false
                currentPermissionsStatus = decideCurrentPermissionStatus(locationPermissionsGranted, shouldShowPermissionRationale)
                locationPermissionsDenied = currentPermissionsStatus == "Denied"
            }
        ))
    }

    @Composable
    fun Loader() {
        Column(
            modifier = androidx.compose.ui.Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ){ StatusDisplay(error = null, isLoading = currentLocation == null) }}

    // initial deny
    if(shouldShowPermissionRationale) LocationPermissions(title = rationaleTitle, rationale = rationale)
    // second deny => show default points
    else if(locationPermissionsDenied) content(DEFAULT_POINT)
    // permission not yet answered
    else if(!locationPermissionsGranted) Loader()
    // permission granted to some degree
    else {
        val request = CurrentLocationRequest.Builder().build()

        val cancellation = CancellationTokenSource()
        x.getCurrentLocation(request, cancellation.token)
            .addOnSuccessListener { location: Location? ->
                if (location == null)
                    Toast.makeText(context, "Cannot get location.", Toast.LENGTH_SHORT).show()
                else {
                    currentLocation = location.toPoint()
                    cancellation.cancel()
                }
            }

        if (currentLocation == null) Loader()
        else {
            cancellation.cancel()
            content(currentLocation!!)
        }
    }
}
