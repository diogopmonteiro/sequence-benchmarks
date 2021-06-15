package io.github.diogopmonteiro.sequence

data class Position(val col: Int, val row: Int)

object SequenceBuilder {
    fun get(start: Position, end: Position) = sequence {
        for (row in start.row..end.row) {
            for (col in start.col..end.col) {
                yield(Position(col, row))
            }
        }
    }

    fun getYieldAllPerRow(start: Position, end: Position) = sequence {
        for (row in start.row..end.row) {
            yieldAll((start.col..end.col).asSequence().map { Position(it, row) })
        }
    }
}

object SequenceFlatMap {
    fun get(start: Position, end: Position) = (start.row..end.row).asSequence().flatMap { row ->
        (start.col..end.col).asSequence().map { col -> Position(col, row) }
    }
}

class RangeIterator(
    private val start: Position,
    private val end: Position
) : Iterator<Position> {

    private var column = start.col
    private var row = start.row

    override fun hasNext(): Boolean {
        return column <= end.col && row <= end.row
    }

    override fun next(): Position {
        val result = Position(column, row)
        row++
        if (row == end.row) {
            column++
            row = 0
        }
        return result
    }
}