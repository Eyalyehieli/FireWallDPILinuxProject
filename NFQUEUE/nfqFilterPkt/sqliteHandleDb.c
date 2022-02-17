#include "sqliteHandleDb.h"



double atod(const char* s){    //definition
    double rez = 0, fact = 1;
    if (*s == '-'){
        s++;
        fact = -1;
    };
    for (int point_seen = 0; *s; s++){
        if (*s == '.'){
            point_seen = 1;
            continue;
        };
        int d = *s - '0';
        if (d >= 0 && d <= 9){
            if (point_seen) fact /= 10.0f;
            rez = rez * 10.0f + (float)d;
        };
    };
    return rez * fact;
    }


int getNumberOfStructs(){



}


void readProtocolsFromDB(list** list_of_protocols){
    sqlite3 *db;
    sqlite3_stmt *stmt;
    int rc;
    char* query="SELECT type,minRange,maxRange,serialNumber,structName,applicationName,ip,port FROM StructFields st INNER JOIN applications app ON st.id=app.StrcutFieldsId INNER JOIN ConnectionToApplicationByPortAndIp conTAppBPortAIp on conTAppBPortAIp.id=app.ConnectionToApplicationByPortAndIpId";
    protocol* protocol_to_add=(protocol*)malloc(sizeof(protocol));
    *list_of_protocols=list_create();
    sqlite3_open("~/Desktop/EyalJavaProgram/packetsNetFilterDB/netFilterDB.sqlite",&db);
    if(db==NULL){printf("failed to open db");}
    rc=sqlite3_prepare_v2(db, query, -1, &stmt, NULL);

    if (rc != SQLITE_OK)
    {
        fprintf(stderr, "Failed to prepare SQL: %s\n", sqlite3_errmsg(db));
    }

    while (sqlite3_step(stmt) != SQLITE_DONE)
    {
        protocol_to_add->type=(char*)malloc(sizeof(sqlite3_column_text(stmt,0))+1);
        protocol_to_add->type=sqlite3_column_text(stmt,0);
        getRange(protocol_to_add,stmt);
        protocol_to_add->serialNumber=sqlite3_column_int(stmt,3);
        protocol_to_add->structName=(char*)malloc(sizeof(sqlite3_column_text(stmt,4))+1);
        protocol_to_add->structName=sqlite3_column_text(stmt,4);
        protocol_to_add->application=(char*)malloc(sizeof(sqlite3_column_text(stmt,5))+1);
        protocol_to_add->application=sqlite3_column_text(stmt,5);
        protocol_to_add->dest_ip=inet_addr(sqlite3_column_text(stmt,6));
        protocol_to_add->dest_port=htons(sqlite3_column_int(stmt,7));
        list_add(*list_of_protocols,protocol_to_add);

      /*protocol_to_add->dest_ip=inet_addr(sqlite3_column_text(stmt,0));
      protocol_to_add->dest_port=htons(sqlite3_column_int(stmt,1));
      protocol_to_add->type=sqlite3_column_text(stmt,2);
      getRange(protocol_to_add,stmt);
      protocol_to_add->serialNumber=sqlite3_column_int(stmt,5);
      list_add(*list_of_protocols,protocol_to_add);
      */
    }

    sqlite3_finalize(stmt);
	sqlite3_close(db);
}

 void getRange(protocol* protocol_to_add,sqlite3_stmt* stmt){
  //not using switch because i cant compare between strings
   if(strcmp("INT",protocol_to_add->type)==0)
     {
       int* minRange;
       int* maxRange;
       minRange=(int*)malloc(sizeof(int));
       maxRange=(int*)malloc(sizeof(int));
       *minRange=atoi(sqlite3_column_text(stmt,1));
       *maxRange=atoi(sqlite3_column_text(stmt,2));
       protocol_to_add->min_range=(int*)minRange;
       protocol_to_add->max_range=(int*)maxRange;
     }
   else if(strcmp("CHAR",protocol_to_add->type)==0)
     {
       char* minRange;
       char* maxRange;
       minRange=(char*)malloc(sizeof(char));
       maxRange=(char*)malloc(sizeof(char));
       *minRange=sqlite3_column_text(stmt,1);
       *maxRange=sqlite3_column_text(stmt,2);
       protocol_to_add->min_range=(char*)&minRange;
       protocol_to_add->max_range=(char*)&maxRange;
     }
    else if(strcmp("FLOAT",protocol_to_add->type)==0)
     {
       float* minRange;
       float* maxRange;
       minRange=(float*)malloc(sizeof(float));
       maxRange=(float*)malloc(sizeof(float));
       *minRange=atof(sqlite3_column_text(stmt,1));
       *maxRange=atof(sqlite3_column_text(stmt,2));
       protocol_to_add->min_range=(float*)&minRange;
       protocol_to_add->max_range=(float*)&maxRange;
     }
    else if(strcmp("STRING",protocol_to_add->type)==0)
     {
       char* minRange;
       char* maxRange;
       minRange=(char*)malloc(sizeof(char));
       maxRange=(char*)malloc(sizeof(char));
       *minRange=sqlite3_column_text(stmt,1);
       *maxRange=sqlite3_column_text(stmt,2);
       protocol_to_add->min_range=(char*)&minRange;
       protocol_to_add->max_range=(char*)&maxRange;
     }

     else if(strcmp("BOOLEAN",protocol_to_add->type)==0)
     {
       int *minRange;
       int *maxRange;
       minRange=(int*)malloc(sizeof(int));
       maxRange=(int*)malloc(sizeof(int));
       *minRange=0;
       *maxRange=1;
       protocol_to_add->min_range=(int*)&minRange;
       protocol_to_add->max_range=(int*)&maxRange;
     }
     else if(strcmp("DOUBLE",protocol_to_add->type)==0)
     {
       double* minRange;
       double* maxRange;
       minRange=(double*)malloc(sizeof(double));//for allocation on the heap and not on the stack of the function
       maxRange=(double*)malloc(sizeof(double));
       *minRange=atod(sqlite3_column_text(stmt,1));
       *maxRange=atod(sqlite3_column_text(stmt,2));
       protocol_to_add->min_range=(double*)&minRange;
       protocol_to_add->max_range=(double*)&maxRange;
     }



   /*switch(protocol_to_add->type)
   {
     //case "INT":protocol_to_add->min_range= (int*)&(atoi(sqlite3_column_text(stmt,3)));protocol_to_add->max_range=(int*)&(atoi(sqlite3_column_text(stmt,4)));break;
     //case "CHAR":protocol_to_add->min_range=(char*)sqlite3_column_text(stmt,3);protocol_to_add->max_range=(char*)sqlite3_column_text(stmt,4);break;
     //case "FLOAT":protocol_to_add->min_range=(float*)&(atof(sqlite3_column_text(stmt,3)));protocol_to_add->max_range=(float*)&(atof(sqlite3_column_text(stmt,4)));break;
     //case "STRING":protocol_to_add->min_range=(char*)sqlite3_column_text(stmt,3);protocol_to_add->max_range=(char*)sqlite3_column_text(stmt,4);break;
     case "BOOLEAN":protocol_to_add->min_range=(int*)&(boolean_min_range);protocol_to_add->max_range=(int*)&(boolean_max_range);break;
     case "DOUBLE":protocol_to_add->min_range=(double*)&(atod(sqlite3_column_text(stmt,3)));protocol_to_add->max_range=(double*)&(atod(sqlite3_column_text(stmt,4)));break;
   }*/
}
