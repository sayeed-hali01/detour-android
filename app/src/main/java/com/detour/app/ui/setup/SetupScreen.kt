package com.detour.app.ui.setup

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.util.Log
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.detour.app.viewmodel.DetourViewModel
import com.detour.app.viewmodel.UiState
import com.google.gson.Gson
import java.net.URLEncoder

private val Background = Color(0xFF0D0D0D)
private val Orange = Color(0xFFFF6D00)
private val Green = Color(0xFF4CAF50)
private val SurfaceColor = Color(0xFF1A1A1A)
private val TextPrimary = Color(0xFFFFFFFF)
private val TextSecondary = Color(0xFF9E9E9E)
private val BorderIdle = Color(0xFF3A3A3A)

private val Categories = listOf(
    "scenic_lookout" to "Scenic Lookout",
    "cafe" to "Cafe",
    "restaurant" to "Restaurant",
    "park" to "Park",
    "tourist_attraction" to "Tourist Attraction"
)

private val RadiusOptions = listOf(
    "5 km" to 10,
    "10 km" to 20,
    "20 km" to 40
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SetupScreen(
    navController: NavController,
    viewModel: DetourViewModel = viewModel()
) {
    val uiState by viewModel.uiState.observeAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var origin by remember { mutableStateOf("") }
    var destination by remember { mutableStateOf("") }
    var selectedCategories by remember { mutableStateOf(setOf<String>()) }
    var selectedRadius by remember { mutableStateOf("10 km") }

    val isLoading = uiState is UiState.Loading

    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is UiState.Success -> {
                Log.d("SetupScreen", "Navigating with detours: ${state.data.proposed_detours.size}")
                val encoded = URLEncoder.encode(Gson().toJson(state.data), "UTF-8")
                navController.navigate("map/$encoded") {
                    popUpTo("setup") { inclusive = false }
                    launchSingleTop = true
                }
            }
            is UiState.Error -> snackbarHostState.showSnackbar(state.message)
            else -> {}
        }
    }

    Scaffold(
        containerColor = Background,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(top = 48.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(28.dp)
        ) {
            // Title
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = TextPrimary)) { append("Detou") }
                    withStyle(SpanStyle(color = Orange)) { append("r") }
                },
                fontSize = 42.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-1).sp
            )

            // Route inputs
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                RouteTextField(
                    value = origin,
                    onValueChange = { origin = it },
                    label = "Origin",
                    dotColor = Green
                )
                RouteTextField(
                    value = destination,
                    onValueChange = { destination = it },
                    label = "Destination",
                    dotColor = Orange
                )
            }

            // Interests
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SectionLabel("Your Interests")
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Categories.forEach { (key, label) ->
                        val selected = key in selectedCategories
                        FilterChip(
                            selected = selected,
                            onClick = {
                                selectedCategories = if (selected)
                                    selectedCategories - key
                                else
                                    selectedCategories + key
                            },
                            label = { Text(label, fontSize = 13.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = SurfaceColor,
                                selectedContainerColor = Orange.copy(alpha = 0.15f),
                                labelColor = TextSecondary,
                                selectedLabelColor = Orange
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = selected,
                                borderColor = BorderIdle,
                                selectedBorderColor = Orange
                            )
                        )
                    }
                }
            }

            // Radius selector
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SectionLabel("Max Detour")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    RadiusOptions.forEach { (label, _) ->
                        val selected = selectedRadius == label
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .weight(1f)
                                .border(
                                    width = 1.5.dp,
                                    color = if (selected) Orange else BorderIdle,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .background(
                                    color = if (selected) Orange.copy(alpha = 0.12f) else Color.Transparent,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable { selectedRadius = label }
                                .padding(vertical = 14.dp)
                        ) {
                            Text(
                                text = label,
                                color = if (selected) Orange else TextSecondary,
                                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // CTA
            Button(
                onClick = {
                    val minutes = RadiusOptions.first { it.first == selectedRadius }.second
                    viewModel.planDetour(
                        origin = origin,
                        destination = destination,
                        categories = selectedCategories.toList(),
                        maxDetourMinutes = minutes
                    )
                },
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Orange,
                    contentColor = Color.White,
                    disabledContainerColor = Orange.copy(alpha = 0.4f),
                    disabledContentColor = Color.White
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(22.dp)
                    )
                } else {
                    Text(
                        text = "Find Detours",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun RouteTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    dotColor: Color
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(dotColor, CircleShape)
            )
        },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            focusedBorderColor = Orange,
            unfocusedBorderColor = BorderIdle,
            focusedLabelColor = Orange,
            unfocusedLabelColor = TextSecondary,
            cursorColor = Orange
        )
    )
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        color = TextPrimary,
        fontSize = 16.sp,
        fontWeight = FontWeight.SemiBold
    )
}
