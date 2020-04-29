#include <include/org_reactivetoolbox_asyncio_IoUring.h>
#include <liburing.h>

jfieldID nativePtr_fid;
struct io_uring_probe* uring_probe;

JNIEXPORT void JNICALL Java_org_reactivetoolbox_asyncio_IoUring_initIds(JNIEnv * env, jclass clazz) {
     nativePtr_fid = (*env)->GetFieldID(env, clazz, "nativePtr", "J");
     uring_probe = io_uring_get_probe();
}

