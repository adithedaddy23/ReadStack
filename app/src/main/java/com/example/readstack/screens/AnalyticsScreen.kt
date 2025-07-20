package com.example.readstack.screens

import android.annotation.SuppressLint
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.fontscaling.MathUtils.lerp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.readstack.R
import com.example.readstack.roomdatabase.Book
import com.example.readstack.roomdatabase.Quote
import com.example.readstack.viewmodel.BookStorageViewModel
import com.example.readstack.viewmodel.QuoteViewModel
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import kotlinx.coroutines.delay
import kotlin.math.absoluteValue

@Composable
fun AnalyticsScreen(
    bookId: String,
    bookStorageViewModel: BookStorageViewModel,
    quoteViewModel: QuoteViewModel,
    navController: NavController,
    hazeState: HazeState,
) {

    val pagesThisMonth = remember { mutableIntStateOf(0) }
    val totalPages = remember { mutableIntStateOf(0) }
    val totalBooks = remember { mutableIntStateOf(0) }
    val booksThisMonth = remember { mutableStateOf(emptyList<Book>()) }
    val allQuotes = remember { mutableStateOf(emptyList<Quote>()) }
    val randomQuotes = remember { mutableStateOf(emptyList<QuoteWithBookTitle>()) }

    LaunchedEffect(Unit) {
        pagesThisMonth.intValue = bookStorageViewModel.getPagesReadThisMonth()
        totalPages.intValue = bookStorageViewModel.getTotalPagesReadTillNow()
        totalBooks.intValue = bookStorageViewModel.getTotalBooksRead()
        booksThisMonth.value = bookStorageViewModel.getBooksReadThisMonth()
    }

    // Collect quotes flow separately and fetch book titles
    LaunchedEffect(Unit) {
        quoteViewModel.getAllQuotes().collect { quotes ->
            allQuotes.value = quotes

            // Convert quotes to quotes with book titles
            val quotesWithTitles = quotes.shuffled().take(6).map { quote ->
                val book = bookStorageViewModel.getBookById(quote.bookId)
                QuoteWithBookTitle(
                    quote = quote,
                    bookTitle = book?.title ?: "Unknown Book"
                )
            }
            randomQuotes.value = quotesWithTitles
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .haze(
                state = hazeState,
                backgroundColor = MaterialTheme.colorScheme.background
            )
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        contentPadding = PaddingValues(
            top = 12.dp,
            bottom = 80.dp + 28.dp
        )
    ) {
        // Header with subtle enhancement
        item {
            Column(
                modifier = Modifier.padding(bottom = 4.dp)
            ) {
                Text(
                    text = "Your Reading",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 32.sp
                    )
                )
                Text(
                    text = "Journey",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Light,
                        fontSize = 32.sp,
                        fontStyle = FontStyle.Italic
                    )
                )
            }
        }

        item {
            Column {
                // Section Header with refined styling
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.primary.copy(0.12f),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.bar),
                                contentDescription = null,
                                modifier = Modifier.size(22.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Column {
                        Text(
                            text = "Reading Insights",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 22.sp
                            ),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Your literary progress",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontStyle = FontStyle.Italic,
                                fontSize = 14.sp
                            ),
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }

        item {
            StatisticCardCarousel(
                pagesThisMonth = pagesThisMonth.intValue,
                totalBooks = totalBooks.intValue,
                booksThisMonth = booksThisMonth.value
            )
        }

        // Quotes Section Header
        item {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.secondary.copy(0.12f),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.quotes), // Assuming you have a quote icon
                                contentDescription = null,
                                modifier = Modifier.size(22.dp),
                                tint = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }

                    Column {
                        Text(
                            text = "Saved Wisdom",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 22.sp
                            ),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Random quotes from your collection",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontStyle = FontStyle.Italic,
                                fontSize = 14.sp
                            ),
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }

        // Quotes Cards
        item {
            QuoteCardCarousel(quotes = randomQuotes.value)
        }
    }
}

// Enhanced data class for the statistics with personal messaging
data class StatCard(
    val title: String,
    val value: String,
    val subtitle: String? = null,
    val isItalic: Boolean = false
)
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PagerIndicator(
    pagerState: PagerState,
    modifier: Modifier = Modifier,
    // Add a parameter for the active dot color, with a default value.
    activeColor: Color = MaterialTheme.colorScheme.primary,
    inactiveColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(20.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(pagerState.pageCount) { iteration ->
            val isSelected = pagerState.currentPage == iteration

            // Use the new 'activeColor' parameter here.
            val color by animateColorAsState(
                targetValue = if (isSelected) activeColor else inactiveColor,
                animationSpec = tween(durationMillis = 300)
            )

            val width by animateDpAsState(
                targetValue = if (isSelected) 24.dp else 8.dp,
                animationSpec = tween(durationMillis = 300)
            )

            Box(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .clip(CircleShape)
                    .background(color)
                    .size(width = width, height = 8.dp)
            )
        }
    }
}

@SuppressLint("RestrictedApi")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StatisticCardCarousel(
    pagesThisMonth: Int,
    totalBooks: Int,
    booksThisMonth: List<Book>
) {
    val cards = listOf(
        StatCard("Pages turned", pagesThisMonth.toString(), "this month", isItalic = true),
        StatCard("Books Read", booksThisMonth.size.toString(), "this month", isItalic = true),
        StatCard("Books in your", totalBooks.toString(), "literary collection")
    )

    val pagerState = rememberPagerState(pageCount = { cards.size })
    val haptics = LocalHapticFeedback.current

    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    // --- CHANGE HERE: Make card width proportional to the screen size ---
    val cardWidth = screenWidth * 0.85f // Card will be 80% of the screen width
    val contentPadding = (screenWidth - cardWidth) / 2

    LaunchedEffect(pagerState.settledPage) {
        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
    }

    // Wrap Pager and Indicator in a Column
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = contentPadding),
            pageSpacing = 18.dp
        ) { page ->
            val card = cards[page]
            val pageOffset = ((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction).absoluteValue
            val animatedModifier = Modifier
                .graphicsLayer {
                    val scale = lerp(1f, 0.85f, pageOffset)
                    scaleX = scale
                    scaleY = scale
                    alpha = lerp(1f, 0.5f, pageOffset)
                }
                .width(cardWidth)
                .height(145.dp)

            ElegantStatCardWithBorder(
                card = card,
                modifier = animatedModifier
            )
        }

        // Add the PagerIndicator below the pager
        Spacer(modifier = Modifier.height(16.dp)) // Add some space
        PagerIndicator(pagerState = pagerState)
    }
}

@SuppressLint("RestrictedApi")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun QuoteCardCarousel(quotes: List<QuoteWithBookTitle>) {
    if (quotes.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxWidth().height(145.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No quotes saved yet",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                fontStyle = FontStyle.Italic
            )
        }
    } else {
        val pagerState = rememberPagerState(pageCount = { quotes.size })
        val haptics = LocalHapticFeedback.current

        val screenWidth = LocalConfiguration.current.screenWidthDp.dp
        // --- CHANGE HERE: Make card width proportional to the screen size ---
        val cardWidth = screenWidth * 0.85f // Card will be 80% of the screen width
        val contentPadding = (screenWidth - cardWidth) / 2

        LaunchedEffect(pagerState.settledPage) {
            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            HorizontalPager(
                state = pagerState,
                contentPadding = PaddingValues(horizontal = contentPadding),
                pageSpacing = 18.dp
            ) { page ->
                val quoteWithTitle = quotes[page]
                val pageOffset = ((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction).absoluteValue
                val animatedModifier = Modifier
                    .graphicsLayer {
                        val scale = lerp(1f, 0.85f, pageOffset)
                        scaleX = scale
                        scaleY = scale
                        alpha = lerp(1f, 0.5f, pageOffset)
                    }
                    .width(cardWidth)
                    .height(145.dp)

                ElegantQuoteCard(
                    quoteWithTitle = quoteWithTitle,
                    modifier = animatedModifier
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Pass the secondary color to the indicator.
            PagerIndicator(
                pagerState = pagerState,
                activeColor = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

// Data class to hold quote with resolved book title
data class QuoteWithBookTitle(
    val quote: Quote,
    val bookTitle: String
)

@Composable
fun ElegantQuoteCard(
    quoteWithTitle: QuoteWithBookTitle,
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(150)
        isVisible = true
    }

    val animatedScale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.95f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        )
    )

    val animatedAlpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 400, easing = EaseInOutCubic)
    )

    Card(
        modifier = modifier
            .scale(animatedScale)
            .alpha(animatedAlpha),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f))
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            // Center the content for a cleaner look when text is short
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                // Center the text vertically within the available space
                verticalArrangement = Arrangement.Center
            ) {
                // Quote text
                Text(
                    text = "\"${quoteWithTitle.quote.quoteText}\"",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        fontStyle = FontStyle.Italic,
                        lineHeight = 18.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    // --- INCREASED maxLines ---
                    maxLines = 5,
                    overflow = TextOverflow.Ellipsis
                )

                // The Spacer is removed to give the Text composable more room.
            }
        }
    }
}

@Composable
fun ElegantStatCardWithBorder(
    card: StatCard,
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(150)
        isVisible = true
    }

    val animatedScale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.95f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        )
    )

    val animatedAlpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 400, easing = EaseInOutCubic)
    )

    Card(
        modifier = modifier
            .scale(animatedScale)
            .alpha(animatedAlpha),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                // Value (large number)
                Text(
                    text = card.value,
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 36.sp,
                        letterSpacing = (-0.5).sp
                    ),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                // Title with conditional italic
                Text(
                    text = card.title,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp,
                        fontStyle = if (card.isItalic) FontStyle.Italic else FontStyle.Normal
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 20.sp
                )

                // Subtitle if available
                card.subtitle?.let { subtitle ->
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Normal,
                            fontSize = 13.sp,
                            fontStyle = FontStyle.Italic
                        ),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }
    }
}


