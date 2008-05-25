#include "net64.h"
#include "nethost.h"

static unsigned char packet[256];

void Net64::Init(NetHost *hst)
{
	Host = hst;
	Timeout = 2000;
	Sequence = 0x41;
}

void Net64::Exit(void)
{
}

char Net64::Send(unsigned char service, unsigned short size)
{
	int repeat, val, wrong;
	unsigned char reply[8];
	char retservice;

	Sequence++;

	packet[0] = 0xCA;
	packet[1] = 0x1F;
	packet[2] = Sequence;
	packet[3] = service;

	retservice = -1;
	if (Host->Send(packet, (size+5) & 0xFFFE))
	{
		repeat = 1;
		while (repeat)
		{
			val = (Host->Recv(reply, 8, Timeout));

			if (val == 0)
			{
				repeat = 0;	// Timeout, kein Paket empfangen.
			}

			if (val == 1)
			{
				// Paket empfangen, überprüfen...

				wrong = 0;
				if (reply[0] != 0xCA) wrong = 1;
				if (reply[1] != 0x1F) wrong = 1;
				if (reply[2] != Sequence) wrong = 1;
				if (reply[3] >= 2) wrong = 1;

				if (!wrong)
				{
					retservice = reply[3];
					repeat = 0;	// Paket empfangen korrekt.
				}
			}
		}
	}
	return retservice;
}

int Net64::SendData(unsigned short address, unsigned short size, unsigned char *buffer)
{
	unsigned long i;

	if (size > 150) return -1;
	packet[4] = (unsigned char)(address >> 8);
	packet[5] = (unsigned char)(address & 0xFF);
	packet[6] = (unsigned char)(size >> 8);
	packet[7] = (unsigned char)(size & 0xFF);
	for (i=0;i<size;i++) packet[i+8] = buffer[i];

	return (Send(4, (size+4)));
}

int Net64::SendFill(unsigned short address, unsigned short size, unsigned char fill)
{
	packet[4] = (unsigned char)(address >> 8);
	packet[5] = (unsigned char)(address & 0xFF);
	packet[6] = (unsigned char)(size >> 8);
	packet[7] = (unsigned char)(size & 0xFF);
	packet[8] = fill;

	return (Send(5, 5));
}

int Net64::ExecJump(unsigned short address)
{
	packet[4] = (unsigned char)(address >> 8);
	packet[5] = (unsigned char)(address & 0xFF);

	return (Send(6, 2));
}

int Net64::ExecRun(void)
{
	return (Send(7, 0));
}

