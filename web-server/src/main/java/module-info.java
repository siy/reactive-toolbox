module org.reactivetoolbox.web {
    requires org.reactivetoolbox.core;
    requires org.reactivetoolbox.codec;
    requires org.reactivetoolbox.eventbus;
    requires undertow.core;

    exports org.reactivetoolbox.build;
    exports org.reactivetoolbox.web.server;
    exports org.reactivetoolbox.web.server.http;
    exports org.reactivetoolbox.web.server.auth;
}