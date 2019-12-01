package dev.mee42.day1

import java.io.File


fun main() {
    println(File("src/main/resources/day1.txt").readLines().sumBy { fuelForMass(it.toInt()) })
}

fun fuelForMass(i :Int): Int {
    val fuel = i / 3 - 2
    if(fuel <= 0) return 0
    return fuel + fuelForMass(fuel)
}