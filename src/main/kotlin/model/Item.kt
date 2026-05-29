package model

import java.io.File

 data class Item(
    val name: String,
    val calories: Int,
    val iconPath: String?
)


private const val CSV_PATH =
    "items.csv"

fun saveItem(item: Item) {

    val file =
        File(CSV_PATH)

    val line =
        "${item.name}," +
        "${item.calories}," +
        "${item.iconPath.orEmpty()}\n"

    file.appendText(line)
}


fun loadItems(): MutableList<Item> {

    val file =
        File(CSV_PATH)

    if (!file.exists()) {
        return mutableListOf()
    }


    val lines =
        file.readLines()


    val items =
        mutableListOf<Item>()

    for (line in lines) {
        
        if (line.isBlank()) continue

        val parts =
            line.split(",")

        if (parts.size < 3) continue


        val item = Item(

            name = parts[0],

            calories = parts[1].toInt(),

            iconPath = parts[2].ifBlank { null }
        )


        items.add(item)
    }


    return items
}


fun createItemAndSave(): Item {

    print("Name: ")

    val name =
        inputValidation("", 20) as String


    print("Calories: ")

    val calories =
        inputValidation(0, 0) as Int


    print("Image path: ")

    val path =
        readln().trim()

    val source =
        File(path)

    val destination =
        File("assets/${source.name}")

    destination.parentFile.mkdirs()


    source.copyTo(
        destination,
        overwrite = true
    )

    val item = Item(

        name = name,

        calories = calories,

        iconPath =
            "assets/${source.name}"
    )

    saveItem(item)


    return item
}


fun inputValidation(default: Any, maxLength: Int): Any {
    while (true) {
        val input = readLine()?.trim() ?: ""

        when (default) {
            is String -> {
                if (input.isNotEmpty() && (maxLength <= 0 || input.length <= maxLength)) {
                    return input
                }
                println("Please enter a non-empty string${if (maxLength > 0) " up to $maxLength characters." else "."}")
            }

            is Int -> {
                val number = input.toIntOrNull()
                if (number != null) {
                    return number
                }
                println("Please enter a valid whole number.")
            }

            else -> throw IllegalArgumentException("Unsupported input validation type: ${default::class}")
        }
    }
}


fun readInt(prompt: String, min: Int, max: Int = Int.MAX_VALUE): Int {
    while (true) {
        print(prompt)
        val value = readLine()?.trim()?.toIntOrNull()
        if (value != null && value in min..max) return value
        println("Please enter a number between $min and ${if (max == Int.MAX_VALUE) "∞" else max}.")
    }
}


fun readFloat(prompt: String, min: Float): Float {
    while (true) {
        print(prompt)
        val value = readLine()?.trim()?.toFloatOrNull()
        if (value != null && value >= min) return value
        println("Please enter a number greater than or equal to $min.")
    }
}


fun loadItemFromFile(): Item {
    val items = loadItems()
    if (items.isEmpty()) {
        throw IllegalStateException("No saved items found. Please create an item first.")
    }

    println("Available items:")
    items.forEachIndexed { index, item ->
        println("[${index + 1}] ${item.name} (${item.calories} kcal)")
    }

    val choice = readInt("Choose item: ", 1, items.size) - 1
    return items[choice]
}


val startingInventory: String
    get() = "Starting inventory details are not available."
