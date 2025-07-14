package com.example.readstack.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.readstack.viewmodel.BookViewModel

@Composable
fun SearchScreen(
    navController: NavHostController,
    bookViewModel: BookViewModel
) {

    var book by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    )  {
        Column {
            Text(
                text = "Search Books",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                modifier =Modifier.fillMaxWidth(),
                value = book,
                onValueChange ={ newValue ->
                    book = newValue
                },
                label = {
                    Text(text = "Search any Book")
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Search
                ),
                leadingIcon = {
                    Icon(
                        modifier = Modifier
                            .padding(4.dp)
                            .size(32.dp),
                        imageVector = Icons.Default.Search,
                        contentDescription = "Your Location"
                    )
                },
                keyboardActions = KeyboardActions(
                    onSearch = {
                        if (book.isNotEmpty()) {
                            bookViewModel.getData(book)
                            keyboardController?.hide()

                        }
                    }
                ),
            )
        }

    }
}