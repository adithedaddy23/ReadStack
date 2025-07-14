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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.readstack.api.ApiResponse
import com.example.readstack.api.NetworkResponseClass
import com.example.readstack.viewmodel.BookViewModel

@Composable
fun SearchScreen(
    navController: NavHostController,
    bookViewModel: BookViewModel
) {

    var book by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    val bookResult = bookViewModel.bookResult.collectAsState()
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

            when (val result = bookResult.value) {
                is NetworkResponseClass.loading -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 48.dp)
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Searching books...")
                    }
                }

                is NetworkResponseClass.Error -> {
                    if(result.message.isNotEmpty()) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 24.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            Text(
                                text = result.message,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }

                }

                is NetworkResponseClass.Success -> {
                    BookDetails(data = result.data)
                }
            }
        }

    }
}

@Composable
fun BookDetails(data: ApiResponse) {

}