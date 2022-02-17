#include "jsonHandle.h"

void append_json_object_to_file(const char* fileName,char* obj)
{
    FILE* fptr;
    fptr = fopen(fileName, "at");
    fwrite(obj,sizeof(char),strlen(obj),fptr);

    // fprintf(fptr, "%s", line); // would work too

    // The file should be closed when everything is written
    fclose(fptr);
}
