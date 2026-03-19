#ifndef MODULE_H
#define MODULE_H

int WordSum(char *word);

void SortWord(char *text[], int count, int f(char*));

void ClassifyWords(char *text[], int count, int &neutral, int &good, int &bad);

#endif 