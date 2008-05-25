#ifndef ___NET64_H___
#define ___NET64_H___

struct Net64
{
private:
	struct NetHost *Host;
	unsigned long Timeout;
	unsigned char Sequence;

	char Send(unsigned char service, unsigned short size);

public:
	void Init(struct NetHost *host);
	void Exit(void);

	int SendData(unsigned short address, unsigned short size, unsigned char *buffer);
	int SendFill(unsigned short address, unsigned short size, unsigned char fill);
	int ExecJump(unsigned short address);
	int ExecRun(void);
};

#endif
