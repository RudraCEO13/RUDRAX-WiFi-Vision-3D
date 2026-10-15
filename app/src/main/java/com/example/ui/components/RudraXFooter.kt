package com.example.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyanBright
import com.example.ui.theme.TextMuted

@Composable
fun RudraXFooter(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp, horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Powered by RudraX Technologies Studio",
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = CyanBright,
            letterSpacing = 0.5.sp,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "© 2026 RudraX Technologies Studio | All rights reserved.",
            fontSize = 10.sp,
            fontWeight = FontWeight.Normal,
            color = TextMuted,
            letterSpacing = 0.3.sp,
            textAlign = TextAlign.Center
        )
    }
}
