#include <linux/version.h>
#include <linux/kernel.h>
#include <linux/module.h>
#include <linux/netfilter.h>
#include <linux/netfilter_ipv4.h>
#include <linux/ip.h>
#include <linux/tcp.h>
#include <linux/udp.h>
#include <linux/string.h>
#include <linux/init.h>
#include <linux/fs.h>
#include <linux/cdev.h>
#include <linux/inet.h>
#define PORT 8080
#define SIZE_INFECTION 4
#define DATA_INFECTION 10
//TODO:infect the packet
MODULE_LICENSE("Dual BSD/GPL");
static dev_t my_dev = 0;
static struct cdev *my_cdev = NULL;
static struct nf_hook_ops nfho;
static const int infectionMethod=1;        //struct holding set of hook function options
//function to be called by hook


/*!@*****************************************************************************
 *!
 *! FUNCTION:			 compute_udp_checksum
 *!
 *! GENERAL DESCRIPTION: compute the udp checksum
 *!
 *! Input:				 iphdr packet,updhdr packet as ipPayload
 *!
 *! Output:             udp checksum
 *!
 *! ALGORITHM:			The algorithm is base on the following operations:
 *!                     1.sum the source address,dest address,IPPROTO(tcp,udp,icmp...),udphdr len
 *!
 *! ASSUMPTIONS:		none.
 *! REMARKS:			none.
 *!
 *!*****************************************************************************
 *!@*/
unsigned short compute_udp_checksum(struct iphdr *pIph, unsigned short *ipPayload)
 {
    register unsigned long sum = 0;
    struct udphdr *udphdrp = (struct udphdr*)(ipPayload);
    unsigned short udpLen = htons(udphdrp->len);
    //add sorce ip
    sum += (pIph->saddr>>16)&0xFFFF;
    sum += (pIph->saddr)&0xFFFF;
    //add dest ip
    sum += (pIph->daddr>>16)&0xFFFF;
    sum += (pIph->daddr)&0xFFFF;
    // add protocol and reserved: 17
    sum += htons(IPPROTO_UDP);
    //add length
    sum += udphdrp->len;

    //add the IP payload
    //printf("add ip payloadn");
    //initialize checksum to 0
    /*udphdrp->check = 0;
    while (udpLen > 1) {
        sum += * ipPayload++;
        udpLen -= 2;
    }
    //if any bytes left, pad the bytes and add
    if(udpLen > 0) {
        //printf("+++++++++++++++padding: %dn", udpLen);
        sum += ((*ipPayload)&htons(0xFF00));
    }
      //Fold sum to 16 bits: add carrier to result
    //printf("add carriern");
      while (sum>>16) {
          sum = (sum & 0xffff) + (sum >> 16);
      }
    //printf("one's complementn");
      sum = ~sum;
    //set computation result
   // udphdrp->check = ((unsigned short)sum == 0x0000)?0xFFFF:(unsigned short)sum;*/
   return ((unsigned short)sum == 0x0000)?0xFFFF:(unsigned short)sum;
}

/*!@*****************************************************************************
 *!
 *! FUNCTION:			 infectionByData
 *!
 *! GENERAL DESCRIPTION: infection the raw data of the packet
 *!
 *! Input:				 socket buffer,udphdr,ihdr,payload
 *!
 *! Output:             none.
 *!
 *! ALGORITHM:			The algorithm is base on the following operations:
 *!                     1.change the payload data by mul,div,plus.minus
 *!
 *! ASSUMPTIONS:		none.
 *! REMARKS:			none.
 *!
 *!*****************************************************************************
 *!@*/
void infectionByData(struct sk_buff *skb,struct udphdr *udph,struct iphdr *iph, unsigned char* payload)
{

    printk(KERN_ALERT "myMITM inside hook function after idntify UDP\n");
    printk("myMITM udph->dest = %d\n",udph->dest);
    printk("myMITM iph->saddr = %x\n",iph->saddr);
    payload=skb->data+(iph->ihl * 4)+sizeof(udph);
    printk("payload is %d",ntohl(*(((int*)payload)+2)));

    //------change UDP data,using htonl function to add bigEndian number---------------//
    //------because the packet sent from java application and java is bigEndian-------//
    *(((int*)payload)+2)+=htonl(DATA_INFECTION);

    printk( KERN_ALERT "myMITM change payload\n");
    printk("payload is %d",ntohl(*(((int*)payload)+2)));
}


/*!@*****************************************************************************
 *!
 *! FUNCTION:			 infectionBySize
 *!
 *! GENERAL DESCRIPTION: infection the size of the packet
 *!
 *! Input:				socket buffer,udphdr,ihdr
 *!
 *! Output:             none.
 *!
 *! ALGORITHM:			The algorithm is base on the following operations:
 *!                     1.change the payload size using skb_trim function from <linux/netfilter.h>
 *!                     2.change the iphdr->tot_len size and udphdr->len size properly
 *!                     3. compute the checksum of ip and udp headers according to the new length
 *!
 *! ASSUMPTIONS:		none.
 *! REMARKS:			none.
 *!infectionMethod
 *!*****************************************************************************
 *!@*/
void infectionBySize(struct sk_buff *skb,struct udphdr *udp_hdr,struct iphdr *ip_hdr)
{
    skb_trim(skb, skb->len-SIZE_INFECTION);
    udp_hdr->len=htons(ntohs(udp_hdr->len)-SIZE_INFECTION);
    ip_hdr->tot_len=htons(ntohs(ip_hdr->tot_len)-SIZE_INFECTION);


    ip_hdr->check=0;
    ip_hdr->check=ip_fast_csum((unsigned char*)ip_hdr, ip_hdr->ihl);
	udp_hdr->check=compute_udp_checksum(ip_hdr,(unsigned short*)udp_hdr);

}


/*!@*****************************************************************************
 *!
 *! FUNCTION:			 hook_func
 *!
 *! GENERAL DESCRIPTION: check if the packet is the packet to infect and choose infection
 *!                      method
 *!
 *! Input:				constant prototype(I'm using only the socket buffer-skb)
 *!
 *! ALGORITHM:			The algorithm is base on the following operations:
 *!                     1.extractinfectionMethod ethdr, iphdr and udphdr
 *!                     2.check if the packet id the packet to infect by port and ip
 *!                     3.choose infection method and infect the packet
 *!
 *! ASSUMPTIONS:		none.
 *! REMARKS:			none.
 *!
 *!*****************************************************************************
 *!@*/
unsigned int hook_func(void *priv, struct sk_buff *skb, const struct nf_hook_state *state)
{

    unsigned char *paylaod_data=NULL;
    struct udphdr *udph;
    struct iphdr *iph;
    struct ethhdr *eth;
    int payload_len=0;
    unsigned char *tail;
    struct sk_buff *new_sk_buff;
    int infectionSize=SIZE_INFECTION;
    if(skb)
    {
        eth=(struct ethhdr *)skb_mac_header(skb);
        iph = (struct iphdr *)skb_network_header(skb);
        if(iph->protocol==IPPROTO_UDP)
        {
            if(iph->daddr==in_aton("127.0.0.1"))
            {
                udph=(struct udphdr*)skb_transport_header(skb);
                printk("iphr->tot_len= %d", htons(iph->tot_len));
                printk("udph->len= %d",htons(udph->len));
                if(udph->dest==htons(9000))
                {
                    switch(infectionMethod)
                    {
                       case 0:infectionByData(skb,udph,iph,paylaod_data);break;
                       case 1:infectionBySize(skb,udph,iph);break;
                       //case 1:infectionBySize(skb,udph,iph,eth,paylaod_data,payload_len);break;//if its to change the size
                                                                                                    //i want to drop the old sk_buff and send the new(infected) sk_buff
                    }
                }
            }

        }
	}
	return NF_ACCEPT; //this will accept the packet
}



/*!@*****************************************************************************
 *!
 *! FUNCTION:			 __init nf_hook_init
 *!
 *! GENERAL DESCRIPTION: module function subscribed on Network Card device
 *!
 *! Input:				none.
 *!
 *! ALGORITHM:			The algorithm is base on the following operations:
 *!                     1.subscribing on Network Card device by module code struct
 *!
 *! ASSUMPTIONS:		none.
 *! REMARKS:			Called when module loaded using 'insmod'
 *!
 *!*****************************************************************************
 *!@*/

static int __init nf_hook_init(void)
{
  int ret = 0;
  int res=0;
  res = alloc_chrdev_region(&my_dev, 0, 1, "myMITM");
  if (res < 0)
    goto register_failed;

  printk(KERN_ALERT "myMITM registered\n");
  my_cdev = cdev_alloc();
  if (NULL == my_cdev)
  {
    res = -ENOMEM;
    goto cdev_fail;
  }
#if LINUX_VERSION_CODE >= KERNEL_VERSION(4,13,0)
    struct net *n;
#endif
  nfho.hook = hook_func;                       //function to call when conditions below met
  nfho.hooknum = NF_INET_PRE_ROUTING;            //called right after packet recieved, first hook in Netfilter
  nfho.pf = PF_INET;                           //IPV4 packets
  nfho.priority = NF_IP_PRI_FIRST;             //set to highest priority over all other hook functions
#if LINUX_VERSION_CODE >= KERNEL_VERSION(4,13,0)
    for_each_net(n)
        ret += nf_register_net_hook(n, &nfho);
#else
    ret = nf_register_hook(&nfho);
#endif
	printk(KERN_ALERT "HII I am the hook in my init MITM\r\n");
	return 0;
    cdev_fail:
    printk(KERN_ALERT "mtyMITM registration failed... unregistering\n");
    unregister_chrdev_region(my_dev, 1);
    register_failed:
    return res;
}

//Called when module unloaded using 'rmmod'
static void __exit nf_hook_exit(void)
{
#if LINUX_VERSION_CODE >= KERNEL_VERSION(4,13,0)
    struct net *n;
    for_each_net(n)
        nf_unregister_net_hook(n, &nfho);
#else
    nf_unregister_hook(&nfho);
#endif
	printk(KERN_INFO "BYEBYE from hook\r\n");
}


module_init(nf_hook_init);
module_exit(nf_hook_exit);
MODULE_LICENSE("Dual BSD/GPL");
