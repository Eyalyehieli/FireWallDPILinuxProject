#include "nfqHandle.h"




/*! GLOBAL DECLARATIONS   */
time_t rawTime;
FILE* packetsLogFile;
list* list_of_active_protocols;




/*!@*****************************************************************************
 *!
 *! FUNCTION:			freeListOfActiveProtocols
 *!
 *! GENERAL DESCRIPTION: This function is a free the db list
 *!
 *! Input:				 list* represents the db.
 *!
 *! Output:				none.
 *!
 *! ALGORITHM:			The algorithm is base on the following operations:
 *!                     1. free all active protocol(rules)
 *!                     2. free for every protocol it's structs
 *!                     3. free for every struct it's structFields,size
 *!
 *! ASSUMPTIONS:        none.
 *! REMARKS:            none.
 *!
 *!*****************************************************************************
 *!@*/

void freeListOfActiveProtocols(list* list_of_protocols)
{
    fflush(stdout);
    activeProtocol *protocol,*tempForFreeProtocol;
    structOfActiveProtocol *structOfProtocol,*tempForFreeStruct;
    structField *structFieldOfStruct,*tempForFreeStructField;
    protocol=list_first(list_of_protocols);
    int i=0,j=0,k=0;
    for(i=0;i<list_of_protocols->nitems;i++)
    {
        structOfProtocol=list_first(protocol->list_of_structs);
        for(j=0;j<protocol->list_of_structs->nitems;j++)
        {
            structFieldOfStruct=list_first(structOfProtocol->list_of_structsFields);
            for(k=0;k<structOfProtocol->list_of_structsFields->nitems;k++)
            {
                //------free struct field-----//
                free(structFieldOfStruct->fieldName);
                free(structFieldOfStruct->max_range);
                free(structFieldOfStruct->min_range);
                free(structFieldOfStruct->type);
                tempForFreeStructField=structFieldOfStruct;
                structFieldOfStruct=list_next(structOfProtocol->list_of_structsFields);
                free(tempForFreeStructField);
            }
            //-----free the struct and move to the next struct-----//
            tempForFreeStruct=structOfProtocol;
            structOfProtocol=list_next(protocol->list_of_structs);
            free(tempForFreeStruct->list_of_structsFields);
            free(tempForFreeStruct);
        }
        //-----free the protocol and move to the next protocol-----//
        tempForFreeProtocol=protocol;
        protocol=list_next(list_of_protocols);
        free(tempForFreeProtocol->list_of_structs);
        free(tempForFreeProtocol);
    }
    //-----free the list of protocols------//
    free(list_of_protocols);
}

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

 void readDB(int signum)
 {
    list *list_of_protocols_for_free;
    if(signum==SIGUSR1)
    {
        list_of_protocols_for_free=list_of_active_protocols;
        list_of_active_protocols=readActiveProtocolsFromDB();//READ ALL THE SQLITE DB"
        if(list_of_protocols_for_free!=NULL)
        {
            printf("/**************Catched the Signal***************/");
            freeListOfActiveProtocols(list_of_protocols_for_free);
        }
    }
 }

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

void startNfq()
{
    signal(SIGUSR1,readDB);
    readDB(SIGUSR1);
    puts("server");
    struct nfq_handle *h;
	struct nfq_q_handle *qh;
	int fd;
	int rv;
	unsigned char buf[4096] __attribute__ ((aligned));
    printf("opening library handle\n");
	h = nfq_open();
	if (!h)
	{
		perror("error during nfq_open()\n");
	}
	qh = nfq_create_queue(h, 0, &cb, NULL);
	if (!qh)
	{
	    puts("error in create");
		perror("error during nfq_create_queue()\n");
	}

	printf("unbinding existing nf_queue handler for AF_INET (if any)\n");
	if (nfq_unbind_pf(h, AF_INET) < 0)
	{
		perror("error during nfq_unbind_pf()\n");
	}

	printf("binding nfnetlink_queue as nf_queue handler for AF_INET\n");
	if (nfq_bind_pf(h, AF_INET) < 0)
	 {
		perror("error during nfq_bind_pf()\n");
	 }

	printf("binding this socket to queue '0'\n");

    printf("setting copy_packet mode\n");
	if (nfq_set_mode(qh, NFQNL_COPY_PACKET, 0xffff) < 0)
	{
		perror("can't set packet_copy mode\n");
	}

	fd = nfq_fd(h);
    while ((rv = recv(fd, buf, sizeof(buf), 0)))
	   {
	    //puts("pkt recv succeded");
		nfq_handle_packet(h, buf, rv);
	   }
	puts("closing library handle\n");
	nfq_close(h);
}

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

u_int32_t print_pkt (struct nfq_data *tb)
{
	int id = 0;
	struct nfqnl_msg_packet_hdr *ph;
	struct nfqnl_msg_packet_hw *hwph;
	u_int32_t mark,ifiIn,ifiOut,ifiPhyIn,ifiPhyOut;
	int ret,i;
	__u16 dest_port,src_port;
	__u32 dest_ip,src_ip;
	struct iphdr *iph;
	struct udphdr *udph;
	unsigned char *data;
	struct in_addr dest_addr;
	struct in_addr src_addr;

	ph = nfq_get_msg_packet_hdr(tb);
	hwph = nfq_get_packet_hw(tb);
	mark = nfq_get_nfmark(tb);
	ifiIn = nfq_get_indev(tb);
	ifiOut = nfq_get_outdev(tb);
	ifiPhyIn = nfq_get_physindev(tb);
	ifiPhyOut = nfq_get_physoutdev(tb);
	ret = nfq_get_payload(tb, &data);
	iph=(struct iphdr *)data;
	udph=(struct udphdr*)(data + (iph->ihl * 4));

    src_port=ntohs(udph->source);
	dest_port=ntohs(udph->dest);
	src_ip=ntohl(iph->saddr);
	dest_ip=ntohl(iph->daddr);
    dest_addr.s_addr = htonl(dest_ip);
    src_addr.s_addr=htonl(src_ip);

	printf("!*********************************************!\n");
	fprintf(packetsLogFile,"\n\n");
    fflush(packetsLogFile);
	fprintf(packetsLogFile,"!*********************************************!\n");
	fflush(packetsLogFile);
	time(&rawTime);
	fprintf(packetsLogFile,"Dropping time: %s\n",asctime(localtime(&rawTime)));
	fflush(packetsLogFile);
	printf("                     Packet                 \n");
	fprintf(packetsLogFile,"                     Packet                 \n");
	fflush(packetsLogFile);
	 if (ph)
	{
		id = ntohl(ph->packet_id);
		//printf("   |-Packet Id              : %u\n",id);
		//printf("   |-Hardware Protocol      : %04x\n",ntohs(ph->hw_protocol));
		//printf("   |\nHardware Protocol :0x%04x\nHook=%u\nid=%u\n",ntohs(ph->hw_protocol), ph->hook, id);
	}

	/*if (hwph)
	{
        int hlen = ntohs(hwph->hw_addrlen);
		printf("   |-Hardware Source Address: ");
		for (i = 0; i < hlen-1; i++)
		{
			printf("%02x:", hwph->hw_addr[i]);
        }
		printf("%02x\n", hwph->hw_addr[hlen-1]);
	}
	*/


	/*if (mark)
		printf("mark=%u\n", mark);
    */

	/*if (ifiIn)
		printf("   |-In Device              : %u\n", ifiIn);

	if (ifiOut)
		printf("   |-Out Device             : %u\n", ifiOut);

	if (ifiPhyIn)
		printf("   |-Physical In Device     : %u\n", ifiPhyIn);

	if (ifiPhyOut)
		printf("   |-Physical Out Device    : %u\n", ifiPhyOut);
    */

    printf("   |-Source IP              : %s\n",inet_ntoa(src_addr));
    printf("   |-Source Port            : %d\n",src_port);
	printf("   |-Destination IP         : %s\n",inet_ntoa(dest_addr));
    printf("   |-Destination Port       : %d\n",dest_port);
    printf("   |-Transport protocol     : %u\n",iph->protocol);

    fprintf(packetsLogFile,"   |-Source IP              : %s\n",inet_ntoa(src_addr));
    fflush(packetsLogFile);
    fprintf(packetsLogFile,"   |-Source Port            : %d\n",src_port);
    fflush(packetsLogFile);
	fprintf(packetsLogFile,"   |-Destination IP         : %s\n",inet_ntoa(dest_addr));
	fflush(packetsLogFile);
    fprintf(packetsLogFile,"   |-Destination Port       : %d\n",dest_port);
    fflush(packetsLogFile);
    fprintf(packetsLogFile,"   |-Transport protocol     : %u\n",iph->protocol);
    fflush(packetsLogFile);

    if (ret >= 0) {
		printf("   |-Payload Length         : %lu\n", ret-(iph->ihl * 4)-sizeof(struct udphdr));
		fprintf(packetsLogFile,"   |-Payload Length         : %lu\n", ret-(iph->ihl * 4)-sizeof(struct udphdr));
		fflush(packetsLogFile);
		//processPacketData (data, ret);
	}

    printf("   |-Payload:             ");
    fprintf(packetsLogFile,"   |-Payload:             ");
    fflush(packetsLogFile);
	for(i=0;i<ret-(iph->ihl * 4)-sizeof(struct udphdr);i++)
	{
        if(i%12==0){printf("\n             ");fprintf(packetsLogFile,"\n             ");}
        printf("%02X ",*(data + (iph->ihl * 4) + sizeof(struct udphdr)+i));
        fprintf(packetsLogFile,"%02X ",*(data + (iph->ihl * 4) + sizeof(struct udphdr)+i));
        fflush(packetsLogFile);
	}
    printf("\n\n");
    fprintf(packetsLogFile,"\n\n");
    fflush(packetsLogFile);

	return id;
}


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
int checkValidationInProtocol(unsigned char *raw_payload,structField *structField_to_check,int *surfer)
{

    printf("-----------------------------------\n");
    fprintf(packetsLogFile,"-----------------------------------\n");
    int i;
    int decision=1;
    //printf("surfer= %d\n",*surfer);
    printf("   |-Field Type             : %s\n",getType(structField_to_check));
    fprintf(packetsLogFile,"   |-Field Type             : %s\n",getType(structField_to_check));
    fflush(packetsLogFile);
   if(strcmp("INT",structField_to_check->type)==0)
     {
        printf("   |-Max Range              : %d\n",*((int*)getMax_range(structField_to_check)));
        printf("   |-Min Range              : %d\n",*((int*)getMin_range(structField_to_check)));
        printf("   |-Payload                : %d\n",intFromBigEToLittleE(*((int*)(raw_payload+*surfer))));
        fprintf(packetsLogFile,"   |-Max Range              : %d\n",*((int*)getMax_range(structField_to_check)));
        fprintf(packetsLogFile,"   |-Min Range              : %d\n",*((int*)getMin_range(structField_to_check)));
        fprintf(packetsLogFile,"   |-Payload                : %d\n",intFromBigEToLittleE(*((int*)(raw_payload+*surfer))));
        fflush(packetsLogFile);
        if(intFromBigEToLittleE(*((int*)(raw_payload+*surfer)))>*((int*)getMax_range(structField_to_check))||intFromBigEToLittleE(*((int*)(raw_payload+*surfer)))<*((int*)getMin_range(structField_to_check)))
        {
            decision=0;
        }
        *surfer+=sizeof(int);
     }
   else if(strcmp("CHAR",structField_to_check->type)==0)
     {
        printf("   |-Max Range              : %c\n",*((char*)getMax_range(structField_to_check)));
        printf("   |-Min Range              : %c\n",*((char*)getMin_range(structField_to_check)));
        printf("   |-Payload                : %c\n",*((char*)(raw_payload+*surfer)));
        fprintf(packetsLogFile,"   |-Max Range              : %c\n",*((char*)getMax_range(structField_to_check)));
        fprintf(packetsLogFile,"   |-Min Range              : %c\n",*((char*)getMin_range(structField_to_check)));
        fprintf(packetsLogFile,"   |-Payload                : %c\n",*((char*)(raw_payload+*surfer)));
        fflush(packetsLogFile);
        if(*((char*)(raw_payload+*surfer))>*((char*)getMax_range(structField_to_check))||*((char*)(raw_payload+*surfer))<*((char*)getMin_range(structField_to_check)))
        {
           decision=0;
        }
        *surfer+=sizeof(char);
     }
    else if(strcmp("FLOAT",structField_to_check->type)==0)
     {
        printf("   |-Max Range              : %f\n",*((float*)getMax_range(structField_to_check)));
        printf("   |-Min Range              : %f\n",*((float*)getMin_range(structField_to_check)));
        printf("   |-Payload                : %f\n",floatFromBigEToLittleE(*((float*)(raw_payload+*surfer))));
        fprintf(packetsLogFile,"   |-Max Range              : %f\n",*((float*)getMax_range(structField_to_check)));
        fprintf(packetsLogFile,"   |-Min Range              : %f\n",*((float*)getMin_range(structField_to_check)));
        fprintf(packetsLogFile,"   |-Payload                : %f\n",floatFromBigEToLittleE(*((float*)(raw_payload+*surfer))));
        fflush(packetsLogFile);
       if(floatFromBigEToLittleE(*((float*)(raw_payload+*surfer)))>*((float*)getMax_range(structField_to_check))||floatFromBigEToLittleE(*((float*)(raw_payload+*surfer)))<*((float*)getMin_range(structField_to_check)))
        {
            decision=0;
        }
        *surfer+=sizeof(float);
     }

     else if(strcmp("DOUBLE",structField_to_check->type)==0)
     {
        printf("   |-Max Range              : %lf\n",*((double*)getMax_range(structField_to_check)));
        printf("   |-Min Range              : %lf\n",*((double*)getMin_range(structField_to_check)));
        printf("   |-Payload                : %lf\n",doubleFromBigEToLittleE(*((double*)(raw_payload+*surfer))));
        fprintf(packetsLogFile,"   |-Max Range              : %lf\n",*((double*)getMax_range(structField_to_check)));
        fprintf(packetsLogFile,"   |-Min Range              : %lf\n",*((double*)getMin_range(structField_to_check)));
        fprintf(packetsLogFile,"   |-Payload                : %lf\n",doubleFromBigEToLittleE(*((double*)(raw_payload+*surfer))));
        fflush(packetsLogFile);
       if(doubleFromBigEToLittleE(*((double*)(raw_payload+*surfer)))>*((double*)getMax_range(structField_to_check))||doubleFromBigEToLittleE(*((double*)(raw_payload+*surfer)))<*((double*)getMin_range(structField_to_check)))
        {
            decision=0;
        }
        *surfer+=sizeof(double);
     }
     else if(strcmp("SHORT",structField_to_check->type)==0)
     {
        printf("   |-Max Range              : %i\n",*((short*)getMax_range(structField_to_check)));
        printf("   |-Min Range              : %i\n",*((short*)getMin_range(structField_to_check)));
        printf("   |-Payload                : %i\n",shortFromBigEToLittleE(*((short*)(raw_payload+*surfer))));
        fprintf(packetsLogFile,"   |-Max Range              : %i\n",*((short*)getMax_range(structField_to_check)));
        fprintf(packetsLogFile,"   |-Min Range              : %i\n",*((short*)getMin_range(structField_to_check)));
        fprintf(packetsLogFile,"   |-Payload                : %i\n",shortFromBigEToLittleE(*((short*)(raw_payload+*surfer))));
        fflush(packetsLogFile);
       if(shortFromBigEToLittleE(*((short*)(raw_payload+*surfer)))>*((short*)getMax_range(structField_to_check))||shortFromBigEToLittleE(*((short*)(raw_payload+*surfer)))<*((short*)getMin_range(structField_to_check)))
        {
            decision=0;
        }
        *surfer+=sizeof(short);
     }
     else if(strcmp("LONG",structField_to_check->type)==0)
     {
        printf("   |-Max Range              : %ld\n",*((long*)getMax_range(structField_to_check)));
        printf("   |-Min Range              : %ld\n",*((long*)getMin_range(structField_to_check)));
        printf("   |-Payload                : %ld\n",longFromBigEToLittleE(*((long*)(raw_payload+*surfer))));
        fprintf(packetsLogFile,"   |-Max Range              : %ld\n",*((long*)getMax_range(structField_to_check)));
        fprintf(packetsLogFile,"   |-Min Range              : %ld\n",*((long*)getMin_range(structField_to_check)));
        fprintf(packetsLogFile,"   |-Payload                : %ld\n",longFromBigEToLittleE(*((long*)(raw_payload+*surfer))));
        fprintf(packetsLogFile,"\n\n");
        fflush(packetsLogFile);
       if(longFromBigEToLittleE(*((long*)(raw_payload+*surfer)))>*((long*)getMax_range(structField_to_check))||longFromBigEToLittleE(*((long*)(raw_payload+*surfer)))<*((long*)getMin_range(structField_to_check)))
        {
            decision=0;
        }
        *surfer+=sizeof(long);
     }
    return decision;
}


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
int check_pkt(__u16 dest_port,__u32 dest_ip,int payload_len,unsigned char *data_payload, struct iphdr *iph, struct udphdr *udph,struct nfq_data *nfa,int *surfer)
{

   unsigned char *raw_payload;
   int i,numberOfStructs,structCode;
   activeProtocol *active_protocol_to_check;
   structOfActiveProtocol *struct_of_active_protocol_to_check;
   structField *structField_to_check;

   //list* list_of_active_protocols=NULL;
   //list* list_of_structFields=NULL;
   i=0;
   int structSize=0;
   //readActiveProtocolsFromDB(&list_of_active_protocols);//READ ALL THE SQLITE DB"
   active_protocol_to_check=list_first(list_of_active_protocols);
   while(active_protocol_to_check)
   {
     raw_payload=data_payload + (iph->ihl * 4)+ sizeof(struct udphdr); //(iph->ihl * 4)=sizeof(Network layer),sizeof(struct udphdr)=sizeof(transport header)
     if(((dest_port)== getPort(active_protocol_to_check))&&(dest_ip==getIp(active_protocol_to_check))&&(getProtocolId(active_protocol_to_check)==intFromBigEToLittleE(((int*)raw_payload)[0])))
         {
           print_pkt(nfa);
           structCode=intFromBigEToLittleE(((int*)raw_payload)[1]);//to read 4 bytes, cast to int*-to read the struct code
           //structCode=1;//because I need to add in the struct the struct code i adeed it manualy
           printf("*******Struct Meta Data********:\n");
           printf("   |-Struct Code            : %d\n",structCode);
           fprintf(packetsLogFile,"*******Struct Meta Data********:\n");
           fflush(packetsLogFile);
           fprintf(packetsLogFile,"   |-Struct Code            : %d\n",structCode);
           fflush(packetsLogFile);

           struct_of_active_protocol_to_check=getStructByStructCode(active_protocol_to_check,structCode);
           //readAllStructFields(structCode,active_protocol_to_check->protocolId,&list_of_structFields,&structSize);

           //instead of using payload_len-sizeof(structCode)-(iph->ihl * 4)-sizeof(struct udphdr)
            printf("   |-Struct Size - Packet   : %lu\n",payload_len-(sizeof(int)*2)-(iph->ihl * 4)-sizeof(struct udphdr));
            printf("   |-Struct size - DB       : %d\n\n",struct_of_active_protocol_to_check->struct_size);
            printf("*************Fields************:\n");
            fprintf(packetsLogFile,"   |-Struct Size - Packet   : %lu\n",payload_len-(sizeof(int)*2)-(iph->ihl * 4)-sizeof(struct udphdr));
            fprintf(packetsLogFile,"   |-Struct size - DB       : %d\n\n",struct_of_active_protocol_to_check->struct_size);
            fprintf(packetsLogFile,"*************Fields************:\n");
            fflush(packetsLogFile);
            if(struct_of_active_protocol_to_check->struct_size != payload_len-(2*sizeof(structCode))-(iph->ihl * 4)-sizeof(struct udphdr))//payload_len=packet_payload+sizeof(structCode)-(iph->ihl * 4)-sizeof(struct udphdr)
            {                                                                                    //so to get to the raw payload i need to sub the headers from the payload data;
                return 0;
            }
            structField_to_check=list_first(struct_of_active_protocol_to_check->list_of_structsFields);
            while(structField_to_check)
            {

             if(checkValidationInProtocol(raw_payload+(2*sizeof(structCode)),structField_to_check,surfer)==0)
              {
                return 0;
              }
              structField_to_check=list_next(struct_of_active_protocol_to_check->list_of_structsFields);
            }
           }
      // }
       //TODO:release the minRange and maxRange pointers???
       active_protocol_to_check=list_next(list_of_active_protocols);
    }
    return 1;
}


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

int cb(struct nfq_q_handle *qh, struct nfgenmsg *nfmsg, struct nfq_data *nfa, void *data)
{
   int surfer=0;
   int decision;
	u_int32_t id,mark;
	int payload_len=0;
	__u16 dest_port;
	__u32 dest_ip;
    unsigned char *data_payload;
    struct iphdr *iph;
    struct udphdr *udph;
    struct nfqnl_msg_packet_hdr *ph;

    packetsLogFile=fopen("packetsLogFIle.txt","a");
    ph = nfq_get_msg_packet_hdr(nfa);
	if (ph)
	{
		id = ntohl(ph->packet_id);
	}

	mark=nfq_get_nfmark(nfa);
	payload_len = nfq_get_payload(nfa, (unsigned char**)&data_payload);//the payload_len includes the raw pyload+ip header+transport header(udp header)
	iph=(struct iphdr *)data_payload;
	if(iph->protocol == IPPROTO_UDP)
	{
	   udph=(struct udphdr*)(data_payload + (iph->ihl * 4));
       dest_port= udph->dest;
       dest_ip=iph->daddr;
       decision=check_pkt(dest_port,dest_ip,payload_len,data_payload,iph,udph,nfa,&surfer);
       if(decision==0)
       {
            fprintf(packetsLogFile,"\n                  Droped Pcket                 \n");
          	fprintf(packetsLogFile,"!*********************************************!\n");
          	fflush(packetsLogFile);
            printf("\n                  Droped Pcket                 \n");
          	printf("!*********************************************!\n");
            fclose(packetsLogFile);
            return nfq_set_verdict2(qh, id, NF_DROP, mark | 0xFFFFFFFF,0, NULL);
       }
    }

    printf("\n                  Accept Pcket                 \n");
    printf("!*********************************************!\n");
    fclose(packetsLogFile);
	return nfq_set_verdict2(qh, id, NF_ACCEPT, mark | 0xFFFFFFFF,0, NULL);
}
