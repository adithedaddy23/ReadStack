package com.example.readstack.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.readstack.R

@Composable
fun BottomNavIcon(iconType: IconType, contentDescription: String) {
    when(iconType) {
        is IconType.Vector -> Icon (
            imageVector = iconType.imageVector,
            contentDescription = contentDescription
        )
        is IconType.Resource -> Icon(
            painter = iconType.painter,
            contentDescription = contentDescription,
            modifier = Modifier.size(24.dp).padding(2.dp)
        )
    }
}

@Composable
fun getBottomNavItems(): List<BottomNavItem> {
    return listOf(
        BottomNavItem(
            label = "My Books",
            icon = IconType.Resource(painterResource(R.drawable.book) ),
            route = "my_books"
        ),
        BottomNavItem(
            label = "Insights",
            icon = IconType.Resource(painterResource(R.drawable.idea)),
            route = "insights/{bookId}"
        ),
        BottomNavItem(
            label = "Explore",
            icon = IconType.Resource(painterResource(R.drawable.explore)),
            route = "explore"
        )
    )
}