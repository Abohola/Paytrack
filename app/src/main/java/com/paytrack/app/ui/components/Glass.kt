package com.paytrack.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.paytrack.app.ui.theme.Crimson

@Composable
fun GlassSurface(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 24.dp,
    padding: PaddingValues = PaddingValues(20.dp),
    accent: Color = Crimson,
    content: @Composable BoxScope.() -> Unit,
) {
    val shape = RoundedCornerShape(cornerRadius)
    Box(
        modifier = modifier
            .shadow(18.dp, shape, ambientColor = Color.Black, spotColor = accent.copy(alpha = 0.2f))
            .clip(shape)
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.105f),
                        accent.copy(alpha = 0.07f),
                        Color.White.copy(alpha = 0.035f),
                    ),
                ),
            )
            .border(1.dp, Color.White.copy(alpha = 0.14f), shape)
            .padding(padding),
        content = content,
    )
}
