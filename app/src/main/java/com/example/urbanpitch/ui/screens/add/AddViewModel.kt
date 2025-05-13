package com.example.urbanpitch.ui.screens.add

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.urbanpitch.data.database.Pitch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class AddPitchState(
    val name: String = "",
    val description: String = "",
    val city: String = "",
    val latitude: Float = 0f,
    val longitude: Float = 0f,
    val imageUrl: Uri = Uri.EMPTY,

    val showLocationDisabledAlert: Boolean = false,
    val showLocationPermissionDeniedAlert: Boolean = false,
    val showLocationPermissionPermanentlyDeniedSnackbar: Boolean = false,
    val showNoInternetConnectivitySnackbar: Boolean = false
) {
    val canSubmit get() = name.isNotBlank() && city.isNotBlank() && description.isNotBlank()

    fun toPitch() = Pitch(
        id = "", // lascia id vuoto, sarà Firebase a generarlo
        name = name,
        description = description,
        city = city,
        imageUrl = imageUrl.toString(),
        latitude = latitude,
        longitude = longitude
    )
}


interface AddPitchActions {
    fun setName(name: String)
    fun setDescription(description: String)
    fun setCity(city: String)
    fun setLatitude(latitude: Float)
    fun setLongitude(longitude: Float)
    fun setImageUrl(imageUrl: Uri)

    fun setShowLocationDisabledAlert(show: Boolean)
    fun setShowLocationPermissionDeniedAlert(show: Boolean)
    fun setShowLocationPermissionPermanentlyDeniedSnackbar(show: Boolean)
    fun setShowNoInternetConnectivitySnackbar(show: Boolean)
}

class AddViewModel : ViewModel() {
    private val _state = MutableStateFlow(AddPitchState())
    val state = _state.asStateFlow()

    val actions = object : AddPitchActions {

        override fun setName(name: String) =
            _state.update { it.copy(name = name) }

        override fun setDescription(description: String) =
            _state.update { it.copy(description = description) }

        override fun setCity(city: String) =
            _state.update { it.copy(city = city) }

        override fun setLatitude(latitude: Float) =
            _state.update { it.copy(latitude = latitude) }

        override fun setLongitude(longitude: Float) =
            _state.update { it.copy(longitude = longitude) }

        override fun setImageUrl(imageUrl: Uri) =
            _state.update { it.copy(imageUrl = imageUrl) }


        override fun setShowLocationDisabledAlert(show: Boolean) =
            _state.update { it.copy(showLocationDisabledAlert = show) }

        override fun setShowLocationPermissionDeniedAlert(show: Boolean) =
            _state.update { it.copy(showLocationPermissionDeniedAlert = show) }

        override fun setShowLocationPermissionPermanentlyDeniedSnackbar(show: Boolean) =
            _state.update { it.copy(showLocationPermissionPermanentlyDeniedSnackbar = show) }

        override fun setShowNoInternetConnectivitySnackbar(show: Boolean) =
            _state.update { it.copy(showNoInternetConnectivitySnackbar = show) }

    }

    fun setLatitudeLongitude(lat: Float, lon: Float) {
        _state.update { it.copy(latitude = lat, longitude = lon) }
    }
}
