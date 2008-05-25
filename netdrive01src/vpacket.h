#ifndef ___VPACKET_H___
#define ___VPACKET_H___

#define VP_SERVICE_OPEN		0x01
#define VP_SERVICE_CHKIN	0x02
#define VP_SERVICE_READ		0x03
#define VP_SERVICE_CLOSE	0x04
#define VP_SERVICE_WRITE	0x05
#define VP_SERVICE_DATA		0x20

struct VPacket
{
private:
	unsigned char *Buffer;

public:
	void Init(void);
	void Exit(void);
	void CreateReply(VPacket *packet, unsigned char reply);
	unsigned char ValidSignature(void);
	unsigned short GetSignature(void);
	unsigned char GetServiceNum(void);
	unsigned char GetSequenceNum(void);
	unsigned char GetLogicalFile(void);
	unsigned char GetSecondary(void);
	unsigned char GetDeviceNum(void);
	unsigned char GetSize(void);
	unsigned char *GetPacket(void);
	unsigned char *GetData(void);
	unsigned char GetFirstDataByte(void);

	void SetSize(unsigned char size);
	void SetServiceNum(unsigned char service);
};

#endif
