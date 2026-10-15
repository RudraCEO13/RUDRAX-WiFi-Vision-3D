package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SignalQuality
import com.example.ui.theme.SignalExcellent
import com.example.ui.theme.SignalFair
import com.example.ui.theme.SignalGood
import com.example.ui.theme.SignalVeryWeak
import com.example.ui.theme.SignalWeak
import com.example.ui.theme.TextMuted

@Composable
fun SignalQualityBadge(
    quality: SignalQuality,
    modifier: Modifier = Modifier
) {
    val (badgeColor, bgColor) = when (quality) {
        SignalQuality.EXCELLENT -> Pair(SignalExcellent, Color(0x2200E5FF))
        SignalQuality.GOOD -> Pair(SignalGood, Color(0x2200E676))
        SignalQuality.FAIR -> Pair(SignalFair, Color(0x22FFD600))
        SignalQuality.WEAK -> Pair(SignalWeak, Color(0x22FF9100))
        SignalQuality.VERY_WEAK -> Pair(SignalVeryWeak, Color(0x22FF1744))
        SignalQuality.UNAVAILABLE -> Pair(TextMuted, Color(0x2264748B))
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .border(1.dp, badgeColor.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(badgeColor)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = quality.label,
            color = badgeColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.8.sp
        )
    }
}
