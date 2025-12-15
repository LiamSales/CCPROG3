data class Item(
    val name: String,
    val calories: Int,
    val icon: BufferedImage?
)

                    val name = inputValidation(readln())
                    val icon = ImageIO.read(input)
                    val calories = inputValidation(readln().toInt())