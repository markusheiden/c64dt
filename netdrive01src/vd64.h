#ifndef ___VD64_H___
#define ___VD64_H___

struct VD64
{
private:
	unsigned char *Image;

public:
	unsigned char Init(char *path);
	void Exit(void);
	unsigned char *GetBlock(unsigned char track, unsigned char sector);
	unsigned short FindFile(unsigned char *name);
};

#endif
