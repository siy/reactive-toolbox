package org.reactivetoolbox.web.server.parameter.auth;

import org.reactivetoolbox.core.functional.Option;
import org.reactivetoolbox.web.server.parameter.validation.Validator;

public class AuthenticationConverter {
    public static Validator<Option<Authentication>, Option<String>> create(final AuthHeader header) {
        return switch(header) {
            case JWT -> JwtConverter.create();
            case BASIC -> BasicConverter.create();
        };
    }
}
