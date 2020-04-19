package org.reactivetoolbox.codec.json.parser;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jsoniter.JsonIterator;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.LongStream;

public class Bench {
    private static final int BATCH_SIZE = 25;
    private static final int PRE_HEATING_COUNT = 200_000;
    private static final int BENCH_COUNT = PRE_HEATING_COUNT / 100;

    private static LongStream range(final long upper) {
        return LongStream.range(0, upper);
    }

    public static void main(final String[] args) {
        final String[] shortData = prepareData(BenchData.dataShort);
        final String[] middleData = prepareData(BenchData.dataMiddle);
        final String[] longData = prepareData(BenchData.dataLong);

        System.out.println("Pre-heating...");

        preheatSingle("...fast...", shortData, Bench::benchFaster);
        preheatSingle("...iter...", shortData, Bench::benchJsonIter);
        preheatSingle("...raw (pure)...", shortData, Bench::benchRawParserPure);
        preheatSingle("...raw (orig)...", shortData, Bench::benchRawParserOrig);

        System.out.println("Benchmarking...");

        doBench(String.format("Short (%db)", BenchData.dataShort.getBytes().length), shortData);
        doBench(String.format("Middle (%db)", BenchData.dataMiddle.getBytes().length), middleData);
        doBench(String.format("Long (%db)", BenchData.dataLong.getBytes().length), longData);
    }

    public static void preheatSingle(final String name, final String[] data, final Consumer<String[]> fn) {
        System.out.println(name);
        range(PRE_HEATING_COUNT).forEach(n -> fn.accept(data));
    }

    public static String[] prepareData(final String data) {
        final String[] shortData = new String[BATCH_SIZE];
        Arrays.fill(shortData, data);

        return shortData;
    }

    public static void doBench(final String benchName, final String[] toParse) {
        range(3).forEach(i -> {
            System.out.printf("Data %s, pass %d...\n", benchName, i + 1);
            bench(toParse);
            System.out.println();
        });
    }

    private static void bench(final String[] toParse) {
        final long iteratorTime = benchSingleParser(toParse, Bench::benchJsonIter);
        final long fasterTime = benchSingleParser(toParse, Bench::benchFaster);
        final long rawParserPureTime = benchSingleParser(toParse, Bench::benchRawParserPure);
        final long rawParserOrigTime = benchSingleParser(toParse, Bench::benchRawParserOrig);

        final long byteCount = Arrays.stream(toParse)
                                     .mapToInt(s -> s.getBytes(StandardCharsets.UTF_8).length)
                                     .sum();

        printTime("Faster", fasterTime, fasterTime, byteCount);
        printTime("Iterator", iteratorTime, fasterTime, byteCount);
        printTime("RawParser (orig)", rawParserOrigTime, fasterTime, byteCount);
        printTime("RawParser (pure)", rawParserPureTime, fasterTime, byteCount);
    }

    private static long benchSingleParser(final String[] data, final Consumer<String[]> parser) {
        return range(BENCH_COUNT).map(i -> bench(() -> parser.accept(data))).sum() / BENCH_COUNT;
    }

    private static void printTime(final String title, final long runTime, final long baseTime, final long byteCount) {
        final var ms = runTime / 1_000_000;
        final var mks = (runTime % 1_000_000) / 1_000;

        final double ratio = (double) baseTime / (double) runTime;
        final double speed = (((double) byteCount) / (double) runTime) * 1e3; //Scale to get MB/s

        System.out.printf("%s : %d.%03dms/run, ratio: %.2f, speed: %.2f MB/s\n", title, ms, mks, ratio, speed);
    }

    private static long bench(final Runnable fn) {
        final long before = System.nanoTime();
        fn.run();
        return System.nanoTime() - before;
    }

    private static void benchFaster(final String[] toParse) {
        final ObjectMapper mapper = new ObjectMapper();

        for (final String parse : toParse) {
            try {
                mapper.readValue(parse, new TypeReference<List<Map<String, Object>>>() {});
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void benchJsonIter(final String[] toParse) {
        for (final String parse : toParse) {
            JsonIterator.deserialize(parse).asList();
        }
    }

    private static void benchRawParserPure(final String[] toParse) {
        for (final String parse : toParse) {
            JsonRawParse1.parse(parse).root();
        }
    }

    private static void benchRawParserOrig(final String[] toParse) {
        for (final String parse : toParse) {
            JsonRawParse1Orig.parse(parse).root();
        }
    }
}
