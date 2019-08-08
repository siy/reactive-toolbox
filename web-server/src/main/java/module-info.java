module org.reactivetoolbox.web {
    exports org.reactivetoolbox.web.server;
    exports org.reactivetoolbox.build;
    exports org.reactivetoolbox.web.server.parameter.conversion;
    exports org.reactivetoolbox.web.server.parameter.validation;
    exports org.reactivetoolbox.web.server.auth;
    requires org.reactivetoolbox.core;
    requires org.reactivetoolbox.eventbus;
    requires undertow.core;
}