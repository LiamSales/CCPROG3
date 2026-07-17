package model

import java.io.File

data class Item(

    val name: String,

    val calories: Int,

    val iconPath: String

)

private val DATA_DIR = File("data")
private val CSV_FILE = File(DATA_DIR, "items.csv")


fun saveItem(item: Item) {

    if (!DATA_DIR.exists()) {
        DATA_DIR.mkdirs()
    }

    if (!CSV_FILE.exists()) {
        CSV_FILE.createNewFile()
    }

    CSV_FILE.appendText(

        "${item.name}," +
        "${item.calories}," +
        "${item.iconPath}\n"

    )
}


fun loadItems(): MutableList<Item> {

    if (!CSV_FILE.exists()) {
        return mutableListOf()
    }

    val items = mutableListOf<Item>()

    CSV_FILE.forEachLine { line ->

        if (line.isBlank()) return@forEachLine

        val parts = line.split(",")

        if (parts.size != 3) return@forEachLine

        items.add(

            Item(

                name = parts[0],

                calories = parts[1].toInt(),

                iconPath = parts[2]

            )

        )

    }

    return items
}


fun getItem(name: String): Item? {

    return loadItems().find {

        it.name.equals(
            name,
            ignoreCase = true
        )

    }

}


fun deleteItem(name: String) {

    val items = loadItems()

    items.removeIf {

        it.name.equals(
            name,
            ignoreCase = true
        )

    }

    overwriteItems(items)

}


fun overwriteItems(items: List<Item>) {

    if (!DATA_DIR.exists()) {
        DATA_DIR.mkdirs()
    }

    CSV_FILE.writeText("")

    items.forEach {

        CSV_FILE.appendText(

            "${it.name}," +
            "${it.calories}," +
            "${it.iconPath}\n"

        )

    }

}