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

*/


val machines: MutableList<VendingMachine> = mutableListOf()

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
    println("Slot Limit: ")
    val slotLimit = inputValidation(0, 8) as Int
    println("Item Limit: ")
    val itemLimit = inputValidation(0, 10) as Int

    machines.add(VendingMachine(slotLimit, itemLimit))
    println("Regular Vending Machine created.")
}

fun createSpecial() {

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

    var index = 1
    machines.forEach {
        println("$index) Regular Vending Machine")
        index++
    }

    print("Choose machine #: ")
    val choice = inputValidation(0, machines.size -1) as Int

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
