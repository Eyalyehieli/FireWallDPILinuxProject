#ifndef STRUCTFIELD_H_INCLUDED
#define STRUCTFIELD_H_INCLUDED

#include <stdlib.h>
#include <string.h>

typedef struct
{
    char* fieldName;
    char* type;
    void* min_range;
    void* max_range;

}structField;

double atod(const char* s);
void setFieldName(structField *sf,char *fieldName);
void setType(structField *sf,char* type);
void setRange(structField *sf,char *min,char *max);

char* getFieldName(structField *sf);
char* getType(structField *sf);
void* getMin_range(structField *sf);
void* getMax_range(structField *sf);

#endif // STRUCTFIELD_H_INCLUDED
