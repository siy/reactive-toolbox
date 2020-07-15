package org.reactivetoolbox.core.examples.lang;

import org.reactivetoolbox.core.lang.functional.Failure;

import java.util.Optional;

import static java.util.Optional.ofNullable;
import static org.reactivetoolbox.core.lang.functional.Option.option;

public class OptionExample {
    private static final Failure USER_NOT_FOUND = null;

    private final UserService userService;
    private final OptionalUserService optionalUserService;
    private final UserProfileService userProfileService;
    private final OptionalUserProfileService optionalUserProfileService;

    public OptionExample(final UserService userService,
                         final OptionalUserService optionalUserService,
                         final UserProfileService userProfileService,
                         final OptionalUserProfileService optionalUserProfileService) {
        this.userService = userService;
        this.optionalUserService = optionalUserService;
        this.userProfileService = userProfileService;
        this.optionalUserProfileService = optionalUserProfileService;
    }

    public UserProfileResponse getUserProfileHandler(final User.Id userId) {
        final User user = userService.findById(userId);
        if (user == null) {
            return UserProfileResponse.error(USER_NOT_FOUND);
        }

        final UserProfileDetails details = userProfileService.findById(userId);

        if (details == null) {
            return UserProfileResponse.of(user, UserProfileDetails.defaultDetails());
        }

        return UserProfileResponse.of(user, details);
    }

    public UserProfileResponse getUserProfileHandler2(final User.Id userId) {
//V1
//        final User user = userService.findById(userId);
//
//        if (user == null) {
//            return UserProfileResponse.error(USER_NOT_FOUND);
//        }
//
//        final UserProfileDetails details = userProfileService.findById(userId);
//
//        if (details == null) {
//            return UserProfileResponse.of(user, UserProfileDetails.defaultDetails());
//        }
//
//        return UserProfileResponse.of(user, details);
//-----------------------------------------------------------
//V2
//final User user = userService.findById(userId);
//
//if (user == null) {
//    return UserProfileResponse.error(USER_NOT_FOUND);
//}
//
//final UserProfileDetails details = userProfileService.findById(userId);
//
//return UserProfileResponse.of(user,
//                              details == null
//                              ? UserProfileDetails.defaultDetails()
//                              : details);
//-----------------------------------------------------------
//V3
//final User user = userService.findById(userId);
//
//if (user == null) {
//    return UserProfileResponse.error(USER_NOT_FOUND);
//}
//
//final Option<UserProfileDetails> details = Option.option(userProfileService.findById(userId));
//
//return UserProfileResponse.of(user,
//                              details.otherwiseGet(UserProfileDetails::defaultDetails));
//-----------------------------------------------------------
//V4
//final Option<User> user1 = Option.option(userService.findById(userId));
//
//return user1.map(user -> {
//    final Option<UserProfileDetails> details = Option.option(userProfileService.findById(userId));
//    return UserProfileResponse.of(user, details.otherwiseGet(UserProfileDetails::defaultDetails));
//})
//            .otherwiseGet(() -> UserProfileResponse.error(USER_NOT_FOUND));
//-----------------------------------------------------------
//V5
//        return Option.option(userService.findById(userId))
//                     .map(user -> UserProfileResponse.of(user,
//                                                         Option.option(userProfileService.findById(userId))
//                                                               .otherwiseGet(UserProfileDetails::defaultDetails)))
//                    .otherwiseGet(() -> UserProfileResponse.error(USER_NOT_FOUND));
//-----------------------------------------------------------
//V6
        return option(userService.findById(userId))
                .map(user -> UserProfileResponse.of(user,
                                                    option(userProfileService.findById(userId))
                                                            .otherwiseGet(UserProfileDetails::defaultDetails)))
                .otherwiseGet(() -> UserProfileResponse.error(USER_NOT_FOUND));
    }

    public UserProfileResponse getUserProfileHandler3(final User.Id userId) {
        return ofNullable(userService.findById(userId))
                .map(user -> UserProfileResponse.of(user,
                                                    ofNullable(userProfileService.findById(userId))
                                                            .orElseGet(UserProfileDetails::defaultDetails)))
                .orElseGet(() -> UserProfileResponse.error(USER_NOT_FOUND));
    }

    public Optional<UserProfileResponse> getUserProfileHandler4(final User.Id userId) {
        return optionalUserService.findById(userId)
                                  .flatMap(user -> optionalUserProfileService.findById(userId)
                                                                             .map(profile -> UserProfileResponse.of(user, profile)));
    }
}
