[![](https://jitpack.io/v/siy/reactive-toolbox.svg)](https://jitpack.io/#siy/reactive-toolbox)
[![](https://jitci.com/gh/siy/reactive-toolbox/svg)](https://jitci.com/gh/siy/reactive-toolbox)


# Reactive Toolbox (Work In Progress) 
An opinionated set of Java libraries for reactive systems development

## Motivation
Are you tired with bloated and slow Java frameworks? Me too. Countless layers of run-time reflection calls, opaque configuration, 
a lot of assumptions and subtle details, unexpected and non-obvious behavior, just to name some of the most obvious issues.

We're so get used to it that many (especially Java haters) believe that this is inherent to all Java applications. But this is 
simply not true. Java is clean, elegant and really fast language. We just need to use all its power instead of accepting bloated 
and slow frameworks in exchange for promise of "fast delivery" (which are false anyway). As a consequence of using those frameworks 
we got stuck with aged Java 8. We just sitting, reading articles about shiny new features of recent versions of Java. We waiting for 
vendors to deliver version of their frameworks compatible with recent versions of Java. Meanwhile Java started moving much faster 
with its 6-month release cycle... It's time to stop waiting and start doing.

Of course, there are some good alternatives to those slow and bloated frameworks. Some alternatives (for example, great 
[Micronaut](https://micronaut.io/) framework) provide quite familiar experience while enabling use of modern Java. 
I realize that providing familiar experience to developers simplifies transition,
but in the same time it conserves old approaches - "servlet-like" (synchronous) request processing, heavy use of exceptions, etc.
Yes, I know that there is an asynchronous processing support. But it's rather artificial and, outside of simple examples, 
quite inconvenient.

Instead of trying to provide similar experience, Reactive Toolbox targeted to provide consistent and convenient to use environment 
for writing asynchronous applications. To achieve this goal, Reactive Toolbox heavily uses functional approaches and tries to avoid 
use of exceptions as much as possible. Of course, I realize that there is a lot of other code which throws exceptions (including JDK), 
so Reactive Toolbox provides ways to deal with such a code.

Well, in fact there is not so much new in the Reactive Toolbox approaches, vast majority of ideas are borrowed from various sources.
Reactive Toolbox just mixes and blends them together into consistent and inter-operable tool set.

#### Why exceptions are bad in application code
Exceptions are not so bad in general. But being used in application code they introduce indirect execution path which is hard to follow
and opaque in code. Once you throw exception there is no way to tell where it will be caught and handled (and how). Beside that, 
exceptions are quite slow comparing to normal execution flow.

## Reactive Toolbox Core

This library contains basic primitives used across all libraries.

- ``Either`` - type used to return either execution result or error. Its the basic tool to write code which does not throw exceptions. 
- ``Tuple1-Tuple9`` - immutable type safe containers for set of values of different types.
- ``Promise`` - eventual result of asynchronous operation.

These three types are base for convenient writing of asynchronous code without introduction of "callback hell".

## Reactive Toolbox Web Server

This library contains implementation of simple reactive web microframework.

### Routing Configuration Example 

```
        final var userService = new UserService();

        final var server = ServerAssembler.with(
            with("/v1/",
                 with("/clips",
                      when(GET, "/one/two/{param1}/{param2}")
                          .description("....")
                          .with(inPath(String.class, "param1").and(Is.required()),
                                inPath(String.class, "param2").and(Is.required()).description("Second parameter"),
                                inQuery(Integer.class, "limit").and(Is.required()),
                                inAuthHeader(AuthorizationHeaderType.JWT).and(Is::loggedIn))
                          .then((param1, param2, limit, user) -> readyOk("[" + param1 + ", " + param2 + ", " + limit + ", " + user.userId() + "]"))
                          //Simple example of request postprocessing, in this case - setting of user-defined headers to response
                          .after((context, result) -> result.then(value -> context.response().setHeader("X-User-Defined", "processed"))),

                      when(POST, "/two/three/{param1}/{param2}/{param3}")
                          .description(".....")
                          .with(inPath(String.class, "param1").and(Is.required()),
                                inPath(UUID.class, "param2").and(Is.required()),
                                inPath(Integer.class, "param3").and(Is.required()),
                                inAuthHeader(AuthorizationHeaderType.JWT).and(Is::loggedIn)
                                                                         .and(Is::belongsToAny, TestRoles.REGULAR, TestRoles.ADMIN))
                          // Cross-parameter validation, here does nothing, but can be used to check if overall combination of parameters is valid
                          .and((param1, param2, param3, user) -> valid(param1, param2, param3, user))
                          .then((param1, param2, param3, user) -> readyOk("[" + user.userId() + "]:" + param1 + " " + param2 + " " + param3))),

                 with("/user",
                      when(PUT, "")
                          .description("Get profile of logged in user")
                          .with(inAuthHeader(AuthorizationHeaderType.JWT).and(Is::loggedIn).and(Is::belongsToAll, TestRoles.REGULAR))
                          .then(userService::getProfile),

                      //User login request
                      when(POST, "/login")
                          .description("Login user into application")
                          .with(inBody(String.class, "login")
                                    .and(Is.notNullOrEmpty())
                                    .description("User login"),
                                inBody(String.class, "password")
                                    .and(Is.notNullOrEmpty())
                                    .description("User password"))
                          .then(userService::login),

                      //More or less traditional user registration request validation
                      when(POST, "/register")
                          .description("Register new user into system")
                          .with(inBody(String.class, "login")
                                    .and(Is.lenBetween(6, 128)).and(Is.email())
                                    .description("Desired user login"),
                                inBody(String.class, "password")
                                    .and(Is.lenBetween(6, 128))
                                    .and(Is.strongPassword())
                                    .description("Proposed user password"))
                          .then(userService::login))),

            //Simplest entrypoint description, independently attached to root
            when(GET, "/")
                .description("Root request")
                .withoutParameters()
                .then(() -> readyOk("Hello world!")),
            when(GET, "/health")
                .description("Healtcheck entrypoint")
                .withoutParameters()
                .then(() -> readyOk("{\"status\":\"UP\"}"))
        ).build(UndertowServerAdapter::with);

```

Note that number and type of parameters passed to request handler are checked by compiler. 

(TBD)
