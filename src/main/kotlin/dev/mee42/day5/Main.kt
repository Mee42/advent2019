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

package dev.mee42.day5

import java.io.File
import java.util.ArrayDeque
import java.util.Queue

val tape = File("res/day5.txt")
    .readLines()[0]
    .split(",")
    .map { it.toInt() }

//val testTape = listOf(3,9,7,9,10,9,4,9,99,-1,8)

fun main() {
    val input = ArrayDeque<Int>()
    input.offer(5)
    val tapeYes = tape.toMutableList()
    runp(tapeYes,input,System.err::println)
    println(tapeYes)
}

fun runp(tape: MutableList<Int>, input: Queue<Int>, output: (Int) -> Unit): Int {
    var index = 0
    while(true){
        val at = tape[index].toString().padStart(5,'0')
        println(at)
        val modes = (0..2).map { at[it] == '1' }.reversed()
        val opp = ("" + at[3] + at[4]).toInt()
        fun get(offset: Int):Int{
            return if(modes[offset - 1]){
                tape[index + offset]
            } else {
                tape[tape[index + offset]]
            }
        }
        val incr = when(opp) {
            1 -> {
                tape[tape[index + 3]] = get(1) + get(2)
                3
            }
            2 -> {
                tape[tape[index + 3]] = get(1) * get(2)
                3
            }
            3 -> {
                tape[tape[index + 1]] = input.remove()
                1
            }
            4 -> {
                output(get(1))
                1
            }
            5 -> {
                if(get(1) != 0){
                    index = get(2)
                    -1 // don't increment at all
                } else 2
            }
            6 -> {
                if(get(1) == 0){
                     index = get(2)
                    -1
                } else 2
            }
            7 -> {
                tape[tape[index + 3]] = if(get(1) < get(2)) 1 else 0
                3
            }
            8 -> {
                tape[tape[index + 3]] = if(get(1) == get(2)) 1 else 0
                3
            }
            99 -> return tape[0]
            else -> error("bitch")
        }
        index += incr + 1
    }
}