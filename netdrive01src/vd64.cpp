#include "vd64.h"
#include <stdio.h>

static unsigned long TrackOffsets[40];
static unsigned char MaxTrackSectors[40];

static unsigned char CompareName(unsigned char *name, unsigned char *pattern)
{
	unsigned long i;
	unsigned char nc, pc;

	for (i=0;i<16;i++)
	{
		nc = name[i];
		pc = pattern[i];
		if (pc == '*') return 1;
		if (nc != pc)
		{
			if ((!pc) && (nc == 0xA0)) return 1;
			if (pc != '?') return 0;
		}
	}
	return 1;
}

static void InitTrackOffsets(void)
{
	unsigned long i, offs;

	for (i= 1;i<18;i++) MaxTrackSectors[i-1] = 21;
	for (i=18;i<25;i++) MaxTrackSectors[i-1] = 19;
	for (i=25;i<31;i++) MaxTrackSectors[i-1] = 18;
	for (i=31;i<41;i++) MaxTrackSectors[i-1] = 17;
	offs = 0;
	for (i= 1;i<41;i++)
	{
		TrackOffsets[i-1] = offs;
		offs += MaxTrackSectors[i-1];
	}
}

unsigned char VD64::Init(char *path)
{
	FILE *f;
	fpos_t size;
	long isize;

	InitTrackOffsets();

	Image = 0;
	isize = 0;

	f = fopen(path, "rb");
	if (f)
	{
		fseek(f, 0, SEEK_END);
		fgetpos(f, &size);
		fseek(f, 0, SEEK_SET);

		if (size < 16777216)
		{
			isize = (long)size;
		}

		switch (isize)
		{
		case (683*256):
		case (683*257):
		case (768*256):
		case (768*257):
			Image = new unsigned char[isize];
			fread((void *)Image, 1, isize, f);
			break;
		default:
			break;
		}
		fclose(f);
	}
	return (Image != 0);
}

void VD64::Exit(void)
{
	if (Image)
	{
		delete[] Image;
		Image = 0;
	}
}

unsigned char *VD64::GetBlock(unsigned char track, unsigned char sector)
{
	if (!Image) return 0;
	if ((track <= 0) || (track > 35)) return 0;
	if (MaxTrackSectors[track-1] <= sector) return 0;

	return &Image[(TrackOffsets[track-1] + sector) << 8];
}

unsigned short VD64::FindFile(unsigned char *name)
{
	unsigned long loop, k;
	unsigned char *block;
	unsigned char *entry;

	if (!Image) return 0;

	loop = 0;
	block = GetBlock(18, 1);
	while (block)
	{
		for (k=0;k<0x0100;k+=0x0020)
		{
			entry = &block[k];
			if ((entry[2] & 0x0F) == 0x02)
			{
				if (CompareName(&entry[5], name)) return ((entry[3] << 8) | (unsigned short)entry[4]);
			}
		}
		if ((block[0]) && (loop < 1024)) block = GetBlock(block[0], block[1]);
		else block = 0;
		loop++;
	}
	return 0;
}
