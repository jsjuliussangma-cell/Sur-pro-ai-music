package com.surpro.aimusic.ui.screens.music

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.surpro.aimusic.ui.components.AudioPlayerCard
import com.surpro.aimusic.ui.components.DropdownSelector
import com.surpro.aimusic.ui.components.MusicPreviewCard
import com.surpro.aimusic.viewmodel.MusicViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicGeneratorScreen(
    navController: NavController,
    viewModel: MusicViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    
    var promptInput by remember { mutableStateOf("") }
    var selectedGenre by remember { mutableStateOf("") }
    var selectedInstrument by remember { mutableStateOf("") }
    var selectedBpm by remember { mutableStateOf("") }

    LaunchedEffect(uiState.error) {
        if (uiState.error != null) {
            snackbarHostState.showSnackbar(
                message = uiState.error ?: "Unknown error",
                duration = SnackbarDuration.Long
            )
            viewModel.clearError()
        }
    }

    LaunchedEffect(uiState.success) {
        if (uiState.success) {
            snackbarHostState.showSnackbar(
                message = "Music generated successfully!",
                duration = SnackbarDuration.Short
            )
            viewModel.clearSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Music Generator") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentPadding = paddingValues
        ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Prompt Input
                    OutlinedTextField(
                        value = promptInput,
                        onValueChange = { promptInput = it },
                        label = { Text("Describe the music you want") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        maxLines = 4,
                        placeholder = { 
                            Text("e.g., Upbeat summer vibes with tropical beats...") 
                        }
                    )

                    // Genre Selection
                    DropdownSelector(
                        label = "Genre",
                        options = viewModel.getGenres(),
                        selectedOption = selectedGenre,
                        onOptionSelected = { selectedGenre = it }
                    )

                    // Instrument Selection
                    DropdownSelector(
                        label = "Primary Instrument",
                        options = viewModel.getInstruments(),
                        selectedOption = selectedInstrument,
                        onOptionSelected = { selectedInstrument = it }
                    )

                    // BPM Selection
                    DropdownSelector(
                        label = "Tempo (BPM)",
                        options = viewModel.getBpmOptions(),
                        selectedOption = selectedBpm,
                        onOptionSelected = { selectedBpm = it }
                    )
                }
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Button(
                        onClick = {
                            if (promptInput.isNotBlank() && 
                                selectedGenre.isNotBlank() && 
                                selectedInstrument.isNotBlank() && 
                                selectedBpm.isNotBlank()) {
                                viewModel.generateMusic(
                                    prompt = promptInput,
                                    genre = selectedGenre,
                                    instrument = selectedInstrument,
                                    bpmRange = selectedBpm
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        enabled = !uiState.isLoading && 
                                promptInput.isNotBlank() && 
                                selectedGenre.isNotBlank() && 
                                selectedInstrument.isNotBlank() && 
                                selectedBpm.isNotBlank()
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.height(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text("Generate Music")
                        }
                    }
                }
            }

            // Music Preview
            item {
                AnimatedVisibility(visible = uiState.musicUrl.isNotBlank()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        MusicPreviewCard(
                            coverImageUrl = uiState.coverImageUrl,
                            title = uiState.generatedMusic?.title ?: "Generated Music",
                            genre = selectedGenre,
                            isPlaying = uiState.isPlaying,
                            currentPosition = uiState.currentPosition,
                            duration = uiState.duration,
                            tokensUsed = uiState.tokensUsed,
                            onPlayPause = {
                                if (uiState.isPlaying) {
                                    viewModel.pauseAudio()
                                } else {
                                    viewModel.playAudio()
                                }
                            },
                            onPositionChange = { position ->
                                viewModel.updatePosition(position)
                            },
                            onDownload = {
                                // TODO: Implement download
                            },
                            onShare = {
                                // TODO: Implement share
                            }
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
