#ifndef ___VBINARY_H___
#define ___VBINARY_H___

struct VBinary
{
private:
	unsigned char *Buffer;
	unsigned long Size;
	unsigned long MaxSize;
	unsigned long Position;
	unsigned long LastPos;

	void WriteByte(unsigned char b);
	void AddEntry(char *name, unsigned long size, unsigned long attr);

public:
	void Init(void);
	void Exit(void);

	long Read(unsigned char *buffer, long size);
	long ReadDirLine(unsigned char *buffer, long size);
	void BufferPosition(unsigned char pos);
	unsigned char Finished(void);
};

#endif
