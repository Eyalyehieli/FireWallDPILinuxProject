#ifndef MEMORYHANDLE_H_INCLUDED
#define MEMORYHANDLE_H_INCLUDED
#include <stdio.h>
#include <stdlib.h>
#include <arpa/inet.h>


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
int intFromBigEToLittleE(const int inInt);


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
short shortFromBigEToLittleE(const short inShort);


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
float floatFromBigEToLittleE( const float inFloat );


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
double doubleFromBigEToLittleE( const double indouble );


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

long longFromBigEToLittleE( const long inLong );
#endif // MEMORYHANDLE_H_INCLUDED
