#include "nethost.h"
#include "net64.h"
#include "stringops.h"
#include "argstream.h"
#include "vdevice.h"
#include "vpacket.h"
#include <stdio.h>
#include <conio.h>

#define ATOKEN_NULL		0
#define ATOKEN_ROOT		1

static ArgToken Tokens[10]=
{
	{ "-r", ATOKEN_ROOT    },
	{ 0,    ATOKEN_NULL    }
};

int main( int argc, char *argv[ ] )
{ 
	NetSocket socket;
	NetHost host;
	unsigned short port;
	ArgStream stream;
	char *rootdir;
	unsigned long kk;
	unsigned char lastseq, dirbuf, error;
	VDevice device;
	VPacket recvpacket, replypacket;

	printf("TFR network drive server V0.1\n");

	recvpacket.Init();
	replypacket.Init();

	stream.Init(argv, argc, Tokens);

	port = 6463;
	rootdir = "";

	stream.GetNext();
	while (stream.HasArguments())
	{
		switch (stream.GetNextToken())
		{
		case ATOKEN_ROOT:
			if (stream.HasArguments())
				rootdir = stream.GetNext();
			break;
		}
	}

	printf("Root directory: %s\n", rootdir);

	socket.Init(0, port);
	host.Init(&socket, 0, 0);

	device.Init(rootdir);
	kk = 0;
	dirbuf = 0;
	while (! _kbhit())
	{
		if (host.Recv(recvpacket.GetPacket(), 0x90, 500))
		{
			if (recvpacket.ValidSignature())
			{
			switch (recvpacket.GetServiceNum())
			{
			case VP_SERVICE_OPEN:
				if (recvpacket.GetSequenceNum() != lastseq)
				{
					error = device.Open(recvpacket.GetData(), recvpacket.GetSecondary());
					replypacket.CreateReply(&recvpacket, error);
					printf("%i OPEN %i,%i,%i,%s\n", error, recvpacket.GetLogicalFile(), recvpacket.GetDeviceNum(), recvpacket.GetSecondary(), recvpacket.GetData());
				} else {
					printf("OPEN repeat %i,%i,%i,%s\n", recvpacket.GetLogicalFile(), recvpacket.GetDeviceNum(), recvpacket.GetSecondary(), recvpacket.GetData());
				}
				host.Send(replypacket.GetPacket(), 10);
				break;
			case VP_SERVICE_CHKIN:
				if (recvpacket.GetSequenceNum() != lastseq)
				{
					error = device.BufferPosition(recvpacket.GetSecondary(), recvpacket.GetFirstDataByte());
					replypacket.CreateReply(&recvpacket, error);
					printf("%i CHKIN %i,%i\n", error, recvpacket.GetDeviceNum(), recvpacket.GetSecondary());
				} else {
					printf("CHKIN repeat %i,%i\n", recvpacket.GetDeviceNum(), recvpacket.GetSecondary());
				}
				host.Send(replypacket.GetPacket(), 10);
				break;
			case VP_SERVICE_READ:
				if (recvpacket.GetSequenceNum() != lastseq)
				{
					replypacket.CreateReply(&recvpacket, 0);
					replypacket.SetServiceNum(VP_SERVICE_DATA);
					error = (unsigned char)device.Read(recvpacket.GetSecondary(), replypacket.GetData(), recvpacket.GetFirstDataByte());
					replypacket.SetSize(error);
					printf(".");
				} else {
					printf("*");
				}
				host.Send(replypacket.GetPacket(), 0x48);
				break;
			case VP_SERVICE_CLOSE:
				if (recvpacket.GetSequenceNum() != lastseq)
				{
					device.Close(recvpacket.GetSecondary());
					replypacket.CreateReply(&recvpacket, 0);
					printf("CLOSE %i,%i\n", recvpacket.GetDeviceNum(), recvpacket.GetSecondary());
				} else {
					printf("CLOSE repeat %i,%i\n", recvpacket.GetDeviceNum(), recvpacket.GetSecondary());
				}
				host.Send(replypacket.GetPacket(), 10);
				break;
			case VP_SERVICE_WRITE:
				if (recvpacket.GetSequenceNum() != lastseq)
				{
					device.Write(recvpacket.GetSecondary(), recvpacket.GetData(), recvpacket.GetSize());
					replypacket.CreateReply(&recvpacket, 0);
					printf("+");
				} else {
					printf("*");
				}
				host.Send(replypacket.GetPacket(), 10);
				break;
			default:
				printf("UNKNOWN %i,%i,%i,%s\n", recvpacket.GetLogicalFile(), recvpacket.GetDeviceNum(), recvpacket.GetSecondary(), recvpacket.GetData());
				break;
			}
			lastseq = recvpacket.GetSequenceNum();
			}
		}
	}
	device.Exit();
	host.Exit();
	socket.Exit();
	stream.Exit();
	replypacket.Exit();
	recvpacket.Exit();

	return 0;
}
