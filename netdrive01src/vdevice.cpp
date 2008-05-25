#include "vdevice.h"
#include "vstream.h"
#include "vdefs.h"
#include "stringops.h"
#include <stdlib.h>
#include <stdio.h>
#include <io.h>

void VDevice::Init(char *path)
{
	unsigned long i;

	DirType = VSUBDIR_DIR;
	RootDevice = new char[_MAX_DRIVE];
	RootPath = new char[_MAX_DIR];
	DevicePath = new char[_MAX_DIR];
	DevicePath[0] = 0;
	_splitpath(path, RootDevice, RootPath, 0, 0);

	Error = 0;
	Streams = new VStream[16];
	for (i=0;i<16;i++) Streams[i].Init();
	ResetError();
}

void VDevice::Exit(void)
{
	unsigned long i;

	if (Streams)
	{
		for (i=0;i<16;i++) Streams[i].Exit();
		delete[] Streams;
		Streams = 0;
	}
	if (Error)
	{
		delete[] Error;
		Error = 0;
	}
	if (DevicePath)
	{
		delete[] DevicePath;
		DevicePath = 0;
	}
}

void VDevice::ResetError(void)
{
	SetError(0, (unsigned char *)" OK", 0, 0);
}

void VDevice::SetError(unsigned char code, unsigned char *str, unsigned char trk, unsigned char sec)
{
	unsigned long i, sl;
	unsigned char c;

	Position = 0;
	LastPos = 0;
	Size = 0;

	if (Error)
	{
		delete[] Error;
		Position = 0;
		Size = 0;
	}

	sl = strlength((char *)str);
	Size = sl+8;
	Error = new unsigned char[sl+9];

	c = (code % 10) | ('0');
	code /= 10;
	Error[0] = (code % 10) | ('0');
	Error[1] = c;
	Error[2] = ',';

	for (i=0;i<(sl-1);i++) Error[i+3] = str[i];
	i = (sl+3-1);

	Error[i  ] = ',';
	c = (trk % 10) | ('0');
	trk /= 10;
	Error[i+1] = (trk % 10) | ('0');
	Error[i+2] = c;

	Error[i+3] = ',';
	c = (sec % 10) | ('0');
	sec /= 10;
	Error[i+4] = (sec % 10) | ('0');
	Error[i+5] = c;

	Error[i+6] = 0;
}


unsigned char VDevice::Open(unsigned char *name, unsigned char sec)
{
	unsigned char error;
	char npath[_MAX_PATH];

	error = VF_ERROR_OK;
	sec &= 0x0F;
	if (sec < 0x0F)
	{
		_makepath(npath, RootDevice, RootPath, DevicePath, 0);
		if (DirType == VSUBDIR_DIR) _makepath(npath, 0, npath, 0, 0);
		error = Streams[sec].Open(npath, name, sec, DirType);

		switch (error)
		{
		case VF_ERROR_SYNTAX31:
			SetError(31, (unsigned char *)" SYNTAX ERROR", 0, 0);
			error = 0x00;
			break;
		case VF_ERROR_SYNTAX33:
			SetError(33, (unsigned char *)" SYNTAX ERROR", 0, 0);
			error = 0x00;
			break;
		case VF_ERROR_FILEOPEN:
			SetError(60, (unsigned char *)" WRITE FILE OPEN", 0, 0);
			error = 0x02;	// ?FILE OPEN  ERROR
			break;
		case VF_ERROR_FILENOTOPEN:
			SetError(61, (unsigned char *)" FILE NOT OPEN", 0, 0);
			error = 0x03;	// ?FILE NOT OPEN  ERROR
			break;
		case VF_ERROR_NOTFOUND:
			SetError(62, (unsigned char *)" FILE NOT FOUND", 0, 0);
			error = 0x04;	// ?FILE NOT FOUND  ERROR
			break;
		case VF_ERROR_EXISTS:
			SetError(63, (unsigned char *)" FILE EXISTS", 0, 0);
			error = 0x00;
			break;
		default:
			ResetError();
			error = 0x00;
			break;
		}
	} else {
		pet2asc(name);
		printf("CMD: %s\n", name);
		if ((name[0] == 'c') && (name[1] == 'd') && (name[2] == ':'))
		{
			DoPath(&name[3]);
		} else {
			if (name[0])
			{
				SetError(31, (unsigned char *)" SYNTAX ERROR", 0, 0);
				error = 0x00;
			}
		}
	}

	return error;
}

unsigned char VDevice::Close(unsigned char sec)
{
	sec &= 0x0F;
	if (sec < 0x0F)
	{
		Streams[sec].Exit();
	}
	return 0;
}

long VDevice::Read(unsigned char sec, unsigned char *buffer, long size)
{
	long i;

	sec &= 0x0F;
	if (sec < 0x0F)
	{
		size = Streams[sec].Read(buffer, size);
	} else {
		if ((Error) && (Position < Size))
		{
			if ((Position+size) > Size) size = Size-Position;

			for (i=0;i<size;i++) buffer[i] = Error[i];
			Position += size;
		} else {
			size = 0;
			ResetError();
		}
	}
	return size;
}

void VDevice::Write(unsigned char sec, unsigned char *buffer, long size)
{
	sec &= 0x0F;
	if (sec < 0x0F)
	{
		Streams[sec].Write(buffer, size);
	}
}

unsigned char VDevice::BufferPosition(unsigned char sec, unsigned char pos)
{
	sec &= 0x0F;
	if (sec < 0x0F)
	{
		return Streams[sec].BufferPosition(pos);
	} else {
		Position = LastPos + pos;
		return 0;
	}
}

unsigned char VDevice::Finished(unsigned char sec)
{
	sec &= 0x0F;
	if (sec < 0x0F)
	{
		return Streams[sec].Finished();
	} else {
		return (Position >= LastPos);
	}
}

unsigned char VDevice::HuntPath(char *path, char *ext)
{
	long handle, ok;
	char npath[_MAX_PATH];
	struct _finddata_t fdata;

	// find matching subdirectory

	_makepath(npath, RootDevice, RootPath, path, ext);
	handle = _findfirst(npath, &fdata);
	ok = (handle != -1);
	if (ok)
	{
		while (ok)
		{
			if (fdata.attrib & _A_SUBDIR)
			{
				if (!ext)
				{
					_makepath(DevicePath, 0, DevicePath, fdata.name, 0);
					DirType = VSUBDIR_DIR;
					break;
				}
			} else {
				if (ext)
				{
					_makepath(DevicePath, 0, DevicePath, fdata.name, 0);
					DirType = VSUBDIR_D64;
					break;
				}
			}
			ok = (_findnext(handle, &fdata) == 0);
		}
		_findclose(handle);
	}
	return (ok != 0);
}

void VDevice::DoPath(unsigned char *path)
{
	unsigned long k;
	long sl;
	unsigned char ok;
	char npath[_MAX_PATH];
	char tpath[_MAX_PATH];

	npath[0] = 0;
	tpath[0] = 0;
	if ((path[0] != '.') && (path[0] != '/'))
	{
		_makepath(tpath, 0, DevicePath, (char *)path, 0);
		for (k=0;k<strlength(tpath);k++)
		{
			if (tpath[k] == '\\') tpath[k] = '/';
		}

		ok = HuntPath(tpath, 0);
		if (!ok)
		{
			ok = HuntPath(tpath, ".d64");
			if (!ok)
			{
				ok = HuntPath(tpath, ".D64");
			}
		}
		printf("path: %s\n", DevicePath);

	} else {
		sl = strlength(DevicePath);
		if (sl>1) DevicePath[sl-2] = 0;
		_splitpath(DevicePath, 0, DevicePath, 0, 0);

		printf("path: %s\n", DevicePath);
		_makepath(npath, RootDevice, RootPath, DevicePath, 0);
		DirType = VSUBDIR_DIR;
	}
}
