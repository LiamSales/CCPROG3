package model

import java.io.File

data class Item(
    val name: String,
    val calories: Int,
    val iconPath: String
)


private const val CSV_PATH =
    "items.csv"

fun saveItem(item: Item) {

    val file =
        File(CSV_PATH)

    val line =
        "${item.name}," +
        "${item.calories}," +
        "${item.iconPath}\n"

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

            iconPath = parts[2]
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
        readln()

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


/**
 * Validates user input - converts string to appropriate type
 * @param default Default value if input is invalid
 * @param maxLength Maximum length for string validation (unused for non-strings)
 * @return Validated input as the appropriate type (String or Int)
 * TODO: Implement proper validation logic when requirements are clearer
 */
fun inputValidation(default: Any, maxLength: Int): Any {
    // TEMPORARY: Hardcoded validation
    return when (default) {
        is String -> {
            // String input with max length validation
            val input = readLine()?.trim() ?: ""
            if (input.isEmpty() || input.length > maxLength) default else input
        }
        is Int -> {
            // Integer input validation
            val input = readLine()?.trim()?.toIntOrNull()
            input ?: default
        }
        else -> default
    }
}


/**
 * Reads and validates an integer from user input
 * @param prompt Message to display to user
 * @param min Minimum acceptable value
 * @param max Maximum acceptable value
 * @return Valid integer within range [min, max]
 * TODO: Add retry logic for invalid inputs
 */
fun readInt(prompt: String, min: Int, max: Int): Int {
    // TEMPORARY: Hardcoded implementation - replace with proper validation
    print(prompt)
    var input = readLine()?.trim()?.toIntOrNull() ?: min
    
    // Clamp value to valid range
    if (input < min) input = min
    if (input > max) input = max
    
    return input
}


/**
 * Reads and validates a float from user input
 * @param prompt Message to display to user
 * @param minValue Minimum acceptable value
 * @return Valid float greater than or equal to minValue
 * TODO: Add proper error handling and retry logic
 */
fun readFloat(prompt: String, minValue: Float): Float {
    // TEMPORARY: Hardcoded implementation
    print(prompt)
    var input = readLine()?.trim()?.toFloatOrNull() ?: minValue
    
    // Ensure value meets minimum
    if (input < minValue) input = minValue
    
    return input
}


/**
 * Loads a single Item from a CSV file
 * @return Item loaded from file, or a default Item if file doesn't exist
 * TODO: Implement file chooser dialog for user to select which item to load
 */
fun loadItemFromFile(): Item {
    // TEMPORARY: Hardcoded - returns first item from CSV or default
    val items = loadItems()
    return if (items.isNotEmpty()) {
        items[0]
    } else {
        // Default item if file is empty
        Item("Default Item", 100, "assets/default.png")
    }
}
