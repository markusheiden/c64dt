#include "argstream.h"
#include "stringops.h"

void ArgStream::Init(char **args, int argc, ArgToken *tkn)
{
	Arguments = args;
	ArgCount = argc;
	ArgPosition = 0;
	Tokens = tkn;
}

void ArgStream::Exit(void)
{
}

int ArgStream::HasArguments(void)
{
	return (ArgPosition < ArgCount);
}

char *ArgStream::GetNext(void)
{
	if (!HasArguments()) return 0;

	return (Arguments[ArgPosition++]);
}

char *ArgStream::Peek(void)
{
	if (!HasArguments()) return 0;

	return (Arguments[ArgPosition]);
}

int ArgStream::GetNextToken(void)
{
	int i;
	char *arg;

	arg = GetNext();
	i = 0;
	if (!arg)
	{
		while (Tokens[i].String) i++;
	} else {
		while (Tokens[i].String)
		{
			if (StringCompare(Tokens[i].String, arg)) break;
			i++;
		}
	}
	return Tokens[i].Token;
}
