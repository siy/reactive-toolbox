package org.reactivetoolbox.io.async.net.server;

import org.reactivetoolbox.core.lang.functional.Functions.FN1;
import org.reactivetoolbox.core.lang.functional.Unit;
import org.reactivetoolbox.io.async.Promise;
import org.reactivetoolbox.io.async.common.SizeT;
import org.reactivetoolbox.io.async.net.Inet4Address;
import org.reactivetoolbox.io.async.net.InetPort;
import org.reactivetoolbox.io.async.net.SocketAddressIn;
import org.reactivetoolbox.io.async.net.SocketFlag;
import org.reactivetoolbox.io.async.net.SocketOption;
import org.reactivetoolbox.io.async.net.context.ActiveServerContext;
import org.reactivetoolbox.io.async.net.context.ConnectionContext;
import org.reactivetoolbox.io.async.net.lifecycle.LifeCycle;
import org.reactivetoolbox.io.async.net.lifecycle.ReadWriteLifeCycle;

import java.util.Set;
import java.util.function.Consumer;

import static org.reactivetoolbox.io.async.net.InetPort.inetPort;

//TODO: add support for IPv6
public class TcpServerConfiguration {
    private final Properties properties;

    private TcpServerConfiguration(final Properties properties) {
        this.properties = properties;
    }

    public static TcpServerConfiguration configuration(final int port, final LifeCycle lifeCycle) {
        return new TcpServerConfiguration(Properties.defaultProperties(props -> {
            props.port = inetPort(port);
            props.lifeCycle = lifeCycle;
        }));
    }

    public TcpServerConfiguration and(final Consumer<Properties> transformer) {
        return new TcpServerConfiguration(properties.copy(transformer));
    }

    public SocketAddressIn address() {
        return SocketAddressIn.create(properties.port, properties.address);
    }

    public SizeT backlogSize() {
        return properties.backlogSize;
    }

    public Set<SocketFlag> listenerFlags() {
        return properties.listenerFlags;
    }

    public Set<SocketFlag> acceptorFlags() {
        return properties.acceptorFlags;
    }

    public Set<SocketOption> listenerOptions() {
        return properties.listenerOptions;
    }

    public FN1<Promise<Unit>, ConnectionContext> connectionHandler() {
        return properties.connectionHandler;
    }

    public LifeCycle lifeCycle() {
        return properties.lifeCycle;
    }

    public static final class Properties {
        public Inet4Address address = Inet4Address.INADDR_ANY;
        public InetPort port = inetPort(8081);
        public Set<SocketFlag> listenerFlags = SocketFlag.closeOnExec();
        public Set<SocketFlag> acceptorFlags = SocketFlag.closeOnExec();
        public Set<SocketOption> listenerOptions = SocketOption.reuseAll();
        //TODO: do we actually need it???
        public FN1<Promise<Unit>, ConnectionContext> connectionHandler = ActiveServerContext::defaultConnectionHandler;
        public SizeT backlogSize = new SizeT(16);
        public LifeCycle lifeCycle = ReadWriteLifeCycle.readWrite(ActiveServerContext::echo);

        private Properties() {
        }

        private static Properties defaultProperties(final Consumer<Properties> propertiesConsumer) {
            return new Properties().copy(propertiesConsumer);
        }

        private Properties copy(final Consumer<Properties> propertiesConsumer) {
            final var copy = new Properties();
            copy.address = address;
            copy.port = port;
            copy.listenerFlags = listenerFlags;
            copy.acceptorFlags = acceptorFlags;
            copy.listenerOptions = listenerOptions;
            copy.backlogSize = backlogSize;
            copy.connectionHandler = connectionHandler;
            copy.lifeCycle = lifeCycle;
            propertiesConsumer.accept(copy);
            return copy;
        }
    }
}
