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

These three types enable are base for convenient writing of asynchronous code without introduction of "callback hell".

(TBD)
