package io.github.diogopmonteiro.sequence;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;

public class TopologicalSortBenchmark {

    @State(Scope.Thread)
    public static class BenchmarkState {

        public DirectedGraph<String> graph = new DirectedGraph<>();

        @Setup(Level.Trial)
        public void setup() {
            for (int i = 0; i < 150000; i++) {
                graph.addEdge(String.valueOf(i), String.valueOf(i*200000));
                graph.addEdge(String.valueOf(i), String.valueOf(i*200000+1));
                graph.addEdge(String.valueOf(i), String.valueOf(i*200000+2));
                graph.addEdge(String.valueOf(i), String.valueOf(i*200000+3));
                graph.addEdge(String.valueOf(i), String.valueOf(i*200000+4));
                graph.addEdge(String.valueOf(i), String.valueOf(i*200000+5));


                if (i != 0) {
                    graph.addEdge(String.valueOf(i-1), String.valueOf(i));
                }
            }
            System.out.println("Generated graph with " + graph.vertices().size() + " vertices.");
        }

    }

    @Benchmark
    @Fork(1)
    @Warmup(iterations = 2, time = 10)
    @Measurement(iterations = 3, time = 15)
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void benchmarkTopologicalSortSequenceBuilder(BenchmarkState state, Blackhole b) {
        Iterator<String> it = TopologicalSortSequenceBuilder.INSTANCE.get(state.graph).iterator();

        while (it.hasNext()) {
            b.consume(it.next());
        }
    }

    @Benchmark
    @Fork(1)
    @Warmup(iterations = 2, time = 10)
    @Measurement(iterations = 3, time = 15)
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void benchmarkTopologicalSortIterator(BenchmarkState state, Blackhole b) {
        Iterator<String> it = new TopologicalSortIterator<>(state.graph);

        while (it.hasNext()) {
            b.consume(it.next());
        }
    }
}
