#ifndef ACTIVEPROTOCOL_H_INCLUDED
#define ACTIVEPROTOCOL_H_INCLUDED

#include <asm/types.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "list.h"
#include "structOfActiveProtocol.h"


/*! STRUCTURE TYPE DEFINITION 	 */
typedef struct
{
    __u32 dest_ip;
    __u16 dest_port;
    int protocolId;
    list *list_of_structs;
}activeProtocol;


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
void setIp(activeProtocol *ap,char *ip);

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
void setPort(activeProtocol *ap,int port);

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

void setProtocolId(activeProtocol*ap,int protocolId);


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
__u32 getIp(activeProtocol *ap);


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
__u16 getPort(activeProtocol *ap);

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

 int  getProtocolId(activeProtocol *ap);

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
structOfActiveProtocol* getStructByStructCode(activeProtocol *protocol,int structCode);

#endif // ACTIVEPROTOCOL_H_INCLUDED
