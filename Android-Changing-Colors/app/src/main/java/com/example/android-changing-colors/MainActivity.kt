package com.example.lab2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lab2.ui.theme.LAB2Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LAB2Theme {
                buildButton()
            }
        }
    }
}

@Composable
fun buildButton(){
    var targetColor by remember { mutableStateOf(value = Color.White) }
    val backgroundColor by animateColorAsState(targetValue = targetColor)
    var count by remember { mutableStateOf(value = 0) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Button(onClick = {
            count++
            targetColor = Color(
                red = (0..255).random(),
                green = (0..255).random(),
                blue = (0..255).random()
            )
        },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black
            )
        ) {
            Text(text = stringResource(R.string.change_color))
            Image(
                painter = painterResource(id = R.drawable.img_1),
                contentDescription = stringResource(R.string.image_desc),
                modifier = Modifier
                    .size(20.dp)
                    .offset(x = 8.dp)
            )
        }
        Text(
            text = stringResource(R.string.color_count, count),
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.offset(y = -60.dp)
        )
        Button(onClick = {
            count = 0
            targetColor = Color(
                red = (255),
                green = (255),
                blue = (255)
            )},
            colors = ButtonDefaults.buttonColors(
            containerColor = Color.Black
            ),
            modifier = Modifier.offset(y = 50.dp)
        ){
            Text(text = stringResource(R.string.restart))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    LAB2Theme {
        buildButton()
    }
}