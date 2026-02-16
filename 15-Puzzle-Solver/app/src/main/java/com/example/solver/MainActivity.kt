package com.example.solver

import android.graphics.Picture
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.abs

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MenuApp()
        }
    }
}

enum class ScreenState { MENU, INFO, START }

val green = Color(21, 37, 38)
val orange = Color(191,54,23)
val white = Color(189, 191, 191)

@Composable
fun MenuApp() {
    var currentScreen by remember { mutableStateOf(value = ScreenState.MENU) }

    // Вибір екрана залежно від поточного стану
    when (currentScreen) {
        // Головне меню
        ScreenState.MENU -> menuScreen(
            onStartClick = { currentScreen = ScreenState.START },
            onInfoClick = { currentScreen = ScreenState.INFO }
        )
        // Інформаційний екран
        ScreenState.INFO -> infoScreen(
            onBackClick = { currentScreen = ScreenState.MENU }
        )
        // Основний екран з грою
        ScreenState.START -> startScreen(
            onBackClick = { currentScreen = ScreenState.MENU }
        )
    }
}

// Функція, що малює екран меню
@Composable
fun menuScreen(onStartClick: () -> Unit, onInfoClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = white),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = onStartClick,
            colors = ButtonDefaults.buttonColors(containerColor = green),
            shape = RoundedCornerShape(size = 19.dp),
            modifier = Modifier
                .size(width = 100.dp, height = 50.dp)
                .offset(y = -35.dp)
        ) {
            Text(
                text = "Start",
                fontSize = 20.sp)
        }
        Button(
            onClick = onInfoClick,
            colors = ButtonDefaults.buttonColors(containerColor = green),
            shape = RoundedCornerShape(size = 19.dp)
        ) {
            Text(
                text = "Information",
                fontSize = 20.sp)
        }
    }
}

// Функція, що малює екран з 15-Puzzle
@Composable
fun startScreen(onBackClick: () -> Unit) {
    // Стан плиток: список чисел від 1 до 15 та 0
    var tiles by remember {
        mutableStateOf(
            value = listOf(
                1, 2, 3, 4,
                5, 6, 7, 8,
                9, 10, 11, 12,
                13, 14, 15, 0
            )
        )
    }
    val scope = rememberCoroutineScope()

    // Функція переміщення плитки при кліку
    fun moveTile(index: Int) {
        val emptyIndex = tiles.indexOf(0) // Індекс порожньої клітинки
        val row = index / 4 // Рядок клітинки, яку клікнули
        val col = index % 4 // Стовпець клітинки, яку клікнули
        val emptyRow = emptyIndex / 4 // Рядок порожньої клітинки
        val emptyCol = emptyIndex % 4 // Стовпець порожньої клітинки

        // Перевірка, чи клітинка сусідня з порожньою
        if ((row == emptyRow && abs(n = col - emptyCol) == 1) || (col == emptyCol && abs(n = row - emptyRow) == 1)
        ) {
            // Міняємо місцями клікнуту плитку і порожню
            val newTiles = tiles.toMutableList()
            newTiles[emptyIndex] = tiles[index]
            newTiles[index] = 0
            tiles = newTiles // Оновлюємо стан
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = white),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(height = 220.dp))
        // Заголовок гри
        Box {
            Text(
                text = "15 Puzzle",
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                color = orange,
                modifier = Modifier.offset(x = 1.dp, y = 1.dp)
            )
            Text(
                text = "15 Puzzle",
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                color = green
            )
        }
        Spacer(modifier = Modifier.height(height = 20.dp))
        // Поле з плитками
        Box(
            contentAlignment = Alignment.Center
        ) {
            GameBoard(
                tiles = tiles,
                onTileClick = { index -> moveTile(index) }
            )
        }
        Spacer(modifier = Modifier.height(height = 20.dp))

        // Кнопка "Solve" для автоматичного рішення
        Button(
            onClick = {
                scope.launch {
                    val solution = withContext(context = Dispatchers.Default) {
                        bfsSolve(start = tiles)
                    }
                    if (solution != null && solution.size > 1) {
                        for (step in solution.drop(n = 1)) {
                            delay(timeMillis = 300)
                            tiles = step
                        }
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = green),
            shape = RoundedCornerShape(size = 15.dp),
            modifier = Modifier
                .offset(y = 20.dp)
                .size(width = 100.dp, height = 50.dp)
        ) {
            Text(text = "Solve", fontSize = 20.sp)
        }

        // Кнопка повернення назад
        Button(
            onClick = onBackClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = green,
            ),
            shape = RoundedCornerShape(size = 15.dp),
            modifier = Modifier.offset(x = -150.dp, y = -580.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.arrow),
                contentDescription = "arrow",
                modifier = Modifier.size(size = 24.dp)
            )
        }
    }
}

// Функція з інформацією для користувача
@Composable
fun infoScreen(onBackClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = white),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "This program is a solver for the 15-puzzle. " +
                    "The user just needs to arrange the tiles in random positions and then press the SOLVE button.",
            fontSize = 20.sp,
            modifier = Modifier.padding(all = 16.dp)
        )
        Spacer(modifier = Modifier.height(height = 20.dp))
        Button(
            onClick = onBackClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = green,
            ),
            shape = RoundedCornerShape(size = 15.dp),
            modifier = Modifier.size(width = 100.dp, height = 50.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.arrow),
                contentDescription = "arrow",
                modifier = Modifier.size(size = 24.dp)
            )
        }
    }
}


// Функція, що малює "дошку" для гри
@Composable
fun GameBoard(tiles: List<Int>, onTileClick: (Int) -> Unit) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
        shape = RoundedCornerShape(size = 16.dp),
        colors = CardDefaults.cardColors(containerColor = green),
        modifier = Modifier
            .padding(all = 8.dp)
            .wrapContentSize()
    ) {
        Column(
            modifier = Modifier
                .padding(all = 12.dp)
                .width(width = 300.dp)
                .height(height = 300.dp)
        ) {
            for (row in 0..3) {
                Row(
                    modifier = Modifier.weight(weight = 1f),
                    horizontalArrangement = Arrangement.spacedBy(space = 6.dp)
                ) {
                    for (col in 0..3) {
                        val index = row * 4 + col
                        val value = tiles[index]

                        Tile(
                            number = value,
                            onClick = { onTileClick(index) },
                            modifier = Modifier
                                .weight(weight = 4f)
                                .fillMaxHeight()
                        )
                    }
                }
                if (row < 3) {
                    Spacer(modifier = Modifier.height(height = 6.dp))
                }
            }
        }
    }
}

// Фунція, що малює один тайл
@Composable
fun Tile(number: Int, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val isEmpty = number == 0
    Box(
        modifier = modifier
            .padding(all = 4.dp)
            .clip(shape = RoundedCornerShape(size = 10.dp))
            .size(size = 70.dp)
            .background(color = if (isEmpty) Color.LightGray else orange)
            .clickable(enabled = !isEmpty) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (!isEmpty) {
            Text(
                text = number.toString(),
                fontSize = 28.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// Клас вузла для BFS: зберігає стан плиток, посилання на батька та хід, що привів до цього стану
data class Node(val state: List<Int>, val parent: Node?, val move: String?)


// Функція BFS для знаходження рішення 15-Puzzle
fun bfsSolve(start: List<Int>): List<List<Int>>? {

    // Цільовий стан
    val goal = listOf(
        1, 2, 3, 4,
        5, 6, 7, 8,
        9, 10, 11, 12,
        13, 14, 15, 0
    )

    // Якщо стартовий стан вже правильний — повертаємо його
    if (start == goal)
        return listOf(start)

    // Черга для BFS та множина відвіданих станів
    val queue = ArrayDeque<Node>()
    val visited = mutableSetOf<List<Int>>()

    // Додаємо початковий вузол до черги та відвіданих
    queue.add(Node(state = start, parent = null, move = null))
    visited.add(start)

    // Можливі напрямки руху порожньої клітинки
    val directions = listOf(
        Pair(first = -1, second = 0), // вверх
        Pair(first = 1, second = 0),  // вниз
        Pair(first = 0, second = -1), // вліво
        Pair(first = 0, second = 1)   // вправо
    )

    // Функція для генерації сусідніх станів
    fun neighbors(state: List<Int>): List<List<Int>> {
        val list = mutableListOf<List<Int>>()
        val zeroIndex = state.indexOf(0) // Знаходимо порожню клітинку
        val r = zeroIndex / 4 // Рядок порожньої клітинки
        val c = zeroIndex % 4 // Стовпець порожньої клітинки

        // Перебираємо всі напрямки
        for ((dr, dc) in directions) {
            val nr = r + dr
            val nc = c + dc
            // Перевірка, чи нова позиція всередині дошки
            if (nr in 0..3 && nc in 0..3) {
                val newIndex = nr * 4 + nc
                val newState = state.toMutableList()
                // Міняємо місцями порожню клітинку і сусідню плитку
                newState[zeroIndex] = newState[newIndex]
                newState[newIndex] = 0
                list.add(newState) // Додаємо новий стан
            }
        }
        return list
    }

    // Основний цикл BFS
    while (queue.isNotEmpty()) {
        val current = queue.removeFirst() // Беремо вузол з черги

        // Перебираємо всі сусідні стани
        for (n in neighbors(state = current.state)) {
            if (n !in visited) { // Якщо стан ще не відвіданий
                val next = Node(state = n, parent = current, move = null)
                // Якщо досягли цільового стану, формуємо шлях рішення
                if (n == goal) {
                    val path = mutableListOf<List<Int>>()
                    var cursor: Node? = next
                    while (cursor != null) {
                        path.add(cursor.state)
                        cursor = cursor.parent
                    }
                    path.reverse() // Шлях будується з кінця, тому перевертаємо
                    return path
                }
                visited.add(n)
                queue.add(next)
            }
        }
    }
    // Якщо рішення немає
    return null
}

