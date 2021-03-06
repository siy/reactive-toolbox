module org.reactivetoolbox.io.async {
    requires org.reactivetoolbox.core;
    requires jdk.unsupported;

    exports org.reactivetoolbox.io;
    exports org.reactivetoolbox.io.async;
    exports org.reactivetoolbox.io.async.common;
    exports org.reactivetoolbox.io.async.file;
    exports org.reactivetoolbox.io.async.file.stat;
    exports org.reactivetoolbox.io.async.net;
    exports org.reactivetoolbox.io.async.net.server;
    exports org.reactivetoolbox.io.async.net.context;
    exports org.reactivetoolbox.io.async.net.lifecycle;
    exports org.reactivetoolbox.io.async.util;
    exports org.reactivetoolbox.io.scheduler;
}
