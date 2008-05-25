#ifndef ___ARGSTREAM_H___
#define ___ARGSTREAM_H___

struct ArgToken
{
public:
	char *String;
	int Token;
};

struct ArgStream
{
private:
	char **Arguments;
	int ArgCount;
	int ArgPosition;
	ArgToken *Tokens;

public:
	void Init(char **args, int argc, ArgToken *tkn);
	void Exit(void);
	int GetNextToken(void);
	char *GetNext(void);
	char *Peek(void);
	int HasArguments(void);
};

#endif
