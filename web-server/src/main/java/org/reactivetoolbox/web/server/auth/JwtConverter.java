package org.reactivetoolbox.web.server.auth;

import org.reactivetoolbox.core.functional.Option;
import org.reactivetoolbox.web.server.parameter.validation.Validator;

/**
 * Converter which transforms string from "Authorization" header into instance of {@link Authentication}
 */
public class JwtConverter {
    public static Validator<Option<Authentication>, Option<String>> create() {
        //TODO: finish it
        return null;
    }
}
