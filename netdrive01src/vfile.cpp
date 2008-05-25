#include "vfile.h"
#include "vdefs.h"
#include "stringops.h"
#include <stdlib.h>
#include <stdio.h>

void VFile::Init(void)
{
	unsigned long i;

	for (i=0;i<sizeof(VFile);i++) ((unsigned char *)(this))[i] = 0;
}

unsigned char VFile::Open(char *path, unsigned char *name, unsigned char access)
{
	unsigned long i;
	unsigned char ret;
	char npath[_MAX_PATH];
	char drive[_MAX_DRIVE];
	char dir[_MAX_DIR];
	char fname[_MAX_FNAME];
	char ext[_MAX_EXT];

	// add path to name

	_splitpath(path, drive, dir, fname, ext);
	_makepath(npath, drive, dir, (char *)name, 0);

	printf("%s\n", npath);

	if (File && WriteAccess) return VF_ERROR_FILEOPEN;
	Exit();
	Init();

	WriteAccess = (access != 'R');
	Path = copystring(path);
	Name = (unsigned char *)copystring((char *)name);

	ret = VF_ERROR_OK;
	switch (access)
	{
	case 'R':
		File = fopen(npath, "rb");
		if (!File)
		{
			ret = VF_ERROR_NOTFOUND;
		}
		break;
	case 'W':
		i = 0;
		while (name[i])
		{
			if ((name[i] == '*') || (name[i] =='?'))
			{
				return VF_ERROR_SYNTAX33;
			}
			i++;
		}

		File = fopen(npath, "rb");
		if (File)
		{
			fclose((FILE *)File);
			File = 0;
			ret = VF_ERROR_EXISTS;
		} else {
			File = fopen(npath, "wb");
		}
		break;
	case 'A':
		File = fopen(npath, "ab");
		if (!File)
		{
			ret = VF_ERROR_NOTFOUND;
		}
		break;
	};
	return (ret);
}

void VFile::Exit(void)
{
	if (Name)
	{
		delete[] Name;
		Name = 0;
	}

	if (File)
	{
		fclose((FILE *)File);
		File = 0;
	}
}

long VFile::Read(unsigned char *buffer, long size)
{
	long cnt;
	fpos_t fp;

	cnt = 0;
	if (File)
	{
		fp = Position;
		fsetpos((FILE *)File, &fp);
		cnt = fread((void *)buffer, 1, size, (FILE *)File);
		fgetpos((FILE *)File, &fp);
		LastPos = Position;
		Position = (unsigned long)fp;
//		printf("%08x %08x\n", LastPos, Position);
	}
	return cnt;
}

void VFile::Write(unsigned char *buffer, long size)
{
	if (File)
	{
		fwrite((void *)buffer, 1, size, (FILE *)File);
	}
}

void VFile::BufferPosition(unsigned char pos)
{
	Position = LastPos + pos;
}

unsigned char VFile::Finished(void)
{
	if (!File) return 1;

	return (feof((FILE *)File) != 0);
}

unsigned char VFile::IsWriteFile(void)
{
	return WriteAccess;
}

