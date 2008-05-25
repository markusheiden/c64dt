#ifndef ___VSTREAM_H___
#define ___VSTREAM_H___

struct VStream
{
private:
	struct VFile *File;
	struct VD64File *D64File;
	struct VDirectory *Directory;

public:
	void Init(void);
	unsigned char Open(char *path, unsigned char *name, unsigned char sec, unsigned char dirtype);
	void Exit(void);

	long Read(unsigned char *buffer, long size);
	void Write(unsigned char *buffer, long size);
	unsigned char BufferPosition(unsigned char pos);
	unsigned char Finished(void);
	unsigned char IsWriteFile(void);
};

#endif
