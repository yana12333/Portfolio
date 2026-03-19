#include "module.h"
#include <iostream>
#include <fstream>
#include <cstring>

// завдання 1
int WordSum(char *word)
{
    int sum = 0;
    for (int i = 0; word[i] != '\0'; i++)
    {
        sum += int(word[i]);
    }
    return sum;
}


void SortWord(char *text[], int count, int f(char*))
{
    for (int i = 0; i < count - 1; i++)
    {
        for (int j = 0; j < count - i - 1; j++)
        {
            if (f(text[j]) < f(text[j + 1]))
            {
                char *temp = text[j];
                text[j] = text[j + 1];
                text[j + 1] = temp;
            }
        }
    }
}

// завдання 2
void ClassifyWords(char *text[], int count, int &neutral, int &good, int &bad)
{
    neutral = good = bad = 0;
    for (int i = 0; i < count; i++)
    {
        int length = std::strlen(text[i]);
        if (length == 1)
            neutral++;
        else if (length < 10)
            good++;
        else
            bad++;
    }
}