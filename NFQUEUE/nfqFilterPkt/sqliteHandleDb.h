#ifndef SQLITEHANDLEDB_H_INCLUDED
#define SQLITEHANDLEDB_H_INCLUDED
#include <stdio.h>
#include <stdlib.h>
#include <sqlite3.h>
//#include <search.h>
#include "myStruct.h"
#include "list.h"
#include "protocol.h"

double atod(const char* s);
void readProtocolsFromDB(list** list_of_protocols);
void getRange(protocol* protocol_to_add,sqlite3_stmt* stmt);


#endif // SQLITEHANDLEDB_H_INCLUDED
