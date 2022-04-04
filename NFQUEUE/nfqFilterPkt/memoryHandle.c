#include "memoryHandle.h"

/*!@*****************************************************************************
 *!
 *! FUNCTION:			intFromBigEToLittleE
 *!
 *! GENERAL DESCRIPTION: This function change the Int var from big endian
 *!                      to little endian.
 *!
 *! Input:				Int var as big endian
 *!
 *! Output:				The var as little indian
 *!
 *! ALGORITHM:			The algorithm is base on the following operations:
 *!                     1. ntohs() function from <arpa/inet.h>
 *!
 *! ASSUMPTIONS:		The input var represented as big endian
 *! REMARKS:			The function is in used because in java the vars store
 *!                     big endian and in c as little indian
 *!
 *!*****************************************************************************
 *!@*/
int intFromBigEToLittleE(const int inInt)
{
   return ntohl(inInt);
}
/*!@*****************************************************************************
 *!
 *! FUNCTION:			shortFromBigEToLittleE
 *!
 *! GENERAL DESCRIPTION: This function change the Short var from big endian
 *!                      to little endian.
 *!
 *! Input:				Short var as big endian
 *!
 *! Output:				The var as little indian
 *!
 *! ALGORITHM:		    The algorithm is base on the following operations:
 *!                     1. ntohs() function from <arpa/inet.h>
 *!
 *! ASSUMPTIONS:		The input var represented as big endian
 *! REMARKS:			The function is in used because in java the vars store
 *!                     big endian and in c as little indian
 *!
 *!*****************************************************************************
 *!@*/
short shortFromBigEToLittleE(const short inShort)
{
   return ntohs(inShort);
}

/*!@*****************************************************************************
 *!
 *! FUNCTION:			floatFromBigEToLittleE
 *!
 *! GENERAL DESCRIPTION: This function change the Float var from big endian
 *!                      to little endian.
 *!
 *! Input:				Float var as big endian
 *!
 *! Output:				The var as little indian
 *!
 *! ALGORITHM:			The algorithm is base on the following operations:
 *!                     1. Represent the var memory as char*
 *!                     2. reverse the char* and return it
 *!
 *! ASSUMPTIONS:		The input var represented as big endian
 *! REMARKS:			The function is in used because in java the vars store
 *!                     big endian and in c as little indian
 *!
 *!*****************************************************************************
 *!@*/
float floatFromBigEToLittleE( const float inFloat )
{
   float retVal;
   char *floatToConvert = ( char* ) & inFloat;
   char *returnFloat = ( char* ) & retVal;

   // swap the bytes into a temporary buffer
   returnFloat[0] = floatToConvert[3];
   returnFloat[1] = floatToConvert[2];
   returnFloat[2] = floatToConvert[1];
   returnFloat[3] = floatToConvert[0];

   return retVal;
}

/*!@*****************************************************************************
 *!
 *! FUNCTION:			doubleFromBigEToLittleE
 *!
 *! GENERAL DESCRIPTION: This function change the Double var from big endian
 *!                      to little endian.
 *!
 *! Input:				Double var as big endian
 *!
 *! Output:				The var as little indian
 *!
 *! ALGORITHM:			The algorithm is base on the following operations:
 *!                     1. Represent the var memory as char*
 *!                     2. reverse the char* and return it
 *!
 *! ASSUMPTIONS:		The input var represented as big endian
 *! REMARKS:			The function is in used because in java the vars store
 *!                     big endian and in c as little indian
 *!
 *!*****************************************************************************
 *!@*/
double doubleFromBigEToLittleE( const double indouble )
{
   double retVal;
   char *doubleToConvert = ( char* ) & indouble;
   char *returndouble = ( char* ) & retVal;

   // swap the bytes into a temporary buffer
   returndouble[0] = doubleToConvert[7];
   returndouble[1] = doubleToConvert[6];
   returndouble[2] = doubleToConvert[5];
   returndouble[3] = doubleToConvert[4];
   returndouble[4] = doubleToConvert[3];
   returndouble[5] = doubleToConvert[2];
   returndouble[6] = doubleToConvert[1];
   returndouble[7] = doubleToConvert[0];

   return retVal;
}


/*!@*****************************************************************************
 *!
 *! FUNCTION:			longFromBigEToLittleE
 *!
 *! GENERAL DESCRIPTION: This function change the Long var from big endian
 *!                      to little endian.
 *!
 *! Input:				Long var as big endian
 *!
 *! Output:				The var as little indian
 *!
 *! ALGORITHM:			The algorithm is base on the following operations:
 *!                     1. Represent the var memory as char*
 *!                     2. reverse the char* and return it
 *!
 *! ASSUMPTIONS:		The input var represented as big endian
 *! REMARKS:			The function is in used because in java the vars store
 *!                     big endian and in c as little indian
 *!
 *!*****************************************************************************
 *!@*/
long longFromBigEToLittleE( const long inLong )
{
   long retVal;
   char *longToConvert = ( char* ) & inLong;
   char *returnLong = ( char* ) & retVal;

   // swap the bytes into a temporary buffer
   returnLong[0] = longToConvert[7];
   returnLong[1] = longToConvert[6];
   returnLong[2] = longToConvert[5];
   returnLong[3] = longToConvert[4];
   returnLong[4] = longToConvert[3];
   returnLong[5] = longToConvert[2];
   returnLong[6] = longToConvert[1];
   returnLong[7] = longToConvert[0];

   return retVal;
}



