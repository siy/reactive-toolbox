module org.reactivetoolbox.configuration {
    requires org.reactivetoolbox.annotations;
    exports org.reactivetoolbox.configuration;
    opens org.reactivetoolbox.configuration to org.reactivetoolbox.injector;
}