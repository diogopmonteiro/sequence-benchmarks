package io.github.diogopmonteiro.sequence;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;

public class RangeBenchmark {

    @State(Scope.Thread)
    public static class BenchmarkState {

        public Position start = new Position(0, 0);
        public Position end = new Position(5000, 5000);

    }

    @Benchmark
    @Fork(1)
    @Warmup(iterations = 2, time = 10)
    @Measurement(iterations = 6, time = 15)
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void benchmarkSequenceBuilder(BenchmarkState state, Blackhole b) {
        Iterator<Position> it = SequenceBuilder.INSTANCE
                .get(state.start, state.end).iterator();

        while (it.hasNext()) {
            b.consume(it.next());
        }
    }

    @Benchmark
    @Fork(1)
    @Warmup(iterations = 2, time = 10)
    @Measurement(iterations = 6, time = 15)
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void benchmarkSequenceBuilderYieldAll(BenchmarkState state, Blackhole b) {
        Iterator<Position> it = SequenceBuilder.INSTANCE
                .getYieldAllPerRow(state.start, state.end).iterator();

        while (it.hasNext()) {
            b.consume(it.next());
        }
    }

    @Benchmark
    @Fork(1)
    @Warmup(iterations = 2, time = 10)
    @Measurement(iterations = 6, time = 15)
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void benchmarkRangeIterator(BenchmarkState state, Blackhole b) {
        Iterator<Position> it = new RangeIterator(state.start, state.end);

        while (it.hasNext()) {
            b.consume(it.next());
        }
    }

    @Benchmark
    @Fork(1)
    @Warmup(iterations = 2, time = 10)
    @Measurement(iterations = 6, time = 15)
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void benchmarkFlatmap(BenchmarkState state, Blackhole b) {
        Iterator<Position> it = SequenceFlatMap.INSTANCE.get(state.start, state.end).iterator();

        while (it.hasNext()) {
            b.consume(it.next());
        }
    }

    @Benchmark
    @Fork(1)
    @Warmup(iterations = 2, time = 10)
    @Measurement(iterations = 6, time = 15)
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void benchmarkEager(BenchmarkState state, Blackhole b) {
        for (Position position : SequenceBuilder.INSTANCE.getEager(state.start, state.end)) {
            b.consume(position);
        }
    }

}
