#include "activeProtocol.h"


void setIp(activeProtocol *p,char *ip)
{
    p->dest_ip=inet_addr(ip);
}

void setPort(activeProtocol *p,int port)
{
    p->dest_port=htons(port);
}

void setProtocolId(activeProtocol*ap,int protocolId)
{
    ap->protocolId=protocolId;
}

__u32 getIp(activeProtocol *p)
{
   return p->dest_ip;
}
__u16 getPort(activeProtocol *p)
{
    return p->dest_port;
}

int getProtocolId(activeProtocol *ap)
{
    return ap->protocolId;
}
