package org.reactivetoolbox.web.server.auth;

import org.reactivetoolbox.core.functional.Option;
import org.reactivetoolbox.value.validation.Validator;

public class AuthenticationConverter {
    public static Validator<Option<Authentication>, Option<String>> create(final AuthorizationHeaderType header) {
        return switch(header) {
            case JWT -> JwtConverter.create();
            case BASIC -> BasicConverter.create();
        };
    }
}
