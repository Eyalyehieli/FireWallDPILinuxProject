#ifndef ACTIVEPROTOCOL_H_INCLUDED
#define ACTIVEPROTOCOL_H_INCLUDED

#include <asm/types.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

typedef struct
{
    __u32 dest_ip;
    __u16 dest_port;
    int protocolId;
}activeProtocol;


void setIp(activeProtocol *ap,char *ip);
void setPort(activeProtocol *ap,int port);
void setProtocolId(activeProtocol*ap,int protocolId);

__u32 getIp(activeProtocol *ap);
__u16 getPort(activeProtocol *ap);
 int  getProtocolId(activeProtocol *ap);

#endif // ACTIVEPROTOCOL_H_INCLUDED
