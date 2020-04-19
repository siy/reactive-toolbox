package org.reactivetoolbox.codec.json;

import org.reactivetoolbox.codec.json.Token.TokenType;
import org.reactivetoolbox.core.lang.collection.Collection;
import org.reactivetoolbox.core.lang.collection.CollectionBuilder;
import org.reactivetoolbox.core.lang.collection.List;
import org.reactivetoolbox.core.lang.functional.Option;
import org.reactivetoolbox.core.lang.functional.Result;
import org.reactivetoolbox.core.lang.support.KSUID;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

import static org.reactivetoolbox.codec.json.Scanner.scanner;
import static org.reactivetoolbox.codec.json.Token.TokenType.COMMA;
import static org.reactivetoolbox.codec.json.Token.TokenType.LB;
import static org.reactivetoolbox.codec.json.Token.TokenType.RB;
import static org.reactivetoolbox.core.lang.functional.Option.option;
import static org.reactivetoolbox.core.lang.functional.Result.ok;

public class Decoder {
    private static final Map<Class, Deserializer> PRIMITIVE_TYPES = new HashMap<>();
    private static final Map<Class, Supplier<CollectionBuilder>> COLLECTION_TYPES = new HashMap<>();

    static {
        add(Boolean.class, TokenDecoders::bool);
        add(boolean.class, TokenDecoders::bool);
        add(String.class, TokenDecoders::string);
        add(byte.class, TokenDecoders::byteInt);
        add(Byte.class, TokenDecoders::byteInt);
        add(short.class, TokenDecoders::shortInt);
        add(Short.class, TokenDecoders::shortInt);
        add(long.class, TokenDecoders::longInt);
        add(Long.class, TokenDecoders::longInt);
        add(int.class, TokenDecoders::regularInt);
        add(Integer.class, TokenDecoders::regularInt);
        add(float.class, TokenDecoders::floatNumber);
        add(Float.class, TokenDecoders::floatNumber);
        add(double.class, TokenDecoders::doubleNumber);
        add(Double.class, TokenDecoders::doubleNumber);
        add(BigDecimal.class, TokenDecoders::bigDecimal);
        add(LocalDate.class, TokenDecoders::localDate);
        add(LocalDateTime.class, TokenDecoders::localDateTime);
        add(ZonedDateTime.class, TokenDecoders::zonedDateTime);
        add(UUID.class, TokenDecoders::uuid);
        add(KSUID.class, TokenDecoders::ksuid);

        COLLECTION_TYPES.put(List.class, List::builder);
    }

    private static <T> void add(final Class<T> type, final Deserializer<T> decoder) {
        PRIMITIVE_TYPES.put(type, decoder);
    }

    private final Scanner scanner;

    private Decoder(final Scanner scanner) {
        this.scanner = scanner;
    }

    public static Decoder decoder(final String source) {
        return new Decoder(scanner(source));
    }

    public <T> Result<Option<T>> read(final Class<T> type) {
        return primitiveDeserializer(type).map(this::readPrimitive)
                                          .otherwiseGet(() -> readObject(type));
    }

    @SuppressWarnings("unchecked")
    private <T> Option<Deserializer<T>> primitiveDeserializer(final Class<T> type) {
        return option((Deserializer<T>) PRIMITIVE_TYPES.get(type));
    }

    public <T> Result<Option<Collection<T>>> read(final Class<Collection<T>> collectionType, final Class<T> elementType) {
        return reader(elementType)
                       .flatMap(reader -> collectionReader(collectionType, reader)
                                                  .flatMap(collectionReader -> collectionReader.read(scanner)));
    }

    private <T> Result<Option<T>> readObject(final Class<T> type) {
        // '{'
        //scanner.next()
        //TODO: finish it
        return null;
    }

    private <T> Result<Option<T>> readPrimitive(final Deserializer<T> deserializer) {
        return scanner.next()
                      .flatMap(deserializer);
    }

    public interface ScannerReader<T> {
        Result<Option<T>> read(final Scanner scanner);
    }

    private static <T> Result<ScannerReader<T>> reader(final Class<T> type) {
        return null;
    }

    private static <T> Result<ScannerReader<Collection<T>>> collectionReader(final Class<Collection<T>> collectionType,
                                                                             final ScannerReader<T> elementReader) {
        return option(COLLECTION_TYPES.get(collectionType))
                       .fold(v -> CodecError.error("Unable to find deserializer for collection type {0}", collectionType.getCanonicalName()),
                            factory -> Result.ok(new ScannerReader<>() {
                                @SuppressWarnings("unchecked")
                                @Override
                                public Result<Option<Collection<T>>> read(final Scanner scanner) {
                                    final var builder = (CollectionBuilder<Collection<T>, T>) factory.get();
                                    return scanner.next()
                                                  .flatMap(token -> token.type() == LB ? readElements(scanner, builder, elementReader, RB)
                                                                                       : CodecError.error("Unexpected token {0}"));
                                }
                            }));
    }

    private static <T> Result<Option<Collection<T>>> readElements(final Scanner scanner,
                                                                  final CollectionBuilder<Collection<T>, T> collectionBuilder,
                                                                  final ScannerReader<T> elementReader,
                                                                  final TokenType expectFor) {
        final Result<Boolean> supplier = elementReader.read(scanner)
                                                      .flatMap(option -> option.fold(
                                                              //TODO: what to do with missing value?
                                                              v -> CodecError.error("Unable to handle null value in collection"),
                                                              element -> {
                                                                  collectionBuilder.append(element);
                                                                  return scanner.next()
                                                                                .flatMap(token -> token.type() == COMMA
                                                                                                  ? ok(true)
                                                                                                  : ok(false));
                                                              }));


        //while ()
        return null;
    }
}
