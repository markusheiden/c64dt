#ifndef ___VD64FILE_H___
#define ___VD64FILE_H___

struct VD64File
{
private:
	unsigned char WriteAccess;	/* read/write access */
	unsigned char *Name;		/* PETSCII name */
	char *Path;					/* access path */

	struct VD64 *DiskImage;
	unsigned char Track, Sector, SectorPos;
	unsigned long Position;
	unsigned char LastTrack, LastSector, LastSectorPos;
	unsigned long LastPos;

	unsigned char FindFile(unsigned char *name);

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
