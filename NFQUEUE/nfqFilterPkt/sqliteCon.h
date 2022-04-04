#ifndef SQLITECON_H_INCLUDED
#define SQLITECON_H_INCLUDED
#include <stdio.h>
#include <stdlib.h>
#include <sqlite3.h>
#include <search.h>
#include "myStruct.h"
#include "list.h"
#include "protocol.h"
#include "activeProtocol.h"
#include "structField.h"
#include "structOfActiveProtocol.h"
//#include "hashT.h"



/*!@*****************************************************************************
 *!
 *! FUNCTION:			 getDBInstance
 *!
 *! GENERAL DESCRIPTION: This function create sqlite3 descriptor
 *!
 *! Input:				 none.
 *!
 *! Output:              sqlite3 descriptor
 *!
 *! ALGORITHM:			The algorithm is base on the following operations:
 *!                     1. implemetetion of singleton
 *!
 *! ASSUMPTIONS:		none.
 *! REMARKS:			none.
 *!
 *!*****************************************************************************
 *!@*/
sqlite3* getDBInstance();

/*!@*****************************************************************************
 *!
 *! FUNCTION:			 readAllStructFields
 *!
 *! GENERAL DESCRIPTION: This function create list af all the struct fields belong
 *!                      to the proper struct code and protocol_id
 *!
 *! Input:				 struct code,protocolID(group of structs),
 *!                      pointer to the struct fields list
 *!
 *! Output:              struct fields list
 *!
 *! ALGORITHM:			The algorithm is base on the following operations:
 *!                     1.read all struct fields from sqlite3 db.
 *!                     2. keep the result in the list pointer
 *!
 *! ASSUMPTIONS:		none.
 *! REMARKS:			none.
 *!
 *!*****************************************************************************
 *!@*/
void readAllStructFields(int strcutCode,int protocolId,list **list_structFields);

/*!@*****************************************************************************
 *!
 *! FUNCTION:			 readAllStrcutsOfActiveProtocol
 *!
 *! GENERAL DESCRIPTION: This function create list af all the structs (with the proper struct fields)
 *!                      belongs to the protocolId.
 *!
 *! Input:				 pointer to list of all active protocols/Fire Wall Rules,
 *!                      protocolId
 *!
 *! Output:              structs list belongs to the protocolId.
 *!
 *! ALGORITHM:			The algorithm is base on the following operations:
 *!                     1.read all structs( with the proper struct fields) belongs to the protocolId from sqlite3 db.
 *!                     2. keep the results in the list pointer.
 *!
 *! ASSUMPTIONS:		none.
 *! REMARKS:			none.
 *!
 *!*****************************************************************************
 *!@*/
void readAllStrcutsOfActiveProtocol(list **list_of_structs,int protocolId);

/*!@*****************************************************************************
 *!
 *! FUNCTION:			 readActiveProtocolsFromDB
 *!
 *! GENERAL DESCRIPTION: This function create list af all the active protocols/
 *!                      Fire Wall Rules.
 *!
 *! Input:				 pointer to list of all active protocols/Fire Wall Rules.
 *!
 *! Output:              active protocols/Fire Wall Rules list.
 *!
 *! ALGORITHM:			The algorithm is base on the following operations:
 *!                     1.read all active protocols/Fire Wall Rules from sqlite3 db.
 *!                     2. keep the results in the list pointer.
 *!
 *! ASSUMPTIONS:		none.
 *! REMARKS:			none.
 *!
 *!*****************************************************************************
 *!@*/
list* readActiveProtocolsFromDB();


#endif // SQLITECON_H_INCLUDED
