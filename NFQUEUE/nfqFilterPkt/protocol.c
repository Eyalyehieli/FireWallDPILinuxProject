#include "protocol.h"

/*double atod(const char* s){    //definition
    double rez = 0, fact = 1;
    if (*s == '-'){
        s++;
        fact = -1;
    };
    for (int point_seen = 0; *s; s++){
        if (*s == '.'){
            point_seen = 1;
            continue;
        };
        int d = *s - '0';
        if (d >= 0 && d <= 9){
            if (point_seen) fact /= 10.0f;
            rez = rez * 10.0f + (float)d;
        };
    };
    return rez * fact;
    }

void setRange(protocol *p,char* min,char*max)
{
    if(strcmp("INT",p->type)==0)
     {
       int* minRange;
       int* maxRange;
       minRange=(int*)malloc(sizeof(int));
       maxRange=(int*)malloc(sizeof(int));
       *minRange=atoi(min);
       *maxRange=atoi(max);
       p->min_range=minRange;
       p->max_range=maxRange;
     }
   else if(strcmp("CHAR",p->type)==0)
     {
       char* minRange;
       char* maxRange;
       minRange=(char*)malloc(sizeof(char));
       maxRange=(char*)malloc(sizeof(char));
       minRange[0]=((char*)min)[0];
       maxRange[0]=((char*)max)[0];
       p->min_range=minRange;
       p->max_range=maxRange;
     }
    else if(strcmp("FLOAT",p->type)==0)
     {
       float* minRange;
       float* maxRange;
       minRange=(float*)malloc(sizeof(float));
       maxRange=(float*)malloc(sizeof(float));
       *minRange=atof(min);
       *maxRange=atof(max);
       p->min_range=minRange;
       p->max_range=maxRange;
     }
     else if(strcmp("DOUBLE",p->type)==0)
     {
       double* minRange;
       double* maxRange;
       minRange=(double*)malloc(sizeof(double));//for allocation on the heap and not on the stack of the function
       maxRange=(double*)malloc(sizeof(double));
       *minRange=atod(min);
       *maxRange=atod(max);
       p->min_range=minRange;
       p->max_range=maxRange;
     }
     else if(strcmp("SHORT",p->type)==0)
     {
       short* minRange;
       short* maxRange;
       minRange=(short*)malloc(sizeof(short));//for allocation on the heap and not on the stack of the function
       maxRange=(short*)malloc(sizeof(short));
       *minRange=(short)atoi(min);
       *maxRange=(short)atoi(max);
       p->min_range=minRange;
       p->max_range=maxRange;
     }
     else if(strcmp("LONG",p->type)==0)
     {
       long* minRange;
       long* maxRange;
       minRange=(long*)malloc(sizeof(long));//for allocation on the heap and not on the stack of the function
       maxRange=(long*)malloc(sizeof(long));
       *minRange=atol(min);
       *maxRange=atol(max);
       p->min_range=minRange;
       p->max_range=maxRange;
     }

}

void setType(protocol *p,char* type)
{
    p->type=(char*)malloc((strlen(type)+1)*sizeof(char));//
    strcpy(p->type,type);
}

void setSerialNumber(protocol *p,int serialNUmber)
{
   p->serialNumber=serialNUmber;
}

void setStructName(protocol* p,char* structName)
{
    p->structName=(char*)malloc((strlen(structName)+1)*sizeof(char));
    strcpy(p->structName,structName);
}

void setApplication(protocol *p,char* app)
{
    p->application=(char*)malloc((strlen(app)+1)*sizeof(char));
    strcpy(p->application,app);
}



char* getType(protocol *p)
{
    return p->type;
}

void* getMin_range(protocol *p)
{
    return p->min_range;
}

void* getMax_range(protocol *p)
{
    return p->max_range;
}

int getSerialNUmber(protocol *p)
{
    return p->serialNumber;
}

char* getStructName(protocol *p)
{
    return p->structName;
}

char* getApplication(protocol *p)
{
    return p->application;
}
*/
