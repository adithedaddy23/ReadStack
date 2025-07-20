package com.example.readstack.screens

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.readstack.R
import com.example.readstack.roomdatabase.Book
import com.example.readstack.roomdatabase.Quote
import com.example.readstack.viewmodel.BookStorageViewModel
import com.example.readstack.viewmodel.QuoteViewModel
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
@SuppressLint("NewApi")
@Composable
fun BookProgressScreen(
    bookId: String,
    bookStorageViewModel: BookStorageViewModel,
    quoteViewModel: QuoteViewModel,
    navController: NavController
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val hazeState = remember { HazeState() }
    val scrollState = rememberScrollState()


    // Add SnackbarHostState
    val snackbarHostState = remember { SnackbarHostState() }

    // Use reactive quotes flow
    val quotes by quoteViewModel.getQuotesFlow(bookId).collectAsState()

    // Use reactive book data flow instead of manual state management
    val fullBookId = "/works/$bookId"
    val book by bookStorageViewModel.getBookFlow(fullBookId).collectAsState(initial = null)

    // State to control the visibility of the Add Quote dialog
    var showQuoteDialog by remember { mutableStateOf(false) }

    // Progress tracking states
    var showProgressDialog by remember { mutableStateOf(false) }

    if (book == null) {
        // Show a loading indicator while the book data is being fetched
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        // Main screen layout
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                // Background Cover Image with Haze effect
                val painter = rememberAsyncImagePainter(
                    model = ImageRequest.Builder(context)
                        .data(book!!.coverUrl)
                        .crossfade(true)
                        .error(R.drawable.image)
                        .build()
                )

                Image(
                    painter = painter,
                    contentDescription = book!!.title,
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

                // Scrollable content area
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
                            // Book Title
                            Text(
                                text = book!!.title,
                                style = MaterialTheme.typography.headlineLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 32.sp,
                                    lineHeight = 38.sp,
                                    letterSpacing = (-0.5).sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(bottom = 24.dp)
                            )

                            // Enhanced Progress Tracker Section
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 24.dp),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                ),
                                elevation = CardDefaults.cardElevation(
                                    defaultElevation = 0.dp
                                ),
                                border = BorderStroke(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.08f)
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(24.dp)
                                ) {
                                    // Header with icon
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .background(
                                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                                                    shape = CircleShape
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                painter = painterResource(R.drawable.open_book),
                                                contentDescription = null,
                                                modifier = Modifier.size(20.dp),
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(12.dp))

                                        Text(
                                            text = "Reading Progress",
                                            style = MaterialTheme.typography.headlineSmall.copy(
                                                fontWeight = FontWeight.SemiBold,
                                                letterSpacing = (-0.5).sp
                                            ),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(24.dp))

                                    // Progress calculation
                                    val progress = if (book!!.totalPages != null && book!!.totalPages!! > 0) {
                                        (book!!.currentPage.toFloat() / book!!.totalPages!!).coerceIn(0f, 1f)
                                    } else 0f

                                    // Progress stats
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column {
                                            Text(
                                                text = "Current Page",
                                                style = MaterialTheme.typography.labelMedium.copy(
                                                    letterSpacing = 0.5.sp
                                                ),
                                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                            )
                                            Text(
                                                text = book!!.currentPage.toString(),
                                                style = MaterialTheme.typography.headlineMedium.copy(
                                                    fontWeight = FontWeight.Bold
                                                ),
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }

                                        if (book!!.totalPages != null && book!!.totalPages!! > 0) {
                                            Column(
                                                horizontalAlignment = Alignment.End
                                            ) {
                                                Text(
                                                    text = "Progress",
                                                    style = MaterialTheme.typography.labelMedium.copy(
                                                        letterSpacing = 0.5.sp
                                                    ),
                                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                                )
                                                Text(
                                                    text = "${(progress * 100).toInt()}%",
                                                    style = MaterialTheme.typography.headlineMedium.copy(
                                                        fontWeight = FontWeight.Bold
                                                    ),
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(20.dp))

                                    // Modern Progress Bar
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(6.dp)
                                            .background(
                                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f),
                                                shape = RoundedCornerShape(3.dp)
                                            )
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxHeight()
                                                .fillMaxWidth(progress)
                                                .background(
                                                    brush = Brush.horizontalGradient(
                                                        colors = listOf(
                                                            MaterialTheme.colorScheme.primary,
                                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                                                        )
                                                    ),
                                                    shape = RoundedCornerShape(3.dp)
                                                )
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    // Total pages indicator
                                    if (book!!.totalPages != null) {
                                        Text(
                                            text = "of ${book!!.totalPages} pages",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(20.dp))

                                    // Update button with modern design
                                    Button(
                                        onClick = { showProgressDialog = true },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(48.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.primary,
                                            contentColor = MaterialTheme.colorScheme.onPrimary
                                        )
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.Edit,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Update Progress",
                                            style = MaterialTheme.typography.bodyLarge.copy(
                                                fontWeight = FontWeight.Medium
                                            )
                                        )
                                    }
                                }
                            }

                            // Action Buttons
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 24.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // Add Quote Button
                                Button(
                                    onClick = { showQuoteDialog = true },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(52.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        contentColor = MaterialTheme.colorScheme.onPrimary
                                    )
                                ) {
                                    Text(
                                        "Add Quote",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    )
                                }

                                // Mark as Finished Button
                                OutlinedButton(
                                    onClick = {
                                        coroutineScope.launch {
                                            bookStorageViewModel.updateBookShelf(bookId, "finished")
                                            snackbarHostState.showSnackbar(
                                                message = "Marked as Finished",
                                                duration = SnackbarDuration.Short
                                            )
                                            navController.popBackStack()
                                        }
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(52.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary)
                                ) {
                                    Text(
                                        "Finished",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.SemiBold
                                        ),
                                        color = MaterialTheme.colorScheme.tertiary
                                    )
                                }
                            }

                            // Quotes Section
                            Text(
                                text = "Thoughts or reflections while reading",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

                            if (quotes.isEmpty()) {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                    )
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(24.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "No quotes saved yet.\nStart adding your thoughts!",
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            } else {
                                // Display quotes in a Column instead of LazyColumn
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    quotes.forEach { quote ->
                                        QuoteCard(quote = quote)
                                    }
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

        // Dialogs
        if (showQuoteDialog) {
            AddQuoteDialog(
                onDismiss = { showQuoteDialog = false },
                onSave = { quoteText ->
                    // Immediately close dialog and show feedback for better UX
                    showQuoteDialog = false

                    coroutineScope.launch {
                        try {
                            // Show immediate feedback
                            snackbarHostState.showSnackbar(
                                message = "Saving quote...",
                                duration = SnackbarDuration.Short
                            )

                            // Perform database operation in background
                            launch(Dispatchers.IO) {
                                quoteViewModel.insertQuote(
                                    Quote(
                                        bookId = bookId,
                                        quoteText = quoteText,
                                        noteText = null,
                                        tags = emptyList(),
                                        timestamp = LocalDateTime.now()
                                    )
                                )
                            }.join() // Wait for completion

                            // Show success message
                            snackbarHostState.showSnackbar(
                                message = "Quote saved successfully",
                                duration = SnackbarDuration.Short
                            )
                        } catch (e: Exception) {
                            // Handle error case
                            snackbarHostState.showSnackbar(
                                message = "Failed to save quote",
                                duration = SnackbarDuration.Short
                            )
                        }
                    }
                }
            )
        }

        if (showProgressDialog) {
            ProgressDialog(
                currentTotalPages = book!!.totalPages?.toString() ?: "",
                currentPagesRead = book!!.currentPage.toString(),
                onDismiss = { showProgressDialog = false },
                onSave = { totalPagesStr, pagesReadStr ->
                    val totalPagesInt = totalPagesStr.toIntOrNull()
                    val pagesReadInt = pagesReadStr.toIntOrNull() ?: 0

                    bookStorageViewModel.updateBookProgress(
                        bookId = book!!.id,
                        totalPages = totalPagesInt,
                        pagesRead = pagesReadInt
                    )

                    showProgressDialog = false
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(
                            message = "Progress updated",
                            duration = SnackbarDuration.Short
                        )
                    }
                }
            )
        }
    }
}

@Composable
fun BookProgressDialog(
    book: Book,
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onUpdateProgress: (totalPages: Int?, pagesRead: Int) -> Unit
) {
    var totalPagesText by remember(showDialog) {
        mutableStateOf(book.totalPages?.toString() ?: "")
    }
    var pagesReadText by remember(showDialog) {
        mutableStateOf(book.currentPage.toString())
    }
    var totalPagesError by remember { mutableStateOf(false) }
    var pagesReadError by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(
                    text = "Update Reading Progress",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Total Pages Input
                    OutlinedTextField(
                        value = totalPagesText,
                        onValueChange = {
                            totalPagesText = it
                            totalPagesError = false
                        },
                        label = { Text("Total Pages") },
                        placeholder = { Text("Optional") },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.book),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        singleLine = true,
                        isError = totalPagesError,
                        supportingText = if (totalPagesError) {
                            { Text("Please enter a valid number") }
                        } else null,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Pages Read Input
                    OutlinedTextField(
                        value = pagesReadText,
                        onValueChange = {
                            pagesReadText = it
                            pagesReadError = false
                        },
                        label = { Text("Pages Read") },
                        placeholder = { Text("0") },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.plus),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary
                            )
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        singleLine = true,
                        isError = pagesReadError,
                        supportingText = if (pagesReadError) {
                            { Text("Please enter a valid number") }
                        } else null,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Progress indicator if total pages is set
                    if (totalPagesText.isNotBlank() && pagesReadText.isNotBlank()) {
                        val totalPages = totalPagesText.toIntOrNull()
                        val pagesRead = pagesReadText.toIntOrNull()

                        if (totalPages != null && pagesRead != null && totalPages > 0) {
                            val progress = (pagesRead.toFloat() / totalPages.toFloat()).coerceIn(0f, 1f)

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "Progress",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = "${(progress * 100).toInt()}%",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }

                                    LinearProgressIndicator(
                                        progress = progress,
                                        modifier = Modifier.fillMaxWidth(),
                                        color = MaterialTheme.colorScheme.primary,
                                        trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val totalPages = if (totalPagesText.isBlank()) {
                            null
                        } else {
                            totalPagesText.toIntOrNull()
                        }

                        val pagesRead = pagesReadText.toIntOrNull()

                        // Validation
                        var hasError = false

                        if (totalPagesText.isNotBlank() && totalPages == null) {
                            totalPagesError = true
                            hasError = true
                        }

                        if (pagesRead == null || pagesRead < 0) {
                            pagesReadError = true
                            hasError = true
                        }

                        if (totalPages != null && pagesRead != null && pagesRead > totalPages) {
                            pagesReadError = true
                            hasError = true
                        }

                        if (!hasError) {
                            onUpdateProgress(totalPages, pagesRead ?: 0)
                            onDismiss()
                        }
                    }
                ) {
                    Text("Update")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun QuoteCard(
    quote: Quote,
    onEditClick: (Quote) -> Unit = {},
    onDeleteClick: (Quote) -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp,
            pressedElevation = 2.dp
        ),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Top row with quote icon and action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Quote icon
                Icon(
                    painter = painterResource(R.drawable.quotes),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                )

                // Action buttons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Edit button
                    IconButton(
                        onClick = { onEditClick(quote) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = "Edit quote",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }

                    // Delete button
                    IconButton(
                        onClick = { onDeleteClick(quote) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = "Delete quote",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Quote text with better typography
            Text(
                text = quote.quoteText,
                style = MaterialTheme.typography.bodyLarge.copy(
                    lineHeight = 28.sp,
                    fontWeight = FontWeight.Normal,
                    letterSpacing = 0.25.sp
                ),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.87f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Timestamp with minimal styling
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = quote.timestamp.format(
                        DateTimeFormatter.ofPattern("MMM dd, yyyy • HH:mm")
                    ),
                    style = MaterialTheme.typography.labelMedium.copy(
                        letterSpacing = 0.4.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressDialog(
    currentTotalPages: String,
    currentPagesRead: String,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var totalPages by remember { mutableStateOf(currentTotalPages) }
    var pagesRead by remember { mutableStateOf(currentPagesRead) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Update Progress",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = totalPages,
                    onValueChange = { totalPages = it },
                    label = { Text("Total Pages") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = pagesRead,
                    onValueChange = { pagesRead = it },
                    label = { Text("Pages Read") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(totalPages, pagesRead) }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun AddQuoteDialog(onDismiss: () -> Unit, onSave: (String) -> Unit) {
    var quoteText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add a Quote") },
        text = {
            OutlinedTextField(
                value = quoteText,
                onValueChange = { quoteText = it },
                label = { Text("Enter your quote") },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    if (quoteText.isNotBlank()) {
                        onSave(quoteText)
                    }
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
