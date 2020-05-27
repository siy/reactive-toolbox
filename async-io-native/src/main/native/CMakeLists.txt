cmake_minimum_required(VERSION 3.16)
project(native C)

set(CMAKE_C_STANDARD 11)

include_directories(include)

#add_executable(native
#        include/org_reactivetoolbox_asyncio_NativeIO.h
#        io_uring_jni.c)

add_library (liburingnative
        include/org_reactivetoolbox_asyncio_NativeIO.h
        io_uring_jni.c)

include_directories(SYSTEM
        /usr/lib/jvm/java-14-openjdk-amd64/include
        /usr/lib/jvm/java-14-openjdk-amd64/include/linux)