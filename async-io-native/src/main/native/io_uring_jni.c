#include <include/org_reactivetoolbox_asyncio_IoUring.h>
#include <liburing.h>
#include <stdlib.h>

#define RING(env, instance) (struct io_uring*)(*env)->GetLongField(env, instance, nativePtr_fid)

// Main class fields
jfieldID nativePtr_fid;
// CompletionResult class fields
jfieldID userData_fid;
jfieldID result_fid;

// Note that this struct is allocated once (load time) and never freed
struct io_uring_probe* uring_probe;


// void initIds(final Class<CompletionResult> clz)
JNIEXPORT void JNICALL Java_org_reactivetoolbox_asyncio_IoUring_initIds(JNIEnv * env, jclass mainClazz, jclass resultClazz) {
    nativePtr_fid = (*env)->GetFieldID(env, mainClazz, "nativePtr", "J"); //long
    userData_fid =  (*env)->GetFieldID(env, resultClazz, "userData", "Ljava/lang/Object;"); //Reference to user data object
    result_fid =  (*env)->GetFieldID(env, resultClazz, "result", "J"); //long

    uring_probe = io_uring_get_probe();
}

// int init(long entries, long flags)

JNIEXPORT jint JNICALL Java_org_reactivetoolbox_asyncio_IoUring_init(JNIEnv *env, jobject instance, jlong entries_count, jlong flags) {
    struct io_uring * ring_instance = malloc(sizeof(struct io_uring));
    (*env)->SetLongField(env, instance, nativePtr_fid, (jlong) ring_instance);

    return (jint) io_uring_queue_init(entries_count, ring_instance, flags);
}

// void exit()

JNIEXPORT void JNICALL Java_org_reactivetoolbox_asyncio_IoUring_exit(JNIEnv *env, jobject instance) {
    struct io_uring* ring = RING(env, instance);
    (*env)->SetLongField(env, instance, nativePtr_fid, 0);

    io_uring_queue_exit(ring);
    free(ring);
}
