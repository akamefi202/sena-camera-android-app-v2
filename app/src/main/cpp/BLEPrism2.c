// BLEPrism2.cpp : 此文件包含 "main" 函数。程序执行将在此处开始并结束。
//

#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <string.h>
#include <jni.h>

//#ifdef __alpha
typedef unsigned int uint32;
//#else
//typedef unsigned long uint32;
//#endif

struct MD5Context {
    uint32 buf[4];
    uint32 bits[2];
    unsigned char in[64];
};

void MD5Init(struct MD5Context* context);
void MD5Update(struct MD5Context* context, unsigned char const* buf,
    unsigned len);
void MD5Final(unsigned char digest[16], struct MD5Context* context);
void MD5Transform(uint32 buf[4], uint32 const in[16]);

/*
 * This is needed to make RSAREF happy on some MS-DOS compilers.
 */
typedef struct MD5Context MD5_CTX;

#ifndef HIGHFIRST
#define byteReverse(buf, len)	/* Nothing */
#else
void byteReverse(unsigned char* buf, unsigned longs);

#ifndef ASM_MD5
/*
 * Note: this code is harmless on little-endian machines.
 */
void
byteReverse(unsigned char* buf, unsigned longs)
{
    uint32 t;
    do {
        t = (uint32)((unsigned)buf[3] << 8 | buf[2]) << 16 |
            ((unsigned)buf[1] << 8 | buf[0]);
        *(uint32*)buf = t;
        buf += 4;
    } while (--longs);
}
#endif
#endif

typedef uint32_t uint32;

struct header {
    char version[24];
    uint32 v2_cookie;
    uint32 product_id;
    uint32 num_of_blocks;
};

struct block_info {
    uint32 type;
    uint32 id;
    uint32 start;
    uint32 length;
    unsigned char digest[16];
};

struct block_v2_info {
    uint32 type;
    uint32 id;
    uint32 start;
    uint32 length;
    uint32 major_ver;
    uint32 minor_ver;
    uint32 sub_ver_1;
    uint32 sub_ver_2;
    uint32 sub_ver_3;
    uint32 product_id;
    char reserved[128];
    char filename[128];
    unsigned char digest[16];
};

JNIEXPORT jint JNICALL
Java_com_sena_senacamera_ui_fragment_FragmentFirmwareUpdate_splitFirmware(JNIEnv *env, jobject obj, jstring filePath, jstring folderPath)
{
//    if (ac != 2)
//    {
//        printf("add file\n");
//        return -1;
//    }

    int i, j, a;
    FILE* f, * write_f;
    char data[1024];
    int num_of_blocks = 0;
    struct MD5Context ctx;

    struct header* hd;
    struct block_info* bl_info1;
    struct block_v2_info* bl_info2;

    // get char array from file path string
    const char *av = (*env) -> GetStringUTFChars(env, filePath, NULL);
    if (av == NULL) {
        return -1;
    }

    // get char array from folder path string
    const char *av1 = (*env) -> GetStringUTFChars(env, folderPath, NULL);
    if (av1 == NULL) {
        return -1;
    }

    f = fopen(av, "rb");
    if (f == NULL) {
        fprintf(stderr, "fopen f is failed\n");
        return -1;
    }

    fseek(f, 0, SEEK_END);
    unsigned int file_size = ftell(f);
    fseek(f, 0, SEEK_SET);
    printf("file: %s, size: %d Bytes\n", av, file_size);

    // get header struct information
    fread(data, 1, sizeof(struct header), f);

    hd = (struct header*)data;
    printf("hdr.version\t[%s]\nhdr.v2_cookie\t[%x]\nhdr.product_id\t[%x]\nhdr.num_of_blocks[%x]\n",
        hd->version,
        hd->v2_cookie,
        hd->product_id,
        hd->num_of_blocks);
    printf("=================================================\n");

    num_of_blocks = hd->num_of_blocks;

    // get block_info struct information : Not use
    fread(data, 1, sizeof(struct block_info) * num_of_blocks, f);

#if 0
    bl_info1 = (struct block_info*)data;
    for (i = 0; i < num_of_blocks; i++) {
        printf("info.type\t[%x]\ninfo.id\t\t[%x]\ninfo.start\t[%x]\ninfo.length\t[%x]\n",
            bl_info1->type,
            bl_info1->id,
            bl_info1->start,
            bl_info1->length);
        bl_info1 += 1;
        printf("\n+++++++++++++++++++++++++++++++++++++++++++++++++\n");
    }
#endif

    // get block_v2_info struct information
    fread(data, 1, sizeof(struct block_v2_info) * num_of_blocks, f);

    bl_info2 = (struct block_v2_info*)data;
    for (i = 0; i < num_of_blocks; i++)
    {
        printf("info.type\t[%x]\ninfo.id\t\t[%x]\ninfo.start\t[%x]\ninfo.length\t[%x]\ninfo.major_ver\t[%x]\ninfo.minor_ver\t[%x]\ninfo.sub_ver_1\t[%x]\ninfo.sub_ver_2\t[%x]\ninfo.sub_ver_3\t[%x]\ninfo.produce_id\t[%x]\ninfo.reserved\t[%s]\ninfo.filename\t[%s]\n",
            bl_info2->type,
            bl_info2->id,
            bl_info2->start,
            bl_info2->length,
            bl_info2->major_ver,
            bl_info2->minor_ver,
            bl_info2->sub_ver_1,
            bl_info2->sub_ver_2,
            bl_info2->sub_ver_3,
            bl_info2->product_id,
            bl_info2->reserved,
            bl_info2->filename);
        printf("\ndigest matching:\n|");
        for (j = 0; j < 16; j++)
            printf("%02x|", bl_info2->digest[j]);

        char payload_data[1024];
        int d_count = bl_info2->length / sizeof(payload_data);
        _Bool is_over_avg = ((bl_info2->length % sizeof(payload_data)) != 0);

        MD5Init(&ctx);

        // get the absolute file path to write
        size_t len1 = strlen(av1);
        size_t len2 = strlen(bl_info2->filename);
        size_t total_len = len1 + len2 + 1;
        char* file_path = malloc(total_len);
        if (file_path == NULL) {
            fprintf(stderr, "malloc failed\n");
            return -1;
        }

        strcpy(file_path, av1);
        strcat(file_path, bl_info2->filename);

        write_f = fopen(file_path, "wb");
        if (write_f == NULL) {
            fprintf(stderr, "fopen write_f is failed\n");
            return -1;
        }

        if (is_over_avg)
            d_count += 1;
        for (a = 0; a < d_count; a++) {
            int count = sizeof(payload_data);
            if (is_over_avg && a == d_count - 1)
                count = bl_info2->length % sizeof(payload_data);

            // get payload data
            fread(payload_data, 1, count, f);

            MD5Update(&ctx, (unsigned char*)payload_data, count);
            fwrite(payload_data, 1, count, write_f);

        }
        fclose(write_f);
        unsigned char digest[16];

        MD5Final(digest, &ctx);

        printf("\n|");
        for (j = 0; j < 16; j++)
            printf("%02x|", digest[j]);

        printf("\n-------------------------------------------------\n");
        bl_info2 += 1;

        // free file_path
        free(file_path);
    }

    // release char array
    (*env)->ReleaseStringUTFChars(env, filePath, av);
    (*env)->ReleaseStringUTFChars(env, folderPath, av1);

    fclose(f);

    return 0;
}

/*
 * Start MD5 accumulation.  Set bit count to 0 and buffer to mysterious
 * initialization constants.
 */
void
MD5Init(struct MD5Context* ctx)
{
    ctx->buf[0] = 0x67452301;
    ctx->buf[1] = 0xefcdab89;
    ctx->buf[2] = 0x98badcfe;
    ctx->buf[3] = 0x10325476;

    ctx->bits[0] = 0;
    ctx->bits[1] = 0;
}

/*
 * Update context to reflect the concatenation of another buffer full
 * of bytes.
 */
void
MD5Update(struct MD5Context* ctx, unsigned char const* buf, unsigned len)
{
    uint32 t;

    /* Update bitcount */

    t = ctx->bits[0];
    if ((ctx->bits[0] = t + ((uint32)len << 3)) < t)
        ctx->bits[1]++;		/* Carry from low to high */
    ctx->bits[1] += len >> 29;

    t = (t >> 3) & 0x3f;	/* Bytes already in shsInfo->data */

    /* Handle any leading odd-sized chunks */

    if (t) {
        unsigned char* p = (unsigned char*)ctx->in + t;

        t = 64 - t;
        if (len < t) {
            memcpy(p, buf, len);
            return;
        }
        memcpy(p, buf, t);
        byteReverse(ctx->in, 16);
        MD5Transform(ctx->buf, (uint32*)ctx->in);
        buf += t;
        len -= t;
    }
    /* Process data in 64-byte chunks */

    while (len >= 64) {
        memcpy(ctx->in, buf, 64);
        byteReverse(ctx->in, 16);
        MD5Transform(ctx->buf, (uint32*)ctx->in);
        buf += 64;
        len -= 64;
    }

    /* Handle any remaining bytes of data. */

    memcpy(ctx->in, buf, len);
}

/*
 * Final wrapup - pad to 64-byte boundary with the bit pattern
 * 1 0* (64-bit count of bits processed, MSB-first)
 */
void
MD5Final(unsigned char digest[16], struct MD5Context* ctx)
{
    unsigned count;
    unsigned char* p;

    /* Compute number of bytes mod 64 */
    count = (ctx->bits[0] >> 3) & 0x3F;

    /* Set the first char of padding to 0x80.  This is safe since there is
       always at least one byte free */
    p = ctx->in + count;
    *p++ = 0x80;

    /* Bytes of padding needed to make 64 bytes */
    count = 64 - 1 - count;

    /* Pad out to 56 mod 64 */
    if (count < 8) {
        /* Two lots of padding:  Pad the first block to 64 bytes */
        memset(p, 0, count);
        byteReverse(ctx->in, 16);
        MD5Transform(ctx->buf, (uint32*)ctx->in);

        /* Now fill the next block with 56 bytes */
        memset(ctx->in, 0, 56);
    }
    else {
        /* Pad block to 56 bytes */
        memset(p, 0, count - 8);
    }
    byteReverse(ctx->in, 14);

    /* Append length in bits and transform */
    ((uint32*)ctx->in)[14] = ctx->bits[0];
    ((uint32*)ctx->in)[15] = ctx->bits[1];

    MD5Transform(ctx->buf, (uint32*)ctx->in);
    byteReverse((unsigned char*)ctx->buf, 4);
    memcpy(digest, ctx->buf, 16);
    memset((char*)ctx, 0, sizeof(ctx));	/* In case it's sensitive */
}

#ifndef ASM_MD5

/* The four core functions - F1 is optimized somewhat */

/* #define F1(x, y, z) (x & y | ~x & z) */
#define F1(x, y, z) (z ^ (x & (y ^ z)))
#define F2(x, y, z) F1(z, x, y)
#define F3(x, y, z) (x ^ y ^ z)
#define F4(x, y, z) (y ^ (x | ~z))

/* This is the central step in the MD5 algorithm. */
#define MD5STEP(f, w, x, y, z, data, s) \
	( w += f(x, y, z) + data,  w = w<<s | w>>(32-s),  w += x )

/*
 * The core of the MD5 algorithm, this alters an existing MD5 hash to
 * reflect the addition of 16 longwords of new data.  MD5Update blocks
 * the data and converts bytes into longwords for this routine.
 */
void
MD5Transform(uint32 buf[4], uint32 const in[16])
{
    register uint32 a, b, c, d;

    a = buf[0];
    b = buf[1];
    c = buf[2];
    d = buf[3];

    MD5STEP(F1, a, b, c, d, in[0] + 0xd76aa478, 7);
    MD5STEP(F1, d, a, b, c, in[1] + 0xe8c7b756, 12);
    MD5STEP(F1, c, d, a, b, in[2] + 0x242070db, 17);
    MD5STEP(F1, b, c, d, a, in[3] + 0xc1bdceee, 22);
    MD5STEP(F1, a, b, c, d, in[4] + 0xf57c0faf, 7);
    MD5STEP(F1, d, a, b, c, in[5] + 0x4787c62a, 12);
    MD5STEP(F1, c, d, a, b, in[6] + 0xa8304613, 17);
    MD5STEP(F1, b, c, d, a, in[7] + 0xfd469501, 22);
    MD5STEP(F1, a, b, c, d, in[8] + 0x698098d8, 7);
    MD5STEP(F1, d, a, b, c, in[9] + 0x8b44f7af, 12);
    MD5STEP(F1, c, d, a, b, in[10] + 0xffff5bb1, 17);
    MD5STEP(F1, b, c, d, a, in[11] + 0x895cd7be, 22);
    MD5STEP(F1, a, b, c, d, in[12] + 0x6b901122, 7);
    MD5STEP(F1, d, a, b, c, in[13] + 0xfd987193, 12);
    MD5STEP(F1, c, d, a, b, in[14] + 0xa679438e, 17);
    MD5STEP(F1, b, c, d, a, in[15] + 0x49b40821, 22);

    MD5STEP(F2, a, b, c, d, in[1] + 0xf61e2562, 5);
    MD5STEP(F2, d, a, b, c, in[6] + 0xc040b340, 9);
    MD5STEP(F2, c, d, a, b, in[11] + 0x265e5a51, 14);
    MD5STEP(F2, b, c, d, a, in[0] + 0xe9b6c7aa, 20);
    MD5STEP(F2, a, b, c, d, in[5] + 0xd62f105d, 5);
    MD5STEP(F2, d, a, b, c, in[10] + 0x02441453, 9);
    MD5STEP(F2, c, d, a, b, in[15] + 0xd8a1e681, 14);
    MD5STEP(F2, b, c, d, a, in[4] + 0xe7d3fbc8, 20);
    MD5STEP(F2, a, b, c, d, in[9] + 0x21e1cde6, 5);
    MD5STEP(F2, d, a, b, c, in[14] + 0xc33707d6, 9);
    MD5STEP(F2, c, d, a, b, in[3] + 0xf4d50d87, 14);
    MD5STEP(F2, b, c, d, a, in[8] + 0x455a14ed, 20);
    MD5STEP(F2, a, b, c, d, in[13] + 0xa9e3e905, 5);
    MD5STEP(F2, d, a, b, c, in[2] + 0xfcefa3f8, 9);
    MD5STEP(F2, c, d, a, b, in[7] + 0x676f02d9, 14);
    MD5STEP(F2, b, c, d, a, in[12] + 0x8d2a4c8a, 20);

    MD5STEP(F3, a, b, c, d, in[5] + 0xfffa3942, 4);
    MD5STEP(F3, d, a, b, c, in[8] + 0x8771f681, 11);
    MD5STEP(F3, c, d, a, b, in[11] + 0x6d9d6122, 16);
    MD5STEP(F3, b, c, d, a, in[14] + 0xfde5380c, 23);
    MD5STEP(F3, a, b, c, d, in[1] + 0xa4beea44, 4);
    MD5STEP(F3, d, a, b, c, in[4] + 0x4bdecfa9, 11);
    MD5STEP(F3, c, d, a, b, in[7] + 0xf6bb4b60, 16);
    MD5STEP(F3, b, c, d, a, in[10] + 0xbebfbc70, 23);
    MD5STEP(F3, a, b, c, d, in[13] + 0x289b7ec6, 4);
    MD5STEP(F3, d, a, b, c, in[0] + 0xeaa127fa, 11);
    MD5STEP(F3, c, d, a, b, in[3] + 0xd4ef3085, 16);
    MD5STEP(F3, b, c, d, a, in[6] + 0x04881d05, 23);
    MD5STEP(F3, a, b, c, d, in[9] + 0xd9d4d039, 4);
    MD5STEP(F3, d, a, b, c, in[12] + 0xe6db99e5, 11);
    MD5STEP(F3, c, d, a, b, in[15] + 0x1fa27cf8, 16);
    MD5STEP(F3, b, c, d, a, in[2] + 0xc4ac5665, 23);

    MD5STEP(F4, a, b, c, d, in[0] + 0xf4292244, 6);
    MD5STEP(F4, d, a, b, c, in[7] + 0x432aff97, 10);
    MD5STEP(F4, c, d, a, b, in[14] + 0xab9423a7, 15);
    MD5STEP(F4, b, c, d, a, in[5] + 0xfc93a039, 21);
    MD5STEP(F4, a, b, c, d, in[12] + 0x655b59c3, 6);
    MD5STEP(F4, d, a, b, c, in[3] + 0x8f0ccc92, 10);
    MD5STEP(F4, c, d, a, b, in[10] + 0xffeff47d, 15);
    MD5STEP(F4, b, c, d, a, in[1] + 0x85845dd1, 21);
    MD5STEP(F4, a, b, c, d, in[8] + 0x6fa87e4f, 6);
    MD5STEP(F4, d, a, b, c, in[15] + 0xfe2ce6e0, 10);
    MD5STEP(F4, c, d, a, b, in[6] + 0xa3014314, 15);
    MD5STEP(F4, b, c, d, a, in[13] + 0x4e0811a1, 21);
    MD5STEP(F4, a, b, c, d, in[4] + 0xf7537e82, 6);
    MD5STEP(F4, d, a, b, c, in[11] + 0xbd3af235, 10);
    MD5STEP(F4, c, d, a, b, in[2] + 0x2ad7d2bb, 15);
    MD5STEP(F4, b, c, d, a, in[9] + 0xeb86d391, 21);

    buf[0] += a;
    buf[1] += b;
    buf[2] += c;
    buf[3] += d;
}
#endif



/*
#include <iostream>
#include <winsock2.h>
#include <ws2bth.h>
#include <bluetoothapis.h>
#pragma comment(lib, "ws2_32.lib")

int main() {
    WSADATA wsaData;
    if (WSAStartup(MAKEWORD(2, 2), &wsaData) != 0) {
        std::cerr << "WSAStartup failed: " << WSAGetLastError() << std::endl;
        return 1;
    }
    printf("%c %c %c %c %c %c %c %c\n", 0x73, 0x65, 0x6E, 0x61, 0x30, 0x30, 0x30, 0x30);

    printf("%c %c %c %c %c %c %c %c %c %c %c %c %c %c", 0x50, 0x72, 0x69, 0x73,0x6D, 0x20, 0x32, 0x5F, 0x31, 0x34, 0x34, 0x39, 0x38, 0x35);


    BLUETOOTH_FIND_RADIO_PARAMS btFindRadioParams = { sizeof(BLUETOOTH_FIND_RADIO_PARAMS) };
    HANDLE hRadio = NULL;
    HBLUETOOTH_RADIO_FIND hFind = BluetoothFindFirstRadio(&btFindRadioParams, &hRadio);
    if (hFind == NULL) {
        std::cerr << "Bluetooth radio not found: " << GetLastError() << std::endl;
        WSACleanup();
        return 1;
    }

    BLUETOOTH_RADIO_INFO btRadioInfo = { sizeof(BLUETOOTH_RADIO_INFO) };
    if (BluetoothGetRadioInfo(hRadio, &btRadioInfo) != ERROR_SUCCESS) {
        std::cerr << "Failed to get Bluetooth radio info: " << GetLastError() << std::endl;
        CloseHandle(hRadio);
        BluetoothFindRadioClose(hFind);
        WSACleanup();
        return 1;
    }

    // Print the Bluetooth device address
    std::cout << "Bluetooth Device Address: ";
    for (int i = 5; i >= 0; --i) {
        printf("%02X", btRadioInfo.address.rgBytes[i]);
        if (i > 0) {
            std::cout << ":";
        }
    }

    std::cout << std::endl;

    CloseHandle(hRadio);
    BluetoothFindRadioClose(hFind);

    WSACleanup();
    return 0;
}
*/