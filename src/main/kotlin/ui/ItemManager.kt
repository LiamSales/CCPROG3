package ui

import model.Item
import java.io.File

object ItemManager {

    val items = mutableListOf<Item>()

    fun loadItems() {

        items.clear()

        val file = File("data/items.csv")

        if (!file.exists())
            return

        file.readLines().forEach { line ->

            if (line.isBlank())
                return@forEach

            val parts = line.split(",")

            if (parts.size < 3)
                return@forEach

            items.add(

                Item(

                    name = parts[0],
                    calories = parts[1].toInt(),
                    iconPath = parts[2]

                )

            )

        }
    }

    fun saveItems() {

        val folder = File("data")
        folder.mkdirs()

        val file = File(folder, "items.csv")

        file.printWriter().use { out ->

            items.forEach {

                out.println(

                    "${it.name}," +
                    "${it.calories}," +
                    it.iconPath

                )

            }

        }
    }
}