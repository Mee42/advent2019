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

package dev.mee42.day10

import java.io.File
import kotlin.math.*

data class Point(val x: Int, val y: Int)
class World<T>(private val arr: Array<Array<T>>) {
    operator fun get(point: Point):T {
        return arr[point.y][point.x]
    }
    operator fun set(point: Point, v: T) {
        arr[point.y][point.x] = v
    }
    val getArrUnsafe
        get() = arr
    inline fun <reified R> map(mapper: (Point, T) -> R):World<R> {
        return World(
            getArrUnsafe.mapIndexed { y, ar ->
                ar.mapIndexed { x, value -> mapper(Point(x,y),value) }.toTypedArray()
            }.toTypedArray()
        )
    }
    val points : List<Point> = arr.indices.flatMap { y -> arr[y].mapIndexed { x,_ -> Point(x,y) } }

    var stringer : ((T) -> String)? = null

    override fun toString(): String {
        fun string(t: T) = stringer?.invoke(t) ?: t.toString()
        val length = arr.flatten().map { string(it).length }.max()!! + 1
        var s = ""
        for(y in arr.indices) {
            for(x in arr[y].indices){
                s += string(get(Point(x,y))).padEnd(length)
            }
            s += "\n"
        }
        return s
    }
}

const val inputString = """
.#..#
.....
#####
....#
...##
"""

val realInput = File("res/day10.txt").readText()

fun main1() {
    val world = parse(realInput)
    world.stringer = { if(it) "1" else "0" }
    println(world)

    // for each asteroid, map the entire world to the vectors to get to that point from that point
    // then, for any that are identical, eliminate the one with the longest length

    // let's start with the asteroid at (3,4)

    
//    val realCenterPoint = 28, 29

    val answer = world.asteroids().map { centerPoint ->

        val vectors = world.map { point, isAsteroid ->
            if (!isAsteroid)  null else {
                val xDis = point.x - centerPoint.x
                val yDis = point.y - centerPoint.y
                Vector.of(xDis, yDis, sqrt(xDis.toDouble().pow(2) + yDis.toDouble().pow(2)))
            }
        }
        val leftOver = vectors.noneNull().filter { (point, vect) ->
            // vect is shortest vector with the same direction
            if (point == centerPoint) return@filter false
            val other = vectors.noneNull()
                .filter { (otherPoint, otherVect) -> vect.isSameDirection(otherVect) && point != otherPoint }
//            println("other: $other")
            if (other.isEmpty()) true
            else other.all { (_, otherVect) -> vect.length < otherVect.length }
        }
        println("center point: $centerPoint ${leftOver.size}")
        leftOver.size to centerPoint
    }.maxBy { it.first }!!.second


    println(answer)
}

val exampleInput = """
.#..##.###...#######
##.############..##.
.#.######.########.#
.###.#######.####.#.
#####.##.#.##.###.##
..#####..#.#########
####################
#.####....###.#.#.##
##.#################
#####.##.###..####..
..######..##.#######
####.##.####...##..#
.#####..#.######.###
##...#.##########...
#.##########.#######
.####.#.###.###.#.##
....##.##.###..#####
.#.#.###########.###
#.#.#.#####.####.###
###.##.####.##.#..##
""".trimIndent()

fun main() {
    val world = parse(realInput)
    world.stringer = { if(it) "1" else "0" }
    println(world)
//    val centerPoint = Point(8, 3)
//    val centerPoint = Point(11,13)
    val centerPoint = Point(28,29)
    val vectorField = world.map { point, isAsteroid ->
        when {
            centerPoint == point -> null
            isAsteroid -> {
                val xDis = point.x - centerPoint.x
                val yDis = point.y - centerPoint.y
                Vector.of(xDis, yDis, sqrt(xDis.toDouble().pow(2) + yDis.toDouble().pow(2)))
            }
            else -> null
        }
    }
    println(vectorField)
    var angle = -90.0 - 0.000001
    var index = 0
    fun clockwiseDistanceBetween(angle1: Double, angle2: Double) :Double {
        return ((angle2 + 360) - angle1) % 360
    }
    val copy = vectorField.map<Int?> { _, _ -> null }
    copy.stringer = { it?.toString() ?: "." }
    vectorField.stringer = { if (it == null) "." else "#" }
    while(vectorField.noneNull().isNotEmpty()) {
        // find the point of the first asteroid that's the smallest value after `angle`
        val destroy = vectorField.noneNull().minBy { clockwiseDistanceBetween(angle,it.second.direction()) * 1000 + it.second.length }!!
        vectorField[destroy.first] = null
        copy[destroy.first] = index + 1
        angle = destroy.second.direction() + 0.001
        println("" + ++index + ": $destroy")
//        println(copy)
        if(index == 200) {
            println("=====")
        }
    }
}


fun World<Boolean>.asteroids():List<Point>{
    return points.filter { get(it) }
}
fun <T> World<T?>.noneNull(): List<Pair<Point,T>> {
    return points.map { it to get(it) }
        .filter { it.second != null }
        .map { it.first to it.second!! }
}

class Vector private constructor(val xUp: Int, val yUp: Int, val length: Double){
    override fun toString(): String {
        return "{$xUp,$yUp:${length.times(100).toInt().toDouble().div(100)}}"
    }


    companion object {
        fun of(xUp_: Int, yUp_: Int, length: Double) :Vector {
            // simplify
            var xUp = xUp_
            var yUp = yUp_
            var divisor = 2
            while(divisor <= max(xUp.absoluteValue,yUp.absoluteValue) + 1){
                if(xUp.absoluteValue % divisor == 0 && yUp.absoluteValue % divisor == 0) {
                    // divide
                    xUp /= divisor
                    yUp /= divisor
                } else {
                    divisor++
                }
            }
            if(xUp == 0) {
                if(yUp > 0) {
                    yUp = 1
                } else if(yUp < 0) {
                    yUp = -1
                }
            } else if(yUp == 0) {
                if(xUp > 0){
                    xUp = 1
                } else if(xUp < 0) {
                    xUp = -1
                }
            }
            return Vector(xUp,yUp,length)
        }
    }

    fun isSameDirection(other: Vector) :Boolean {
        //        println(" $this and $other are " + if(result) "equal" else "not equal")
        return xUp == other.xUp && yUp == other.yUp && xUp.toDouble().div(yUp) == other.xUp.toDouble().div(other.yUp)
    }

    fun direction() : Double {
        return Math.toDegrees(atan2(yUp.toDouble(), xUp.toDouble()))
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Vector

        if (xUp != other.xUp) return false
        if (yUp != other.yUp) return false
        if (length != other.length) return false

        return true
    }

    override fun hashCode(): Int {
        var result = xUp
        result = 31 * result + yUp
        result = 31 * result + length.hashCode()
        return result
    }
}

fun parse(inputString: String) =
    World(inputString.lines()
        .filter { it.isNotBlank() }
        .map { line ->
            line.map { char -> char == '#' }.toTypedArray()
        }.toTypedArray())
