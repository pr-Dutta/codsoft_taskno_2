package com.example.codsoft_task_2

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.codsoft_task_2.Data.Quote
import com.example.codsoft_task_2.Data.RetrofitInstance
import com.example.codsoft_task_2.ui.theme.Codsoft_task_2Theme
import retrofit2.HttpException
import java.io.IOException

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val quote: MutableState<Quote> = mutableStateOf(
            Quote(0, "----------------------------------", "-----")
        )
        lifecycleScope.launchWhenCreated {

            val response = try {
                RetrofitInstance.api.getDate()
            } catch (e: IOException) {
                Log.e("MainActivity", "IOException, you might not have internet connection.")
                return@launchWhenCreated
            } catch (e: HttpException) {
                Log.e("MainActivity", "HttpException, unexpected response.")
                return@launchWhenCreated
            }


            if (response.isSuccessful && response.body() != null) {
                quote.value = response.body()!!
            }else {
                Log.e("MainActivity", "Response not successful.")
            }

        }

        val context = this

        setContent {
            Codsoft_task_2Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MyQuoteApp(quote.value, context)
                }
            }
        }
    }
}

@Composable
fun MyQuoteApp(
    quote: Quote,
    context: MainActivity
) {
    val saveCount = remember { mutableStateOf(0) }

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home_screen") {

        composable("home_screen") {

            HomeScreen(
                navigateToFavoriteQuote = { navController.navigate("favorite_quotes_list/") },
                quote = quote,
                context,
                saveCount
            )
        }

        composable(route = "favorite_quotes_list/") {
            FavoriteQuotesList(
                navigateToHomeScreen = { navController.navigate("home_screen") },
                context
            )
        }
    }
}