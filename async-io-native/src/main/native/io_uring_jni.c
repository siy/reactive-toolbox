#include <include/org_reactivetoolbox_asyncio_NativeIO.h>
#include <liburing.h>
#include <stdlib.h>

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

    struct io_uring* ring = (struct io_uring*) ringPtr;
    io_uring_queue_exit(ring);
    free(ring);
}

/*
 * Class:     org_reactivetoolbox_asyncio_NativeIO
 * Method:    peekCQ
 * Signature: (J[J)I
 */
JNIEXPORT jint JNICALL Java_org_reactivetoolbox_asyncio_NativeIO_peekCQ(JNIEnv *env, jclass clazz, jlong ringPtr, jlongArray data) {
    struct io_uring* ring = (struct io_uring*) ringPtr;
    int count = (*env)->GetArrayLength(env, data) >> 1;

    struct io_uring_cqe* batch[count];
    int ready = io_uring_peek_batch_cqe(ring, batch, count);

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
JNIEXPORT void JNICALL Java_org_reactivetoolbox_asyncio_NativeIO_advanceCQ
  (JNIEnv *, jclass, jlong, jint);

/*
 * Class:     org_reactivetoolbox_asyncio_NativeIO
 * Method:    spaceLeft
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL Java_org_reactivetoolbox_asyncio_NativeIO_spaceLeft
  (JNIEnv *, jclass, jlong);

/*
 * Class:     org_reactivetoolbox_asyncio_NativeIO
 * Method:    submit
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL Java_org_reactivetoolbox_asyncio_NativeIO_submit
  (JNIEnv *, jclass, jlong);

/*
 * Class:     org_reactivetoolbox_asyncio_NativeIO
 * Method:    submitAndWait
 * Signature: (JJ)J
 */
JNIEXPORT jlong JNICALL Java_org_reactivetoolbox_asyncio_NativeIO_submitAndWait
  (JNIEnv *, jclass, jlong, jlong);

/*
 * Class:     org_reactivetoolbox_asyncio_NativeIO
 * Method:    prepNop
 * Signature: (JJ)I
 */
JNIEXPORT jint JNICALL Java_org_reactivetoolbox_asyncio_NativeIO_prepNop
  (JNIEnv *, jclass, jlong, jlong);

/*
 * Class:     org_reactivetoolbox_asyncio_NativeIO
 * Method:    prepFsync
 * Signature: (JJIJ)I
 */
JNIEXPORT jint JNICALL Java_org_reactivetoolbox_asyncio_NativeIO_prepFsync
  (JNIEnv *, jclass, jlong, jlong, jint, jlong);

/*
 * Class:     org_reactivetoolbox_asyncio_NativeIO
 * Method:    prepTimeout
 * Signature: (JJJJJJ)I
 */
JNIEXPORT jint JNICALL Java_org_reactivetoolbox_asyncio_NativeIO_prepTimeout
  (JNIEnv *, jclass, jlong, jlong, jlong, jlong, jlong, jlong);

/*
 * Class:     org_reactivetoolbox_asyncio_NativeIO
 * Method:    prepTimeoutRemove
 * Signature: (JJJ)I
 */
JNIEXPORT jint JNICALL Java_org_reactivetoolbox_asyncio_NativeIO_prepTimeoutRemove
  (JNIEnv *, jclass, jlong, jlong, jlong);

/*
 * Class:     org_reactivetoolbox_asyncio_NativeIO
 * Method:    prepCancel
 * Signature: (JJI)I
 */
JNIEXPORT jint JNICALL Java_org_reactivetoolbox_asyncio_NativeIO_prepCancel
  (JNIEnv *, jclass, jlong, jlong, jint);

/*
 * Class:     org_reactivetoolbox_asyncio_NativeIO
 * Method:    prepLinkedTimeout
 * Signature: (JJJJJ)I
 */
JNIEXPORT jint JNICALL Java_org_reactivetoolbox_asyncio_NativeIO_prepLinkedTimeout
  (JNIEnv *, jclass, jlong, jlong, jlong, jlong, jlong);

/*
 * Class:     org_reactivetoolbox_asyncio_NativeIO
 * Method:    prepSplice
 * Signature: (JJIJIJJJ)I
 */
JNIEXPORT jint JNICALL Java_org_reactivetoolbox_asyncio_NativeIO_prepSplice
  (JNIEnv *, jclass, jlong, jlong, jint, jlong, jint, jlong, jlong, jlong);

/*
 * Class:     org_reactivetoolbox_asyncio_NativeIO
 * Method:    prepReadVector
 * Signature: (JJI[J[IJ)I
 */
JNIEXPORT jint JNICALL Java_org_reactivetoolbox_asyncio_NativeIO_prepReadVector
  (JNIEnv *, jclass, jlong, jlong, jint, jlongArray, jintArray, jlong);

/*
 * Class:     org_reactivetoolbox_asyncio_NativeIO
 * Method:    prepWriteVector
 * Signature: (JJI[J[IJ)I
 */
JNIEXPORT jint JNICALL Java_org_reactivetoolbox_asyncio_NativeIO_prepWriteVector
  (JNIEnv *, jclass, jlong, jlong, jint, jlongArray, jintArray, jlong);

/*
 * Class:     org_reactivetoolbox_asyncio_NativeIO
 * Method:    prepReadFixed
 * Signature: (JJIJIJI)I
 */
JNIEXPORT jint JNICALL Java_org_reactivetoolbox_asyncio_NativeIO_prepReadFixed
  (JNIEnv *, jclass, jlong, jlong, jint, jlong, jint, jlong, jint);

/*
 * Class:     org_reactivetoolbox_asyncio_NativeIO
 * Method:    prepWriteFixed
 * Signature: (JJIJIJI)I
 */
JNIEXPORT jint JNICALL Java_org_reactivetoolbox_asyncio_NativeIO_prepWriteFixed
  (JNIEnv *, jclass, jlong, jlong, jint, jlong, jint, jlong, jint);

/*
 * Class:     org_reactivetoolbox_asyncio_NativeIO
 * Method:    prepRead
 * Signature: (JJIJIJ)I
 */
JNIEXPORT jint JNICALL Java_org_reactivetoolbox_asyncio_NativeIO_prepRead
  (JNIEnv *, jclass, jlong, jlong, jint, jlong, jint, jlong);

/*
 * Class:     org_reactivetoolbox_asyncio_NativeIO
 * Method:    prepWrite
 * Signature: (JJIJIJ)I
 */
JNIEXPORT jint JNICALL Java_org_reactivetoolbox_asyncio_NativeIO_prepWrite
  (JNIEnv *, jclass, jlong, jlong, jint, jlong, jint, jlong);

/*
 * Class:     org_reactivetoolbox_asyncio_NativeIO
 * Method:    prepOpen
 * Signature: (JJ[BIJ)I
 */
JNIEXPORT jint JNICALL Java_org_reactivetoolbox_asyncio_NativeIO_prepOpen
  (JNIEnv *, jclass, jlong, jlong, jbyteArray, jint, jlong);

/*
 * Class:     org_reactivetoolbox_asyncio_NativeIO
 * Method:    prepClose
 * Signature: (JJI)I
 */
JNIEXPORT jint JNICALL Java_org_reactivetoolbox_asyncio_NativeIO_prepClose
  (JNIEnv *, jclass, jlong, jlong, jint);

//
//// void initIds(final Class<CompletionResult> clz)
//JNIEXPORT void JNICALL Java_org_reactivetoolbox_asyncio_IoUring_initIds(JNIEnv * env, jclass mainClazz, jclass resultClazz) {
//    nativePtr_fid = (*env)->GetFieldID(env, mainClazz, "nativePtr", "J"); //long
//    userData_fid =  (*env)->GetFieldID(env, resultClazz, "userData", "Ljava/lang/Object;"); //Reference to user data object
//    result_fid =  (*env)->GetFieldID(env, resultClazz, "result", "J"); //long
//
//    uring_probe = io_uring_get_probe();
//}
//
//// int init(long entries, long flags)
//
//JNIEXPORT jint JNICALL Java_org_reactivetoolbox_asyncio_IoUring_init(JNIEnv *env, jobject instance, jlong entries_count, jlong flags) {
//    struct io_uring * ring_instance = malloc(sizeof(struct io_uring));
//
//    (*env)->SetLongField(env, instance, nativePtr_fid, (jlong) ring_instance);
//
//    int rc = io_uring_queue_init(entries_count, ring_instance, flags);
//
//    // Don't share any data with child processes
//    if (rc == 0) {
//        rc = io_uring_ring_dontfork(ring_instance);
//    }
//
//    return (jint) rc;
//}
//
//// void exit()
//
//JNIEXPORT void JNICALL Java_org_reactivetoolbox_asyncio_IoUring_exit(JNIEnv *env, jobject instance) {
//    struct io_uring* ring = RING(env, instance);
//    (*env)->SetLongField(env, instance, nativePtr_fid, 0);
//
//    io_uring_queue_exit(ring);
//    free(ring);
//}
//
//// long peekCQ(final long[] data);
//JNIEXPORT jint JNICALL Java_org_reactivetoolbox_asyncio_IoUring_peekCQ(JNIEnv *env, jobject instance, jlongArray data) {
//    int count = (*env)->GetArrayLength(env, data) >> 1;
//
//    struct io_uring_cqe* batch[count];
//    int ready = io_uring_peek_batch_cqe(RING(env, instance), batch, count);
//
//    if (ready) {
//        int outputCount = ready << 1;
//        long localData[outputCount];
//
//        for (int i = 0, ndx = 0; i < ready; i++) {
//            localData[ndx++] = batch[i]->user_data;
//            localData[ndx++] = batch[i]->res << 32 | batch[i]->flags;
//        }
//        (*env)->SetLongArrayRegion(env, data, 0, outputCount, localData);
//    }
//
//    return (jint) ready;
//}
//
//// void advanceCQ(long count)
//JNIEXPORT void JNICALL Java_org_reactivetoolbox_asyncio_IoUring_advanceCQ(JNIEnv *env, jobject instance, jint count) {
//    io_uring_cq_advance(RING(env, instance), count);
//}
