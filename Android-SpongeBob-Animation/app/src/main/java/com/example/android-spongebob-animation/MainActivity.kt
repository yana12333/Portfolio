package com.example.lab3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.DrawableRes
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.lab3.ui.theme.LAB3Theme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LAB3Theme {
                animation()
            }
        }
    }
}


@Composable
fun animation() {
    val offsetMeduza = remember { Animatable(initialValue = 800f) }
    val offsetBubble1 = remember { Animatable(initialValue = 900f) }
    val offsetBubble2 = remember { Animatable(initialValue = 900f) }
    val offsetBubble3 = remember { Animatable(initialValue = 900f) }

    // Запуск анімації
    LaunchedEffect(key1 = Unit) {
        launch { animateOffset(offset = offsetMeduza, targetValue = 100f) }
        launch { animateOffset(offset = offsetBubble1, targetValue = -100f, delayMillis = 1000) }
        launch { animateOffset(offset = offsetBubble2, targetValue = -100f, delayMillis = 500) }
        launch { animateOffset(offset = offsetBubble3, targetValue = -100f, delayMillis = 1200) }
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(red = 61, green = 130, blue = 242))
    ) {

        //Медуза, що рухається
        MovingImage(resId = R.drawable.meduza, size = 150.dp, offsetX = 130.dp, offsetY = offsetMeduza.value.dp)

        // Бульбашки
        MovingImage(resId = R.drawable.buble, size = 100.dp, offsetX = 30.dp, offsetY = offsetBubble1.value.dp)
        MovingImage(resId = R.drawable.buble, size = 80.dp, offsetX = 260.dp, offsetY = offsetBubble2.value.dp)
        MovingImage(resId = R.drawable.buble, size = 50.dp, offsetX = 180.dp, offsetY = offsetBubble3.value.dp)

        // Губка Боб
        MovingImage(resId = R.drawable.spongebob, size = 0.dp, offsetY = 200.dp, fillMaxSize = true)

        // Трава
        MovingImage(resId = R.drawable.grass, size = 0.dp, offsetY = 420.dp, fillMaxSize = true)
    }
}

// Функція для анімації
suspend fun animateOffset(
    offset: Animatable<Float, AnimationVector1D>,
    targetValue: Float,
    delayMillis: Long = 0,
    duration: Int = 3000
) {
    delay(timeMillis = delayMillis)
    offset.animateTo(
        targetValue = targetValue,
        animationSpec = tween(durationMillis = duration, easing = LinearEasing)
    )
}

// Функція-шаблон для зображень
@Composable
fun MovingImage(
    @DrawableRes resId: Int,
    size: Dp,
    offsetX: Dp = 0.dp,
    offsetY: Dp = 0.dp,
    fillMaxSize: Boolean = false
) {
    Image(
        painter = painterResource(id = resId),
        contentDescription = null,
        modifier = if (fillMaxSize) {
            Modifier
                .fillMaxSize()
                .offset(x = offsetX, y = offsetY)
        } else {
            Modifier
                .size(size)
                .offset(x = offsetX, y = offsetY)
        }
    )
}



@Composable
@Preview(showBackground = true)
fun prev(){
    animation()
}