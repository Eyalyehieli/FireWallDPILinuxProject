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
#include "myStruct.h"
#include "list.h"
#include "protocol.h"
#include "sqliteCon.h"
#include "activeProtocol.h"
#include "jsonHandle.h"
//#include "hashT.h"

#define MAX 0
#define MIN 1

void startNfq();

u_int32_t print_pkt (struct nfq_data *tb);

int checkValidationInProtocol(unsigned char *raw_payload,structField *structField_to_check,int *surfer);

int check_pkt(__u16 dest_port,__u32 dest_ip,int payload_len,unsigned char *data_payload, struct iphdr *iph, struct udphdr *udph,struct nfq_q_handle *qh,u_int32_t id,u_int32_t mark,struct nfq_data *nfa,int *surfer);

int cb(struct nfq_q_handle *qh, struct nfgenmsg *nfmsg, struct nfq_data *nfa, void *data);



#endif // NFQHANDLE_H_INCLUDED
