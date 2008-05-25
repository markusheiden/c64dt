#include "vpacket.h"

void VPacket::Init(void)
{
	unsigned long i;

	Buffer = new unsigned char[0x90];
	for (i=0;i<0x90;i++) Buffer[i] = 0;
}

void VPacket::Exit(void)
{
	if (Buffer)
	{
		delete[] Buffer;
		Buffer = 0;
	}
}

void VPacket::CreateReply(VPacket *packet, unsigned char reply)
{
	unsigned long i;

	for (i=0;i<7;i++) Buffer[i] = packet->Buffer[i];
	SetSize(1);
	Buffer[8] = reply;
	Buffer[9] = 0;
}

unsigned char VPacket::ValidSignature(void)
{
	return (GetSignature() == 0xADF8);
}

unsigned short VPacket::GetSignature(void)
{
	return ((((unsigned short)Buffer[0]) << 8) | ((unsigned short)Buffer[1]));
}

unsigned char VPacket::GetServiceNum(void)
{
	return Buffer[2];
}

unsigned char VPacket::GetSequenceNum(void)
{
	return Buffer[3];
}

unsigned char VPacket::GetLogicalFile(void)
{
	return Buffer[4];
}

unsigned char VPacket::GetSecondary(void)
{
	return (Buffer[5] & 0x0F);
}

unsigned char VPacket::GetDeviceNum(void)
{
	return Buffer[6];
}

unsigned char VPacket::GetSize(void)
{
	return Buffer[7];
}

unsigned char *VPacket::GetPacket(void)
{
	return Buffer;
}

unsigned char *VPacket::GetData(void)
{
	return &Buffer[8];
}

unsigned char VPacket::GetFirstDataByte(void)
{
	return Buffer[8];
}

void VPacket::SetSize(unsigned char size)
{
	Buffer[7] = size;
}

void VPacket::SetServiceNum(unsigned char service)
{
	Buffer[2] = service;
}

