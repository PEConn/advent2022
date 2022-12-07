package day07

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

data class File(
    val dir: List<String>,
    val name: String,

    val size: Int
)

private fun parseInput(input: String): Set<File> {
    val files = mutableSetOf<File>()
    var dir = listOf("/")

    input.lines().filterNot(String::isEmpty).forEach {
        if (it.startsWith("$ cd ")) {
            // Format: $ cd xyz
            val target = it.split(' ')[2]
            if (target == "/") {
                dir = listOf("/")
            } else if (target == "..") {
                dir = dir.dropLast(1)
            } else {
                dir = dir + target
            }
        } else if (it.startsWith("$ ls")) {
            // Do nothing.
        } else {
            // Format: 123 filename
            val parts = it.split(" ")
            if (parts[0] != "dir") {
                files.add(File(
                    dir = dir,
                    name = parts[1],
                    size = parts[0].toInt()
                ))
            }
        }
    }

    return files
}

data class MeasuredDirectory(
    val dir: List<String>,
    val size: Long
)

private fun calculateDirectorySizes(files: Set<File>): Set<MeasuredDirectory> {
    val dirs = HashMap<List<String>, Long>()

    files.forEach { file ->
        file.dir.indices.forEach { i ->
            // Add the file size to each directory along its path
            val dir = file.dir.take(i + 1)
            dirs[dir] = dirs.getOrDefault(dir, 0) + file.size
        }
    }

    return dirs.map { MeasuredDirectory(it.key, it.value) }.toSet()
}

private fun part1(input: String): Long {
    return calculateDirectorySizes(parseInput(input)).filter { it.size <= 100000 }.sumOf { it.size }
}

private fun part2(input: String): Long {
    val dirs = calculateDirectorySizes(parseInput(input))

    val targetSize: Long = 70000000 - 30000000

    // Yeah, this is a little ugly...
    val consumedSpace: Long = dirs.first { it.dir == listOf("/") }.size
    val spaceToFree = consumedSpace - targetSize

    return dirs.filter { it.size >= spaceToFree }.minOf { it.size }
}

const val GIVEN_EXAMPLE = """
${'$'} cd /
${'$'} ls
dir a
14848514 b.txt
8504156 c.dat
dir d
${'$'} cd a
${'$'} ls
dir e
29116 f
2557 g
62596 h.lst
${'$'} cd e
${'$'} ls
584 i
${'$'} cd ..
${'$'} cd ..
${'$'} cd d
${'$'} ls
4060174 j
8033020 d.log
5626152 d.ext
7214296 k
"""

class Day07Test {
    @Test
    fun testParse() {
        val expected = setOf(
            File(listOf("/", "a", "e"), "i", 584),
            File(listOf("/", "a"), "f", 29116),
            File(listOf("/", "a"), "g", 2557),
            File(listOf("/", "a"), "h.lst", 62596),
            File(listOf("/"), "b.txt", 14848514),
            File(listOf("/"), "c.dat", 8504156),
            File(listOf("/", "d"), "j", 4060174),
            File(listOf("/", "d"), "d.log", 8033020),
            File(listOf("/", "d"), "d.ext", 5626152),
            File(listOf("/", "d"), "k", 7214296),
        )

        assertEquals(expected, parseInput(GIVEN_EXAMPLE))
    }

    @Test
    fun testCalculateSizes() {
        val expected = setOf(
            MeasuredDirectory(listOf("/"), 48381165),
            MeasuredDirectory(listOf("/", "a"), 94853 ),
            MeasuredDirectory(listOf("/", "a", "e"), 584),
            MeasuredDirectory(listOf("/", "d"), 24933642),
        )

        val dirs = calculateDirectorySizes(parseInput(GIVEN_EXAMPLE))

        assertEquals(expected, dirs)
    }

    @Test
    fun example1() {
        assertEquals(95437, part1(GIVEN_EXAMPLE))
    }

    @Test
    fun challenge1() {
        assertEquals(1348005, part1(java.io.File("input/day07.txt").readText(Charsets.UTF_8)))
    }

    @Test
    fun example2() {
        assertEquals(24933642, part2(GIVEN_EXAMPLE))
    }

    @Test
    fun challenge2() {
        assertEquals(12785886, part2(java.io.File("input/day07.txt").readText(Charsets.UTF_8)))
    }
}