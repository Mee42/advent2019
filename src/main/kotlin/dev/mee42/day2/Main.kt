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

package dev.mee42.day2

const val baseInputString = "1,0,0,3,1,1,2,3,1,3,4,3,1,5,0,3,2,13,1,19,1,19,9,23,1,5,23,27,1,27,9,31,1,6,31,35,2,35,9,39,1,39,6,43,2,9,43,47,1,47,6,51,2,51,9,55,1,5,55,59,2,59,6,63,1,9,63,67,1,67,10,71,1,71,13,75,2,13,75,79,1,6,79,83,2,9,83,87,1,87,6,91,2,10,91,95,2,13,95,99,1,9,99,103,1,5,103,107,2,9,107,111,1,111,5,115,1,115,5,119,1,10,119,123,1,13,123,127,1,2,127,131,1,131,13,0,99,2,14,0,0"

val baseInput = baseInputString.split(",").map { it.toInt() }

fun main() {
    println((0..99).flatMap { a -> (0..99).map { b -> Pair(a, b) } }.first { (a, b) -> runWithDiff(a, b) == 19690720 })
}
fun runWithDiff(noun: Int, verb: Int):Int {
    val arr = baseInput.toMutableList()
    arr[1] = noun
    arr[2] = verb
    return run(arr)
}

fun run(tape: MutableList<Int>): Int {
    var index = 0
    while(true){
        when(tape[index]) {
            1 -> tape[tape[index + 3]] = tape[tape[index + 2]] + tape[tape[index + 1]]
            2 -> tape[tape[index + 3]] = tape[tape[index + 2]] * tape[tape[index + 1]]
            99 -> return tape[0]
        }
        index += 4
    }
}