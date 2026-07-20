package model

import java.io.File

data class Item(

    val name: String,

    val calories: Int,

    val iconPath: String

)

private const val CSV_PATH = "items.csv"



fun saveItem(item: Item) {

    val file = File(CSV_PATH)

    if (!file.exists()) {
        file.createNewFile()
    }

    file.appendText(

        "${item.name}," +
        "${item.calories}," +
        "${item.iconPath}\n"

    )
}



fun loadItems(): MutableList<Item> {

    val file = File(CSV_PATH)

    if (!file.exists()) {
        return mutableListOf()
    }

    val items = mutableListOf<Item>()

    file.forEachLine { line ->

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

    val file = File(CSV_PATH)

    file.writeText("")

    items.forEach {

        file.appendText(

            "${it.name}," +
            "${it.calories}," +
            "${it.iconPath}\n"

        )

    }

}