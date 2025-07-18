package com.example.readstack.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.readstack.R
import com.example.readstack.viewmodel.BookStorageViewModel
import com.example.readstack.viewmodel.BookViewModel
import com.example.readstack.viewmodel.NetworkResponseClass
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild

@Composable
fun MyBooksDetailsScreen(
    workKey: String,
    bookStorageViewModel: BookStorageViewModel,
    navController: NavController,
    bookViewModel: BookViewModel,
) {
    val bookResult = bookViewModel.bookResult.collectAsState()
    val detailResult = bookViewModel.bookDetailsResult.collectAsState()
    val hazeState = remember { HazeState() }
    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(workKey) {
        bookViewModel.getBookDetails(workKey)
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when(val result = detailResult.value) {
                is com.example.readstack.api.NetworkResponseClass.loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary,
                            strokeWidth = 4.dp,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }
                is com.example.readstack.api.NetworkResponseClass.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Something Went Wrong",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
                is com.example.readstack.api.NetworkResponseClass.Success -> {
                    val data = result.data
                    val coverImageUrl = data.covers?.firstOrNull()?.let {
                        "https://covers.openlibrary.org/b/id/$it-L.jpg"
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                    ) {
                        // Cover Image Section
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(400.dp)
                        ) {
                            if (coverImageUrl != null) {
                                val painter = rememberAsyncImagePainter(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(coverImageUrl)
                                        .crossfade(true)
                                        .error(R.drawable.image)
                                        .build()
                                )

                                when (painter.state) {
                                    is AsyncImagePainter.State.Loading -> {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(MaterialTheme.colorScheme.surface.copy(0.9f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(32.dp),
                                                color = MaterialTheme.colorScheme.primary,
                                                strokeWidth = 3.dp
                                            )
                                        }
                                    }
                                    is AsyncImagePainter.State.Error -> {
                                        ModernPlaceholderContent()
                                    }
                                    else -> {
                                        Image(
                                            painter = painter,
                                            contentDescription = data.title ?: "Book cover",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .haze(
                                                    state = hazeState,
                                                    backgroundColor = MaterialTheme.colorScheme.background,
                                                    tint = Color.Black.copy(alpha = 0.3f),
                                                    blurRadius = 25.dp,
                                                )
                                        )
                                    }
                                }
                            } else {
                                ModernPlaceholderContent()
                            }
                        }

                        // Content Section
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                                .background(MaterialTheme.colorScheme.surface.copy(0.65f))
                                .hazeChild(
                                    state = hazeState,
                                    shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
                                )
                                .border(
                                    1.dp,
                                    MaterialTheme.colorScheme.outline.copy(0.15f),
                                    RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
                                )
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(32.dp)
                                    .fillMaxWidth()
                            ) {
                                // Title
                                Text(
                                    text = data.title ?: "No Title",
                                    style = MaterialTheme.typography.headlineLarge.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 32.sp,
                                        lineHeight = 38.sp,
                                        letterSpacing = (-0.5).sp
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(bottom = 16.dp)
                                )

                                // Author(s)
                                val authorNames = data.authors?.mapNotNull { author ->
                                    author.author?.key?.let { authorKey ->
                                        // You might want to fetch author details separately
                                        // For now, we'll use a placeholder or the key
                                        author.author?.key?.substringAfterLast("/") ?: "Unknown Author"
                                    }
                                }?.joinToString(", ") ?: "Unknown Author"

                                Text(
                                    text = "by $authorNames",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 20.sp,
                                        letterSpacing = (-0.2).sp
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                                    modifier = Modifier.padding(bottom = 24.dp)
                                )

                                // Description
                                val description = when {
                                    data.description is String -> data.description
                                    data.description is Map<*, *> -> {
                                        (data.description as? Map<String, Any>)?.get("value") as? String
                                    }
                                    else -> null
                                } ?: "No description available"

                                Text(
                                    text = "Description",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(bottom = 12.dp)
                                )

                                Text(
                                    text = description,
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontSize = 16.sp,
                                        lineHeight = 24.sp
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
                                    modifier = Modifier.padding(bottom = 24.dp)
                                )

                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
private fun ModernPlaceholderContent() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "No Image",
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.size(48.dp)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "No Cover Available",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}