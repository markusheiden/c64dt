#include "stringops.h"

char pet2asc(unsigned char c)
{
	if ((c >= 0xC0) && (c <= 0xDF))
	{
		c = c + 'a' - 0xC1;
	}

	if ((c >= 'a') && (c <= 'z'))
	{
		c = c + 'A' - 'a';
	} else {
		if ((c >= 'A') && (c <= 'Z'))
		{
			c = c + 'a' - 'A';
		}
	}

	return c;
}

unsigned char asc2pet(char c)
{
	if ((c >= 'a') && (c <= 'z'))
	{
		c = c + 'A' - 'a';
	} else {
		if ((c >= 'A') && (c <= 'Z'))
		{
			c = c + 'a' - 'A';
		}
	}

	return c;
}

void pet2asc(unsigned char *name)
{
	unsigned long i;

	i = 0;
	while (name[i])
	{
		name[i] = pet2asc(name[i]);
		i++;
	}
}

unsigned int strlength(const char *string)
{
	unsigned int i;

	if (!string) return 0;
	i = 0;
	while (i < 1024)
	{
		if (!string[i]) break;
		i++;
	}
	return (i + 1);
}

char *copystring(const char *string)
{
	unsigned int len, i;
	char *newstr;

	len = strlength(string);
	newstr = 0;
	if (len > 0)
	{
		newstr = new char[len];
		for (i=0;i<len;i++) newstr[i] = string[i];
	}
	return newstr;
}

int StringCompare(char *string1, char *string2)
{
	unsigned long i;

	i = 0;
	while ((string1[i] == string2[i]) && (string1[i]) && (string2[i]))
	{
		i++;
	}

	return (string1[i] == string2[i]);
}

unsigned long ConvertNumber(char *string)
{
	unsigned long i, val;
	char c;

	if (!string) return 0;

	i = 0;
	while (string[i] == 0x20)
	{
		i++;
	}

	val = 0;
	if (string[i] == '$')
	{
		while (1)
		{
			i++;
			c = string[i];
			if ((c >= '0') && (c <= '9'))
			{
				val <<= 4;
				val |= (c - '0');
			} else {
				if ((c >= 'a') && (c <= 'f'))
				{
					val <<= 4;
					val |= (c - 'a' + 10);
				} else {
					if ((c >= 'A') && (c <= 'F'))
					{
						val <<= 4;
						val |= (c - 'A' + 10);
					} else break;
				}
			}
		}
	} else {
		while (1)
		{
			c = string[i];
			i++;
			if ((c >= '0') && (c <= '9'))
			{
				val *= 10;
				val += (c - '0');
			} else break;
		}
	}
	return val;
}
