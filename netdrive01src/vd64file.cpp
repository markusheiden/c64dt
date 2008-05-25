#include "vd64file.h"
#include "vd64.h"
#include "vdefs.h"
#include "stringops.h"
#include <stdlib.h>
#include <stdio.h>

void VD64File::Init(void)
{
	unsigned long i;

	for (i=0;i<sizeof(VD64File);i++) ((unsigned char *)(this))[i] = 0;
}

unsigned char VD64File::Open(char *path, unsigned char *name, unsigned char access)
{
	unsigned long i;
	unsigned char ret;
	unsigned short trksec;
	char npath[_MAX_PATH];
	char drive[_MAX_DRIVE];
	char dir[_MAX_DIR];
	char fname[_MAX_FNAME];
	char ext[_MAX_EXT];

	// add path to name

	_splitpath(path, drive, dir, fname, ext);
	_makepath(npath, 0, path, (char *)name, 0);

	printf("%s\n", npath);

	if (DiskImage && WriteAccess) return VF_ERROR_FILEOPEN;
	Exit();
	Init();

	WriteAccess = (access != 'R');
	Path = copystring(path);
	Name = (unsigned char *)copystring((char *)name);
	DiskImage = new VD64;
	if (!DiskImage->Init(path)) return VF_ERROR_NOTFOUND;

	Position = 0;
	LastPos = 0;
	Track = 0;
	Sector = 0;
	SectorPos = 0;
	LastTrack = 0;
	LastSector = 0;
	LastSectorPos = 0;

	ret = VF_ERROR_OK;
	switch (access)
	{
	case 'R':
		trksec = DiskImage->FindFile(name);
		Track = trksec >> 8;
		Sector = trksec & 0x00FF;
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
		break;
	case 'A':
		break;
	};
	return (ret);
}

void VD64File::Exit(void)
{
	if (Name)
	{
		delete[] Name;
		Name = 0;
	}

	if (DiskImage)
	{
		DiskImage->Exit();
		delete[] DiskImage;
		DiskImage = 0;
	}
}

long VD64File::Read(unsigned char *buffer, long size)
{
	long cnt, bsize;
	unsigned char *block;

	cnt = 0;

	if (Track)
	{
		LastPos = Position;
		LastTrack = Track;
		LastSector = Sector;
		LastSectorPos = SectorPos;

		block = DiskImage->GetBlock(Track, Sector);
		if (!block) return 0;
		if (block[0]) bsize = 254;
		else bsize = block[1]-1;

		cnt = 0;
		while (cnt < size)
		{
			if (SectorPos >= bsize)
			{
				Track = block[0];
				Sector = block[1];
				SectorPos = 0;
				block = DiskImage->GetBlock(block[0], block[1]);
				if (!block) break;
				if (!block[0]) bsize = block[1]-1;
			}
			buffer[cnt] = block[2+SectorPos];
			cnt++;
			SectorPos++;
		}
	}
	Position += cnt;
	return cnt;
}

void VD64File::Write(unsigned char *buffer, long size)
{
/*	if (File)
	{
		fwrite((void *)buffer, 1, size, (FILE *)File);
	}*/
}

void VD64File::BufferPosition(unsigned char pos)
{
	Position = LastPos + pos;
}

unsigned char VD64File::Finished(void)
{
//	if (!File) return 1;

//	return (feof((FILE *)File) != 0);
	return 1;
}

unsigned char VD64File::IsWriteFile(void)
{
	return WriteAccess;
}

