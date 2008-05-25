#include "nethost.h"
#include <stdio.h>

unsigned char NetSocket::Init(char *ip, unsigned short port)
{
	int error;
	unsigned long i;

#ifdef WIN32
	error = WSAStartup( MAKEWORD( 2, 0 ), &WSAData );
#else
	error = 0;
#endif

	for (i=0;i<sizeof(Server);i++) ((unsigned char *)(&Server))[i] = 0;
	if (ip) Server.sin_addr.s_addr = inet_addr(ip);
	else Server.sin_addr.s_addr = INADDR_ANY;
	Server.sin_family = AF_INET;
	Server.sin_port = htons(port);

	if (error == 0)
	{
		Socket = socket(AF_INET, SOCK_DGRAM, IPPROTO_UDP);
		error = (Socket == INVALID_SOCKET);

		if (!error)
		{
			error = bind(Socket, (sockaddr *)(&Server), sizeof(Server));
//			if (!error) printf("bind success!\n");
		}
	}

	return (error == 0);
}

void NetSocket::Exit(void)
{
	if (Socket)
	{
#ifdef WIN32
		closesocket((SOCKET)Socket);
#else
		close(Socket);
#endif
		Socket = 0;
	}
#ifdef WIN32
	WSACleanup();
#endif
}

SOCKET NetSocket::GetSocket(void)
{
	return Socket;
}

void NetHost::Init(NetSocket *sock, char *adr, unsigned short prt)
{
	unsigned long i;

	for (i=0;i<sizeof(Address);i++) ((unsigned char *)(&Address))[i] = 0;

	Socket = sock->GetSocket();
	if (adr) Address.sin_addr.s_addr = inet_addr(adr);
	else Address.sin_addr.s_addr = INADDR_ANY;
	Address.sin_family = AF_INET;
	Address.sin_port = htons(prt);
	Connected = 0;
}

void NetHost::Exit(void)
{
}

//unsigned char dropcnt=0x11;

int NetHost::Send(unsigned char *buf, unsigned long size)
{
	int error;

	error = sendto(Socket, (char *)buf, size, 0, (sockaddr *)(&Address), sizeof(Address));
	
	return (error != SOCKET_ERROR);
}

int NetHost::Recv(unsigned char *buf, unsigned long size, unsigned long timeout)
{
#ifdef WIN32
	int addrrecvsize;
#else
	socklen_t addrrecvsize;
#endif

	fd_set sockset;
	timeval tv;
	int error;
	unsigned long i;

	for (i=0;i<sizeof(Incoming);i++) ((char *)&Incoming)[i] = 0;
	addrrecvsize = sizeof(Incoming);
	FD_ZERO(&sockset);
	FD_SET(Socket, &sockset);
	tv.tv_sec = timeout / 1000;
	tv.tv_usec = (timeout % 1000) * 1000;

//	dropcnt++;
//	if ((dropcnt % 3) == 0) return 0;


	error = select(Socket+1, &sockset, 0, 0, &tv);
	if (error > 0)
	{
		error = recvfrom(Socket, (char *)buf, size, 0, (sockaddr *)(&Incoming), &addrrecvsize);
		if (error != SOCKET_ERROR)
		{
			if ((!Connected) && (buf[0] == 0xAD) && (buf[1] == 0xF8))
			{
				Address.sin_addr = Incoming.sin_addr;
				Address.sin_port = Incoming.sin_port;
				Address.sin_family = Incoming.sin_family;
				Connected = 1;
			}
			return 1;
		}
	}
	else
	{
		return 0;
	}

	return -1;
}
