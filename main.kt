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

    you can set variables to a when clause

*/


var regMachines = mutableListOf<VendingMachine>()
var specMachines = mutableListOf<SpecialMachine>()

fun inputValidation(template: Any?): Any {
    while (true) {
        val value = readln()

        when (template) {
            is Int -> {
                val n = value.toIntOrNull()
                if (n != null && n >= 0) return n
            }

            is Float -> {
                val f = value.toFloatOrNull()
                if (f != null && f >= 0f) return f
            }

            is String -> {
                if (value.isNotBlank() && value.length <= 10) return value
            }

            else -> return value
        }

        println("Invalid input, try again:")
    }
}

fun readPositiveInt(prompt: String): Int {
    while (true) {
        print(prompt)
        val n = inputValidation(0) as Int
        if (n >= 1) return n
        println("Value must be at least 1.")
    }
}

fun createRegular() {
    val slotLimit = readPositiveInt("Slot limit: ")
    val itemLimit = readPositiveInt("Item limit: ")

    regMachines.add(VendingMachine(slotLimit, itemLimit))
    println("Regular Vending Machine created.")
}

fun createSpecial() {
    val slotLimit = readPositiveInt("Slot limit: ")
    val itemLimit = readPositiveInt("Item limit: ")

    specMachines.add(SpecialMachine(slotLimit, itemLimit))
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
    if (regMachines.isEmpty() && specMachines.isEmpty()) {
        println("No machines available.")
        return
    }

    println("\n=== Select a Machine ===")

    var index = 1
    regMachines.forEach {
        println("$index) Regular Vending Machine")
        index++
    }
    specMachines.forEach {
        println("$index) Special Vending Machine")
        index++
    }

    print("Choose machine #: ")
    val choice = inputValidation(0) as Int

    val selected: VendingMachine = when {
        choice in 1..regMachines.size ->
            regMachines[choice - 1]

        choice in (regMachines.size + 1)..(regMachines.size + specMachines.size) ->
            specMachines[choice - regMachines.size - 1]

        else -> {
            println("Invalid machine number.")
            return
        }
    }

    println("[1] Test Features")
    println("[2] Test Maintenance")

    when (inputValidation(0) as Int) {
        1 -> selected.transaction()
        2 -> selected.testMaintenance()
        else -> println("Invalid choice.")
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
