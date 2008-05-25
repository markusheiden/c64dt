#include "stringops.h"

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
