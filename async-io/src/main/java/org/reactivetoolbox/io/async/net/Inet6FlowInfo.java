package org.reactivetoolbox.io.async.net;

/**
 * IPv6 flow info.
 */
public class Inet6FlowInfo {
    private final int flowInfo;

    private Inet6FlowInfo(final int flowInfo) {
        this.flowInfo = flowInfo;
    }

    public static Inet6FlowInfo inet6FlowInfo(int flowInfo) {
        return new Inet6FlowInfo(flowInfo);
    }

    public int value() {
        return flowInfo;
    }

    @Override
    public String toString() {
        return "Inet6FlowInfo(" + flowInfo + ')';
    }
}
