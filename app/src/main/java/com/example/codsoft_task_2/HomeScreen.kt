package com.example.codsoft_task_2

import android.annotation.SuppressLint
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.codsoft_task_2.Data.Quote
import com.example.codsoft_task_2.Data.supabase
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@Composable
fun HomeScreen(
    navigateToFavoriteQuote: () -> Unit,
    quote: Quote,
    context: MainActivity,
    saveCount: MutableState<Int>
) {

    //val quoteViewModel: MainViewModel = viewModel()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xff00afb9)),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Today's Quote",
                Modifier.padding(20.dp),
                fontWeight = FontWeight.Bold,
            )

            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = "favorite icon",
                tint = Color(0xfff07167),
                modifier = Modifier
                    .padding(20.dp)
                    .clickable {
                        navigateToFavoriteQuote()
                    }
            )
        }

        QuoteCard(quote = quote, context, saveCount)
    }
}


@SuppressLint("QueryPermissionsNeeded")
@Composable
fun QuoteCard(
    quote: Quote,
    context: MainActivity,
    saveCount: MutableState<Int>,
) {

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Card(
            modifier = Modifier.padding(24.dp),
            //.background(Color(0xfff07167)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = "\"${quote.quote}\"",
                Modifier
                    .padding(20.dp)
                    .align(Alignment.CenterHorizontally),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                text = quote.author,
                Modifier
                    .padding(20.dp)
                    .align(Alignment.End),
                fontStyle = FontStyle.Italic
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = "share icon",
                tint = Color(0xff0081a7),
                modifier = Modifier.clickable {

                    val sendQuote = "Quote: -  \"${quote.quote}\" \n\n Author: - ${quote.author}"

                    val intent = Intent()

                    intent.action = Intent.ACTION_SEND
                    intent.putExtra(Intent.EXTRA_TEXT, sendQuote)
                    intent.type = "text/plain"

                    if (intent.resolveActivity(context.packageManager) != null) {
                        context.startActivity(Intent.createChooser(intent, "Share to :"))
                    } else {
                        Toast.makeText(context, "No app available to share the quote", Toast.LENGTH_SHORT).show()
                    }
                }
            )


            // Save favorite quote
            var newQuote = quote.quote
            var newAuthor = quote.author
            val composableScope = rememberCoroutineScope()

            val iconColor = remember { mutableStateOf(Color(0xff0081a7)) }
            if (saveCount.value > 0) {
                iconColor.value = Color(0xfff07167)
            }

            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = "favorite icon",
                tint = iconColor.value,
                modifier = Modifier.clickable {
                    composableScope.launch(Dispatchers.IO) {
                        if (saveCount.value < 1) {
                            supabase.from("quote").insert(
                                mapOf("quote" to newQuote, "author" to newAuthor)
                            ) {
                                select()
                                single()
                            }.decodeAs<Quote>()
                            newQuote = ""
                            newAuthor = ""
                            saveCount.value++
                        }
                    }
                    iconColor.value = Color.Red
                    Toast.makeText(context, "Quote added to Favorites", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
}