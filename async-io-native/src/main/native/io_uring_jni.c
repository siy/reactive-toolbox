#include <include/org_reactivetoolbox_asyncio_NativeIO.h>
#include <liburing.h>
#include <stdlib.h>
#include <string.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <sys/socket.h>
#include <errno.h>
#include <linux/stat.h>

// ------------------------------------------------------------------------------------------------
// Implementation details:
// Each ring internally represented as struct ring_with_data. This structure beside ring itself
// holds a number of buffers associated with request ID's. Java side ensures that requestId's are
// some reasonable integer values (basically indexes in array) which can be used as indexes in
// array at C side as well. Also, Java side ensures that requestId's are unique across
// all _active_ requests and reused only when request is completed.
// This approach enables use of associated memory to hold additional data at JNI side, for example
// struct iovec's or client address for accepted connection.
// ------------------------------------------------------------------------------------------------

#define RING_WITH_DATA  ((struct ring_with_data*)ringPtr)
#define RING            (&(RING_WITH_DATA)->ring)
#define EXTRA_DATA_SIZE 256

union request_data;
struct associated_data {
    int count;
    union request_data** ptrs;
};

struct ring_with_data {
    struct io_uring ring;
    struct associated_data data;
};

union request_data {
    struct __kernel_timespec timeout;
    struct {
        socklen_t addrlen;
        struct sockaddr addr;
    } accept;
    struct iovec vector[1];
    struct msghdr message_header;
    struct sockaddr connect;
    char open_path[1];
    struct statx statx_data;
};

//--------------------------------------------------
// Utility functions
//--------------------------------------------------
void* zmalloc(size_t size) {
    void* ptr = malloc(size);

    if (ptr) {
        memset(ptr, 0, size);
    }

    return ptr;
}

/*
 * Class:     org_reactivetoolbox_asyncio_NativeIO
 * Method:    initRing
 * Signature: (JJ)J
 */
JNIEXPORT jlong JNICALL Java_org_reactivetoolbox_asyncio_NativeIO_initRing(JNIEnv *env, jclass clazz, jlong entries_count, jlong flags) {
    struct ring_with_data* ring_instance = zmalloc(sizeof(struct ring_with_data));

    int rc = io_uring_queue_init(entries_count, &ring_instance->ring, flags);

    // Don't share any data with child processes
    if (rc == 0) {
        rc = io_uring_ring_dontfork(&ring_instance->ring);

        if (rc != 0) {
            //TODO: throw an exception?
            free(ring_instance);
            ring_instance = 0;
        }
    }

    ring_instance->data.ptrs = (union request_data**) zmalloc(sizeof(union request_data*) * entries_count * 2);
    ring_instance->data.count = entries_count * 2;

    return (jlong) ring_instance;
}

/*
 * Class:     org_reactivetoolbox_asyncio_NativeIO
 * Method:    closeRing
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_org_reactivetoolbox_asyncio_NativeIO_closeRing(JNIEnv * env, jclass clazz, jlong ringPtr) {
    if (ringPtr == 0) {
        return;
    }

    io_uring_queue_exit(RING);

    struct ring_with_data* ring_instance = RING_WITH_DATA;

    for (int i = 0; i < ring_instance->data.count; i++) {
        free(ring_instance->data.ptrs[i]);
    }

    free(RING_WITH_DATA);
}

/*
 * Class:     org_reactivetoolbox_asyncio_NativeIO
 * Method:    peekCQ
 * Signature: (J[J)I
 */
JNIEXPORT jint JNICALL Java_org_reactivetoolbox_asyncio_NativeIO_peekCQ(JNIEnv *env, jclass clazz, jlong ringPtr, jlongArray data) {
    int count = (*env)->GetArrayLength(env, data) >> 1;

    struct io_uring_cqe* batch[count];
    int ready = io_uring_peek_batch_cqe(RING, batch, count);

    if (ready) {
        int outputCount = ready << 1;
        jlong localData[outputCount];

        for (int i = 0, ndx = 0; i < ready; i++) {
            localData[ndx++] = batch[i]->user_data;
            localData[ndx++] = ((jlong) batch[i]->res) << 32 | batch[i]->flags;
        }
        (*env)->SetLongArrayRegion(env, data, 0, outputCount, localData);
    }

    return (jint) ready;
}

/*
 * Class:     org_reactivetoolbox_asyncio_NativeIO
 * Method:    advanceCQ
 * Signature: (JI)V
 */
JNIEXPORT void JNICALL Java_org_reactivetoolbox_asyncio_NativeIO_advanceCQ(JNIEnv *env, jclass clazz, jlong ringPtr, jint count) {
    io_uring_cq_advance(RING, count);
}

/*
 * Class:     org_reactivetoolbox_asyncio_NativeIO
 * Method:    submitAndWait
 * Signature: (JJ)J
 */
JNIEXPORT jlong JNICALL Java_org_reactivetoolbox_asyncio_NativeIO_submitAndWait(JNIEnv *env, jclass clazz, jlong ringPtr, jlong numWait) {
    return (jlong) io_uring_submit_and_wait(RING, (unsigned) numWait);
}

/*
 * Class:     org_reactivetoolbox_asyncio_NativeIO
 * Method:    spaceLeft
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL Java_org_reactivetoolbox_asyncio_NativeIO_spaceLeft(JNIEnv *env, jclass clazz, jlong ringPtr) {
    return (jlong) io_uring_sq_space_left(RING);
}

/*
 * Class:     org_reactivetoolbox_asyncio_NativeIO
 * Method:    prepareIO
 * Signature: (JJIJJJJJ)I
 */
static void ensure_memory(struct ring_with_data* ring_instance, jlong requestId) {
    if (ring_instance->data.count >= requestId) {
        // Reallocate pointers
        union request_data** new_ptrs = zmalloc(sizeof(void*) * ring_instance->data.count * 2);

        memcpy(new_ptrs, ring_instance->data.ptrs, sizeof(void*) * ring_instance->data.count);
        free(ring_instance->data.ptrs);
        ring_instance->data.ptrs = new_ptrs;
        ring_instance->data.count *= 2;
    }

    if (!ring_instance->data.ptrs[requestId]) {
        ring_instance->data.ptrs[requestId] = zmalloc(EXTRA_DATA_SIZE);
    }
}

JNIEXPORT jint JNICALL Java_org_reactivetoolbox_asyncio_NativeIO_prepareIO(JNIEnv *env, jclass clazz,
                                                                           jlong ringPtr, jint operation,
                                                                           jint fd, jlong address,
                                                                           jlong len, jlong offset,
                                                                           jint opFlags, jint sqFlags,
                                                                           jlong requestId) {
    struct io_uring_sqe *sqe = io_uring_get_sqe(RING);

    if (!sqe) {
        return (jint)(-ENOSPC);
    }

    // Make sure accompanying memory is available
    ensure_memory(RING_WITH_DATA, requestId);

    sqe->__pad2[0] = sqe->__pad2[1] = sqe->__pad2[2] = 0;
    sqe->opcode = (__u8) operation;
    sqe->flags = (__u8) (sqFlags & 0xFF);
    sqe->ioprio = 0;
    sqe->fd = (__s32) fd;
    sqe->off = (__u64) offset;
    sqe->addr = (__u64) address;
    sqe->len = (__u32) len;
    sqe->rw_flags = (__kernel_rwf_t) opFlags;
    sqe->user_data = (__u64) requestId;

    return 0;
}

/*
 * Class:     org_reactivetoolbox_asyncio_NativeIO
 * Method:    createSocket
 * Signature: (I)I
 */
#define SO_OPEN_STREAM      (1 << 0)
#define SO_OPEN_NONBLOCK    (1 << 1)
#define SO_OPEN_REUSE_ADDR  (1 << 2)
#define SO_OPEN_REUSE_PORT  (1 << 3)

JNIEXPORT jint JNICALL Java_org_reactivetoolbox_asyncio_NativeIO_createSocket(JNIEnv *env, jclass clazz, jint flags) {

    int openFlags = SOCK_CLOEXEC | ((flags & SO_OPEN_STREAM) ? SOCK_STREAM : SOCK_DGRAM);

    if (flags & SO_OPEN_NONBLOCK) {
        openFlags |= SOCK_NONBLOCK;
    }

    int rc = socket(PF_INET, openFlags, 0);

    if (rc < 0) {
        return rc;
    }

    const int set = 1;
    const int socket = rc;
    int options = 0;

    if (flags & SO_OPEN_REUSE_ADDR) {
        options |= SO_REUSEADDR;
    }

    if (flags & SO_OPEN_REUSE_PORT) {
        options |= SO_REUSEPORT;
    }

    if (options) {
        rc = setsockopt(socket, SOL_SOCKET, options, &set, sizeof(set));

        if (rc < 0) {
            return (jint) rc;
        }
    }

    return (jint) socket;
}

/*
 * Class:     org_reactivetoolbox_asyncio_NativeIO
 * Method:    bind
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_org_reactivetoolbox_asyncio_NativeIO_bind(JNIEnv *env, jclass clazz, jint socket, jint port, jbyteArray byteAddress) {
    struct sockaddr_in bind_addr;

    memset(&bind_addr, 0, sizeof(bind_addr));
    bind_addr.sin_family = AF_INET;
    bind_addr.sin_port = htons((unsigned short) port);

    if (!byteAddress) {
        bind_addr.sin_addr.s_addr = INADDR_ANY;
    } else {
        char address[32];
        memset(address, 0, sizeof(address));
        jsize len = (*env)->GetArrayLength(env, byteAddress);

        if (len >= sizeof(address)) {
            len = sizeof(address) - 1;
        }

        (*env)->GetByteArrayRegion(env, byteAddress, 0, len, (jbyte*)address);

        int rc = inet_pton(AF_INET, address, &bind_addr.sin_addr);

        if (rc < 0) {
            return (jint) rc;
        }
    }

    return (jint) bind(socket, (struct sockaddr *)&bind_addr, sizeof(bind_addr));
}

/*
 * Class:     org_reactivetoolbox_asyncio_NativeIO
 * Method:    listen
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_org_reactivetoolbox_asyncio_NativeIO_listen(JNIEnv *env, jclass clazz, jint socket, jint backlog) {
    return (jint) listen(socket, backlog);
}
