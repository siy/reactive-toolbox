#include <include/org_reactivetoolbox_asyncio_NativeIO.h>
#include <liburing.h>
#include <stdlib.h>
#include <errno.h>

#define RING        ((struct io_uring*)ringPtr)

// Note that this struct is allocated once (load time) and never freed
struct io_uring_probe* uring_probe;

/*
 * Class:     org_reactivetoolbox_asyncio_NativeIO
 * Method:    initApi
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_org_reactivetoolbox_asyncio_NativeIO_initApi(JNIEnv *env, jclass clazz) {
    uring_probe = io_uring_get_probe();

    //TODO: check available API's
    return uring_probe == 0 ? -1 : 0;
}

/*
 * Class:     org_reactivetoolbox_asyncio_NativeIO
 * Method:    initRing
 * Signature: (JJ)J
 */
JNIEXPORT jlong JNICALL Java_org_reactivetoolbox_asyncio_NativeIO_initRing(JNIEnv *env, jclass clazz, jlong entries_count, jlong flags) {
    struct io_uring* ring_instance = malloc(sizeof(struct io_uring));

    int rc = io_uring_queue_init(entries_count, ring_instance, flags);

    // Don't share any data with child processes
    if (rc == 0) {
        rc = io_uring_ring_dontfork(ring_instance);
    }

    if (rc != 0) {
        //TODO: throw an exception?
        free(ring_instance);
        ring_instance = 0;
    }

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
    free(RING);
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
 * Method:    spaceLeft
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL Java_org_reactivetoolbox_asyncio_NativeIO_spaceLeft(JNIEnv *env, jclass clazz, jlong ringPtr) {
    return (jlong) io_uring_sq_space_left(RING);
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
JNIEXPORT jint JNICALL Java_org_reactivetoolbox_asyncio_NativeIO_prepareIO(JNIEnv *env, jclass clazz,
                                                                           jlong ringPtr, jlong operation,
                                                                           jint fd, jlong address,
                                                                           jlong len, jlong offset,
                                                                           jint flags, jlong requestId) {
    struct io_uring_sqe *sqe = io_uring_get_sqe(RING);

    if (!sqe) {
        return (jint)(-ENOSPC);
    }

    sqe->__pad2[0] = sqe->__pad2[1] = sqe->__pad2[2] = 0;
    sqe->opcode = op;
    sqe->flags = 0;
    sqe->ioprio = 0;
    sqe->fd = (int) fd;
    sqe->off = (__u64) offset;
    sqe->addr = (unsigned long) addr;
    sqe->len = (unsigned) len;
    sqe->rw_flags = (__kernel_rwf_t) flags;
    sqe->user_data = (__u64) requestId;
}

/*
 * Class:     org_reactivetoolbox_asyncio_NativeIO
 * Method:    createSocket
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_org_reactivetoolbox_asyncio_NativeIO_createSocket(JNIEnv *env, jclass clazz, jint flags) {

}

/*
 * Class:     org_reactivetoolbox_asyncio_NativeIO
 * Method:    bind
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_org_reactivetoolbox_asyncio_NativeIO_bind
  (JNIEnv *, jclass, jint, jint);

/*
 * Class:     org_reactivetoolbox_asyncio_NativeIO
 * Method:    listen
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_org_reactivetoolbox_asyncio_NativeIO_listen
  (JNIEnv *, jclass, jint, jint);
