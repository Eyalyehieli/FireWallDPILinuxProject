
#include "sqliteCon.h"
#include "protocol.h"
#include "activeProtocol.h"



sqlite3* getDBInstance()
{
    static sqlite3 *db=NULL;
    if(db==NULL)
    {
        sqlite3_open("/home/eyalyehieli/Desktop/EyalJavaProgram/packetsNetFilterDB/netFilterDB.sqlite",&db);
    }
    return db;
}

/*hashtable_t* initializer_hash_from_db(hashtable_t *hash)
{
    sqlite3 *db;
    sqlite3_stmt *stmt;
    int* initial_data=(int*)malloc(sizeof(int));
    char *query="SELECT st.structName, COUNT(*) FROM StructFields st INNER JOIN applications app ON st.id=app.StructFieldsId INNER JOIN ConnectionToApplicationByPortAndIp conTAppBPortAIp on conTAppBPortAIp.id=app.ConnectionToApplicationByPortAndIpId Group by st.structName";
    int rc,count;
    db=getDBInstance();
    rc=sqlite3_prepare_v2(db,query,-1,&stmt,NULL);
    count=0;
    *initial_data=0;
    if (rc != SQLITE_OK)
    {
        fprintf(stderr, "Failed to prepare SQL: %s\n", sqlite3_errmsg(db));
    }

    while (sqlite3_step(stmt) != SQLITE_DONE)
    {
        ht_put(hash,(char*)sqlite3_column_text(stmt,0),*initial_data);
        printf("data from db %s \n",sqlite3_column_text(stmt,0));
        printf("struct name %s\n",ht_get(hash,(char*)sqlite3_column_text(stmt,0)));
    }
    sqlite3_finalize(stmt);
    return hash;
}*/

void readAllStructFields(int strcutCode,int protocolId,list **list_structFields,int *structSize)
{
    sqlite3 *db;
    sqlite3_stmt *stmtStructFields,*stmtStructSize;
    int rcStructFields,rcStructSize;
    char *queryStructSize="SELECT size FROM Structs WHERE Structs.code=? AND Structs.protocol_id = ?";
    char *queryStructFields="SELECT fieldName,type,minRange,maxRange FROM StructFields INNER JOIN Structs ON StructFields.struct_id=Structs.id WHERE Structs.code= ? AND Structs.protocol_id = ?";
    structField *structField_to_add;
    *list_structFields=list_create();
    db=getDBInstance();
    if(db==NULL){printf("failed to open db");}
    rcStructFields=sqlite3_prepare_v2(db,queryStructFields,-1,&stmtStructFields,NULL);
    rcStructSize=sqlite3_prepare_v2(db,queryStructSize,-1,&stmtStructSize,NULL);
    if (rcStructFields == SQLITE_OK)
        {

            sqlite3_bind_int(stmtStructFields,1,strcutCode);
            sqlite3_bind_int(stmtStructFields,2,protocolId);
        }
    else
        {
            fprintf(stderr, "Failed to execute statement 1: %s\n", sqlite3_errmsg(db));
        }
    if (rcStructSize == SQLITE_OK)
        {

            sqlite3_bind_int(stmtStructSize,1,strcutCode);
            sqlite3_bind_int(stmtStructSize,2,protocolId);
        }
    else
        {
            fprintf(stderr, "Failed to execute statement 2: %s\n", sqlite3_errmsg(db));
        }
    while (sqlite3_step(stmtStructFields) != SQLITE_DONE)
        {
            structField_to_add=(structField*)malloc(sizeof(structField));
            setFieldName(structField_to_add,(char*)sqlite3_column_text(stmtStructFields,0));
            setType(structField_to_add,(char*)sqlite3_column_text(stmtStructFields,1));
            setRange(structField_to_add,(char*)sqlite3_column_text(stmtStructFields,2),(char*)sqlite3_column_text(stmtStructFields,3));
            list_add(*list_structFields,structField_to_add);
        }
    sqlite3_finalize(stmtStructFields);

    if(sqlite3_step(stmtStructSize) != SQLITE_ROW)
    {
        fprintf(stderr, "Failed to execute statement 3: %s\n", sqlite3_errmsg(db));
    }
    else
    {
        *structSize=sqlite3_column_int(stmtStructSize,0);
    }
    sqlite3_finalize(stmtStructSize);
}

void readActiveProtocolsFromDB(list **list_of_active_Protocol)
{
    sqlite3 *db;
    sqlite3_stmt *stmt;
    int rc;
    char* query="SELECT ip,port,protocol_id FROM Connections INNER JOIN FireWallRules ON Connections.id=FireWallRules.connection_id INNER JOIN Protocols ON FireWallRules.protocol_id=Protocols.id WHERE FireWallRules.activeStatus=1";
    activeProtocol* activeProtocol_to_add;
    *list_of_active_Protocol=list_create();
    //sqlite3_open("/home/eyalyehieli/Desktop/EyalJavaProgram/packetsNetFilterDB/netFilterDB.sqlite",&db);
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
            list_add(*list_of_active_Protocol,activeProtocol_to_add);
        }
    }
    else
    {
        fprintf(stderr, "Failed to prepare SQL: %s\n", sqlite3_errmsg(db));
    }

    sqlite3_finalize(stmt);
}
/*void readProtocolsFromDB(list** list_of_protocols){
    sqlite3 *db;
    sqlite3_stmt *stmt;
    int rc;
    //TODO: CHANGE THE READ FROM THE DB TO EACH PROTOCOL TO SAVE TIME-char* query="SELECT type,minRange,maxRange,serialNumber,structName,applicationName,ip,port FROM StructFields st INNER JOIN applications app ON st.id=app.StructFieldsId INNER JOIN ConnectionToApplicationByPortAndIp conTAppBPortAIp on conTAppBPortAIp.id=app.ConnectionToApplicationByPortAndIpId";
    protocol* protocol_to_add;
    *list_of_protocols=list_create();
    //sqlite3_open("/home/eyalyehieli/Desktop/EyalJavaProgram/packetsNetFilterDB/netFilterDB.sqlite",&db);
    db=getDBInstance();
    if(db==NULL){printf("failed to open db");}
    rc=sqlite3_prepare_v2(db, query, -1, &stmt, NULL);

    if (rc != SQLITE_OK)
    {
        fprintf(stderr, "Failed to prepare SQL: %s\n", sqlite3_errmsg(db));
    }

    while (sqlite3_step(stmt) != SQLITE_DONE)
    {

        protocol_to_add=(protocol*)malloc(sizeof(protocol));
        setType(protocol_to_add,(char*)sqlite3_column_text(stmt,0));
        setRange(protocol_to_add,(char*)sqlite3_column_text(stmt,1),(char*)sqlite3_column_text(stmt,2));
        setSerialNumber(protocol_to_add,sqlite3_column_int(stmt,3));
        setStructName(protocol_to_add,(char*)sqlite3_column_text(stmt,4));
        setApplication(protocol_to_add,(char*)sqlite3_column_text(stmt,5));
        setIp(protocol_to_add,(char*)sqlite3_column_text(stmt,6));
        setPort(protocol_to_add,sqlite3_column_int(stmt,7));

        list_add(*list_of_protocols,protocol_to_add);
    }

    sqlite3_finalize(stmt);
	//sqlite3_close(db);
}*/

