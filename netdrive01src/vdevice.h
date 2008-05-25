#ifndef ___VDEVICE_H___
#define ___VDEVICE_H___

struct VDevice
{
private:
	struct VStream *Streams;

	unsigned char *Error;
	unsigned long Position;
	unsigned long LastPos;
	unsigned long Size;

	char *RootDevice;
	char *RootPath;
	char *DevicePath;
	unsigned char DirType;

	void ResetError(void);
	void SetError(unsigned char code, unsigned char *str, unsigned char trk, unsigned char sec);

	void DoPath(unsigned char *path);
	unsigned char HuntPath(char *path, char *ext);

public:
	void Init(char *path);
	void Exit(void);

	unsigned char Open(unsigned char *name, unsigned char sec);
	unsigned char Close(unsigned char sec);

	long Read(unsigned char sec, unsigned char *buffer, long size);
	void Write(unsigned char sec, unsigned char *buffer, long size);
	unsigned char BufferPosition(unsigned char sec, unsigned char pos);
	unsigned char Finished(unsigned char sec);
};


#endif
