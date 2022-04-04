#include "nfqHandle.h"

//iptables rule
//sudo iptables -I INPUT -j NFQUEUE --queue-num 0
//sudo iptables -F



//how to install
//sudo apt update
//sudo apt install libnetfilter-queue-dev

//how to compile
//sudo gcc -o nfq main.c list.c sqliteCon.c activeProtocol.c nfqHandle.c structField.c memoryHandle.c structOfActiveProtocol.c -lnetfilter_queue -lsqlite3
//sudo ./nfq


/*!@*****************************************************************************
 *!
 *! FUNCTION:			main
 *!
 *! GENERAL DESCRIPTION: This function is the main function.
 *!                      the first function to be executed.
 *!
 *! Input:				none.
 *!
 *! Output:				none.
 *!
 *! ALGORITHM:          none.
 *!
 *! ASSUMPTIONS:		The whole project work as expected
 *! REMARKS:			none.
 *!
 *!*****************************************************************************
 *!@*/
int main()
{
    startNfq();
}

