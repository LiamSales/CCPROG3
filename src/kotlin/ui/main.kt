/*notes from gpt fixes

new trick to prevent errors:

        if (i !in indices) return


Use Double for calculations
Use Float only for storage/display

Convert once at boundaries

move imports to the files that actually use them for more modularization

you can place print prompts in the function arg
    val slotLimit = readPositiveInt("Slot limit: ")

    buildString{
    } is a better string builder implementation


REWRITE (UI layer)
main()
createMachine()
testMachine()
all println / readln

Kotlin + JavaFX + FXML

*/


val machines: MutableList<VendingMachine> = mutableListOf()

fun readInt(prompt: String, min: Int = Int.MIN_VALUE, max: Int = Int.MAX_VALUE): Int {
    while (true) {
        print(prompt)
        val value = readln().toIntOrNull()
        if (value != null && value in min..max) return value
        println("Invalid input, try again:")
    }
}

fun readFloat(prompt: String, min: Float = Float.MIN_VALUE, max: Float = Float.MAX_VALUE): Float {
    while (true) {
        print(prompt)
        val value = readln().toFloatOrNull()
        if (value != null && value in min..max) return value
        println("Invalid input, try again:")
    }
}

fun readText(prompt: String, maxLength: Int = Int.MAX_VALUE): String {
    while (true) {
        print(prompt)
        val value = readln()
        if (value.isNotBlank() && value.length <= maxLength) return value
        println("Invalid input, try again:")
    }
}

fun inputValidation(template: Any?, limit: Int): Any {
    while (true) {
        val value = readln()

        when (template) {
            is Int -> {
                val n = value.toIntOrNull()
                if (n != null && n >= limit) return n
            }

            is Float -> {
                val f = value.toFloatOrNull()
                if (f != null && f >= limit.toFloat()) return f
            }

            is String -> {
                if (value.isNotBlank() && value.length <= limit) return value
            }

            else -> return value
        }

        println("Invalid input, try again:")
    }
}

fun createRegular() {
    val slotLimit = readInt("Slot limit (1-8): ", 1, 8)
    val itemLimit = readInt("Item limit (1-10): ", 1, 10)

    machines.add(VendingMachine(slotLimit, itemLimit))
    println("Regular Vending Machine created.")
}

fun createSpecial() {
    val slotLimit = readInt("Slot limit (1-8): ", 1, 8)
    val itemLimit = readInt("Item limit (1-10): ", 1, 10)
    val addOnLimit = readInt("Add-on slot limit (1-5): ", 1, 5)

    machines.add(SpecialMachine(slotLimit, itemLimit, addOnLimit))
    println("Special Vending Machine created.")
}

fun createMachine() {
    while (true) {
        println("\n=== Create a Vending Machine ===")
        println("[R] Regular")
        println("[S] Special")
        println("[X] Exit")
        print("Enter choice: ")

        when (readLine()?.trim()?.uppercase()) {
            "R" -> createRegular()
            "S" -> createSpecial()
            "X" -> return
            else -> println("Invalid choice.")
        }
    }
}

fun testMachine() {
    if (machines.isEmpty()) {
        println("No machines available.")
        return
    }

    println("\n=== Select a Machine ===")

    machines.forEachIndexed { index, machine ->
        val label = when (machine) {
            is SpecialMachine -> "Special Vending Machine"
            else -> "Regular Vending Machine"
        }
        println("${index + 1}) $label")
    }

    val choice = readInt("Choose machine #: ", 1, machines.size) - 1

    //fix in the ui?

    while (true){
        println("[1] Test Features")
        println("[2] Test Maintenance")
        println("[X] Exit")
        print("Enter choice: ")

        when (readLine()?.trim()?.uppercase()) {
            "1" -> machines[choice].transaction()
            "2" -> machines[choice].testMaintenance()
            "X" -> return
            else -> println("Invalid choice.")
        }
    }
}

fun main() {
    while (true) {
        println("\n=== Vending Machine Menu ===")
        println("[C] Create Machine")
        println("[T] Test Machine")
        println("[X] Exit")
        print("Enter choice: ")

        when (readLine()?.trim()?.uppercase()) {
            "C" -> createMachine()
            "T" -> testMachine()
            "X" -> return
            else -> println("Invalid choice.")
        }
    }
}
