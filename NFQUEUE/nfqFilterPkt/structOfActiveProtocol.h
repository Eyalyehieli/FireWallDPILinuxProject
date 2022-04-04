#ifndef STRUCTOFACTIVEPROTOCOL_H_INCLUDED
#define STRUCTOFACTIVEPROTOCOL_H_INCLUDED
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "structField.h"
#include "list.h"

/*! STRUCTURE TYPE DEFINITION 	 */
typedef struct
{
    int struct_code;
    int struct_size;
    int protocol_id;
    list *list_of_structsFields;
}structOfActiveProtocol;


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
void setStructCode(structOfActiveProtocol *s,int struct_code);

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
void setStructSIze(structOfActiveProtocol *s,int struct_size);


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
void setProtocol_id(structOfActiveProtocol *s,int protocolId);



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
int getStructCode(structOfActiveProtocol *s);

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
int getStructSIze(structOfActiveProtocol *s);


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
int getProtocol_id(structOfActiveProtocol *s,int protocolId);


#endif // STRUCTOFACTIVEPROTOCOL_H_INCLUDED
