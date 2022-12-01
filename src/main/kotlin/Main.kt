import java.io.File

fun main(args: Array<String>) {
    val path = System.getProperty("user.dir")

    println("Working Directory = $path")

    val input = File("input/day1.txt").readText(Charsets.UTF_8)

    println(day01part1(input))
    println(day01part2(input))
}

