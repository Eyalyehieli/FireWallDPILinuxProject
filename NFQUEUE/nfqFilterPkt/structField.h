#ifndef STRUCTFIELD_H_INCLUDED
#define STRUCTFIELD_H_INCLUDED

#include <stdlib.h>
#include <string.h>

/*! STRUCTURE TYPE DEFINITION 	 */
typedef struct
{
    char* fieldName;
    char* type;
    void* min_range;
    void* max_range;

}structField;


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
double atod(const char* s);

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
void setFieldName(structField *sf,char *fieldName);

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
void setType(structField *sf,char* type);

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
void setRange(structField *sf,char *min,char *max);

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
char* getFieldName(structField *sf);

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
char* getType(structField *sf);

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
void* getMin_range(structField *sf);

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
void* getMax_range(structField *sf);

#endif // STRUCTFIELD_H_INCLUDED
