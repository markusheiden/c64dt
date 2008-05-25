#ifndef ___VDIRECTORY_H___
#define ___VDIRECTORY_H___

struct VDirectory
{
private:
	unsigned char *Buffer;
	unsigned long Size;
	unsigned long MaxSize;
	unsigned long Position;
	unsigned long LastPos;

	void WriteByte(unsigned char b);
	void WriteWord(unsigned short w);
	void AddEntry(char *name, unsigned long size, unsigned long attr);
	unsigned char ScanDirectory(char *path);
	unsigned char ScanDirectory(struct VD64 *image);
	void CreatePath(char *newpath, char *path, unsigned char *name);

public:
	void Init(void);
	unsigned char Open(char *path, unsigned char *name, unsigned char dirtype);
	void Exit();

	long Read(unsigned char *buffer, long size);
	void BufferPosition(unsigned char pos);
	unsigned char Finished(void);
};

#endif
