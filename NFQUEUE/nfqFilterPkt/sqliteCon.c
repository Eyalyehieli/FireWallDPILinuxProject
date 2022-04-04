
#include "sqliteCon.h"
#include "protocol.h"
#include "activeProtocol.h"


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
sqlite3* getDBInstance()
{
    static sqlite3 *db=NULL;
    if(db==NULL)
    {
        sqlite3_open("/home/eyalyehieli/Desktop/FireWallProject/FireWallDPILinuxProject/packetsNetFilterDB/netFilterDB.sqlite",&db);
    }
    return db;
}


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
void readAllStructFields(int strcutCode,int protocolId,list **list_structFields)
{
    sqlite3 *db;
    sqlite3_stmt *stmtStructFields;
    int rcStructFields;
    char *queryStructFields="SELECT fieldName,type,minRange,maxRange FROM StructFields INNER JOIN Structs ON StructFields.struct_id=Structs.id WHERE Structs.code= ? AND Structs.protocol_id = ?";
    structField *structField_to_add;
    *list_structFields=list_create();

    db=getDBInstance();
    if(db==NULL){printf("failed to open db");}
    rcStructFields=sqlite3_prepare_v2(db,queryStructFields,-1,&stmtStructFields,NULL);

    //initial struct fields stmt
    if (rcStructFields == SQLITE_OK)
        {

            sqlite3_bind_int(stmtStructFields,1,strcutCode);
            sqlite3_bind_int(stmtStructFields,2,protocolId);

            //read all struct fields
            while (sqlite3_step(stmtStructFields) != SQLITE_DONE)
                {
                    structField_to_add=(structField*)malloc(sizeof(structField));
                    setFieldName(structField_to_add,(char*)sqlite3_column_text(stmtStructFields,0));
                    setType(structField_to_add,(char*)sqlite3_column_text(stmtStructFields,1));
                    setRange(structField_to_add,(char*)sqlite3_column_text(stmtStructFields,2),(char*)sqlite3_column_text(stmtStructFields,3));
                    list_add(*list_structFields,structField_to_add);
                }
        }

    else
        {
            fprintf(stderr, "Failed to execute statement 1: %s\n", sqlite3_errmsg(db));
        }



    sqlite3_finalize(stmtStructFields);
}

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
void readAllStrcutsOfActiveProtocol(list **list_of_structs,int protocolId)
{
    sqlite3 *db;
    sqlite3_stmt *stmt;
    int rc;
    char* query="SELECT code,size,protocol_id FROM Structs WHERE Structs.protocol_id=?";
    structOfActiveProtocol* struct_of_active_protocol;
    *list_of_structs=list_create();
     db=getDBInstance();
     if(db==NULL){printf("failed to open db");}
     rc=sqlite3_prepare_v2(db, query, -1, &stmt, NULL);

    if (rc == SQLITE_OK)
    {
        sqlite3_bind_int(stmt,1,protocolId);

        while (sqlite3_step(stmt) != SQLITE_DONE)
        {

            struct_of_active_protocol=(structOfActiveProtocol*)malloc(sizeof(structOfActiveProtocol));
            setStructCode(struct_of_active_protocol,sqlite3_column_int(stmt,0));
            setStructSIze(struct_of_active_protocol,sqlite3_column_int(stmt,1));
            setProtocol_id(struct_of_active_protocol,sqlite3_column_int(stmt,2));
            readAllStructFields(struct_of_active_protocol->struct_code,struct_of_active_protocol->protocol_id,&struct_of_active_protocol->list_of_structsFields);
            list_add(*list_of_structs,struct_of_active_protocol);
        }
    }
    else
    {
        fprintf(stderr, "Failed to prepare SQL: %s\n", sqlite3_errmsg(db));
    }

    sqlite3_finalize(stmt);
}



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
list* readActiveProtocolsFromDB()
{
    sqlite3 *db;
    sqlite3_stmt *stmt;
    int rc;
    char* query="SELECT ip,port,protocol_id FROM Connections INNER JOIN FireWallRules ON Connections.id=FireWallRules.connection_id INNER JOIN Protocols ON FireWallRules.protocol_id=Protocols.id WHERE FireWallRules.activeStatus=1";
    activeProtocol* activeProtocol_to_add;
    list *list_of_active_Protocol=list_create();

    db=getDBInstance();
    if(db==NULL){printf("failed to open db");}
    rc=sqlite3_prepare_v2(db, query, -1, &stmt, NULL);

    if (rc == SQLITE_OK)
    {
        while (sqlite3_step(stmt) != SQLITE_DONE)
        {

            activeProtocol_to_add=(activeProtocol*)malloc(sizeof(activeProtocol));
            setIp(activeProtocol_to_add,(char*)sqlite3_column_text(stmt,0));
            setPort(activeProtocol_to_add,sqlite3_column_int(stmt,1));
            setProtocolId(activeProtocol_to_add,sqlite3_column_int(stmt,2));
            readAllStrcutsOfActiveProtocol(&activeProtocol_to_add->list_of_structs,activeProtocol_to_add->protocolId);
            list_add(list_of_active_Protocol,activeProtocol_to_add);
        }
    }
    else
    {
        fprintf(stderr, "Failed to prepare SQL: %s\n", sqlite3_errmsg(db));
    }

    sqlite3_finalize(stmt);
    return list_of_active_Protocol;
}

