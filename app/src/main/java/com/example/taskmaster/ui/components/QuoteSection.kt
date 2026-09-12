package com.example.taskmaster.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun QuoteSection(quote: String) {
    Text(text = "Quote: $quote")
}
