package org.reactivetoolbox.core.functional;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EitherTest {
    @Test
    void theInstanceCanBeMapperIntoOtherType() {
        var original = Either.<String, Integer>success(1);

        assertEquals(Either.failure("1"), original.flatMap((right) -> Either.failure(Objects.toString(right))));
    }

    @Test
    void theFailureInstanceRemainsUnchanged() {
        var original = Either.<String, Integer>failure("1");

        assertEquals(Either.failure("1"), original.flatMap((right) -> Either.success(right + right)));
    }

    @Test
    void theFailureValueCanBeMapperIntoOtherType() {
        var original = Either.<Integer, String>failure(1);

        assertEquals(Either.failure("1"), original.mapFailure(Objects::toString));
    }

    @Test
    void theSuccessValueCanBeMapperIntoOtherType() {
        var original = Either.<String, Integer>success(1);

        assertEquals(Either.success("1"), original.mapSuccess(Objects::toString));
    }

    @Test
    void theSuccessInstanceRemainsUnchangedWhenMappedLeft() {
        var original = Either.<String, Integer>success(1);

        assertEquals(Either.success(1), original.mapFailure(Objects::toString));
    }

    @Test
    void theSuccessInstanceDoesCallConsumerInIfSuccess() {
        var original = Either.<String, Integer>success(1);
        var holder = new AtomicBoolean(false);

        original.onSuccess(val -> holder.set(true));

        assertTrue(holder.get());
    }

    @Test
    void theFailureInstanceDoesNotCallConsumerInIfSuccess() {
        var original = Either.<Integer, Integer>failure(1);
        var holder = new AtomicBoolean(false);

        original.onSuccess(val -> holder.set(true));

        assertFalse(holder.get());
    }

    @Test
    void theSuccessInstanceDoesNotCallConsumerInIfFailure() {
        var original = Either.<String, Integer>success(1);
        var holder = new AtomicBoolean(false);

        original.onFailure(val -> holder.set(true));

        assertFalse(holder.get());
    }

    @Test
    void theFailureInstanceDoesCallConsumerInIfSuccess() {
        var original = Either.<Integer, Integer>failure(1);
        var holder = new AtomicBoolean(false);

        original.onFailure(val -> holder.set(true));

        assertTrue(holder.get());
    }

    @Test
    void ifSuccessThenValueIsReturned() {
        var original = Either.<Integer, Integer>success(1);

        assertEquals(1, original.otherwise(2));
    }

    @Test
    void ifFailureThenReplacementValueIsReturned() {
        var original = Either.<Integer, Integer>failure(1);

        assertEquals(2, original.otherwise(2));
    }

    @Test
    void ifSuccessThenValueIsReturnedAndSupplierIsNotInvoked() {
        var original = Either.<Integer, Integer>success(1);
        var holder = new AtomicBoolean(false);

        assertEquals(1, original.otherwise(() -> {
            holder.set(true);
            return 2;
        }));

        assertFalse(holder.get());
    }

    @Test
    void ifFailureThenReplacementValueIsReturnedAndSupplierIsInvoked() {
        var original = Either.<Integer, Integer>failure(1);

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

    private String throwingFunction(final Integer input) throws IOException {

        if (input == 10) {
            throw new IOException("Throwing!");
        }
        return input.toString();
    }
}
