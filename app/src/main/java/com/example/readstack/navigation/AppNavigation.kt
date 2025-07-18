import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.readstack.navigation.BottomNavIcon
import com.example.readstack.navigation.getBottomNavItems
import com.example.readstack.roomdatabase.ReadStackDatabase
import com.example.readstack.screens.AnalyticsScreen
import com.example.readstack.screens.BookDetailScreen
import com.example.readstack.screens.MyBooksScreen
import com.example.readstack.screens.SearchScreen
import com.example.readstack.viewmodel.BookStorageViewModel
import com.example.readstack.viewmodel.BookStorageViewModelFactory
import com.example.readstack.viewmodel.BookViewModel
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeChild

@Composable

fun AppNavigation(

    database: ReadStackDatabase

) {

    val navController = rememberNavController()

    val bookViewModel: BookViewModel = viewModel()

    val hazeState = remember { HazeState() }



// Remove Scaffold and its padding to allow content to go behind bottom bar

    Box(modifier = Modifier

        .fillMaxSize()) {

        NavHost(

            navController = navController,

            startDestination = "my_books",

            modifier = Modifier.fillMaxSize()

        ) {

            composable("my_books") {

                val bookStorageViewModel: BookStorageViewModel = viewModel(

                    factory = BookStorageViewModelFactory(

                        bookDao = database.bookDao()

                    )

                )

                MyBooksScreen(

                    navController = navController,

                    bookStorageViewModel = bookStorageViewModel,

                    hazeState = hazeState

                )

            }

            composable("insights") {

                AnalyticsScreen(

                    navController = navController,

                    hazeState = hazeState

                )

            }

            composable("explore") {

                SearchScreen(

                    navController = navController,

                    bookViewModel = bookViewModel,

                    hazeState = hazeState

                )

            }

            composable("bookDetail/{workKey}") { backStackEntry ->

                val workKey = backStackEntry.arguments?.getString("workKey") ?: ""

                val bookViewModel: BookViewModel = viewModel()

                val bookStorageViewModel: BookStorageViewModel = viewModel(

                    factory = BookStorageViewModelFactory(

                        bookDao = database.bookDao()

                    )

                )

                BookDetailScreen(

                    workKey = workKey,

                    bookViewModel = bookViewModel,

                    bookStorageViewModel = bookStorageViewModel,

                    navController = navController,

                    hazeState = hazeState

                )

            }

        }



// Bottom navigation bar with frosted glass effect

        BottomNavigationBar(

            navController = navController,

            hazeState = hazeState,

            modifier = Modifier.align(Alignment.BottomCenter)

        )

    }

}

@Composable
fun BottomNavigationBar(
    navController: NavController,
    hazeState: HazeState,
    modifier: Modifier = Modifier,
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val haptic = LocalHapticFeedback.current
    val bottomNavItems = getBottomNavItems()

    // This condition ensures the bottom bar only shows on specific screens
    if (currentRoute in listOf("my_books", "insights", "explore")) {
        NavigationBar(
            modifier = modifier
                .hazeChild(
                    state = hazeState,
                )
                .border(
                    width = 0.5.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp)
                ),
            containerColor = Color.Transparent,
            tonalElevation = 0.dp
        ) {
            bottomNavItems.forEach { navItem ->
                val selected = currentRoute == navItem.route

                val iconScale by animateFloatAsState(
                    targetValue = if (selected) 1.3f else 1.0f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioHighBouncy,
                        stiffness = Spring.StiffnessLow
                    ),
                    label = "iconScaleAnimation"
                )

                val iconOffsetY by animateDpAsState(
                    targetValue = if (selected) 0.dp else 0.dp,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    ),
                    label = "iconOffsetYAnimation"
                )

                NavigationBarItem(
                    selected = selected,
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                        indicatorColor = Color.Transparent
                    ),
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)

                        navController.navigate(navItem.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = {
                        Box(
                            modifier = Modifier
                                .scale(iconScale)
                                .offset(y = iconOffsetY)
                        ) {
                            BottomNavIcon(
                                iconType = navItem.icon,
                                contentDescription = navItem.label,
                            )
                        }
                    },
                    label = { Text(text = navItem.label) },
                    alwaysShowLabel = true
                )
            }
        }
    }
}