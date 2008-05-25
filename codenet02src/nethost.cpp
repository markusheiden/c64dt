#include "nethost.h"

void NetSocket::Init(void)
{
	int error;

#ifdef WIN32
	error = WSAStartup( MAKEWORD( 2, 0 ), &WSAData );
#else
	error = 0;
#endif

	if (error == 0)
	{
		Socket = socket(AF_INET, SOCK_DGRAM, IPPROTO_UDP);
//		if (Socket == INVALID_SOCKET)

//		for (i=0;i<sizeof(Server);i++) ((unsigned char *)(&Server))[i] = 0;
//		Server.sin_addr.s_addr = INADDR_ANY;
//		Server.sin_family = AF_INET;
//		Server.sin_port = htons(6667);
//		bind(Socket, (sockaddr *)(&Server), sizeof(Server));
	}
	else
	{
		Socket = INVALID_SOCKET;
	}
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
	Address.sin_addr.s_addr = inet_addr(adr);
	Address.sin_family = AF_INET;
	Address.sin_port = htons(prt);
}

void NetHost::Exit(void)
{
}

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
	SOCKADDR_IN addrrecv;
	int error;
	unsigned long i;

	for (i=0;i<sizeof(addrrecv);i++) ((char *)&addrrecv)[i] = 0;
	addrrecvsize = sizeof(addrrecv);
	FD_ZERO(&sockset);
	FD_SET(Socket, &sockset);
	tv.tv_sec = timeout / 1000;
	tv.tv_usec = (timeout % 1000) * 1000;

	error = select(Socket+1, &sockset, 0, 0, &tv);
	if (error > 0)
	{
		error = recvfrom(Socket, (char *)buf, size, 0, (sockaddr *)(&addrrecv), &addrrecvsize);
		if (error != SOCKET_ERROR) return 1;
	}
	else
	{
		return 0;
	}

	return -1;
}
