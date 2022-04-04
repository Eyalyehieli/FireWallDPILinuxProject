#include "activeProtocol.h"

/*!@*****************************************************************************
 *!
 *! FUNCTION:			setIp
 *!
 *! GENERAL DESCRIPTION: This function set the ip field
 *!
 *! Input:				Proper ip
 *!
 *! Output:				none.
 *!
 *! ALGORITHM:			none.
 *!
 *!
 *! ASSUMPTIONS:		The var is validate properly
 *! REMARKS:			none.
 *!
 *!*****************************************************************************
 *!@*/
void setIp(activeProtocol *p,char *ip)
{
    p->dest_ip=inet_addr(ip);
}

/*!@*****************************************************************************
 *!
 *! FUNCTION:			setPort
 *!
 *! GENERAL DESCRIPTION: This function set the port field
 *!
 *! Input:				Proper port
 *!
 *! Output:				none.
 *!
 *! ALGORITHM:			none.
 *!
 *!
 *! ASSUMPTIONS:		The var is validate properly
 *! REMARKS:			none.
 *!
 *!*****************************************************************************
 *!@*/
void setPort(activeProtocol *p,int port)
{
    p->dest_port=htons(port);
}

/*!@*****************************************************************************
 *!
 *! FUNCTION:			setProtocolId
 *!
 *! GENERAL DESCRIPTION: This function set the ProtocolId field
 *!
 *! Input:				Proper setProtocolId
 *!
 *! Output:				none.
 *!
 *! ALGORITHM:			none.
 *!
 *!
 *! ASSUMPTIONS:		The var is validate properly
 *! REMARKS:			none.
 *!
 *!*****************************************************************************
 *!@*/

void setProtocolId(activeProtocol*ap,int protocolId)
{
    ap->protocolId=protocolId;
}

/*!@*****************************************************************************
 *!
 *! FUNCTION:			getIp
 *!
 *! GENERAL DESCRIPTION: This function return the ip field
 *!
 *! Input:				none.
 *!
 *! Output:				ip field
 *!
 *! ALGORITHM:			none.
 *!
 *!
 *! ASSUMPTIONS:		none.
 *! REMARKS:			none.
 *!
 *!*****************************************************************************
 *!@*/

__u32 getIp(activeProtocol *p)
{
   return p->dest_ip;
}

/*!@*****************************************************************************
 *!
 *! FUNCTION:			getPort
 *!
 *! GENERAL DESCRIPTION: This function return the port field
 *!
 *! Input:				none.
 *!
 *! Output:				port field
 *!
 *! ALGORITHM:			none.
 *!
 *!
 *! ASSUMPTIONS:		none.
 *! REMARKS:			none.
 *!
 *!*****************************************************************************
 *!@*/
__u16 getPort(activeProtocol *p)
{
    return p->dest_port;
}


/*!@*****************************************************************************
 *!
 *! FUNCTION:			getProtocolId
 *!
 *! GENERAL DESCRIPTION: This function return the ProtocolId field
 *!
 *! Input:				none.
 *!
 *! Output:				ProtocolId field
 *!
 *! ALGORITHM:			none.
 *!
 *!
 *! ASSUMPTIONS:		none.
 *! REMARKS:			none.
 *!
 *!*****************************************************************************
 *!@*/
int getProtocolId(activeProtocol *ap)
{
    return ap->protocolId;
}


/*!@*****************************************************************************
 *!
 *! FUNCTION:			getStructByStructCode
 *!
 *! GENERAL DESCRIPTION: This function return the struct matching to the proper
 *!                      protocolId and structode from the structs list
 *!
 *! Input:			     proper protocol and struct code
 *!
 *! Output:				struct
 *!
 *! ALGORITHM:          The algorithm is base on the following operations:
 *!                     1. iterate all the structs of the proper protocol
 *!                     2. find the proper struct and return it,otherwise return null
 *!
 *!
 *! ASSUMPTIONS:		none.
 *! REMARKS:			none.
 *!
 *!*****************************************************************************
 *!@*/
structOfActiveProtocol* getStructByStructCode(activeProtocol *protocol,int structCode)
{
    structOfActiveProtocol *struct_to_check=list_first(protocol->list_of_structs);
    while(struct_to_check)
    {
        if(struct_to_check->protocol_id==protocol->protocolId&&struct_to_check->struct_code==structCode)
        {
            return struct_to_check;
        }
        struct_to_check=list_next(protocol->list_of_structs);
    }
    return NULL;
}
