package org.reactivetoolbox.core.functional;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EitherTest {

//    @Test
//    void instanceCanBeDeserializedForRightValue() throws IOException {
//        final ObjectMapper mapper = ObjectMapperSingleton.get();
//
//        final Either<String, Integer> value =
//                mapper.readValue("{\"left\":null,\"right\":10}\n",
//                        new TypeReference<Either<String, Integer>>() {});
//
//        assertTrue(value.isRight());
//        assertEquals(Integer.valueOf(10), value.right().get());
//    }
//
//    @Test
//    void instanceCanBeDeserializedForLeftValue() throws IOException {
//        final ObjectMapper mapper = ObjectMapperSingleton.get();
//
//        final Either<String, Integer> value =
//                mapper.readValue("{\"left\":\"Some text\",\"right\":null}\n",
//                        new TypeReference<Either<String, Integer>>() {});
//
//        assertTrue(value.isLeft());
//        assertEquals("Some text", value.left().get());
//    }

    @Test
    void functionCanBeLiftedAndWillReturnValueOrException() {
        final Function<Integer, Either<Exception, String>> newFunction = Either.lift(this::throwingFunction);

        final Either<Exception, String> rightResult = newFunction.apply(3);

        assertTrue(rightResult.isRight());
        assertEquals("3", rightResult.right().get());

        final Either<Exception, String> leftResult = newFunction.apply(10);

        assertTrue(leftResult.isLeft());
        assertEquals(IOException.class, leftResult.left().get().getClass());
    }

    @Test
    void functionCanBeLiftedAndWillReturnValueOrExceptionAndInitialValue() {
        final Function<Integer, Either<Pair<Exception, Integer>, String>> newFunction = Either.liftWithValue(this::throwingFunction);

        final Either<Pair<Exception, Integer>, String> rightResult = newFunction.apply(3);

        assertTrue(rightResult.isRight());
        assertEquals("3", rightResult.right().get());

        final Either<Pair<Exception, Integer>, String> leftResult = newFunction.apply(10);

        assertTrue(leftResult.isLeft());
        assertEquals(Pair.class, leftResult.left().get().getClass());

        final Pair<Exception, Integer> pair = leftResult.left().get();

        assertEquals(IOException.class, pair.left().getClass());
        assertEquals(Integer.valueOf(10), pair.right());
    }

    @Test
    void exceptionCanBeMappedIntoDifferentValue() {
        final Function<Integer, Either<String, String>> newFunction = Either.lift(this::throwingFunction,
                                                                                  Throwable::getMessage);

        final Either<String, String> rightResult = newFunction.apply(3);

        assertTrue(rightResult.isRight());
        assertEquals("3", rightResult.right().get());

        final Either<String, String> leftResult = newFunction.apply(10);

        assertTrue(leftResult.isLeft());
        assertEquals("Throwing!", leftResult.left().get());
    }

    private String throwingFunction(final Integer input) throws IOException {

        if (input == 10) {
            throw new IOException("Throwing!");
        }
        return input.toString();
    }
}
