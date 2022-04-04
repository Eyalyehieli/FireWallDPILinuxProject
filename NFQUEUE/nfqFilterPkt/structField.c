#include "structField.h"


/*!@*****************************************************************************
 *!
 *! FUNCTION:			 atod
 *!
 *! GENERAL DESCRIPTION: ascii to double
 *!
 *! Input:				 char* represented double
 *!
 *! Output:             double var
 *!
 *! ALGORITHM:			The algorithm is base on the following operations:
 *!                     1.check if its negative double
 *!                     2.change to double before point and after point
 *!
 *! ASSUMPTIONS:		none.
 *! REMARKS:			none.
 *!
 *!*****************************************************************************
 *!@*/
double atod(const char* s)
{    //definition
    double rez = 0, fact = 1;
    if (*s == '-'){
        s++;
        fact = -1;
    };
    for (int point_seen = 0; *s; s++)
    {
        if (*s == '.')
        {
            point_seen = 1;
            continue;
        }
        int d = *s - '0';
        if (d >= 0 && d <= 9)
        {
            if (point_seen) fact /= 10.0f;
            rez = rez * 10.0f + (float)d;
        }
    }
    return rez * fact;
}


/*!@*****************************************************************************
 *!
 *! FUNCTION:			 setFieldName
 *!
 *! GENERAL DESCRIPTION: set the field name
 *!
 *! Input:				 structField ,field name
 *!
 *! Output:              none.
 *!
 *! ALGORITHM:			The algorithm is base on the following operations:
 *!                     1.allocate new fieldName
 *!                     2.copy the field name using strcpy from <string.h>
 *!
 *! ASSUMPTIONS:		none.
 *! REMARKS:			none.
 *!
 *!*****************************************************************************
 *!@*/
void setFieldName(structField *sf,char *fieldName)
{
    sf->fieldName=(char*)malloc((strlen(fieldName)+1)*sizeof(char));
    strcpy(sf->fieldName,fieldName);
}

/*!@*****************************************************************************
 *!
 *! FUNCTION:			 setRange
 *!
 *! GENERAL DESCRIPTION: set the field name
 *!
 *! Input:				 structField,char* of range(min and max data)
 *!
 *! Output:              none.
 *!
 *! ALGORITHM:			The algorithm is base on the following operations:
 *!                     1.allocate new range by proper type
 *!                     2.cast to the proper type (based on void* behavior
 *!
 *! ASSUMPTIONS:		none.
 *! REMARKS:			none.
 *!
 *!*****************************************************************************
 *!@*/
void setRange(structField *sf,char* min,char*max)
{
    if(strcmp("INT",sf->type)==0)
     {
       int* minRange;
       int* maxRange;
       minRange=(int*)malloc(sizeof(int));
       maxRange=(int*)malloc(sizeof(int));
       *minRange=atoi(min);
       *maxRange=atoi(max);
       sf->min_range=minRange;
       sf->max_range=maxRange;
     }
   else if(strcmp("CHAR",sf->type)==0)
     {
       char* minRange;
       char* maxRange;
       minRange=(char*)malloc(sizeof(char));
       maxRange=(char*)malloc(sizeof(char));
       minRange[0]=((char*)min)[0];
       maxRange[0]=((char*)max)[0];
       sf->min_range=minRange;
       sf->max_range=maxRange;
     }
    else if(strcmp("FLOAT",sf->type)==0)
     {
       float* minRange;
       float* maxRange;
       minRange=(float*)malloc(sizeof(float));
       maxRange=(float*)malloc(sizeof(float));
       *minRange=atof(min);
       *maxRange=atof(max);
       sf->min_range=minRange;
       sf->max_range=maxRange;
     }
     else if(strcmp("DOUBLE",sf->type)==0)
     {
       double* minRange;
       double* maxRange;
       minRange=(double*)malloc(sizeof(double));//for allocation on the heap and not on the stack of the function
       maxRange=(double*)malloc(sizeof(double));
       *minRange=atod(min);
       *maxRange=atod(max);
       sf->min_range=minRange;
       sf->max_range=maxRange;
     }
     else if(strcmp("SHORT",sf->type)==0)
     {
       short* minRange;
       short* maxRange;
       minRange=(short*)malloc(sizeof(short));//for allocation on the heap and not on the stack of the function
       maxRange=(short*)malloc(sizeof(short));
       *minRange=(short)atoi(min);
       *maxRange=(short)atoi(max);
       sf->min_range=minRange;
       sf->max_range=maxRange;
     }
     else if(strcmp("LONG",sf->type)==0)
     {
       long* minRange;
       long* maxRange;
       minRange=(long*)malloc(sizeof(long));//for allocation on the heap and not on the stack of the function
       maxRange=(long*)malloc(sizeof(long));
       *minRange=atol(min);
       *maxRange=atol(max);
       sf->min_range=minRange;
       sf->max_range=maxRange;
     }

}


/*!@*****************************************************************************
 *!
 *! FUNCTION:			 setType
 *!
 *! GENERAL DESCRIPTION: set the field type
 *!
 *! Input:				 structField,field type
 *!
 *! Output:              none.
 *!
 *! ALGORITHM:			The algorithm is base on the following operations:
 *!                     1.allocate new fiel type
 *!                     2.copy the field type using strcpy from <string.h>
 *!
 *! ASSUMPTIONS:		none.
 *! REMARKS:			none.
 *!
 *!*****************************************************************************
 *!@*/
void setType(structField *sf,char* type)
{
    sf->type=(char*)malloc((strlen(type)+1)*sizeof(char));//
    strcpy(sf->type,type);
}


/*!@*****************************************************************************
 *!
 *! FUNCTION:			 getFieldName
 *!
 *! GENERAL DESCRIPTION: return the field name
 *!
 *! Input:				structField
 *!
 *! Output:              field name
 *!
 *! ALGORITHM:			none.
 *!
 *! ASSUMPTIONS:		none.
 *! REMARKS:			none.
 *!
 *!*****************************************************************************
 *!@*/
char* getFieldName(structField *sf)
{
    return sf->fieldName;
}


/*!@*****************************************************************************
 *!
 *! FUNCTION:			 getType
 *!
 *! GENERAL DESCRIPTION: return the field type
 *!
 *! Input:				structField
 *!
 *! Output:             field type
 *!
 *! ALGORITHM:			none.
 *!
 *! ASSUMPTIONS:		none.
 *! REMARKS:			none.
 *!
 *!*****************************************************************************
 *!@*/
char* getType(structField *sf)
{
    return sf->type;
}


/*!@*****************************************************************************
 *!
 *! FUNCTION:			 getMin_range
 *!
 *! GENERAL DESCRIPTION: return the field min range
 *!
 *! Input:				structField
 *!
 *! Output:             field min range
 *!
 *! ALGORITHM:			none.
 *!
 *! ASSUMPTIONS:		none.
 *! REMARKS:			none.
 *!
 *!*****************************************************************************
 *!@*/
void* getMin_range(structField *sf)
{
    return sf->min_range;
}

/*!@*****************************************************************************
 *!
 *! FUNCTION:			 getMax_range
 *!
 *! GENERAL DESCRIPTION: return the field max range
 *!
 *! Input:				structField
 *!
 *! Output:             field max range
 *!
 *! ALGORITHM:			none.
 *!
 *! ASSUMPTIONS:		none.
 *! REMARKS:			none.
 *!
 *!*****************************************************************************
 *!@*/
void* getMax_range(structField *sf)
{
    return sf->max_range;
}
