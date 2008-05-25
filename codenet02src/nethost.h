#ifndef ___NETHOST_H___
#define ___NETHOST_H___

#ifdef WIN32
	#define WIN32_LEAN_AND_MEAN 1
	#include <winsock2.h>
#else
	#ifdef __APPLE__
		#include <netinet/in.h>
	#endif

	#include <sys/types.h>
	#include <sys/socket.h>
	#include <netdb.h>
	#include <unistd.h>
	#include <arpa/inet.h>
	#include <sys/select.h>
#endif

#ifndef SOCKET
typedef unsigned int	SOCKET;
#endif

#ifndef SOCKADDR_IN
typedef struct sockaddr_in 	SOCKADDR_IN;
#endif

#ifndef SOCKET_ERROR
#define SOCKET_ERROR            (-1)
#endif

#ifndef INVALID_SOCKET
#define INVALID_SOCKET  (SOCKET)(~0)
#endif

struct NetSocket
{
private:
#ifdef WIN32
	WSADATA WSAData;
#endif
	SOCKET Socket;

public:
	void Init(void);
	void Exit(void);

	SOCKET GetSocket(void);
};

struct NetHost
{
private:
	SOCKET Socket;
	SOCKADDR_IN Address;

public:
	void Init(NetSocket *sock, char *adr, unsigned short prt);
	void Exit(void);

	int Send(unsigned char *buf, unsigned long size);
	int Recv(unsigned char *buf, unsigned long size, unsigned long timeout);
};

#endif
