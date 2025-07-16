package com.example.readstack.screens

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.readstack.R
import com.example.readstack.api.NetworkResponseClass
import com.example.readstack.viewmodel.BookStorageResult
import com.example.readstack.viewmodel.BookStorageViewModel
import com.example.readstack.viewmodel.BookViewModel
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalLayoutApi::class)
@SuppressLint("UnrememberedMutableState")
@Composable
fun BookDetailScreen(
    workKey: String,
    bookViewModel: BookViewModel,
    bookStorageViewModel: BookStorageViewModel,
    navController: NavController,
    hazeState: HazeState
) {
    val detailResult = bookViewModel.bookDetailsResult.collectAsState()
    val storageResult = bookStorageViewModel.storageResult.collectAsState()
    val hazeState = remember { HazeState() }
    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Handle storage result for showing feedback
    LaunchedEffect(storageResult.value) {
        storageResult.value.message?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
        }
    }

    LaunchedEffect(workKey) {
        bookViewModel.getBookDetails(workKey)
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (val result = detailResult.value) {
                is NetworkResponseClass.loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary,
                            strokeWidth = 4.dp,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }

                is NetworkResponseClass.Error -> {
                    ErrorScreen(message = result.message, onRetry = { bookViewModel.getBookDetails(workKey) })
                }

                is NetworkResponseClass.Success -> {
                    val data = result.data
                    val coverImageUrl = data.covers?.firstOrNull()?.let {
                        "https://covers.openlibrary.org/b/id/$it-L.jpg"
                    }

                    // Background image with haze effect
                    if (coverImageUrl != null) {
                        val painter = rememberAsyncImagePainter(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(coverImageUrl)
                                .crossfade(true)
                                .error(R.drawable.image)
                                .build()
                        )

                        Image(
                            painter = painter,
                            contentDescription = data.title ?: "Book cover",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(400.dp)
                                .haze(
                                    state = hazeState,
                                    backgroundColor = MaterialTheme.colorScheme.background,
                                    tint = Color.Black.copy(alpha = 0.3f),
                                    blurRadius = 25.dp
                                )
                        )

                        if (painter.state is AsyncImagePainter.State.Loading) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(400.dp)
                                    .background(
                                        MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(32.dp),
                                    color = MaterialTheme.colorScheme.primary,
                                    strokeWidth = 3.dp
                                )
                            }
                        } else if (painter.state is AsyncImagePainter.State.Error) {
                            ModernPlaceholderContent()
                        }
                    } else {
                        ModernPlaceholderContent()
                    }

                    // Scrollable content
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                            .padding(top = 350.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                                .background(
                                    MaterialTheme.colorScheme.surface.copy(alpha = 0.65f)
                                )
                                .hazeChild(
                                    state = hazeState,
                                    shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
                                )
                                .border(
                                    1.dp,
                                    MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
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
                                    text = data.title ?: "Untitled",
                                    style = MaterialTheme.typography.headlineLarge.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 32.sp,
                                        lineHeight = 38.sp,
                                        letterSpacing = (-0.5).sp
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(bottom = 16.dp)
                                )

                                // Metadata
                                Row(
                                    modifier = Modifier.padding(bottom = 24.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    data.first_publish_date?.let {
                                        Surface(
                                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f),
                                            shape = RoundedCornerShape(12.dp),
                                            tonalElevation = 4.dp
                                        ) {
                                            Text(
                                                text = it,
                                                style = MaterialTheme.typography.labelLarge.copy(
                                                    fontWeight = FontWeight.Medium
                                                ),
                                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                            )
                                        }
                                    }

                                }

                                // Action buttons
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 32.dp),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Surface(
                                        onClick = { bookStorageViewModel.saveBookFromApi(workKey, "finished") },
                                        shape = RoundedCornerShape(16.dp),
                                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(48.dp)
                                            .border(
                                                width = 1.dp,
                                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                                                shape = RoundedCornerShape(16.dp)
                                            )
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxSize(),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            Icon(
                                                painter = painterResource(R.drawable.check),
                                                contentDescription = null,
                                                modifier = Modifier.size(20.dp),
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                            Spacer(Modifier.width(8.dp))
                                            Text(
                                                text = "Finished",
                                                style = MaterialTheme.typography.labelLarge.copy(
                                                    fontWeight = FontWeight.Medium
                                                ),
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }

                                    Surface(
                                        onClick = { bookStorageViewModel.saveBookFromApi(workKey, "want_to_read") },
                                        shape = RoundedCornerShape(16.dp),
                                        color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f),
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(48.dp)
                                            .border(
                                                width = 1.dp,
                                                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f),
                                                shape = RoundedCornerShape(16.dp)
                                            )
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxSize(),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            Icon(
                                                painter = painterResource(R.drawable.bookmark),
                                                contentDescription = null,
                                                modifier = Modifier.size(20.dp),
                                                tint = MaterialTheme.colorScheme.secondary
                                            )
                                            Spacer(Modifier.width(8.dp))
                                            Text(
                                                text = "Want to Read",
                                                style = MaterialTheme.typography.labelLarge.copy(
                                                    fontWeight = FontWeight.Medium
                                                ),
                                                color = MaterialTheme.colorScheme.secondary
                                            )
                                        }
                                    }
                                }

                                // Description
                                data.getDescriptionText()?.let { description ->
                                    Text(
                                        text = "About the Book",
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 24.sp,
                                            letterSpacing = (-0.3).sp
                                        ),
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(bottom = 16.dp)
                                    )
                                    Text(
                                        text = description,
                                        style = MaterialTheme.typography.bodyLarge.copy(
                                            lineHeight = 28.sp,
                                            letterSpacing = 0.1.sp,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
                                        ),
                                        modifier = Modifier.padding(bottom = 32.dp)
                                    )
                                }

                                // Subjects
                                data.subjects?.takeIf { it.isNotEmpty() }?.let { subjects ->
                                    Text(
                                        text = "Subjects",
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 24.sp,
                                            letterSpacing = (-0.3).sp
                                        ),
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(bottom = 16.dp)
                                    )
                                    FlowRow(
                                        modifier = Modifier.padding(bottom = 32.dp),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        verticalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        subjects.take(10).forEach { subject ->
                                            Surface(
                                                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.8f),
                                                shape = RoundedCornerShape(16.dp),
                                                tonalElevation = 6.dp
                                            ) {
                                                Text(
                                                    text = subject,
                                                    style = MaterialTheme.typography.labelLarge.copy(
                                                        fontWeight = FontWeight.Medium
                                                    ),
                                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
                                                )
                                            }
                                        }
                                    }
                                }

                                // "Currently Reading" button
                                Button(
                                    onClick = { bookStorageViewModel.saveBookFromApi(workKey, "currently_reading") },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(56.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.tertiary,
                                        contentColor = MaterialTheme.colorScheme.onTertiary
                                    ),
                                    elevation = ButtonDefaults.buttonElevation(
                                        defaultElevation = 4.dp,
                                        pressedElevation = 8.dp
                                    )
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            painter = painterResource(R.drawable.open_book),
                                            contentDescription = null,
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Text(
                                            text = "Currently Reading",
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        )
                                    }
                                }
                            }
                        }
                        Spacer(Modifier.height(40.dp))
                    }

                    // Floating Back Button with Frosted Glass Effect
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .haze(
                                state = HazeState(),
                                backgroundColor = Color.Black.copy(0.3f)
                            )
                            .padding(start = 16.dp, top = 16.dp),
                        contentAlignment = Alignment.TopStart
                    ) {
                        IconButton(
                            onClick = { navController.popBackStack() },
                            modifier = Modifier
                                .background(
                                    MaterialTheme.colorScheme.surface.copy(alpha = 0.4f),
                                    CircleShape
                                )
                                .hazeChild(
                                    state = HazeState()
                                )
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f),
                                    shape = CircleShape
                                )
                                .size(48.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ErrorScreen(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = "Error",
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(56.dp)
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Something Went Wrong",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 22.sp
            ),
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = onRetry,
            modifier = Modifier
                .height(48.dp)
                .width(160.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 2.dp,
                pressedElevation = 6.dp
            )
        ) {
            Text(
                text = "Try Again",
                style = MaterialTheme.typography.labelLarge
            )
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
