#include "nethost.h"
#include "net64.h"
#include "stringops.h"
#include "argstream.h"
#include <stdio.h>

#define ACMD_NULL		0
#define ACMD_WRITE		1
#define ACMD_WRTADR		2
#define ACMD_WRTBIN		3
#define ACMD_FILL		4
#define ACMD_JUMP		5
#define ACMD_RUN		6
#define ACMD_EXECUTE	7

#define ATOKEN_NULL		0
#define ATOKEN_IP		1
#define ATOKEN_PORT		2
#define ATOKEN_WRITE	3
#define ATOKEN_WRTADR	4
#define ATOKEN_WRTBIN	5
#define ATOKEN_EXECUTE	6
#define ATOKEN_FILL		7
#define ATOKEN_JUMP		8
#define ATOKEN_RUN		9

static ArgToken Tokens[10]=
{
	{ "-n", ATOKEN_IP      },
	{ "-p", ATOKEN_PORT    },
	{ "-w", ATOKEN_WRITE   },
	{ "-wa",ATOKEN_WRTADR  },
	{ "-wb",ATOKEN_WRTBIN  },
	{ "-x", ATOKEN_EXECUTE },
	{ "-f", ATOKEN_FILL    },
	{ "-e", ATOKEN_JUMP    },
	{ "-r", ATOKEN_RUN     },
	{ 0,    ATOKEN_NULL    }
};

int main( int argc, char *argv[ ] )
{
	FILE *file;
	NetSocket socket;
	NetHost host;
	Net64 c64;
	unsigned long size, count;
	unsigned char buffer[160];
	char *ipaddress;
	int retval;
	char *filename;
	unsigned char cmd, fillbyte;
	unsigned short address, endaddr, port;
	ArgStream stream;

	stream.Init(argv, argc, Tokens);

	ipaddress = "192.168.1.64";
	filename = 0;
	address = 0;
	endaddr = 0;
	cmd = ACMD_NULL;
	fillbyte = 0;
	port = 6462;

	stream.GetNext();
	while (stream.HasArguments())
	{
		switch (stream.GetNextToken())
		{
		case ATOKEN_IP:
			if (stream.HasArguments())
				ipaddress = stream.GetNext();
			break;
		case ATOKEN_PORT:
			if (stream.HasArguments())
				port = (unsigned short)ConvertNumber(stream.GetNext());
			break;
		case ATOKEN_WRITE:
			if (stream.HasArguments())
			{
				filename = stream.GetNext();
				cmd = ACMD_WRITE;
			}
			break;
		case ATOKEN_WRTADR:
			if (stream.HasArguments())
			{
				filename = stream.GetNext();
				if (stream.HasArguments())
				{
					address = (unsigned short)ConvertNumber(stream.GetNext());
					cmd = ACMD_WRTADR;
				}
			}
			break;
		case ATOKEN_WRTBIN:
			if (stream.HasArguments())
			{
				filename = stream.GetNext();
				if (stream.HasArguments())
				{
					address = (unsigned short)ConvertNumber(stream.GetNext());
					cmd = ACMD_WRTBIN;
				}
			}
			break;
		case ATOKEN_EXECUTE:
			if (stream.HasArguments())
			{
				filename = stream.GetNext();
				cmd = ACMD_EXECUTE;
			}
			break;
		case ATOKEN_FILL:
			if (stream.HasArguments())
			{
				address = (unsigned short)ConvertNumber(stream.GetNext());
				if (stream.HasArguments())
				{
					endaddr = (unsigned short)ConvertNumber(stream.GetNext());
					if (stream.HasArguments())
					{
						fillbyte = (unsigned char)ConvertNumber(stream.GetNext());
						cmd = ACMD_FILL;
					}
				}
			}
			break;
		case ATOKEN_JUMP:
			if (stream.HasArguments())
			{
				address = (unsigned short)ConvertNumber(stream.GetNext());
				cmd = ACMD_JUMP;
			}
			break;
		case ATOKEN_RUN:
			cmd = ACMD_RUN;
			break;

		}
	}

	if (cmd == ACMD_NULL)
	{
		printf("\nCodeNet V0.2 alpha by John \"Graham\" Selck on 17.5.2005.\n");
		printf("\nOptionen:\n");
		printf("    -n IP-address      Sets the IP-address of the C64 server.\n");
		printf("    -p Port            Sets the port number of the protocol.\n");
		printf("    -w Filename        Sends a PRG file to the C64.\n");
		printf("    -wa Filename Addr  Sends a PRG file to the C64 to a specified address.\n");
		printf("    -wb Filename Addr  Sends a binary file to the C64 to a specified address.\n");
		printf("    -x Filename        Sends a PRG file to the C64 and executes it\n");
		printf("    -f Start End Fill  Fills a block of C64 memory.\n");
		printf("    -e Adress          Jumps to an address in memory.\n");
		printf("    -r                 Executes a program via \"RUN\".\n");
		printf("\nExample:\n");
		printf("> codenet -f $1000 $2080 $C0 -n 192.168.1.99\n");

		return 0;
	}

	socket.Init();
	host.Init(&socket, ipaddress, port);
	c64.Init(&host);

	retval = 0;
	switch (cmd)
	{
	case ACMD_WRITE:
	case ACMD_WRTADR:
	case ACMD_WRTBIN:
	case ACMD_EXECUTE:
		file = fopen(filename, "rb");
/*		LARGE_INTEGER freq, time1, time2;
		double dfreq, dtime1, dtime2;
		QueryPerformanceFrequency(&freq);
		dfreq = freq.QuadPart;
		QueryPerformanceCounter(&time1);*/
		if (file)
		{
			if (cmd != ACMD_WRTBIN)
			{
				size = fread((void *)buffer, 1, 2, file);

				if (cmd != ACMD_WRTADR)
				{
					address  = buffer[1];
					address <<= 8;
					address |= buffer[0];
				}
			}

			endaddr = address;

			count = 0;
			while ((!feof(file)) && (retval >= 0))
			{
				size = fread((void *)buffer, 1, 128, file);
				if (size > 0)
				{
					retval = c64.SendData(endaddr, (unsigned short)size, buffer);
					if (count == 0)
					{
						if (retval >= 0)
						{
							printf("Transferring file %s to address $%04X.\n", filename, address);
							count++;
						} else {
							printf("C64 did not acknowledge file transfer request!\n");
						}
					}
				}
				endaddr += (unsigned short)size;
			}
			if ((count) && (retval < 0))
			{
				printf("Transfer interruptet!\n");
			}
			if ((retval >= 0) && (cmd == ACMD_EXECUTE))
			{
				if (address > 0x0801)
				{
					retval = c64.ExecJump(address);
				} else {
					retval = c64.ExecRun();
				}
			}
		} else {
			printf("File not found.\n");
		}
/*		QueryPerformanceCounter(&time2);
		dtime1 = time1.QuadPart;
		dtime2 = time2.QuadPart;
		printf("Transfer speed: %f KB/s\n", (float)(((dtime2-dtime1)/dfreq)/(double)(endaddr-address)));*/

		break;
	case ACMD_FILL:
		retval = c64.SendFill(address, endaddr-address, fillbyte);
		if (retval >= 0)
		{
			printf("Filling memory from $%04X to $%04X with $%02X.\n", address, endaddr, fillbyte);
		} else {
			printf("C64 did not acknowledge memory fill request!\n");
		}
		break;
	case ACMD_JUMP:
		retval = c64.ExecJump(address);
		if (retval >= 0)
		{
			printf("Jumping to address $%04X.\n", address);
		} else {
			printf("C64 did not acknowledge jump request!\n");
		}
		break;
	case ACMD_RUN:
		retval = c64.ExecRun();
		if (retval >= 0)
		{
			printf("Executing Basic program.\n");
		} else {
			printf("C64 did not acknowledge Basic execute request!\n");
		}
		break;
	};

	c64.Exit();
	host.Exit();
	socket.Exit();

	return (retval < 0);
}
