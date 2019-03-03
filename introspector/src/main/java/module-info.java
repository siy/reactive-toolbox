module org.reactivetoolbox.introspector {
    exports org.reactivetoolbox.introspector;
    requires org.reactivetoolbox.injector;
    opens org.reactivetoolbox.introspector to org.reactivetoolbox.injector;
}