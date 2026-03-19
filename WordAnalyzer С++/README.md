# WordAnalyzer

## Description
This program reads words either from a file or from command-line arguments, sorts them by the sum of ASCII codes of their characters, and classifies them as neutral, good, or bad. The results are saved to a specified output file.

## Features
- Reads words from a file or command-line arguments
- Dynamically allocates memory for words
- Sorts words by descending sum of ASCII codes
- Classifies words into neutral, good, or bad categories
- Saves results to a formatted output file

## Technologies
- C++
- Dynamic Memory Management
- File I/O
- String Manipulation
- Custom Sorting and Classification Functions

## Purpose
The goal of this project was to practice working with C++ features such as dynamic memory, file operations, and string handling while implementing simple algorithms for sorting and classification.

## How to Run
- Clone the repository
- Compile the project using a C++ compiler:
  g++ main.cpp module.cpp -o WordAnalyzer
- Run the application:
  ./WordAnalyzer output.txt
- Follow the prompts to enter words via a file or directly as command-line arguments
