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
#define SUB_SIZE 4
//TODO:infect the packet
MODULE_LICENSE("Dual BSD/GPL");
static dev_t my_dev = 0;
static struct cdev *my_cdev = NULL;
static struct nf_hook_ops nfho;
static const int infectionMethod=1;        //struct holding set of hook function options
//function to be called by hook

void infectionByData(struct sk_buff *skb,struct udphdr *udph,struct iphdr *iph, unsigned char* payload)
{
    //struct udphdr *udph;
    //struct iphdr *iph;
    //unsigned char* payload;
    //printk(KERN_ALERT "myMITM inside hook function after idntify UDP\n");
		//iph = (struct iphdr *)skb_network_header(skb);
		//if(iph->protocol==IPPROTO_UDP)
	    //{
           printk(KERN_ALERT "myMITM inside hook function after idntify UDP\n");
          // udph=(struct udphdr*)skb_transport_header(skb);
           printk("myMITM udph->dest = %d\n",udph->dest);
           //printk("myMITM udph->source = %d\n",udph->source);
           printk("myMITM iph->saddr = %x\n",iph->saddr);
           //printk("myMITM iph->daddr = %x\n",iph->daddr);

           //printk("dest_port = %x\n",udph->dest);
           //printk("udph->dest = %x\n",udph->dest);
              payload=skb->data+(iph->ihl * 4)+sizeof(udph);

			  //printk("myMITM  skb->tail %p\n",skb->tail);
			/*  for (i=0;i<9;i++)
			     printk("payload[%d]=%x \n",i,payload[i]);
			*/
			printk("payload is %d",*((int*)payload));
              // change UDP data
              *((int*)payload+1)+=1;
              /*
              for (i=0;i<9;i++)
              {
			     payload[i]=payload[i] + 1 ;
              }*/
              printk("myMITM change payload\n");
              /*
              for (i=0;i<9;i++)
			     printk("payload[%d]=%x \n",i,payload[i]);
			     */
			     printk("payload is %d",*((int*)payload));

}

void infectionBySizeOld(struct sk_buff *skb1,struct udphdr *udph,struct iphdr *iph,struct ethhdr *eth,unsigned char *payload,int payload_len)
{
    unsigned char* data;
    int i=0;
    __wsum partial;

    //GET NET DEVICE

    unsigned char mac_source_char[ETH_ALEN]={};
    unsigned char mac_dest_char[ETH_ALEN]={};
    struct net_device *enp0s3;
    char addr[ETH_ALEN] = {0x00,0x00,0x00,0x00,0x00,0x00};
	uint8_t dest_addr[ETH_ALEN];
	uint8_t source_addr[ETH_ALEN];
    enp0s3 = dev_get_by_name(&init_net,"enp0s3");
    memcpy(dest_addr, addr,ETH_ALEN);
    memcpy(source_addr,addr,ETH_ALEN);
	/* Skb */
    struct sk_buff* skb = alloc_skb(ETH_HLEN+payload_len+sizeof(struct udphdr)+sizeof(struct iphdr), GFP_ATOMIC);//allocate a network buffer
    skb->dev = enp0s3;
    //dev_hard_start_xmit(skb,lo);
    //lo->hard_start_xmit=hard_start_xmit(skb,lo);
    skb->pkt_type = PACKET_OUTGOING;
    skb->protocol = htons(ETH_P_IP);
    skb->no_fcs = 1;
    skb->ip_summed = CHECKSUM_NONE;
    skb->priority = 0;
    skb->next = skb->prev = NULL;
    skb_reserve(skb, ETH_HLEN+sizeof(struct iphdr)+sizeof(struct udphdr));//adjust headroom

    /* Allocate space to data and write it */
    data = skb_put(skb,payload_len);
    memcpy(data, payload, payload_len);

    /* UDP header */
    struct udphdr* udp_hdr = (struct udphdr*)skb_push(skb,sizeof(struct udphdr));
    udp_hdr->len = htons(payload_len+sizeof(struct udphdr));
    udp_hdr->source = udph->source;
    udp_hdr->dest = udph->dest;
    udp_hdr->check = 0;
    printk("udp_hdr->len %hu",ntohs(udp_hdr->len));
    printk("udp_hdr->source %hu",ntohs(udp_hdr->source));
    printk("udp_hdr->dest %hu",ntohs(udp_hdr->dest));

    /* IP header */
    struct iphdr* ip_hdr = (struct iphdr*)skb_push(skb,sizeof(struct iphdr));
    ip_hdr->ihl =iph->ihl;//4*5=20 ip_header_len
    ip_hdr->version = iph->version; // IPv4u
    ip_hdr->tos = iph->tos;
    ip_hdr->tot_len=htons(payload_len+sizeof(struct udphdr)+sizeof(struct iphdr));
    ip_hdr->id=iph->id;
    ip_hdr->frag_off = iph->frag_off;
    ip_hdr->ttl = iph->ttl; // Set a TTL.
    ip_hdr->protocol = iph->protocol; //  protocol.
    //ip_hdr->check = iph->check;
    ip_hdr->saddr = iph->saddr;
    ip_hdr->daddr = iph->daddr;


    printk("ip_hdr->saddr %x",ip_hdr->saddr);
    printk("ip_hdr->daddr %x",ip_hdr->daddr);



  /* Mac address */
    struct ethhdr* eth_hdr = (struct ethhdr*)skb_push(skb, sizeof (struct ethhdr));//add data to the start of a buffer
    eth_hdr->h_proto = htons(ETH_P_IP);
    //memcpy(eth_hdr->h_source,skb1->data, ETH_ALEN);
    //memcpy(eth_hdr->h_dest, skb1->data+6, ETH_ALEN);
    memcpy(eth_hdr->h_source,dest_addr, ETH_ALEN);
    memcpy(eth_hdr->h_dest,source_addr, ETH_ALEN);
    printk("eth_hdr->h_source :");
    printk("Source MAC=%x:%x:%x:%x:%x:%x\n",eth_hdr->h_source[0],eth_hdr->h_source[1],eth_hdr->h_source[2],eth_hdr->h_source[3],eth_hdr->h_source[4],eth_hdr->h_source[5]);
    printk("Dest MAC=%x:%x:%x:%x:%x:%x\n",eth_hdr->h_dest[0],eth_hdr->h_dest[1],eth_hdr->h_dest[2],eth_hdr->h_dest[3],eth_hdr->h_dest[4],eth_hdr->h_dest[5]);

  /* caculate checksum */
    skb->csum=0;
	skb->csum = skb_checksum(skb, ip_hdr->ihl*4, skb->len-ip_hdr->ihl*4, 0);
	ip_hdr->check=0;
	ip_hdr->check = ip_fast_csum((unsigned char*)ip_hdr, ip_hdr->ihl);
    partial=csum_partial((unsigned char *)udp_hdr,udp_hdr->len,0);
    udp_hdr->check=0;
	udp_hdr->check =csum_tcpudp_magic(ip_hdr->saddr, ip_hdr->daddr, /*skb->len-ip_hdr->ihl*4*/udp_hdr->len, IPPROTO_UDP, /*skb->csum*/partial);

     if (dev_queue_xmit(skb)<0)
      {
                dev_put(enp0s3);
                kfree_skb(skb);
                printk("send packet by skb failed.\n");
                return;
      }
        printk("send packet by skb success.\n");

}

void infectionBySize(struct sk_buff *skb,struct udphdr *udp_hdr,struct iphdr *ip_hdr,int infectionSize)
{
    __wsum partial;
    skb_trim(skb, skb->len - infectionSize);
    ip_hdr->tot_len=htons(ntohs(ip_hdr->tot_len)-infectionSize);
    udp_hdr->len = htons(ntohs(udp_hdr->len )-infectionSize);
    skb->csum=0;
	skb->csum = skb_checksum(skb, ip_hdr->ihl*4, skb->len-ip_hdr->ihl*4, 0);
	ip_hdr->check=0;
	ip_hdr->check = ip_fast_csum((unsigned char*)ip_hdr, ip_hdr->ihl);
    partial=csum_partial((unsigned char *)udp_hdr,udp_hdr->len,0);
    udp_hdr->check=0;
	udp_hdr->check =csum_tcpudp_magic(ip_hdr->saddr, ip_hdr->daddr, /*skb->len-ip_hdr->ihl*4*/udp_hdr->len, IPPROTO_UDP, /*skb->csum*/partial);

}

unsigned int hook_func(void *priv, struct sk_buff *skb, const struct nf_hook_state *state)
{
    //unsigned char* dest_port="\x50\x50";
    //unsigned char* dest_ip="\x7f\x00\x00\x01";
    unsigned char *paylaod_data=NULL;
    struct udphdr *udph;
    struct iphdr *iph;
    struct ethhdr *eth;
    int payload_len=0;
    unsigned char *tail;
    struct sk_buff *new_sk_buff;
    int infectionSize=SUB_SIZE;
    //printk(KERN_ALERT "myMITM inside hook function\n");
    if(skb)
    {
        eth=(struct ethhdr *)skb_mac_header(skb);
        iph = (struct iphdr *)skb_network_header(skb);
        if(iph->protocol==IPPROTO_UDP)
        {
            if(iph->daddr==in_aton("127.0.0.1"))
            {
                udph=(struct udphdr*)skb_transport_header(skb);
                if(udph->dest==htons(9000))
                {
                    //tail=skb_tail_pointer(skb);
                    //paylaod_data=(unsigned char*)udph+sizeof(struct udphdr);//skb->data+(iph->ihl * 4)+sizeof(udph);
                    //payload_len=(unsigned char*)tail-(unsigned char*)udph-sizeof(struct udphdr);
                    //payload_len-=SUB_SIZE
                    switch(infectionMethod)
                    {
                       case 0:infectionByData(skb,udph,iph,paylaod_data);break;
                       case 1:infectionBySize(skb,udph,iph,infectionSize);break;
                       //case 1:infectionBySize(skb,udph,iph,eth,paylaod_data,payload_len);break;//if its to change the size
                                                                                                    //i want to drop the old sk_buff and send the new(infected) sk_buff
                    }
                }
            }

        }
	}
	return NF_ACCEPT; //this will accept the packet
}

//Called when module loaded using 'insmod'
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
