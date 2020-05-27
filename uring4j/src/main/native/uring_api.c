#include <liburing.h>
#include <stdlib.h>
#include <string.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <sys/socket.h>
#include <errno.h>
#include <linux/stat.h>
#include "include/org_reactivetoolbox_io_uring_Uring.h"

#define RING_PTR        ((struct io_uring *) base_address)
#define CQE_BATCH_PTR   ((struct io_uring_cqe **) completions_address)
#define COUNT           ((unsigned) count)

JNIEXPORT jint JNICALL Java_org_reactivetoolbox_io_uring_Uring_init(JNIEnv *env, jclass clazz, jint num_entries, jlong base_address, jlong flags) {
    return (jint) io_uring_queue_init((unsigned) num_entries, RING_PTR, (unsigned) flags);
}

JNIEXPORT void JNICALL Java_org_reactivetoolbox_io_uring_Uring_close(JNIEnv *env, jclass clazz, jlong base_address) {
    io_uring_queue_exit(RING_PTR);
}

JNIEXPORT jint JNICALL Java_org_reactivetoolbox_io_uring_Uring_peekCQ(JNIEnv *env, jclass clazz, jlong base_address, jlong completions_address, jlong count) {
    return (jint) io_uring_peek_batch_cqe(RING_PTR, CQE_BATCH_PTR, COUNT);
}

JNIEXPORT void JNICALL Java_org_reactivetoolbox_io_uring_Uring_advanceCQ(JNIEnv *env, jclass clazz, jlong base_address, jlong count) {
    io_uring_cq_advance(RING_PTR, COUNT);
}

JNIEXPORT jint JNICALL Java_org_reactivetoolbox_io_uring_Uring_readyCQ(JNIEnv *env, jclass clazz, jlong base_address) {
    return (jint) io_uring_cq_ready(RING_PTR);
}

JNIEXPORT jlong JNICALL Java_org_reactivetoolbox_io_uring_Uring_spaceLeft(JNIEnv *env, jclass clazz, jlong base_address) {
    return (jlong) io_uring_sq_space_left(RING_PTR);
}

JNIEXPORT jlong JNICALL Java_org_reactivetoolbox_io_uring_Uring_nextSQEntry(JNIEnv *env, jclass clazz, jlong base_address) {
    return (jlong) io_uring_get_sqe(RING_PTR);
}

JNIEXPORT jlong JNICALL Java_org_reactivetoolbox_io_uring_Uring_submitAndWait(JNIEnv *env, jclass clazz, jlong base_address, jint count) {
    return (jlong) io_uring_submit_and_wait(RING_PTR, COUNT);
}


