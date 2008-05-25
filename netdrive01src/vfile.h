#ifndef ___VFILE_H___
#define ___VFILE_H___

struct VFile
{
private:
	unsigned char WriteAccess;	/* read/write access */
	unsigned char *Name;		/* PETSCII name */
	char *Path;					/* access path */

	void *File;					/* OS file */
	unsigned long Position;
	unsigned long LastPos;

public:
	void Init(void);
	unsigned char Open(char *path, unsigned char *name, unsigned char access);

	void Exit(void);

	long Read(unsigned char *buffer, long size);
	void Write(unsigned char *buffer, long size);
	void BufferPosition(unsigned char pos);
	unsigned char Finished(void);
	unsigned char IsWriteFile(void);
};

#endif
