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

package dev.mee42.day3

import java.io.File
import java.time.Duration
import java.util.*
import kotlin.math.absoluteValue
import kotlin.system.measureNanoTime


val file = File("res/day3.txt")

fun <R> time(str :String, runner: () -> R):R{
    var r :R? = null
    val nano = measureNanoTime {
        r = runner()
    }
    println(str.replace("()","" + Duration.ofNanos(nano).toSeconds() + "s " + Duration.ofNanos(nano).toMillisPart() + "ms"))
    return r!!
}

fun main() {

    val (points1, points2) = time("plotted wires in ()") { file.readLines().map(::plotWire) }

//    val points1 = plotWire("R8,U5,L5,D3")
//    val points2 = plotWire("U7,R6,D4,L4")

//    val points1 = plotWire("R98,U47,R26,D63,R33,U87,L62,D20,R33,U53,R51")
//    val points2 = plotWire("U98,R91,D20,R16,D67,R40,U7,R15,U6,R7")


    val hits = time("found hits in ()") { points1.filter { points2.contains(it) } }
    println(hits)

    // find the shortest distance to each of the hits
    val maybe = time("calculated distance in ()") {hits.map { hit ->
        val i1 = points1.indexOf(hit)
        val i2 = points2.indexOf(hit)
        i1 + i2 + 2
    } }
    println(maybe)
    println("answer:" + maybe.min()!!)

}

data class Point(val x: Int, val y: Int){
    fun next(direction: Direction) :Point {
        return if(direction.isOnXAxis) {
            Point(if(direction.isPositive) x + 1 else x - 1, y)
        } else {
            Point(x,if(direction.isPositive) y + 1 else y - 1)
        }
    }
}
class Direction(val isOnXAxis: Boolean, val isPositive: Boolean)
fun plotWire(strIn: String): List<Point> {
    var current = Point(0,0)
    return strIn.split(",")
        .map {
            when(it[0]) {
                'R' -> Direction(isOnXAxis = true,   isPositive = true)
                'L' -> Direction(isOnXAxis = true,  isPositive = false)
                'U' -> Direction(isOnXAxis = false,  isPositive = true)
                'D' -> Direction(isOnXAxis = false, isPositive = false)
                else -> error("you done fucked up")
            } to it.substring(1).toInt()
        }.flatMap { f -> Collections.nCopies(f.second,f.first) }
        .map { current = current.next(it);current }
}