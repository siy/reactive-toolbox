package org.reactivetoolbox.net.http.server.router;

import org.junit.jupiter.api.Test;
import org.reactivetoolbox.codec.validation.Is;
import org.reactivetoolbox.core.async.Promise;
import org.reactivetoolbox.core.lang.collection.List;
import org.reactivetoolbox.core.lang.support.KSUID;
import org.reactivetoolbox.net.http.ContentType;

import static org.reactivetoolbox.core.async.Promise.readyOk;
import static org.reactivetoolbox.net.http.server.router.RequestParameters.inBody;
import static org.reactivetoolbox.net.http.server.router.RequestParameters.inPath;
import static org.reactivetoolbox.net.http.server.router.RouteBuilder.get;
import static org.reactivetoolbox.net.http.server.router.RouteBuilder.patch;
import static org.reactivetoolbox.net.http.server.router.Router.router;
import static org.reactivetoolbox.net.http.server.router.Routes.routes;

class HttpRouteBuilderTest {

    @Test
    void routeCanBeCreated() {
        final Route one = get("/users/{id}")
                                  .accepts(ContentType.TEXT_PLAIN)
                                  .returns(ContentType.JSON)
                                  .with(inPath(String.class, "id"))
                                  .then(id -> readyOk(""));
    }

    @Test
    void apiCanBeBuilt() {
        final UserService userService = new UserService() {};
        final FeedService feedService = new FeedService() {};

        final RequestParameter<KSUID> userId = inPath(KSUID.class, "userId").and(Is::notNull);

        router("/v1",
               routes("/user/{userId}",
                      get().with(userId)
                           .then(userService::profile),
                      patch().with(userId,
                                   inBody(ProfileUpdate.class).and(Is::notNull))
                             .then(userService::update)),
               routes("/feed",
                      get().without()
                           .then(feedService::anonymous),
                      get("{userId}").with(userId)
                                     .then(feedService::customized)));
    }

    interface UserService {
        default Promise<UserProfile> profile(final KSUID userId) {
            return Promise.readyOk(null);
        }

        default Promise<Status> update(final KSUID userId, final ProfileUpdate update) {
            return Promise.readyOk(null);
        }
    }

    interface FeedService {
        default Promise<List<FeedEntry>> anonymous() {
            return Promise.readyOk(List.list());
        }

        default Promise<List<FeedEntry>> customized(final KSUID userId) {
            return Promise.readyOk(List.list());
        }
    }

    interface UserProfile {
    }

    class ProfileUpdate {

    }

    class Status {

    }

    class FeedEntry {

    }
}