package org.reactivetoolbox.json;

//TODO: Javadoc
public class TypeCodec<T> {
    private final Class<T> type;
    private final TypeEncoder<T> encoder;
    private final TypeDecoder<T> decoder;

    private TypeCodec(final Class<T> type,
                     final TypeEncoder<T> encoder,
                     final TypeDecoder<T> decoder) {
        this.type = type;
        this.encoder = encoder;
        this.decoder = decoder;
    }

    public static <T> TypeCodec<T> of(final Class<T> type, final TypeEncoder<T> encoder, final TypeDecoder<T> decoder) {
        return new TypeCodec<>(type, encoder, decoder);
    }

    public static <T> TypeCodec<T> codec(final Class<T> type, final TypeEncoder<T> encoder, final TypeDecoder<T> decoder) {
        return new TypeCodec<>(type, encoder, decoder);
    }

    public TypeEncoder<T> encoder() {
        return encoder;
    }

    public TypeDecoder<T> decoder() {
        return decoder;
    }

    public Class<T> type() {
        return type;
    }
}
