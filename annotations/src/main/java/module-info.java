module org.reactivetoolbox.annotations {
    exports org.reactivetoolbox.annotations.injector;
    exports org.reactivetoolbox.annotations.web;
    exports org.reactivetoolbox.annotations.service;
    exports org.reactivetoolbox.annotations.configuration;
    opens org.reactivetoolbox.annotations.injector to org.reactivetoolbox.injector;
    opens org.reactivetoolbox.annotations.web to org.reactivetoolbox.injector;
    opens org.reactivetoolbox.annotations.service to org.reactivetoolbox.injector;
    opens org.reactivetoolbox.annotations.configuration to org.reactivetoolbox.injector;
}