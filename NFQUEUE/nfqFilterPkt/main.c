#include "nfqHandle.h"

//iptables rule
//sudo iptables -I INPUT -j NFQUEUE --queue-num 0
//sudo iptables -F



//how to install
//sudo apt update
//sudo apt install libnetfilter-queue-dev

//how to compile
//sudo gcc -o test main.c list.c sqliteCon.c activeProtocol.c nfqHandle.c structField.c -lnetfilter_queue -lsqlite3 -ljson-c
//sudo ./test


/*
static u_int32_t print_pkt (struct nfq_data *tb)
{
	int id = 0;
	struct nfqnl_msg_packet_hdr *ph;
	struct nfqnl_msg_packet_hw *hwph;
	u_int32_t mark,ifi;
	int ret;
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
		int i, hlen = ntohs(hwph->hw_addrlen);
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
	dest_port= udph->dest;
    dest_ip=iph->daddr;
    printf("dest_ip = %x\n",dest_ip);
    printf("dest_port = %x\n",dest_port);
    printf("transport protocol= %u\n",iph->protocol);
	fputc('\n', stdout);

	return id;
}


static int checkValidationInProtocol(unsigned char *raw_payload,protocol *protocol_to_check)
{
    static int surfer_on_payload=0;
   /* if(strcmp("INT",protocol_to_add->type)==0)
     {

     }
   else if(strcmp("CHAR",protocol_to_add->type)==0)
     {
       char* minRange;
       char* maxRange;
       minRange=(char*)malloc(sizeof(char));
       maxRange=(char*)malloc(sizeof(char));
       *minRange=sqlite3_column_text(stmt,3);
       *maxRange=sqlite3_column_text(stmt,4);
       protocol_to_add->min_range=(char*)&minRange;
       protocol_to_add->max_range=(char*)&maxRange;
     }
    else if(strcmp("FLOAT",protocol_to_add->type)==0)
     {
       float* minRange;
       float* maxRange;
       minRange=(float*)malloc(sizeof(float));
       maxRange=(float*)malloc(sizeof(float));
       *minRange=atof(sqlite3_column_text(stmt,3));
       *maxRange=atof(sqlite3_column_text(stmt,4));
       protocol_to_add->min_range=(float*)&minRange;
       protocol_to_add->max_range=(float*)&maxRange;
     }
    else if(strcmp("STRING",protocol_to_add->type)==0)
     {
       char* minRange;
       char* maxRange;
       minRange=(char*)malloc(sizeof(char));
       maxRange=(char*)malloc(sizeof(char));
       *minRange=sqlite3_column_text(stmt,3);
       *maxRange=sqlite3_column_text(stmt,4);
       protocol_to_add->min_range=(char*)&minRange;
       protocol_to_add->max_range=(char*)&maxRange;
     }

     else if(strcmp("BOOLEAN",protocol_to_add->type)==0)
     {
       int *minRange;
       int *maxRange;
       minRange=(int*)malloc(sizeof(int));
       maxRange=(int*)malloc(sizeof(int));
       *minRange=0;
       *maxRange=1;
       protocol_to_add->min_range=(int*)&minRange;
       protocol_to_add->max_range=(int*)&maxRange;
     }
     else if(strcmp("DOUBLE",protocol_to_add->type)==0)
     {
       double* minRange;
       double* maxRange;
       minRange=(double*)malloc(sizeof(double));//for allocation on the heap and not on the stack of the function
       maxRange=(double*)malloc(sizeof(double));
       *minRange=atod(sqlite3_column_text(stmt,3));
       *maxRange=atod(sqlite3_column_text(stmt,4));
       protocol_to_add->min_range=(double*)&minRange;
       protocol_to_add->max_range=(double*)&maxRange;
     }


}


static int check_pkt(__u16 dest_port,__u32 dest_ip,int payload_len,unsigned char *data_payload, struct iphdr *iph, struct udphdr *udph,struct nfq_q_handle *qh,u_int32_t id,u_int32_t mark,struct nfq_data *nfa)
{
   //list* list_of_protocols=list_create();
   //char minRange='1',maxRange='9';
   //protocol base_protocol={htons(8080),inet_addr("127.0.0.1"),(char*)&maxRange,(char*)&minRange};
   //list_add(list_of_protocols,&base_protocol);

   unsigned char *raw_payload;
   int i=0;
   protocol* protocol_to_check;
   list* list_of_protocols=NULL;

   readProtocolsFromDB(&list_of_protocols);//READ ALL THE SQLITE DB"
   protocol_to_check=list_first(list_of_protocols);

   while(protocol_to_check)
   {
     if(((dest_port)== getPort(protocol_to_check))&&(dest_ip==getIp(protocol_to_check)))
         {
           print_pkt(nfa);
           raw_payload=data_payload + (iph->ihl * 4)+ sizeof(struct udphdr);
           printf("max range: %c\n",*((char*)protocol_to_check->max_range));
           printf("min range: %c\n",*((char*)protocol_to_check->min_range));
           for(i=0;i<payload_len-(iph->ihl * 4)-sizeof(struct udphdr);i++)
           {
      if(check validationInProtocol)if(raw_payload[i]>*((char*)getMax_range(protocol_to_check))||raw_payload[i]<*((char*)getMin_range(protocol_to_check)))//TODO:change the method of droping to each protocol in the list
              {
                printf("DROP packet\n");
                return 0;
              }
               printf("payload[%d]=%x \n",i,raw_payload[i]);
           }
       }
       //TODO:release the minRange and maxRange pointers???
       protocol_to_check=list_next(list_of_protocols);
    }
    return 1;
}



static int cb(struct nfq_q_handle *qh, struct nfgenmsg *nfmsg, struct nfq_data *nfa, void *data)
{

    //list* list_of_protocols=list_create();
   //char minRange='1',maxRange='9';
   //protocol base_protocol={htons(8080),inet_addr("127.0.0.1"),(char*)&maxRange,(char*)&minRange};
   //list_add(list_of_protocols,&base_protocol);
   //unsigned char *raw_payload;
   //int i=0;
    //protocol* protocol_to_check=list_first(list_of_protocols);
   int decision;
	u_int32_t id,mark;
	int payload_len=0;
	__u16 dest_port;
	__u32 dest_ip;
    unsigned char *data_payload;
    struct iphdr *iph;
    struct udphdr *udph;
    struct nfqnl_msg_packet_hdr *ph;

   // print_pkt(nfa);


    ph = nfq_get_msg_packet_hdr(nfa);
	if (ph)
	{
		id = ntohl(ph->packet_id);
	}
	mark=nfq_get_nfmark(nfa);
	payload_len = nfq_get_payload(nfa, &data_payload);//the payload_len includes the raw pyload+ip header+transport header(udp header)
    //return nfq_set_verdict2(qh, id, NF_ACCEPT, mark | 0xFFFFFFFF,0, NULL);
	iph=(struct iphdr *)data_payload;
	if(iph->protocol == IPPROTO_UDP)
	{
	   udph=(struct udphdr*)(data_payload + (iph->ihl * 4));
       dest_port= udph->dest;
       dest_ip=iph->daddr;
       decision=check_pkt(dest_port,dest_ip,payload_len,data_payload,iph,udph,qh,id,mark,nfa);
       if(decision==0)
       {
          return nfq_set_verdict2(qh, id, NF_DROP, mark | 0xFFFFFFFF,0, NULL);
       }
     /* while(protocol_to_check)
     {
      if(((dest_port)== protocol_to_check->dest_port)&&(dest_ip==protocol_to_check->dest_ip))
       {
           print_pkt(nfa);
           raw_payload=data_payload + (iph->ihl * 4)+ sizeof(struct udphdr);
           for(i=0;i<payload_len-(iph->ihl * 4)-sizeof(struct udphdr);i++)
           {
             if(raw_payload[i]>*((char*)protocol_to_check->max_range)||raw_payload[i]<*((char*)protocol_to_check->min_range))
              {
                printf("DROP packet\n");
               return nfq_set_verdict2(qh, id, NF_DROP, mark | 0xFFFFFFFF,0, NULL);
              }
               printf("payload[%d]=%x \n",i,raw_payload[i]);
           }
       }
       protocol_to_check=list_next(list_of_protocols);
	}
	return nfq_set_verdict2(qh, id, NF_ACCEPT, mark | 0xFFFFFFFF,0, NULL);
    }
}





static int cb_orig(struct nfq_q_handle *qh, struct nfgenmsg *nfmsg, struct nfq_data *nfa, void *data)
{

    //puts("pkt received\n");
	u_int32_t id,mark;
	int payload_len=0,i=0;
	__u16 dest_port;
	__u32 dest_ip;
    unsigned char *data_payload;
    unsigned char *raw_payload;
    struct iphdr *iph;
    struct udphdr *udph;
    struct nfqnl_msg_packet_hdr *ph;
    // Lior Code   ----------------------------------------------
    	//struct nfqnl_msg_packet_hdr *ph;
	int decision;
	// id=0;

	ph = nfq_get_msg_packet_hdr(nfa);
	if (ph)
	{
		id = ntohl(ph->packet_id);
	}

	/* check if we should block this packet
//	decision = pkt_decision(nfa);
//	if( decision == PKT_ACCEPT)
//	{
	//return nfq_set_verdict2(qh, id, NF_ACCEPT, nfq_get_nfmark(nfa) | 0xFFFFFFFF,0, NULL);
	//return nfq_set_verdict2(qh, id, NF_DROP, nfq_get_nfmark(nfa) | 0xFFFFFFFF,0, NULL);
//	}




       //return nfq_set_verdict(qh, id, NF_ACCEPT, 0, NULL);

    //  Lior code End
    print_pkt(nfa);
    //return nfq_set_verdict2(qh, id, NF_ACCEPT, nfq_get_nfmark(nfa) | 0xFFFFFFFF,0, NULL);
	//ph = nfq_get_msg_packet_hdr(nfa);
	//id = ntohl(ph->packet_id);
	mark=nfq_get_nfmark(nfa);
	payload_len = nfq_get_payload(nfa, &data_payload);//the payload_len includes the raw pyload+ip header+transport header(udp header)
	iph=(struct iphdr *)data_payload;
	printf("iph->protocol= %u\n",iph->protocol);
	printf(" IPPROTO_UDP= %u\n",IPPROTO_UDP);
	return nfq_set_verdict2(qh, id, NF_ACCEPT, nfq_get_nfmark(nfa) | 0xFFFFFFFF,0, NULL);
	if(iph->protocol == IPPROTO_UDP)
	{
	   puts("its udp pkt");
	   udph=(struct udphdr*)(data_payload + (iph->ihl * 4));
       dest_port= udph->dest;
       dest_ip=iph->daddr;
       printf("dest_ip = %x\n",dest_ip);
       printf("dest_port = %x\n",dest_port);
       //return nfq_set_verdict2(qh, id, NF_ACCEPT, nfq_get_nfmark(nfa) | 0xFFFFFFFF,0, NULL);
       //decision=check_pkt(dest_port,dest_ip,payload_len,data_payload,iph,udph,qh,id,mark);//not return from the check func
       //printf("after check pkt");
      // if(decision==0)
       //{
      //   return nfq_set_verdict2(qh, id, NF_DROP, nfq_get_nfmark(nfa) | 0xFFFFFFFF,0, NULL);
       //}
      // return nfq_set_verdict2(qh, id, NF_ACCEPT, nfq_get_nfmark(nfa) | 0xFFFFFFFF,0, NULL);
       if(((dest_port)== htons(8080))&&(dest_ip== inet_addr("127.0.0.1")))
       {
           puts("pkt received\n");
           print_pkt(nfa);
           printf("\niph->daddr = %x\n",dest_ip);
           printf("dest_port = %x\n",dest_port);
           printf("payload_len=%d\n",payload_len);
           raw_payload=data_payload + (iph->ihl * 4)+ sizeof(struct udphdr);
           for(i=0;i<payload_len-(iph->ihl * 4)-sizeof(struct udphdr);i++)
           {
              if(raw_payload[i]>'9'||raw_payload[i]<'1')
              {
                printf("in NF_DROP\n");
               return nfq_set_verdict2(qh, id, NF_DROP, mark | 0xFFFFFFFF,0, NULL);
              }
               printf("payload[%d]=%x \n",i,raw_payload[i]);
           }
       }

	}
	//printf("entering callback\n");
	return nfq_set_verdict2(qh, id, NF_ACCEPT, mark | 0xFFFFFFFF,0, NULL);
}
*/
static void prepared_line_file(char* line)
{
    FILE* fptr;
    fptr = fopen("nfqFile1.json", "at");
    fwrite(line,sizeof(char),strlen(line),fptr);

    // fprintf(fptr, "%s", line); // would work too

    // The file should be closed when everything is written
    fclose(fptr);
}

int main()
{
     char *serialized_json;

    // Create an empty object : {}
    json_object * jobj = json_object_new_object();
    // Create an empty int
    json_object *jint = json_object_new_int(9);
    json_object *jint2 = json_object_new_int(180);

    // Put the array into the object : { "USER" : [] }
    json_object_object_add(jobj,"age", jint);
    json_object_object_add(jobj,"height",jint2);

    // Transforms the binary representation into a string representation
    serialized_json = json_object_to_json_string(jobj);
    prepared_line_file(serialized_json);

    startNfq();
}

