cd src/main/java
javac --enable-preview --release 15 -h ../native/include/ -d ../../../target/classes org/reactivetoolbox/io/uring/Uring.java
