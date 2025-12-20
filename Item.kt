import java.io.File
import java.awt.image.BufferedImage //in-memory bitmap image (pixels live in heap memory), as vector values
import javax.imageio.ImageIO //utility class to decode image files (PNG, JPG) into BufferedImage


data class Item(
    val name: String = "",
    val calories: Int = 0,
    val icon: BufferedImage? = null //a pointer to the heap mem or pix
)

fun saveItemToFile(item: Item, iconFileName: String?) { //serializes metadata, Images stay as files; text files store references.
    val file = File("items/${item.name}.txt") //This does not create the file yet, It’s just a path abstraction
    file.parentFile.mkdirs() //Checks filesystem, creates directories if missing; safe to call repeatedly

    file.writeText( //Now we actually touch disk.
        buildString {
            appendLine(item.name)
            appendLine(item.calories)
            appendLine(iconFileName ?: "NONE") // no nulls are written to disk
        }
    )
}

fun createItemAndSave(): Item {
    print("Name: ")
    val name = inputValidation("", 10) as String

    print("Calories: ")
    val calories = inputValidation(0,0) as Int

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
        dest.parentFile.mkdirs() //Ensures src/icons/ exists.
        source.copyTo(dest, overwrite = true)

        icon = ImageIO.read(dest)
    }

    val item = Item(name, calories, icon)
    saveItemToFile(item, iconFileName)

    return item
}

fun loadItemFromFile(): Item {
    print("Enter item name to load: ")
    val name = inputValidation("",0) as String

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