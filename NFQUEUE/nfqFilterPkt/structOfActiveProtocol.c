#include "structOfActiveProtocol.h"





/*!@*****************************************************************************
 *!
 *! FUNCTION:			setStructCode
 *!
 *! GENERAL DESCRIPTION: This function set the structCode field
 *!
 *! Input:				Proper structCode
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
void setStructCode(structOfActiveProtocol *s,int struct_code)
{
    s->struct_code=struct_code;
}

/*!@*****************************************************************************
 *!
 *! FUNCTION:			setStructSIze
 *!
 *! GENERAL DESCRIPTION: This function set the StructSIze field
 *!
 *! Input:				Proper StructSIze
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
void setStructSIze(structOfActiveProtocol *s,int struct_size)
{
    s->struct_size=struct_size;
}


/*!@*****************************************************************************
 *!
 *! FUNCTION:			setProtocol_id
 *!
 *! GENERAL DESCRIPTION: This function set the protocolId field
 *!
 *! Input:				Proper protocolId
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
void setProtocol_id(structOfActiveProtocol *s,int protocolId)
{
    s->protocol_id=protocolId;
}



/*!@*****************************************************************************
 *!
 *! FUNCTION:			getStructCode
 *!
 *! GENERAL DESCRIPTION: This function return the structCode field
 *!
 *! Input:				proper structOfActiveProtocol
 *!
 *! Output:				structCode
 *!
 *! ALGORITHM:			none.
 *!
 *!
 *! ASSUMPTIONS:		The var is validate properly
 *! REMARKS:			none.
 *!
 *!*****************************************************************************
 *!@*/
int getStructCode(structOfActiveProtocol *s)
{
    return s->struct_code;
}

/*!@*****************************************************************************
 *!
 *! FUNCTION:			getStructSIze
 *!
 *! GENERAL DESCRIPTION: This function return the StructSIze field
 *!
 *! Input:				Proper structOfActiveProtocol
 *!
 *! Output:				structSize
 *!
 *! ALGORITHM:			none.
 *!
 *!
 *! ASSUMPTIONS:		The var is validate properly
 *! REMARKS:			none.
 *!
 *!*****************************************************************************
 *!@*/
int getStructSIze(structOfActiveProtocol *s)
{
    return s->struct_size;
}


/*!@*****************************************************************************
 *!
 *! FUNCTION:			getProtocol_id
 *!
 *! GENERAL DESCRIPTION: This function return the protocolId field
 *!
 *! Input:				Proper structOfActiveProtocol
 *!
 *! Output:				protocolId
 *!
 *! ALGORITHM:			none.
 *!
 *!
 *! ASSUMPTIONS:		The var is validate properly
 *! REMARKS:			none.
 *!
 *!*****************************************************************************
 *!@*/
int getProtocol_id(structOfActiveProtocol *s,int protocolId)
{
    return s->protocol_id;
}
