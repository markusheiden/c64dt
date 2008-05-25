#ifndef ___STRINGOPS_H___
#define ___STRINGOPS_H___

extern char pet2asc(unsigned char c);
extern unsigned char asc2pet(char c);
extern void pet2asc(unsigned char *name);

extern char *copystring(const char *string);
extern int StringCompare(char *string1, char *string2);
extern unsigned long ConvertNumber(char *string);
extern unsigned int strlength(const char *string);

#endif
