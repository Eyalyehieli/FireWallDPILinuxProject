#include "nfqHandle.h"

#define CAPACITY 100
FILE *logFile;
void startNfq()
{
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

u_int32_t print_pkt (struct nfq_data *tb)
{
	int id = 0;
	struct nfqnl_msg_packet_hdr *ph;
	struct nfqnl_msg_packet_hw *hwph;
	u_int32_t mark,ifi;
	int ret,i;
	__u16 dest_port;
	__u32 dest_ip;
	struct iphdr *iph;
	struct udphdr *udph;
	unsigned char *data;
	printf("-------------------------------");
	ph = nfq_get_msg_packet_hdr(tb);
	if (ph) {
		id = ntohl(ph->packet_id);
		printf("\nhw_protocol=0x%04x\nhook=%u\nid=%u\n",ntohs(ph->hw_protocol), ph->hook, id);
	}

	hwph = nfq_get_packet_hw(tb);
	if (hwph)
	{
        int hlen = ntohs(hwph->hw_addrlen);
		printf("hw_src_addr=");
		for (i = 0; i < hlen-1; i++)
			printf("%02x:", hwph->hw_addr[i]);
		printf("%02x\n", hwph->hw_addr[hlen-1]);
	}

	mark = nfq_get_nfmark(tb);
	if (mark)
		printf("mark=%u\n", mark);

	ifi = nfq_get_indev(tb);
	if (ifi)
		printf("indev=%u\n", ifi);

	ifi = nfq_get_outdev(tb);
	if (ifi)
		printf("outdev=%u\n", ifi);
	ifi = nfq_get_physindev(tb);
	if (ifi)
		printf("physindev=%u\n", ifi);

	ifi = nfq_get_physoutdev(tb);
	if (ifi)
		printf("physoutdev=%u\n", ifi);

	ret = nfq_get_payload(tb, &data);
	if (ret >= 0) {
		printf("payload_len=%d\n", ret);
		//processPacketData (data, ret);
	}

	iph=(struct iphdr *)data;
	udph=(struct udphdr*)(data + (iph->ihl * 4));
	for(i=0;i<ret-(iph->ihl * 4)-sizeof(struct udphdr);i++)
	{
        printf("pylaod[%d]= %x\n",i,data + (iph->ihl * 4) + sizeof(struct udphdr));
	}
	dest_port= udph->dest;
    dest_ip=iph->daddr;
    printf("dest_ip = %x\n",dest_ip);
    printf("dest_port = %x\n",dest_port);
    printf("transport protocol= %u\n",iph->protocol);
	fputc('\n', stdout);

	return id;
}


int checkValidationInProtocol(unsigned char *raw_payload,structField *structField_to_check,int *surfer)
{
    //static int surfer=0;
    printf("----------------------\n");
    int i;
    int decision=1;
    printf("surfer= %d\n",*surfer);
    printf("type= %s\n",getType(structField_to_check));
   if(strcmp("INT",structField_to_check->type)==0)
     {
        printf("max range: %d\n",*((int*)getMax_range(structField_to_check)));
        printf("min range: %d\n",*((int*)getMin_range(structField_to_check)));
        printf("pyload= %d\n",*((int*)(raw_payload+*surfer)));
        if(*((int*)(raw_payload+*surfer))>*((int*)getMax_range(structField_to_check))||*((int*)(raw_payload+*surfer))<*((int*)getMin_range(structField_to_check)))
        {
            decision=0;
        }
        *surfer+=sizeof(int);
     }
   else if(strcmp("CHAR",structField_to_check->type)==0)
     {
        printf("max range: %c\n",*((char*)getMax_range(structField_to_check)));
        printf("min range: %c\n",*((char*)getMin_range(structField_to_check)));
        printf("pyload= %c\n",*((char*)(raw_payload+*surfer)));
        if(*((char*)(raw_payload+*surfer))>*((char*)getMax_range(structField_to_check))||*((char*)(raw_payload+*surfer))<*((char*)getMin_range(structField_to_check)))
        {
           decision=0;
        }
        *surfer+=sizeof(char);
     }
    else if(strcmp("FLOAT",structField_to_check->type)==0)
     {
        printf("max range: %f\n",*((float*)getMax_range(structField_to_check)));
        printf("min range: %f\n",*((float*)getMin_range(structField_to_check)));
        printf("pyload= %f\n",*((float*)(raw_payload+*surfer)));
       if(*((float*)(raw_payload+*surfer))>*((float*)getMax_range(structField_to_check))||*((float*)(raw_payload+*surfer))<*((float*)getMin_range(structField_to_check)))
        {
            decision=0;
        }
        *surfer+=sizeof(float);
     }

     else if(strcmp("DOUBLE",structField_to_check->type)==0)
     {
        printf("max range: %lf\n",*((double*)getMax_range(structField_to_check)));
        printf("min range: %lf\n",*((double*)getMin_range(structField_to_check)));
        printf("pyload= %lf\n",*((double*)(raw_payload+*surfer)));
       if(*((double*)(raw_payload+*surfer))>*((double*)getMax_range(structField_to_check))||*((double*)(raw_payload+*surfer))<*((double*)getMin_range(structField_to_check)))
        {
            decision=0;
        }
        *surfer+=sizeof(double);
     }
     else if(strcmp("SHORT",structField_to_check->type)==0)
     {
        printf("max range: %i\n",*((short*)getMax_range(structField_to_check)));
        printf("min range: %i\n",*((short*)getMin_range(structField_to_check)));
        printf("pyload= %i\n",*((short*)(raw_payload+*surfer)));
       if(*((short*)(raw_payload+*surfer))>*((short*)getMax_range(structField_to_check))||*((short*)(raw_payload+*surfer))<*((short*)getMin_range(structField_to_check)))
        {
            decision=0;
        }
        *surfer+=sizeof(short);
     }
     else if(strcmp("LONG",structField_to_check->type)==0)
     {
        printf("max range: %ld\n",*((long*)getMax_range(structField_to_check)));
        printf("min range: %ld\n",*((long*)getMin_range(structField_to_check)));
        printf("pyload= %ld\n",*((long*)(raw_payload+*surfer)));
       if(*((long*)(raw_payload+*surfer))>*((long*)getMax_range(structField_to_check))||*((long*)(raw_payload+*surfer))<*((long*)getMin_range(structField_to_check)))
        {
            decision=0;
        }
        *surfer+=sizeof(long);
     }
    fputs("End of checkValidationInProtocol\n",logFile);
    fflush(logFile);
    return decision;
}


int check_pkt(__u16 dest_port,__u32 dest_ip,int payload_len,unsigned char *data_payload, struct iphdr *iph, struct udphdr *udph,struct nfq_q_handle *qh,u_int32_t id,u_int32_t mark,struct nfq_data *nfa,int *surfer)
{
   //list* list_of_protocols=list_create();
   //char minRange='1',maxRange='9';
   //protocol base_protocol={htons(8080),inet_addr("127.0.0.1"),(char*)&maxRange,(char*)&minRange};
   //list_add(list_of_protocols,&base_protocol);
   unsigned char *raw_payload;
   int i,numberOfStructs,structCode;
   activeProtocol *active_protocol_to_check;
   structField *structField_to_check;
   list* list_of_active_protocols=NULL;
   list* list_of_structFields=NULL;
   int structSize=0;
    static countPacket=0;
   readActiveProtocolsFromDB(&list_of_active_protocols);//READ ALL THE SQLITE DB"
   fputs("End of readActiveProtocolsFromDB\n",logFile);
   fflush(logFile);
   //fsync(logFile);
   active_protocol_to_check=list_first(list_of_active_protocols);

   i=0;
   while(active_protocol_to_check)
   {

     if(((dest_port)== getPort(active_protocol_to_check))&&(dest_ip==getIp(active_protocol_to_check)))
         {
           fprintf(logFile,"dest port =%x and dest ip =%x\n",getPort(active_protocol_to_check),getIp(active_protocol_to_check));
           fflush(logFile);
           countPacket++;
           fprintf(logFile,"count packet %d\n",countPacket);
           fflush(logFile);
           print_pkt(nfa);
           raw_payload=data_payload + (iph->ihl * 4)+ sizeof(struct udphdr); //(iph->ihl * 4)=sizeof(Network layer),sizeof(struct udphdr)=sizeof(transport header)
           structCode=((int*)raw_payload)[0];//to read 4 bytes, cast to int*-to read the struct code
           //structCode=1;//because I need to add in the struct the struct code i adeed it manualy
           printf("struct code= %d\n",structCode);
           readAllStructFields(structCode,active_protocol_to_check->protocolId,&list_of_structFields,&structSize);
           fputs("End of readAllStructFields\n",logFile);
           fflush(logFile);
           //fsync(logFile);
           //structSize-=1;
           //instead of using payload_len-sizeof(structCode)-(iph->ihl * 4)-sizeof(struct udphdr)
            printf("struct size by packet= %d\n",payload_len-sizeof(structCode)-(iph->ihl * 4)-sizeof(struct udphdr));
            printf("struct size by var= %d\n",structSize);
            fprintf(logFile,"struct size %d\n",structSize);
            fflush(logFile);
            if(structSize != payload_len-sizeof(structCode)-(iph->ihl * 4)-sizeof(struct udphdr))//payload_len=packet_payload+sizeof(structCode)-(iph->ihl * 4)-sizeof(struct udphdr)
            {                                                                                    //so to get to the raw payload i need to sub the headers from the payload data;
                return 0;
            }
            structField_to_check=list_first(list_of_structFields);
            while(structField_to_check)
            {

             if(checkValidationInProtocol(raw_payload+sizeof(structCode),structField_to_check,surfer)==0)
              {
                return 0;
              }
              structField_to_check=list_next(list_of_structFields);
            }
           }
      // }
       //TODO:release the minRange and maxRange pointers???
       active_protocol_to_check=list_next(list_of_active_protocols);
    }
    fputs("End of check_pkt\n",logFile);
    fflush(logFile);
    return 1;
}



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

   logFile=fopen("logFile.txt","w");
   fputs("file created\n",logFile);
   fflush(logFile);
   // print_pkt(nfa);
    ph = nfq_get_msg_packet_hdr(nfa);
	if (ph)
	{
		id = ntohl(ph->packet_id);
	}
	mark=nfq_get_nfmark(nfa);
	payload_len = nfq_get_payload(nfa, (unsigned char**)&data_payload);//the payload_len includes the raw pyload+ip header+transport header(udp header)
    //return nfq_set_verdict2(qh, id, NF_ACCEPT, mark | 0xFFFFFFFF,0, NULL);
	iph=(struct iphdr *)data_payload;
	if(iph->protocol == IPPROTO_UDP)
	{
	   udph=(struct udphdr*)(data_payload + (iph->ihl * 4));
       dest_port= udph->dest;
       dest_ip=iph->daddr;
       decision=check_pkt(dest_port,dest_ip,payload_len,data_payload,iph,udph,qh,id,mark,nfa,&surfer);
       if(decision==0)
       {
          printf("DROP packet\n");
          fputs("DROP packet\n",logFile);
          fflush(logFile);
          fclose(logFile);
          return nfq_set_verdict2(qh, id, NF_DROP, mark | 0xFFFFFFFF,0, NULL);
       }
    }

    printf("ACCEPT packet\n");
    fputs("ACCEPT packet\n",logFile);
    fflush(logFile);
    fclose(logFile);
	return nfq_set_verdict2(qh, id, NF_ACCEPT, mark | 0xFFFFFFFF,0, NULL);
}
