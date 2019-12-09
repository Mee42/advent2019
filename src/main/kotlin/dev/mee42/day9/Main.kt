/*
 * Copyright (c) 2019 Carson Graham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package dev.mee42.day9

import java.io.File
import java.util.*

val inTape = File("res/day9.txt")
    .readLines()[0]
    .split(",")
    .map { it.toLong() }

val testTape = "104,1125899906842624,99"
    .split(",")
    .map { it.toLong() }

val tape = inTape

fun main() {
    runp(tape.copyToInfinite(0), inputOf(2L),::println)
}


fun <T> inputOf(vararg inputs: T) :Queue<T> {
    val queue = ArrayDeque<T>()
    for(i in inputs) {
        queue.offer(i)
    }
    return queue
}


enum class Modes(val mode: Int){
    PARAMETER(0),
    IMMEDIATE(1),
    RELATIVE(2)
}

fun <T> List<T>.copyToInfinite(defaultValue: T): InfiniteList<T> {
    return InfiniteList(defaultValue,this)
}

class InfiniteList<T>(private val defaultValue: T, initial: List<T>) {
    private val map = mutableMapOf<Long,T>()
    init {
        initial.forEachIndexed { i, v -> map[i.toLong()] = v}
    }
    operator fun set(index: Long, element: T) {
        map[index] = element
    }

    operator fun get(index: Long): T {
        return map[index] ?: defaultValue
    }
}


fun runp(tape: InfiniteList<Long>, input: Queue<Long>, output: (Long) -> Unit) {
    var index = 0L
    var relativeBase = 0L
    while(true){
        val at = tape[index].toString().padStart(5,'0')
        val modes = (0..2).map {
            Modes.values().first { mode -> mode.mode == at[it].toString().toInt() }
        }.reversed()
        val opp = ("" + at[3] + at[4]).toInt()
        fun get(offset: Int):Long{
            return when(modes[offset - 1]) {
                Modes.IMMEDIATE -> tape[index + offset]
                Modes.PARAMETER -> tape[tape[index + offset]]
                Modes.RELATIVE  -> tape[tape[index + offset] + relativeBase]
            }
        }
        fun set(offset: Int, new :Long) {
            when(modes[offset - 1]) {
                Modes.IMMEDIATE -> error("no")
                Modes.PARAMETER -> tape[tape[index + offset]] = new
                Modes.RELATIVE -> tape[tape[index + offset] + relativeBase] = new
            }
        }
        if(at == "203") {}
        val incr = when(opp) {
            1 -> {
                set(3,get(1) + get(2))
                3
            }
            2 -> {
                set(3,get(1) * get(2))
                3
            }
            3 -> {
                set(1,input.remove())
                1
            }
            4 -> {
                output(get(1))
                1
            }
            5 -> {
                if(get(1) != 0L){
                    index = get(2)
                    -1 // don't increment at all
                } else 2
            }
            6 -> {
                if(get(1) == 0L){
                     index = get(2)
                    -1
                } else 2
            }
            7 -> {
                set(3,if(get(1) < get(2)) 1 else 0)
                3
            }
            8 -> {
                set(3,if(get(1) == get(2)) 1 else 0)
                3
            }
            9 -> {
                relativeBase += get(1)
                1
            }
            99 -> return
            else -> error("bitch")
        }
        index += incr + 1
    }
}