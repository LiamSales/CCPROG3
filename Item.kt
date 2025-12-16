data class Item(
    val name: String,
    val calories: Int,
    val icon: BufferedImage?
)

fun createItemAndSave(): Item {
    print("Name: ")
    val name = readln()

    print("Calories: ")
    val calories = inputValidation(0) as Int

    print("Image path (blank = none): ")
    val path = readln()
    val icon = if (path.isBlank()) null else ImageIO.read(File(path))

    val item = Item(name, calories, icon)
    saveItemToFile(item)

    return item
}

fun loadItemFromFile(): Item {
    
}