import java.io.File
import java.awt.image.BufferedImage
import javax.imageio.ImageIO


data class Item(
    val name: String,
    val calories: Int,
    val icon: BufferedImage?
)

fun saveItemToFile(item: Item, iconFileName: String?) {
    val file = File("items/${item.name}.txt")
    file.parentFile.mkdirs()

    file.writeText(
        buildString {
            appendLine(item.name)
            appendLine(item.calories)
            appendLine(iconFileName ?: "NONE")
        }
    )
}

fun createItemAndSave(): Item {
    print("Name: ")
    val name = inputValidation("") as String

    print("Calories: ")
    val calories = inputValidation(0) as Int

    print("Image path (blank = none): ")
    val path = readln()

    val iconFileName: String?
    val icon: BufferedImage?

    if (path.isBlank()) {
        icon = null
        iconFileName = null
    } else {
        val source = File(path)
        iconFileName = source.name

        val dest = File("src/icons/$iconFileName")
        dest.parentFile.mkdirs()
        source.copyTo(dest, overwrite = true)

        icon = ImageIO.read(dest)
    }

    val item = Item(name, calories, icon)
    saveItemToFile(item, iconFileName)

    return item
}

fun loadItemFromFile(): Item {
    print("Enter item name to load: ")
    val name = inputValidation("") as String

    val file = File("items/$name.txt")
    if (!file.exists()) error("Item file not found.")

    val lines = file.readLines()
    val itemName = lines[0]
    val calories = lines[1].toInt()
    val iconName = lines[2]

    val icon =
        if (iconName == "NONE") null
        else ImageIO.read(File("src/icons/$iconName"))

    return Item(itemName, calories, icon)
}