# SQLite List App (Android)

## Description
This Android application demonstrates working with a SQLite database without using adapter classes.  
The program allows users to view, interact with, and modify a list of records stored in the database using Jetpack Compose (LazyColumn).

---

## Program Functionality

### User Interface
- On launch, the app displays a list of rows from the database.  
- Tapping on a row shows a **Toast** message displaying the unique ID of that record.  
- Long-pressing a row deletes it from the database.  

### Example Actions
1. Tap a row → Toast shows its ID  
2. Long-press a row → Row is deleted  
3. UI automatically updates to reflect changes  

The app uses **mutableStateOf** and a **refresh()** function to keep the UI in sync with the database.

---

## Features
- Displays a list of records from a SQLite database  
- Shows the record ID on tap using Toast  
- Deletes a record on long press  
- Automatic UI refresh after database updates  

---

## Technologies
- Kotlin  
- Android SDK  
- Jetpack Compose (LazyColumn)  
- SQLite  
- State Management

---

## Purpose
The goal of this project was to:  
- Learn how to work with SQLite in Android without adapters  
- Understand how to display and manage dynamic data using Jetpack Compose  
- Implement user interaction with list items (tap and long press)  
- Keep the UI synchronized with the database using state management  

---

## How to Run
1. Clone the repository  
2. Open the project in Android Studio  
3. Run the app on an emulator or physical device  
