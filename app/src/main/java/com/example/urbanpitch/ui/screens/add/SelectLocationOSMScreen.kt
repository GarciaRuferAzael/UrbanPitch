import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.urbanpitch.data.remote.OSMDataSource
import com.example.urbanpitch.utils.Coordinates
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.events.MapEventsReceiver

@SuppressLint("ClickableViewAccessibility")
@Composable
fun SelectLocationOSMScreen(
    initialLatitude: Double = 41.9028,
    initialLongitude: Double = 12.4964,
    navController: NavController,
    osmDataSource: OSMDataSource
) {
    val context = LocalContext.current
    var mapView by remember { mutableStateOf<MapView?>(null) }
    var selectedPoint by remember { mutableStateOf(GeoPoint(initialLatitude, initialLongitude)) }

    DisposableEffect(Unit) {
        Configuration.getInstance().load(context, context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE))
        onDispose {
            mapView?.onDetach()
        }
    }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = {
            MapView(it).apply {
                setTileSource(org.osmdroid.tileprovider.tilesource.TileSourceFactory.MAPNIK)
                controller.setZoom(15.0)
                controller.setCenter(selectedPoint)
                setMultiTouchControls(true)
                mapView = this

                // Marker iniziale
                val marker = Marker(this).apply {
                    position = selectedPoint
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                }
                overlays.add(marker)

                // EVENT HANDLER usando MapEventsOverlay
                val mapEventsOverlay = MapEventsOverlay(object : MapEventsReceiver {
                    override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                        if (p != null) {
                            selectedPoint = p

                            overlays.removeAll { it is Marker }
                            overlays.add(Marker(this@apply).apply {
                                position = selectedPoint
                                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                            })

                            val coordinates = Coordinates(p.latitude, p.longitude)

                            CoroutineScope(Dispatchers.IO).launch {
                                try {
                                    val place = osmDataSource.getPlace(coordinates)
                                    withContext(Dispatchers.Main) {
                                        navController.previousBackStackEntry?.savedStateHandle?.apply {
                                            set("selected_latitude", p.latitude)
                                            set("selected_longitude", p.longitude)
                                            set("selected_city", place.displayName)
                                        }
                                        navController.popBackStack()
                                    }
                                } catch (e: Exception) {
                                    // errore nella chiamata a osmDataSource
                                }
                            }

                            invalidate()
                        }
                        return true
                    }

                    override fun longPressHelper(p: GeoPoint?): Boolean {
                        return false
                    }
                })

                overlays.add(mapEventsOverlay)
            }
        }
    )
}
