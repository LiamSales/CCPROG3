import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import java.io.InputStream

var itemList = ArrayList<Item>()
var regMachines = ArrayList<VendingMachine>()
var specMachines = ArrayList<SpecialMachine>()


fun inputValidation(input: Any?): Any {
    while (true) {
        val value = readln()

        when (input) {
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

fun createRegular() {
    println("slot limit:")
    val slot = readln().toInt()

    println("item limit:")
    val item = readln().toInt()

    val newMachine = VendingMachine(slot, item)
    regMachines.add(newMachine)

    println("Regular Vending Machine created.")
}

fun createSpecial() {
    println("slot limit:")
    val slot = readln().toInt()

    println("item limit:")
    val item = readln().toInt()

    val newMachine = SpecialMachine(slot, item)
    specMachines.add(newMachine)

    println("Special Vending Machine created.")
}

fun createMachine() {
    while (true) {
        println("=== Create a Vending Machine ===")
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
        println()
    }
}


fun testMachine() {
    println("\n=== Select a Machine to Test ===")

    if (regMachines.isEmpty() && specMachines.isEmpty()) {
        println("No machines available.")
        return
    }

    var index = 1

    regMachines.forEach {
        println("$index) Regular VM")
        index++
    }

    specMachines.forEach {
        println("$index) Special VM")
        index++
    }

    print("Choose machine #: ")
    val choice = readln().toInt()

    val selected = when {
        choice <= regMachines.size -> regMachines[choice - 1]
        choice <= regMachines.size + specMachines.size ->
            specMachines[choice - 1 - regMachines.size]
        else -> {
            println("Invalid machine #.")
            return
        }
    }

    println("[1] Test Features")
    println("[2] Test Maintenance")
    when (readln().toInt()) {
        1 -> selected.transaction()
        2 -> selected.testMaintenance()
    }
}

fun testFeatures(){
    //transaction
}

fun testMaintenance(){
    //everything else
}

fun main() {
    while (true) {
        println("\n=== Vending Machine Menu ===")
        println("[C] Create")
        println("[T] Test")
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
