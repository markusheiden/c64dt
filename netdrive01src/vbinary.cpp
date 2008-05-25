#include "vbinary.h"
#include "vdefs.h"
#include "stringops.h"
#include <stdlib.h>
#include <io.h>
#include <string.h>

void VBinary::Init(void)
{
	Position = 0;
	LastPos = 0;
	Size = 0;
	MaxSize = 16384;
	Buffer = new unsigned char[MaxSize];
}

void VBinary::Exit(void)
{
	if (Buffer)
	{
		delete[] Buffer;
		Buffer = 0;
	}
}

long VBinary::ReadDirLine(unsigned char *buffer, long size)
{
	long i;

	if (Buffer)
	{
		if ((Position+size) > Size) size = Size-Position;
		for (i=0;i<size;i++) buffer[i] = Buffer[Position+i];
		LastPos = Position;
		Position += size;
	}
	return size;
}

long VBinary::Read(unsigned char *buffer, long size)
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

void VBinary::BufferPosition(unsigned char pos)
{
	Position = LastPos + pos;
}

unsigned char VBinary::Finished(void)
{
	return (Position >= Size);
}

void VBinary::AddEntry(char *name, unsigned long size, unsigned long attr)
{
	char c;
	unsigned long spc, i;
	char fname[_MAX_FNAME];
	char ext[_MAX_EXT];

	if (attr & _A_HIDDEN) return;
	if (attr & _A_SYSTEM) return;

	_splitpath(name, 0, 0, fname, ext);
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

	WriteByte(0x01);
	WriteByte(0x01);
	WriteByte((unsigned char)(size & 0x00FF));
	WriteByte((unsigned char)(size >> 8));
	for (i=0;i<spc;i++) WriteByte(0x20);
	WriteByte(0x22);
	spc = 0;
	for (i=0;i<26;i++)
	{
		c = name[i];
		if (!c) break;
		c = asc2pet(c);
		WriteByte(c);
		spc++;
	}
	WriteByte(0x22);
	for (i=0;i<(27-spc);i++) WriteByte(0x20);

	if (attr & _A_SUBDIR)
	{
		WriteByte('D');
		WriteByte('I');
		WriteByte('R');
	} else {
		if (strcmp(ext, ".D64") == 0)
		{
			WriteByte('D');
			WriteByte('6');
			WriteByte('4');
		} else {
			WriteByte('P');
			WriteByte('R');
			WriteByte('G');
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

void VBinary::WriteByte(unsigned char b)
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

