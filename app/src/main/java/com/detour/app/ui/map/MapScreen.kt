package com.detour.app.ui.map

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.detour.app.data.model.Detour
import com.detour.app.data.model.DetourResponse
import com.detour.app.viewmodel.DetourViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.gson.Gson
import com.google.maps.android.PolyUtil
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import java.net.URLEncoder

private val Background = Color(0xFF0D0D0D)
private val Orange = Color(0xFFFF6D00)
private val SheetBg = Color(0xFF1A1A1A)
private val TextPrimary = Color(0xFFFFFFFF)
private val TextSecondary = Color(0xFF9E9E9E)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    navController: NavController,
    detourResponseJson: String,
    viewModel: DetourViewModel = viewModel()
) {
    val detourResponse = remember {
        Gson().fromJson(detourResponseJson, DetourResponse::class.java)
    }
    Log.d("MapScreen", "Detours received: ${detourResponse.proposed_detours.size}")

    BackHandler {
        navController.popBackStack("setup", inclusive = false)
    }

    val cameraPositionState = rememberCameraPositionState()
    val scaffoldState = rememberBottomSheetScaffoldState()
    var mapLoaded by remember { mutableStateOf(false) }

    LaunchedEffect(mapLoaded) {
        if (!mapLoaded) return@LaunchedEffect
        val builder = LatLngBounds.Builder()
        PolyUtil.decode(detourResponse.baseline_route.polyline).forEach { builder.include(it) }
        detourResponse.proposed_detours.forEach { builder.include(LatLng(it.latitude, it.longitude)) }
        cameraPositionState.animate(CameraUpdateFactory.newLatLngBounds(builder.build(), 120))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        BottomSheetScaffold(
            scaffoldState = scaffoldState,
            sheetPeekHeight = 240.dp,
            sheetContainerColor = SheetBg,
            sheetShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
            containerColor = Color.Transparent,
            sheetContent = {
                DetourBottomSheet(
                    detours = detourResponse.proposed_detours,
                    onDetourClick = { detour ->
                        viewModel.selectDetour(detour)
                        val encoded = URLEncoder.encode(Gson().toJson(detour), "UTF-8")
                        navController.navigate("detail/$encoded")
                    }
                )
            }
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                onMapLoaded = { mapLoaded = true }
            ) {
                Polyline(
                    points = PolyUtil.decode(detourResponse.baseline_route.polyline),
                    color = Orange,
                    width = 10f
                )
                detourResponse.proposed_detours.forEach { detour ->
                    Marker(
                        state = rememberMarkerState(
                            position = LatLng(detour.latitude, detour.longitude)
                        ),
                        title = detour.name,
                        snippet = detour.category.replace("_", " "),
                        icon = BitmapDescriptorFactory.defaultMarker(markerHue(detour.category))
                    )
                }
            }
        }

        // Top bar overlaid on the map
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            IconButton(
                onClick = { navController.popBackStack("setup", inclusive = false) },
                modifier = Modifier
                    .size(42.dp)
                    .background(SheetBg.copy(alpha = 0.88f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextPrimary
                )
            }
            Box(
                modifier = Modifier
                    .background(SheetBg.copy(alpha = 0.88f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "${detourResponse.proposed_detours.size} stops found",
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
private fun DetourBottomSheet(
    detours: List<Detour>,
    onDetourClick: (Detour) -> Unit
) {
    Column {
        Box(
            modifier = Modifier
                .width(40.dp)
                .height(4.dp)
                .align(Alignment.CenterHorizontally)
                .background(Color(0xFF3A3A3A), RoundedCornerShape(2.dp))
        )
        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = "Suggested Detours",
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier.padding(horizontal = 20.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(detours) { detour ->
                DetourListItem(detour = detour, onClick = { onDetourClick(detour) })
            }
        }
    }
}

@Composable
private fun DetourListItem(detour: Detour, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(categoryColor(detour.category), CircleShape)
        )
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = detour.name,
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp
            )
            Text(
                text = detour.category.replace("_", " ").replaceFirstChar { it.uppercase() },
                color = TextSecondary,
                fontSize = 12.sp
            )
        }
        Text(
            text = "+%.1f km".format(detour.added_distance_km),
            color = Orange,
            fontWeight = FontWeight.Medium,
            fontSize = 13.sp
        )
    }
}

private fun markerHue(category: String): Float = when (category) {
    "scenic_lookout", "park" -> BitmapDescriptorFactory.HUE_GREEN
    "cafe", "restaurant" -> BitmapDescriptorFactory.HUE_YELLOW
    "tourist_attraction" -> BitmapDescriptorFactory.HUE_VIOLET
    else -> BitmapDescriptorFactory.HUE_ORANGE
}

private fun categoryColor(category: String): Color = when (category) {
    "scenic_lookout", "park" -> Color(0xFF4CAF50)
    "cafe", "restaurant" -> Color(0xFFFFEB3B)
    "tourist_attraction" -> Color(0xFF9C27B0)
    else -> Orange
}
