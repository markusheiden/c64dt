#include "vstream.h"
#include "vfile.h"
#include "vd64file.h"
#include "vdirectory.h"
#include "vdefs.h"
#include "stringops.h"
#include <stdlib.h>

void VStream::Init(void)
{
	unsigned long i;

	for (i=0;i<sizeof(VStream);i++) ((unsigned char *)(this))[i] = 0;
}

void VStream::Exit(void)
{
	if (File)
	{
		File->Exit();
		delete[] File;
		File = 0;
	}
	if (D64File)
	{
		D64File->Exit();
		delete[] D64File;
		D64File = 0;
	}
	if (Directory)
	{
		Directory->Exit();
		delete[] Directory;
		Directory = 0;
	}
}

unsigned char VStream::Open(char *path, unsigned char *name, unsigned char sec, unsigned char dirtype)
{
	unsigned long i, cnt;
	unsigned char type, access;
	char xnameext[_MAX_PATH];

	Init();

	if (!name) return 0;

	type = 'P';
	access = 'R';
	if (sec == 1) access = 'W';

	i = 0;
	cnt = 0;
	while (name[i])
	{
		if (name[i] == ',')
		{
			name[i] = 0;
			if (cnt == 0)
			{
				switch (name[i+1])
				{
				default:
					type = 'A';
					break;
				case 'P':
					type = 'P';
					break;
				case 'S':
					type = 'S';
					break;
				case 'U':
					type = 'U';
					break;
				}
			}
			if (cnt == 1)
			{
				switch (name[i+1])
				{
				case 'R':
				default:
					access = 'R';
					break;
				case 'W':
					access = 'W';
					break;
				case 'A':
					access = 'A';
					break;
				}
			}
			cnt++;
		}
		i++;
	}

	if ((name[0] == '$') && (access == 'R'))
	{
		pet2asc(name);
		Directory = new VDirectory;
		Directory->Init();
		return Directory->Open(path, &name[1], dirtype);
	}

	switch (dirtype)
	{
	case VSUBDIR_DIR:
		switch (type)
		{
		default:
			_makepath(xnameext, 0, 0, (char *)name, 0);
			break;
		case 'P':
			_makepath(xnameext, 0, 0, (char *)name, ".PRG");
			break;
		case 'S':
			_makepath(xnameext, 0, 0, (char *)name, ".SEQ");
			break;
		case 'U':
			_makepath(xnameext, 0, 0, (char *)name, ".USR");
			break;
		}
		pet2asc((unsigned char *)xnameext);
		File = new VFile;
		File->Init();
		return File->Open(path, (unsigned char *)xnameext, access);
	case VSUBDIR_D64:
		D64File = new VD64File;
		D64File->Init();
		return D64File->Open(path, name, access);
	}

	return VF_ERROR_NOTFOUND;
}

long VStream::Read(unsigned char *buffer, long size)
{
	if (Directory) return Directory->Read(buffer, size);
	if (File) return File->Read(buffer, size);
	if (D64File) return D64File->Read(buffer, size);
	return 0;
}

void VStream::Write(unsigned char *buffer, long size)
{
	if (File) File->Write(buffer, size);
}

unsigned char VStream::BufferPosition(unsigned char pos)
{
	unsigned char error;

	error = VF_ERROR_FILENOTOPEN;

	if (Directory)
	{
		Directory->BufferPosition(pos);
		error = VF_ERROR_OK;
	}
	if (File)
	{
		File->BufferPosition(pos);
		error = VF_ERROR_OK;
	}
	return error;
}

unsigned char VStream::Finished(void)
{
	if (Directory) return Directory->Finished();
	if (File) return File->Finished();
	return 1;
}

unsigned char VStream::IsWriteFile(void)
{
	if (File) return File->IsWriteFile();
	return 0;
}

