package org.reactivetoolbox.web.server;

//TODO: rework and rename
public interface ErrorDescriptor {
    ErrorDescriptor PARAMETER_IS_NULL = new ErrorDescriptor() {};
    ErrorDescriptor PARAMETER_IS_BELOW_RANGE = new ErrorDescriptor() {};
    ErrorDescriptor PARAMETER_IS_ABOVE_RANGE = new ErrorDescriptor() {};
}
