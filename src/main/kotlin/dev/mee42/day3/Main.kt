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
import java.util.*


val file = File("res/day3.txt")

fun main() {

    val (points1, points2) = file.readLines().map(::plotWire)

    println(points1.filterKeys { key -> points2.containsKey(key) }.map { (point,i) ->
        i + points2.getValue(point) + 2
    }.min())

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
fun plotWire(strIn: String): Map<Point,Int> {
    var index = 0
    var current = Point(0,0)
    val map = mutableMapOf<Point,Int>()
    strIn.split(",")
        .map {
            when(it[0]) {
                'R' -> Direction(isOnXAxis = true,   isPositive = true)
                'L' -> Direction(isOnXAxis = true,  isPositive = false)
                'U' -> Direction(isOnXAxis = false,  isPositive = true)
                'D' -> Direction(isOnXAxis = false, isPositive = false)
                else -> error("you done fucked up")
            } to it.substring(1).toInt()
        }.flatMap { f -> Collections.nCopies(f.second,f.first) }
        .forEach { current = current.next(it);map[current] = index++ }
    return map
}