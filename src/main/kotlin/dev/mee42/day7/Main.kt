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

package dev.mee42.day7

import java.io.File
import java.time.Duration
import java.util.*
import kotlin.concurrent.thread
import kotlin.system.measureNanoTime
import kotlin.time.measureTime

val inTape = File("res/day7.txt")
    .readLines()[0]
    .split(",")
    .map { it.toInt() }

val testTape = "3,26,1001,26,-4,26,3,27,1002,27,2,27,1,27,26,27,4,27,1001,28,-1,28,1005,28,6,99,0,0,5"
    .split(",")
    .map { it.toInt() }

val tape = inTape

fun main() {
    val duration = measureNanoTime {
        val perms = listOf(5, 6, 7, 8, 9).permutations()
        println("permutations:" + perms.size)
        var i = 0
        val answer =
            perms
                .parallelStream()
                .map {
                    println("starting ~${i++}. ~" + (i.toDouble().times(100) / perms.size).toString() + "% done")
                    it
                }
                .map { it to runAmps(it) }
                .map {
                    println("Done with ${it.first}: ${it.second}")
                    it
                }
                .max { a, b -> a.second.compareTo(b.second) }
        println(answer)
    }.let { Duration.ofNanos(it) }
    println("" + duration.toMinutesPart() + "m   " + duration.toSecondsPart() + "s   "  + duration.toMillisPart() + "ms");
}

private fun <T> List<T>.permutations() :List<List<T>> {
    return perms(size,this)
}

fun <T> perms(length: Int, components: List<T>): List<List<T>> =
    if (components.isEmpty() || length <= 0) listOf(emptyList())
    else perms(length - 1, components)
        .flatMap { sub -> components.map { sub + it } }
        .filter { it.distinct().size == it.size }


fun runAmps(inputs: List<Int>) : Int {
    val startingPower = 0
    val aQueue = ArrayDeque<Int>()
    aQueue.offer(inputs[0])
    aQueue.offer(startingPower)
    val bQueue = ArrayDeque<Int>()
    bQueue.offer(inputs[1])
    val cQueue = ArrayDeque<Int>()
    cQueue.offer(inputs[2])
    val dQueue = ArrayDeque<Int>()
    dQueue.offer(inputs[3])
    val eQueue = ArrayDeque<Int>()
    eQueue.offer(inputs[4])


    val aThread = thread {
        runp("A",tape.toMutableList(),aQueue) {
//            println("A sends out $it")
            synchronized(bQueue) {
                bQueue.offer(it)
            }
        }
    }
    val bThread = thread {
        runp("B",tape.toMutableList(),bQueue) {
//            println("B sends out $it")
            synchronized(cQueue) {
                cQueue.offer(it)
            }
        }
    }
    val cThread = thread(start = true) {
        runp("C",tape.toMutableList(),cQueue) {
//            println("C sends out $it")
            synchronized(dQueue) {
                dQueue.offer(it)
            }
        }
    }
    val dThread = thread(start = true) {
        runp("D",tape.toMutableList(),dQueue) {
//            println("D sends out $it")
            synchronized(eQueue) {
                eQueue.offer(it)
            }
        }
    }
    val returValues = Collections.synchronizedList(mutableListOf<Int>())
    val eThread = thread(start = true) {
        runp("E",tape.toMutableList(),eQueue) {
//            println("E sends out $it")
            synchronized(aQueue) {
                aQueue.offer(it)
            }
            returValues.add(it)
        }
    }
    aThread.join()
    bThread.join()
    cThread.join()
    dThread.join()
    eThread.join()
    return returValues.max()!!
}

fun inputOf(vararg inputs: Int) :Queue<Int> {
    val queue = ArrayDeque<Int>()
    for(i in inputs) {
        queue.offer(i)
    }
    return queue
}

fun runp(name: String, tape: MutableList<Int>, input: Queue<Int>, output: (Int) -> Unit): Int {
    var index = 0
    while(true){
        val at = tape[index].toString().padStart(5,'0')
//        println("$name:$at")
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
//                println("$name waiting for input")
                var o :Int? = null
                while(o == null){
                    synchronized(input) {
                        o =  input.poll()
                    }
                    Thread.sleep(100)
                }
//                println("$name got input $o")
                tape[tape[index + 1]] = o!!
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