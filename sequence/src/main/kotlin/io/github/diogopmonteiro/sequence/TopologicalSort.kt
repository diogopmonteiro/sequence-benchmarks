package io.github.diogopmonteiro.sequence

import java.lang.RuntimeException
import java.util.*
import kotlin.NoSuchElementException

class DirectedGraph<V> {
    private val outgoingEdges = mutableMapOf<V, MutableSet<V>>()
    private val incomingEdges = mutableMapOf<V, MutableSet<V>>()

    fun addEdge(source: V, target: V) {
        addToSet(source, target, outgoingEdges)
        addToSet(target, source, incomingEdges)
    }

    private fun addToSet(source: V, target: V, s: MutableMap<V, MutableSet<V>>) {
        val edgeSet = outgoingEdges[source]

        if (edgeSet == null) {
            outgoingEdges[source] = mutableSetOf(target)
            return
        }

        edgeSet.add(target)
    }

    fun outgoingEdgesOf(v: V): Set<V> = outgoingEdges[v] ?: emptySet()

    fun incomingEdgesOf(v: V): Set<V> = incomingEdges[v] ?: emptySet()

    fun vertices(): Set<V> = outgoingEdges.keys
}

class MutableInteger(
    var value: Int
)

/**
 * Inspired by https://github.com/jgrapht/jgrapht/blob/master/jgrapht-core/src/main/java/org/jgrapht/traverse/TopologicalOrderIterator.java
 */
class TopologicalSortIterator<V>(
    private val graph: DirectedGraph<V>
) : Iterator<V> {

    private val queue: Queue<V> = ArrayDeque()
    private val inDegreeMap = mutableMapOf<V, MutableInteger>()
    private var remainingVertices = graph.vertices().size
    private var current: V? = null

    init {
        graph.vertices().forEach { v ->
            var d = 0
            graph.incomingEdgesOf(v).forEach { u ->
                if (v == u) {
                    throw RuntimeException("Graph is not a DAG")
                }
                d++
            }
            inDegreeMap[v] = MutableInteger(d)
            if (d == 0) {
                queue.offer(v)
            }
        }
    }

    override fun hasNext(): Boolean {
        if (current != null) {
            return true
        }

        current = advance()
        return current != null
    }

    private fun advance(): V {
        val result = queue.poll()
        if (result != null) {
            graph.outgoingEdgesOf(result).forEach { other ->
                val inDegree = inDegreeMap[other]!!
                if (inDegree.value > 0) {
                    inDegree.value--
                    if (inDegree.value == 0) {
                        queue.offer(other)
                    }
                }
            }
            remainingVertices--
        } else {
            if (remainingVertices > 0) {
                throw RuntimeException("Graph is not a DAG")
            }
        }
        return result
    }

    override fun next(): V {
        if (!hasNext()) {
            throw NoSuchElementException()
        }

        val result = current
        current = null
        return result!!
    }
}

object TopologicalSortSequenceBuilder {
    fun <V> get(graph: DirectedGraph<V>) = sequence {
        val queue: Queue<V> = ArrayDeque()
        val inDegreeMap = mutableMapOf<V, MutableInteger>()
        var remainingVertices = graph.vertices().size

        graph.vertices().forEach { v ->
            var d = 0
            graph.incomingEdgesOf(v).forEach { u ->
                if (v == u) {
                    throw RuntimeException("Graph is not a DAG")
                }
                d++
            }
            inDegreeMap[v] = MutableInteger(d)
            if (d == 0) {
                queue.offer(v)
            }
        }

        while (queue.isNotEmpty()) {
            val result = queue.poll()

            graph.outgoingEdgesOf(result).forEach { other ->
                val inDegree = inDegreeMap[other]!!
                if (inDegree.value > 0) {
                    inDegree.value--
                    if (inDegree.value == 0) {
                        queue.offer(other)
                    }
                }
            }
            remainingVertices--
            yield(result!!)
        }

        if (remainingVertices > 0) {
            throw RuntimeException("Graph is not a DAG")
        }
    }
}