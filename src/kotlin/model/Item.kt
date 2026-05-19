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
