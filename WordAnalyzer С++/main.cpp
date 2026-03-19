#include "module.h"
#include <iostream>
#include <fstream>
#include <cstring>

int main(int argc, char *argv[])
{
    int size = 100;
    int neutral, good, bad;
    char **text;
    int count = argc - 2;
    int count2 = 0;

    if (argc < 2)
    {
        std::cout << "Введіть ім'я файлу-виводу!" << std::endl;
    }
    else if (argc < 3)
    {
        std::ofstream outfile(argv[1]);
        if (!outfile.is_open())
        {
            std::cout << "Помилка відкриття файлу для запису " << argv[1] << std::endl;
            return 0;
        }

        std::string filename;
        std::cout << "Введіть ім'я файлу вводу: ";
        std::cin >> filename;
        std::ifstream file(filename);
        if (!file.is_open())
        {
            std::cout << "Помилка відкриття файлу " << filename << std::endl;
            return 0;
        }

        text = new char *[size]; // Створюємо динамічний масив
        for (int i = 0; i < size; i++)
        {
            text[i] = new char[size];
        }

        int index = 0;
        char word[size]; // створюємо масив символів для зберігання слів
        while (file >> word && index < size)
        {
            text[index] = new char[strlen(word) + 1]; // виділяємо пам'ять для копіювання слова
            strcpy(text[index], word);                // копіюємо слово в масив
            count2++;
            index++;
        }

        for (int i = 0; i < index; i++)
        {
            outfile << text[i] << " ";
        }
        outfile << std::endl;

        outfile << "------------------------------------------------" << std::endl;
        SortWord(text, count2, WordSum);
        outfile << "Слова упорядковані за незростанням суми кодів:" << std::endl;
        for (int i = 0; i < count2; i++)
        {
            outfile << text[i] << std::endl;
        }
        outfile << "------------------------------------------------" << std::endl;
        ClassifyWords(text, count2, neutral, good, bad);
        outfile << "Кількість ніяких слів: " << neutral << std::endl;
        outfile << "Кількість гарних слів: " << good << std::endl;
        outfile << "Кількість поганих слів: " << bad << std::endl;
        std::cout << "--------------------------------------------" << std::endl;
        std::cout << "|Результати успішно записано у файл " << argv[1] << "|" << std::endl;
        std::cout << "--------------------------------------------" << std::endl;

        file.close();
        outfile.close();
        for (int i = 0; i < size; i++)
        {
            delete[] text[i]; // Звільняємо пам'ять
        }
        delete[] text;
    }
    else
    {
        text = new char *[count];
        for (int i = 0; i < count; i++)
        {
            text[i] = new char[strlen(argv[i + 2]) + 1];
            strcpy(text[i], argv[i + 2]);
        }

        std::ofstream outfile(argv[1]);
        if (!outfile.is_open())
        {
            std::cout << "Помилка відкриття файлу" << argv[1] << std::endl;
            return 0;
        }
        outfile << "Введений текст: " << std::endl;
        for (int i = 0; i < count; i++)
        {
            outfile << text[i] << " ";
        }
        outfile << std::endl;
        outfile << "------------------------------------------------" << std::endl;
        SortWord(text, count, WordSum);

        outfile << "Слова упорядковані за незростанням суми кодів:" << std::endl;
        for (int i = 0; i < count; i++)
        {
            outfile << text[i] << std::endl;
        }
        outfile << "------------------------------------------------" << std::endl;
        ClassifyWords(text, count, neutral, good, bad);
        outfile << "Кількість ніяких слів: " << neutral << std::endl;
        outfile << "Кількість гарних слів: " << good << std::endl;
        outfile << "Кількість поганих слів: " << bad << std::endl;
        std::cout << "--------------------------------------------" << std::endl;
        std::cout << "|Результати успішно записано у файл " << argv[1] << "|" << std::endl;
        std::cout << "--------------------------------------------" << std::endl;


        outfile.close();
        for (int i = 0; i < count; i++)
        {
            delete[] text[i]; // Звільняємо пам'ять
        }
        delete[] text; // Звільнення пам'яті
    }
    return 0;
}
