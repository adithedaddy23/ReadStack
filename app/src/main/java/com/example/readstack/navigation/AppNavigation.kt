import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.readstack.screens.AnalyticsScreen
import com.example.readstack.screens.MyBooksScreen
import com.example.readstack.screens.SearchScreen
import com.example.readstack.viewmodel.BookViewModel

@Composable
fun AppNavigation(
) {
    val navController = rememberNavController()
    val bookViewModel: BookViewModel = viewModel()
    Scaffold (
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { paddingValues ->
        NavHost(
            navController =navController,
            startDestination = "my_books",
            modifier = Modifier.padding(paddingValues)
        )  {
            composable("my_books") {
                MyBooksScreen(navController = navController)
            }
            composable("insights") {
                AnalyticsScreen(navController = navController)
            }
            composable("search") {
                SearchScreen(
                    navController = navController,
                    bookViewModel = bookViewModel
                )
            }
        }
    }
}

@Composable
fun BottomNavigationBar(
    navController: NavController,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    modifier: Modifier = Modifier,
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val haptic = LocalHapticFeedback.current
    val bottomNavItems = getBottomNavItems() //

    if (currentRoute in listOf("my_books", "insights", "search")) {
        NavigationBar(modifier = modifier) {
            bottomNavItems.forEach { navItem ->
                val selected = currentRoute == navItem.route

                val iconScale by animateFloatAsState(
                    targetValue = if (selected) 1.2f else 1.0f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
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
                        unselectedIconColor = MaterialTheme.colorScheme.secondary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        unselectedTextColor = MaterialTheme.colorScheme.secondary,
                        indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
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