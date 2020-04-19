package org.reactivetoolbox.net.experiment;

import org.junit.jupiter.api.Test;
import org.reactivetoolbox.core.lang.collection.List;
import org.reactivetoolbox.net.http.Method;

import java.util.UUID;

import static org.reactivetoolbox.core.lang.collection.List.list;

public interface RequestBuilder {
    static Stage1 get(String uri) {
        return new Builder(Method.GET, uri, list());
    }

    interface Stage1 {
        default Stage2 withoutParameters(Object... parameters) {
            return with();
        }

        Stage2 with(Object... parameters);
    }

    interface Stage2 {
        Request build();
    }

    class Builder implements Stage1, Stage2 {
        private final Method method;
        private final String uri;
        private final List<?> parameters;

        private Builder(final Method method, final String uri, final List<?> parameters) {
            this.method = method;
            this.uri = uri;
            this.parameters = parameters;
        }


        @Override
        public Stage2 with(final Object... parameters) {
            return new Builder(method, uri, list(parameters));
        }

        @Override
        public Request build() {
            return new Request(method, uri, parameters);
        }
    }

    class Tester {
        @Test
        void name() {
            UUID userId1 = UUID.randomUUID()
                    ;
            RequestBuilder.get("http://somewhere.com/api/users/{param1}")
                          .with(userId1)
                          .build();
        }
    }
}
