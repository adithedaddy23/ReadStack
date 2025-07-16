package com.example.readstack.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.readstack.R
import com.example.readstack.roomdatabase.Book
import com.example.readstack.viewmodel.BookStorageViewModel
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MyBooksScreen(
    navController: NavHostController,
    bookStorageViewModel: BookStorageViewModel,
    hazeState: HazeState
) {
    val currentlyReadingBooks = bookStorageViewModel.getBooksByShelf("currently_reading").collectAsState()
    val finishedBooks = bookStorageViewModel.getBooksByShelf("finished").collectAsState()
    val wantToReadBooks = bookStorageViewModel.getBooksByShelf("want_to_read").collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .haze(
                state = hazeState,
                backgroundColor = MaterialTheme.colorScheme.background
            )
            .padding(horizontal = 10.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        contentPadding = PaddingValues(
            top = 10.dp,
            bottom = 80.dp + 24.dp // Bottom nav height + extra padding
        )
    ) {
        // Header
        item {
            Text(
                text = "My Books",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        // Currently Reading Section
        item {
            BookShelfSection(
                title = "Currently Reading",
                books = currentlyReadingBooks.value,
                onBookClick = { book ->
                    navController.navigate("book_detail/${book.id}")
                },
                icon = R.drawable.open_book,
                color = MaterialTheme.colorScheme.primary
            )
        }

        // Want to Read Section
        item {
            BookShelfSection(
                title = "Want to Read",
                books = wantToReadBooks.value,
                onBookClick = { book ->
                    navController.navigate("book_detail/${book.id}")
                },
                icon = R.drawable.bookmark,
                color = MaterialTheme.colorScheme.secondary
            )
        }

        // Finished Section
        item {
            BookShelfSection(
                title = "Finished",
                books = finishedBooks.value,
                onBookClick = { book ->
                    navController.navigate("book_detail/${book.id}")
                },
                icon = R.drawable.check,
                color = MaterialTheme.colorScheme.tertiary
            )
        }
    }
}

@Composable
private fun BookShelfSection(
    title: String,
    books: List<Book>,
    onBookClick: (Book) -> Unit,
    icon: Int,
    color: Color
) {
    Column {
        // Section Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                color = color.copy(alpha = 0.15f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.size(40.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(icon),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = color
                    )
                }
            }

            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                ),
                color = MaterialTheme.colorScheme.onBackground
            )

            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = books.size.toString(),
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }
        }

        // Books List
        if (books.isEmpty()) {
            EmptyShelfMessage(title = title)
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                items(books) { book ->
                    ModernBookItem(
                        book = book,
                        onClick = { onBookClick(book) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ModernBookItem(
    book: Book,
    onClick: () -> Unit
) {
    val hazeState = remember { HazeState() }

    Card(
        onClick = onClick,
        modifier = Modifier
            .width(140.dp)
            .height(250.dp),
        shape = RoundedCornerShape(16.dp),
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Background Image
            if (book.coverUrl != null) {
                val painter = rememberAsyncImagePainter(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(book.coverUrl)
                        .crossfade(true)
                        .error(R.drawable.image)
                        .build()
                )

                Image(
                    painter = painter,
                    contentDescription = book.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .haze(
                            state = hazeState,
                            backgroundColor = MaterialTheme.colorScheme.surface,
                            tint = Color.Black.copy(alpha = 0.1f),
                            blurRadius = 12.dp
                        )
                )

                if (painter.state is AsyncImagePainter.State.Loading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.primary,
                            strokeWidth = 2.dp
                        )
                    }
                } else if (painter.state is AsyncImagePainter.State.Error) {
                    ModernBookPlaceholder()
                }
            } else {
                ModernBookPlaceholder()
            }

            // Frosted Glass Overlay for Text
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
                    .align(Alignment.BottomCenter)
                    .background(
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.3f)
                    )
                    .hazeChild(
                        state = hazeState,
                        shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    Text(
                        text = book.title,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        ),
                        color = Color.White,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                }
            }

            // Shelf indicator badge
            Surface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp),
                color = when (book.shelf) {
                    "currently_reading" -> MaterialTheme.colorScheme.primary
                    "finished" -> MaterialTheme.colorScheme.tertiary
                    "want_to_read" -> MaterialTheme.colorScheme.secondary
                    else -> MaterialTheme.colorScheme.surfaceVariant
                }.copy(alpha = 0.9f),
                shape = CircleShape
            ) {
                Icon(
                    painter = painterResource(
                        when (book.shelf) {
                            "currently_reading" -> R.drawable.open_book
                            "finished" -> R.drawable.check
                            "want_to_read" -> R.drawable.bookmark
                            else -> R.drawable.bookmark
                        }
                    ),
                    contentDescription = book.shelf,
                    modifier = Modifier
                        .size(24.dp)
                        .padding(4.dp),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

@Composable
private fun ModernBookPlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f),
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.image),
                contentDescription = "No Cover",
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "No Cover",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun EmptyShelfMessage(title: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .background(
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                RoundedCornerShape(12.dp)
            )
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                shape = RoundedCornerShape(12.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.open_book),
                contentDescription = "Empty shelf",
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "No books in $title",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
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
