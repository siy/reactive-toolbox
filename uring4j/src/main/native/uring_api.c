#include <liburing.h>
#include <stdlib.h>
#include <string.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <sys/socket.h>
#include <errno.h>
#include <linux/stat.h>
#include "include/org_reactivetoolbox_io_uring_Uring.h"

/*
 * Class:     org_reactivetoolbox_io_uring_Uring
 * Method:    init
 * Signature: (IJJ)I
 */
JNIEXPORT jint JNICALL Java_org_reactivetoolbox_io_uring_Uring_init(JNIEnv *env, jclass clazz, jint num_entries, jlong base_address, jlong flags) {
    return (jint) 0;
}

/*
 * Class:     org_reactivetoolbox_io_uring_Uring
 * Method:    close
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_org_reactivetoolbox_io_uring_Uring_close(JNIEnv *env, jclass clazz, jlong base_address) {
}

/*
 * Class:     org_reactivetoolbox_io_uring_Uring
 * Method:    peekCQ
 * Signature: (JJJ)I
 */
JNIEXPORT jint JNICALL Java_org_reactivetoolbox_io_uring_Uring_peekCQ(JNIEnv *env, jclass clazz, jlong base_address, jlong completions_address, jlong count) {
    return (jint) 0;
}

/*
 * Class:     org_reactivetoolbox_io_uring_Uring
 * Method:    advanceCQ
 * Signature: (JJ)I
 */
JNIEXPORT jint JNICALL Java_org_reactivetoolbox_io_uring_Uring_advanceCQ(JNIEnv *env, jclass clazz, jlong base_address, jlong count) {
    return (jint) 0;
}

/*
 * Class:     org_reactivetoolbox_io_uring_Uring
 * Method:    readyCQ
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_org_reactivetoolbox_io_uring_Uring_readyCQ(JNIEnv *env, jclass clazz, jlong base_address) {
    return (jint) 0;
}

/*
 * Class:     org_reactivetoolbox_io_uring_Uring
 * Method:    spaceLeft
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL Java_org_reactivetoolbox_io_uring_Uring_spaceLeft(JNIEnv *env, jclass clazz, jlong base_address) {
    return (jlong) 0;
}

/*
 * Class:     org_reactivetoolbox_io_uring_Uring
 * Method:    nextSQEntry
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL Java_org_reactivetoolbox_io_uring_Uring_nextSQEntry(JNIEnv *env, jclass clazz, jlong base_address) {
    return (jlong) 0;
}

/*
 * Class:     org_reactivetoolbox_io_uring_Uring
 * Method:    submitAndWait
 * Signature: (JI)J
 */
JNIEXPORT jlong JNICALL Java_org_reactivetoolbox_io_uring_Uring_submitAndWait(JNIEnv *env, jclass clazz, jlong base_address, jint nr_wait) {
    return (jlong) 0;
}


