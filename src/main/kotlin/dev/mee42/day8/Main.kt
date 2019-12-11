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

package dev.mee42.day8

import java.io.File

const val testInput = "123456789012"

val realInput = File("res/day8.txt")
    .readText()

class Layer(private val pixels :Array<Array<Int>>) {
override fun toString(): String {
    val s = StringBuilder()
    for(x in pixels.indices) {
        for(y in pixels[x].indices) {
            when {
                !printTransparentAs2 && pixels[x][y] == 2 -> s.append(' ',' ')
                printWhiteAsYes && pixels[x][y] == 1 -> s.append('█',' ')
                printWhiteAsYes && pixels[x][y] == 0 -> s.append("░",' ')
                else -> s.append(pixels[x][y].toString(), ' ')
            }
        }
        s.append('\n')
    }
    return s.toString()
}
    fun countOf(i :Int):Int {
        return pixels.sumBy { arr ->
            arr.count { it == i }
        }
    }

    val indexes: List<Pair<Int,Int>>
        get() = pixels.indices.map { x -> pixels[x].indices.map { y -> x to y } }.flatten()

    operator fun get(x: Int, y: Int): Int {
        return pixels[x][y]
    }
    operator fun set(x: Int, y: Int, value: Int) {
        pixels[x][y] = value
    }
    fun copy():Layer {
        return Layer(pixels.map { it.clone() }.toTypedArray())
    }
    companion object {
        var printWhiteAsYes = false
        var printTransparentAs2 = true
    }
}

fun main() {
    val layers = realInput.toPicture(25,6)
    for(layer in layers) {
        println(layer)
    }
    val specialLayer = layers.minBy { it.countOf(0) } ?: error("fuck you")
    println(specialLayer)
    val answer = specialLayer.countOf(1) * specialLayer.countOf(2)
    println(answer)

    Layer.printTransparentAs2 = false
    Layer.printWhiteAsYes     = true
    val newLayer = layers[0].copy()
    var layerIndex = 1
    while(layerIndex < layers.size && newLayer.countOf(1) + newLayer.countOf(2) != 0) {
        val applyLayer = layers[layerIndex]
        // apply applyLayer to newLayer
        for((x,y) in newLayer.indexes){
            if(newLayer[x,y] == 2) {
                newLayer[x,y] = applyLayer[x,y]
            }
        }
        layerIndex++
        println(newLayer)
    }

}

fun String.toPicture(width: Int, height: Int): List<Layer> {
    val layers = mutableListOf<Layer>()
    var i = 0
    while(i != this.length) {
        val arr = arrayOfNulls<Array<Int>>(height)
        for(y in 0 until height) {
            val subArr = arrayOfNulls<Int>(width)
            for (x in 0 until width) {
                subArr[x] = this[i++].toString().toInt()
            }
            arr[y] = subArr.map { it!! }.toTypedArray()
        }
        layers.add(Layer(arr.map { it!! }.toTypedArray()))
    }
    return layers
}