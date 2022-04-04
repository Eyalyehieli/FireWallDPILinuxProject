#ifndef NFQHANDLE_H_INCLUDED
#define NFQHANDLE_H_INCLUDED
#include <stdio.h>
#include <stdlib.h>
#include <stdint-gcc.h>
#include <search.h>
#include <sqlite3.h>
#include <unistd.h>
#include <string.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <arpa/inet.h>
#include <netinet/udp.h>
#include <netinet/ip.h>
#include <netinet/in.h>
#include <libnetfilter_queue/libnetfilter_queue.h>
#include <libnetfilter_queue/libnetfilter_queue_ipv4.h>
#include <libnetfilter_queue/libnetfilter_queue_udp.h>
//#include <nfnetlink_queue.h>
#include <linux/netfilter.h>
#include <signal.h>
#include "myStruct.h"
#include "list.h"
#include "protocol.h"
#include "sqliteCon.h"
#include "activeProtocol.h"
#include "jsonHandle.h"
#include "memoryHandle.h"

//#include "hashT.h"

/*
#define MAX 0
#define MIN 1
*/

/*! CONSTANT DECLARATIONS   */
#define SIZE_OF_IP_ADDRESS_IN_BYTES 4

/*!@*****************************************************************************
 *!
 *! FUNCTION:			freeListOfActiveProtocols
 *!
 *! GENERAL DESCRIPTION: This function is free the db list
 *!
 *! Input:				list to free
 *!
 *! Output:				none.
 *!
 *! ALGORITHM:			The algorithm is base on the following operations:
 *!                     1.free the whole list
 *!
 *! ASSUMPTIONS:
 *! REMARKS:
 *!
 *!*****************************************************************************
 *!@*/

void freeListOfActiveProtocols(list *list_of_protocols);

/*!@*****************************************************************************
 *!
 *! FUNCTION:			readDB
 *!
 *! GENERAL DESCRIPTION: This function is a callBack function
 *!                      of the signal SIGUSR1 Waiting to signal from the DB GUI app
 *!
 *! Input:				signum-signal number
 *!
 *! Output:				none.
 *!
 *! ALGORITHM:			The algorithm is base on the following operations:
 *!                     1. read all active protocol(rules)
 *!                     2. read for every protocol it's structs
 *!                     3. read for every struct it's structFields,size
 *!
 *! ASSUMPTIONS:
 *! REMARKS:
 *!
 *!*****************************************************************************
 *!@*/

 void readDB(int signum);

/*!@*****************************************************************************
 *!
 *! FUNCTION:			startNfq
 *!
 *! GENERAL DESCRIPTION: This function initializing the NFQ
 *!
 *! Input:				none.
 *!
 *! Output:				none.
 *!
 *! ALGORITHM:			The algorithm is base on the following operations:
 *!                     1. create, bind, set, and receive the packets
 *!
 *! ASSUMPTIONS:		This queue is deafault nfq (nfq number 0)
 *! REMARKS:			sudo iptables -I INPUT -j NFQUEUE --queue-num 0
 *!                     command have executed
 *!
 *!*****************************************************************************
 *!@*/
void startNfq();


/*!@*****************************************************************************
 *!
 *! FUNCTION:			print_pkt
 *!
 *! GENERAL DESCRIPTION: This function print the packet meta data
 *!
 *! Input:				 nfq_data* of packet
 *!
 *! Output:				packet id
 *!
 *! ALGORITHM:			The algorithm is base on the following operations:
 *!                     1. extract meta data using proper functions from
 *!                       <libnetfilter_queue>
 *!
 *! ASSUMPTIONS:		none.
 *! REMARKS:			none.
 *!
 *!*****************************************************************************
 *!@*/
u_int32_t print_pkt (struct nfq_data *tb);


/*!@*****************************************************************************
 *!
 *! FUNCTION:			checkValidationInProtocol
 *!
 *! GENERAL DESCRIPTION: This function decide if to Drop or Accept packet
 *!                      by proper DPI
 *!
 *! Input:				 raw_payload of packet, proper struct field to check,surfer to
 *!                      to run all over the raw_payload by current sizeof(var)
 *!
 *! Output:				 decision
 *!
 *! ALGORITHM:			The algorithm is base on the following operations:
 *!                     1. check the incoming struct field from packet using
 *!                        surfer, if it's in range of the FireWall or not
 *!
 *! ASSUMPTIONS:		The struct field representd as big endian
 *! REMARKS:			none.
 *!
 *!*****************************************************************************
 *!@*/
int checkValidationInProtocol(unsigned char *raw_payload,structField *structField_to_check,int *surfer);


/*!@*****************************************************************************
 *!
 *! FUNCTION:			 check_pkt
 *!
 *! GENERAL DESCRIPTION: This function decide if to Drop or Accept packet
 *!
 *! Input:				 dest_port,dest_ip,packet buffer as data_payload,iph,udph,
 *!                      about packet,surfer for run on the packet struct fields
 *!             		 decision
 *!
 *! Output:             decision
 *!
 *! ALGORITHM:			The algorithm is base on the following operations:
 *!                     1. check the incoming packet if its belong to Fire Wall Rule
 *!                     2. check the size and the data from MITM attack of some Net issues
 *!
 *! ASSUMPTIONS:		The first 4 bytes represnt struct code as big endian
 *! REMARKS:			none.
 *!
 *!*****************************************************************************
 *!@*/
int check_pkt(__u16 dest_port,__u32 dest_ip,int payload_len,unsigned char *data_payload, struct iphdr *iph, struct udphdr *udph,struct nfq_data *nfa,int *surfer);


/*!@*****************************************************************************
 *!
 *! FUNCTION:			 cb
 *!
 *! GENERAL DESCRIPTION: This function is a call back function decide if to Drop
 *!                      or Accept packet. The function subscribed on the
 *!                      nfq_create_queue function.
 *!
 *! Input:				 nfq_q_handle,nfgenmsg,nfq_data of packet,data
 *!
 *! Output:              verdict about packet
 *!
 *! ALGORITHM:			The algorithm is base on the following operations:
 *!                     1. check if the packet is udp packet
 *!                     2. using the check_pkt function to decide
 *!
 *! ASSUMPTIONS:		none.
 *! REMARKS:			The function check only udp packets
 *!
 *!*****************************************************************************
 *!@*/
int cb(struct nfq_q_handle *qh, struct nfgenmsg *nfmsg, struct nfq_data *nfa, void *data);



#endif // NFQHANDLE_H_INCLUDED
