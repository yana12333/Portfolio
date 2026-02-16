package com.example.sqltest

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.sqltest.ui.theme.SQLTestTheme

class MainActivity : ComponentActivity() {

    private lateinit var db: SQLiteDatabase

    private val DATABASE_NAME = "myDatabase.db"
    private val TABLE = "mainTable"

    // Клас даних для одного елемента
    data class Item(val id: Int, val text: String)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Відкрити або створити БД
        db = openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE, null)

        // Створити таблицю
        val createTable =
            "CREATE TABLE IF NOT EXISTS $TABLE (" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "column_one TEXT NOT NULL);"
        db.execSQL(createTable)

        // Додати записи, якщо таблиця порожня
        val cursor = db.rawQuery("SELECT COUNT(*) FROM $TABLE", null)
        cursor.moveToFirst()
        val count = cursor.getInt(0)
        cursor.close()

        if (count == 0) {
            db.execSQL("DELETE FROM sqlite_sequence WHERE name='$TABLE'")

            db.execSQL("INSERT INTO $TABLE (column_one) VALUES ('First item')")
            db.execSQL("INSERT INTO $TABLE (column_one) VALUES ('Second item')")
            db.execSQL("INSERT INTO $TABLE (column_one) VALUES ('Third item')")
        }

        setContent {
            MaterialTheme {
                AppScreen()
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun AppScreen() {

        var items by remember { mutableStateOf(loadItems()) }

        fun refresh() {
            items = loadItems()
        }

        Scaffold(
            topBar = {
                TopAppBar(title = { Text("SQLite Jetpack Compose") })
            }
        ) { padding ->

            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) {
                ItemList(
                    items = items,
                    onItemClick = { item ->
                        Toast.makeText(
                            this@MainActivity,
                            "ID = ${item.id}",
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    onItemDelete = { item ->
                        deleteItem(item.id)
                        refresh()
                    }
                )
            }
        }
    }

    @Composable
    fun ItemList(
        items: List<Item>,
        onItemClick: (Item) -> Unit,
        onItemDelete: (Item) -> Unit
    ) {
        Column {
            for (item in items) {
                ItemRow(
                    item = item,
                    onClick = { onItemClick(item) },
                    onDelete = { onItemDelete(item) }
                )
            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun ItemRow(
        item: Item,
        onClick: () -> Unit,
        onDelete: () -> Unit
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .combinedClickable(
                    onClick = onClick,
                    onLongClick = onDelete
                ),
            tonalElevation = 3.dp
        ) {
            Text(
                text = item.text,
                modifier = Modifier.padding(16.dp)
            )
        }
    }

    // Запит отримання всіх записів
    private fun loadItems(): List<Item> {
        val cursor = db.rawQuery("SELECT _id, column_one FROM $TABLE", null)

        val list = mutableListOf<Item>()

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(0)
                val text = cursor.getString(1)
                list.add(Item(id, text))
            } while (cursor.moveToNext())
        }

        cursor.close()
        return list
    }

    // Видалення запису
    private fun deleteItem(id: Int) {
        db.execSQL("DELETE FROM $TABLE WHERE _id = $id")
    }

}
