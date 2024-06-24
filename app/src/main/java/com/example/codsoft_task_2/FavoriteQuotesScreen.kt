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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
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
import kotlinx.coroutines.withContext

@Composable
fun FavoriteQuotesList(
    navigateToHomeScreen: () -> Unit,
    context: MainActivity,
) {
    Column {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xff00afb9)),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "back icon",
                Modifier
                    .padding(20.dp)
                    .clickable {
                        navigateToHomeScreen()
                    }
            )

            Text(
                text = "Favorite Quotes",
                Modifier.padding(20.dp),
                fontWeight = FontWeight.Bold,
            )
        }

        FavoriteQuotesCard(context)
    }
}

@SuppressLint("QueryPermissionsNeeded")
@Composable
fun FavoriteQuotesCard(context: MainActivity) {
    val composableScope = rememberCoroutineScope()
    val quotes = remember { mutableStateListOf<Quote>() }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            val results = supabase.from("quote").select().decodeList<Quote>()
            quotes.addAll(results)
        }
    }


    LazyColumn {
        items(quotes) {
                quote ->

            if (quote.quote != null) {
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

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                tint = Color(0xff0081a7),
                                contentDescription = "share icon",
                                modifier = Modifier.clickable {
                                    val sendQuote = "Quote: -  \"${quote.quote}\" \n\n Author: - ${quote.author}"

                                    val intent = Intent(Intent.ACTION_VIEW)

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


                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = "favorite icon",
                                tint = Color(0xfff07167),
                                modifier = Modifier.clickable {
                                    composableScope.launch(Dispatchers.IO) {

                                        supabase.from("quote").delete {
                                            filter { eq("id", quote.id) }
                                        }

                                    }
                                    quotes.remove(quote)

                                    Toast.makeText(context, "Quote removed from Favorites", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    }
                }
            }
        }
    }

}