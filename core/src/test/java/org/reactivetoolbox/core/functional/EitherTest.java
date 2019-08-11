package org.reactivetoolbox.core.functional;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EitherTest {
    @Test
    void theInstanceCanBeMapperIntoOtherType() {
        final var original = Either.<String, Integer>success(1);

        assertEquals(Either.failure("1"), original.flatMap((right) -> Either.failure(Objects.toString(right))));
    }

    @Test
    void theFailureInstanceRemainsUnchanged() {
        final var original = Either.<String, Integer>failure("1");

        assertEquals(Either.failure("1"), original.flatMap((right) -> Either.success(right + right)));
    }

    @Test
    void theFailureValueCanBeMapperIntoOtherType() {
        final var original = Either.<Integer, String>failure(1);

        assertEquals(Either.failure("1"), original.mapFailure(Objects::toString));
    }

    @Test
    void theSuccessValueCanBeMapperIntoOtherType() {
        final var original = Either.<String, Integer>success(1);

        assertEquals(Either.success("1"), original.mapSuccess(Objects::toString));
    }

    @Test
    void theSuccessInstanceRemainsUnchangedWhenMappedLeft() {
        final var original = Either.<String, Integer>success(1);

        assertEquals(Either.success(1), original.mapFailure(Objects::toString));
    }

    @Test
    void theSuccessInstanceDoesCallConsumerInIfSuccess() {
        final var original = Either.<String, Integer>success(1);
        final var holder = new AtomicBoolean(false);

        original.onSuccess(val -> holder.set(true));

        assertTrue(holder.get());
    }

    @Test
    void theFailureInstanceDoesNotCallConsumerInIfSuccess() {
        final var original = Either.<Integer, Integer>failure(1);
        final var holder = new AtomicBoolean(false);

        original.onSuccess(val -> holder.set(true));

        assertFalse(holder.get());
    }

    @Test
    void theSuccessInstanceDoesNotCallConsumerInIfFailure() {
        final var original = Either.<String, Integer>success(1);
        final var holder = new AtomicBoolean(false);

        original.onFailure(val -> holder.set(true));

        assertFalse(holder.get());
    }

    @Test
    void theFailureInstanceDoesCallConsumerInIfSuccess() {
        final var original = Either.<Integer, Integer>failure(1);
        final var holder = new AtomicBoolean(false);

        original.onFailure(val -> holder.set(true));

        assertTrue(holder.get());
    }

    @Test
    void ifSuccessThenValueIsReturned() {
        final var original = Either.<Integer, Integer>success(1);

        assertEquals(1, original.otherwise(2));
    }

    @Test
    void ifFailureThenReplacementValueIsReturned() {
        final var original = Either.<Integer, Integer>failure(1);

        assertEquals(2, original.otherwise(2));
    }

    @Test
    void ifSuccessThenValueIsReturnedAndSupplierIsNotInvoked() {
        final var original = Either.<Integer, Integer>success(1);
        final var holder = new AtomicBoolean(false);

        assertEquals(1, original.otherwise(() -> {
            holder.set(true);
            return 2;
        }));

        assertFalse(holder.get());
    }

    @Test
    void ifFailureThenReplacementValueIsReturnedAndSupplierIsInvoked() {
        final var original = Either.<Integer, Integer>failure(1);

        assertEquals(2, original.otherwise(() -> 2));
    }

    //    @Test
//    void instanceCanBeDeserializedForRightValue() throws IOException {
//        final ObjectMapper mapper = ObjectMapperSingleton.get();
//
//        final Either<String, Integer> get =
//                mapper.readValue("{\"failure\":null,\"success\":10}\n",
//                        new TypeReference<Either<String, Integer>>() {});
//
//        assertTrue(get.isSuccess());
//        assertEquals(Integer.valueOf(10), get.success().get());
//    }
//
//    @Test
//    void instanceCanBeDeserializedForLeftValue() throws IOException {
//        final ObjectMapper mapper = ObjectMapperSingleton.get();
//
//        final Either<String, Integer> get =
//                mapper.readValue("{\"failure\":\"Some text\",\"success\":null}\n",
//                        new TypeReference<Either<String, Integer>>() {});
//
//        assertTrue(get.isFailure());
//        assertEquals("Some text", get.failure().get());
//    }

    @Test
    void functionCanBeLiftedAndWillReturnValueOrException() {
        final Function<Integer, Either<Throwable, String>> newFunction = Either.lift(this::throwingFunction);

        final Either<Throwable, String> rightResult = newFunction.apply(3);

        assertTrue(rightResult.isSuccess());
        assertEquals("3", rightResult.success().get());

        final Either<Throwable, String> leftResult = newFunction.apply(10);

        assertTrue(leftResult.isFailure());
        assertEquals(IOException.class, leftResult.failure().get().getClass());
    }

    @Test
    void functionCanBeLiftedAndWillReturnValueOrExceptionAndInitialValue() {
        final Function<Integer, Either<Pair<Throwable, Integer>, String>> newFunction = Either.liftWithValue(this::throwingFunction);

        final Either<Pair<Throwable, Integer>, String> rightResult = newFunction.apply(3);

        assertTrue(rightResult.isSuccess());
        assertEquals("3", rightResult.success().get());

        final Either<Pair<Throwable, Integer>, String> leftResult = newFunction.apply(10);

        assertTrue(leftResult.isFailure());
        assertEquals(Pair.class, leftResult.failure().get().getClass());

        final Pair<Throwable, Integer> pair = leftResult.failure().get();

        assertEquals(IOException.class, pair.left().getClass());
        assertEquals(Integer.valueOf(10), pair.right());
    }

    @Test
    void exceptionCanBeMappedIntoDifferentValue() {
        final Function<Integer, Either<String, String>> newFunction = Either.lift(this::throwingFunction)
                .andThen(result -> result.mapFailure(Throwable::getMessage));

        final Either<String, String> rightResult = newFunction.apply(3);

        assertTrue(rightResult.isSuccess());
        assertEquals("3", rightResult.success().get());

        final Either<String, String> leftResult = newFunction.apply(10);

        assertTrue(leftResult.isFailure());
        assertEquals("Throwing!", leftResult.failure().get());
    }

    @Test
    void statesAreMutuallyExclusive() {
        final Either<String, String> success = Either.success("success");
        final Either<String, String> failure = Either.failure("failure");

        assertTrue(success.isSuccess());
        assertFalse(success.isFailure());
        assertEquals(Option.of("success"), success.success());
        assertEquals(Option.empty(), success.failure());

        assertTrue(failure.isFailure());
        assertFalse(failure.isSuccess());
        assertEquals(Option.empty(), failure.success());
        assertEquals(Option.of("failure"), failure.failure());
    }

    @Test
    void failureRemainsFailureAfterSuccessMapping() {
        final Either<String, Integer> failure = Either.failure("failure");

        final Either<String, String> mapped = failure.mapSuccess(Object::toString);

        assertEquals(failure, mapped);
    }

    private String throwingFunction(final Integer input) throws IOException {

        if (input == 10) {
            throw new IOException("Throwing!");
        }
        return input.toString();
    }
}
