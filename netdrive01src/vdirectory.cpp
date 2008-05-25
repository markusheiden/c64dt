#include "vdirectory.h"
#include "vdefs.h"
#include "vd64.h"
#include "stringops.h"
#include <stdlib.h>
#include <io.h>
#include <string.h>

static unsigned char FileType1541[26]=
{
	0x44, 0x53, 0x50, 0x55, 0x52,
	0x45, 0x45, 0x52, 0x53, 0x45,
	0x4C, 0x51, 0x47, 0x52, 0x4C,
	0x08, 0x00, 0x00, 0x3F, 0x7F,
	0xBF, 0xFF, 0x11, 0x12, 0x13,
	0x15
};

static unsigned char BlocksFree[13]="BLOCKS FREE.";

void VDirectory::Init(void)
{
	unsigned long i;

	for (i=0;i<sizeof(VDirectory);i++) ((unsigned char *)(this))[i] = 0;
}

unsigned char VDirectory::Open(char *path, unsigned char *name, unsigned char dirtype)
{
	unsigned char ok, error;
	char npath[_MAX_PATH];
	VD64 diskimage;

	Init();
	MaxSize = 16384;
	Buffer = new unsigned char[MaxSize];

	error = VF_ERROR_OK;
	switch (dirtype)
	{
	case VSUBDIR_DIR:
		CreatePath(npath, path, name);
		ok = ScanDirectory(npath);
		if (!ok) error = VF_ERROR_DIRERROR;
		break;
	case VSUBDIR_D64:
		ok = diskimage.Init(path);
		if (ok)
		{
			ok = ScanDirectory(&diskimage);
			diskimage.Exit();
		}
		if (!ok) error = VF_ERROR_DIRERROR;
		break;
	}
	return error;
}

void VDirectory::Exit(void)
{
	if (Buffer)
	{
		delete[] Buffer;
		Buffer = 0;
	}
}

void VDirectory::CreatePath(char *newpath, char *path, unsigned char *name)
{
	char drive[_MAX_DRIVE];
	char dir[_MAX_DIR];

	// add path to name
	_splitpath(path, drive, dir, 0, 0);
	if (!name[0])
	{
		_makepath(newpath, drive, dir, "*", 0);
	} else {
		_makepath(newpath, drive, dir, (char *)name, 0);
	}
}

unsigned char VDirectory::ScanDirectory(char *path)
{
	unsigned char ok;
	long handle;
	struct _finddata_t fdata;

	// add path to name

	handle = _findfirst(path, &fdata);
	ok = (handle != -1);
	if (ok)
	{
		WriteWord(0x0401);
		while (ok)
		{
			AddEntry(fdata.name, fdata.size, fdata.attrib);
			ok = (_findnext(handle, &fdata) == 0);
		}
		_findclose(handle);
		WriteWord(0x0000);
		Size = Position;
		Position = 0;
		LastPos = 0;
		ok = 1;
	}
	return ok;
}

unsigned char VDirectory::ScanDirectory(VD64 *image)
{
	unsigned char ok, nameend;
	unsigned short size, free;
	unsigned long i, k, spc, max;
	unsigned char *block;
	unsigned char *entry;

	WriteWord(0x0401);
	WriteWord(0x0101);
	WriteWord(0x0000);
	WriteByte(0x12);
	WriteByte(0x22);
	block = image->GetBlock(18, 0);
	for (i=0;i<16;i++) WriteByte(block[i+0x90] & 0x7F);
	WriteByte(0x22);
	WriteByte(0x20);
	for (i=0;i<5;i++) WriteByte(block[i+0xA2] & 0x7F);
	WriteByte(0x00);

	free = 0;
	for (i=4;i<0x90;i+=4) free += block[i];
	free -= block[0x48];

	max = 0;
	block = image->GetBlock(18, 1);
	while (block)
	{
		for (i=0;i<0x100;i+=0x20)
		{
			entry = &block[i];
			if (entry[2])
			{
				size = (entry[0x1F] << 8) | entry[0x1E];
				spc = 1;
				if (size < 100) spc = 2;
				if (size < 10) spc = 3;
				WriteWord(0x0101);
				WriteWord(size);
				for (k=0;k<spc;k++) WriteByte(0x20);
				WriteByte(0x22);
				nameend = 0;
				for (k=0;k<16;k++)
				{
					if ((!nameend) && (entry[5+k] == 0xA0))
					{
						WriteByte(0x22);
						nameend = 1;
					} else {
						if (nameend) WriteByte(entry[5+k] & 0x7F);
						else WriteByte(entry[5+k]);
					}
				}
				if (nameend) WriteByte(0x20);
				else WriteByte(0x22);
				if (entry[2] % 0x80) WriteByte(0x20);
				else WriteByte(0x2A);
				spc = entry[2] & 0x0F;
				WriteByte(FileType1541[spc]);
				WriteByte(FileType1541[spc+5]);
				WriteByte(FileType1541[spc+10]);
				if (entry[2] % 0x40) WriteByte(0x3C);
				else WriteByte(0x20);
				WriteByte(0x00);
			}
		}
		if (block[0] != 18) block = 0;
		else
		{
			block = image->GetBlock(block[0], block[1]);
			max++;
			if (max >= 1024) block = 0;
		}
	}
	WriteWord(0x0101);
	WriteWord(free);
	for (i=0;i<13;i++) WriteByte(BlocksFree[i]);

	WriteByte(0x00);
	WriteByte(0x00);
	Size = Position;
	Position = 0;
	LastPos = 0;
	ok = 1;

	return 1;
}

long VDirectory::Read(unsigned char *buffer, long size)
{
	long i;

	if (Buffer)
	{
		if ((Position+size) > Size) size = Size-Position;
		LastPos = Position;

		if (Position < 2)
		{
			buffer[0] = Buffer[0];
			buffer[1] = Buffer[1];
			Position += 2;
			return 2;
		}

		if ((Buffer[Position]) && (Position < Size))
		{
			for (i=5;i<size;i++)
			{
				if ((!Buffer[Position+i-1]) && (i < size)) size = i;
			}

			for (i=0;i<size;i++) buffer[i] = Buffer[Position+i];

			Position += size;
		} else {
			if (Position < Size)
			{
				Position = Size;
				buffer[0] = 0;
				buffer[1] = 0;
				size = 2;
			} else {
				size = 0;
			}
		}
	}
	return size;
}

void VDirectory::BufferPosition(unsigned char pos)
{
	Position = LastPos + pos;
}

unsigned char VDirectory::Finished(void)
{
	return (Position >= Size);
}

void VDirectory::AddEntry(char *name, unsigned long size, unsigned long attr)
{
	char c, any;
	unsigned long spc, i;
	char fname[_MAX_FNAME];
	char ext[_MAX_EXT];

	if (attr & _A_HIDDEN) return;
	if (attr & _A_SYSTEM) return;

	_splitpath(name, 0, 0, fname, ext);

	// convert extensions to upper case for easier string compare
	i = 0;
	while (ext[i])
	{
		c = ext[i];
		if ((c >= 'a') && (c <= 'z')) ext[i] = (c + 'A' - 'a');
		i++;
	}

	size = (size + 253)/254;
	if (size > 65535) size = 65535;

	spc = 3;
	if (size >= 10) spc = 2;
	if (size >= 100) spc = 1;
	if (size >= 1000) spc = 0;

	WriteWord(0x0101);
	WriteWord((unsigned short)size);
	for (i=0;i<spc;i++) WriteByte(0x20);
	WriteByte(0x22);
	spc = 0;
	if ((strcmp(ext, ".D64") == 0) ||
		(strcmp(ext, ".PRG") == 0) ||
		(strcmp(ext, ".SEQ") == 0) ||
		(strcmp(ext, ".USR") == 0))
	{
		for (i=0;i<26;i++)
		{
			c = fname[i];
			if (!c) break;
			c = asc2pet(c);
			WriteByte(c);
			spc++;
		}
	} else {
		for (i=0;i<26;i++)
		{
			c = name[i];
			if (!c) break;
			c = asc2pet(c);
			WriteByte(c);
			spc++;
		}
	}
	WriteByte(0x22);
	for (i=0;i<(27-spc);i++) WriteByte(0x20);

	if (attr & _A_SUBDIR)
	{
		WriteByte('D');
		WriteByte('I');
		WriteByte('R');
	} else {
		any = 1;
		if (strcmp(ext, ".D64") == 0)
		{
			WriteByte('D');
			WriteByte('6');
			WriteByte('4');
			any = 0;
		}
		if (strcmp(ext, ".PRG") == 0)
		{
			WriteByte('P');
			WriteByte('R');
			WriteByte('G');
			any = 0;
		}
		if (strcmp(ext, ".SEQ") == 0)
		{
			WriteByte('S');
			WriteByte('E');
			WriteByte('Q');
			any = 0;
		}
		if (strcmp(ext, ".USR") == 0)
		{
			WriteByte('U');
			WriteByte('S');
			WriteByte('R');
			any = 0;
		}
		if (any)
		{
			WriteByte('A');
			WriteByte('N');
			WriteByte('Y');
		}
	}
	if (attr & _A_RDONLY)
	{
		WriteByte(0x3C);
	} else {
		WriteByte(0x20);
	}
	WriteByte(0x00);
}

void VDirectory::WriteByte(unsigned char b)
{
	unsigned long i;
	unsigned char *nb;

	if (Position >= MaxSize)
	{
		nb = new unsigned char[MaxSize << 1];
		for (i=0;i<MaxSize;i++) nb[i] = Buffer[i];
		delete[] Buffer;
		Buffer = nb;
		MaxSize = MaxSize << 1;
	}

	Buffer[Position] = b;
	Position++;
}

void VDirectory::WriteWord(unsigned short w)
{
	WriteByte((unsigned char)(w & 0x00FF));
	WriteByte((unsigned char)(w >> 8));
}
