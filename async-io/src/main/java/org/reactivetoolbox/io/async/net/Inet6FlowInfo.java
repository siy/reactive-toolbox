package org.reactivetoolbox.io.async.net;

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
}
