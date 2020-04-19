package org.reactivetoolbox.core.lang;

import org.junit.jupiter.api.Test;
import org.reactivetoolbox.core.lang.functional.ThrowingFunctions.TFN1;
import org.reactivetoolbox.core.lang.support.WebFailureTypes;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.reactivetoolbox.core.lang.functional.ThrowingFunctions.lift;

class ThrowingFunctionsTest {

    @Test
    void lift1() {
        final var uriParser = lift((TFN1<URI, String>) URI::new);

        uriParser.apply("https://dev.to/")
                 .onFailure(failure -> fail())
                 .onSuccess(uri -> assertEquals("https://dev.to/", uri.toString()));

        uriParser.apply(":malformed/url")
                 .onFailure(failure -> assertEquals(failure.type(), WebFailureTypes.BAD_REQUEST))
                 .onSuccess(uri -> fail());
    }
}