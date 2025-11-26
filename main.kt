
var itemList = ArrayList<Item>() // itemlist is global but add-ons are specific to each machine
var regMachines = ArrayList<VendingMachine>()
var specMachines = ArrayList<SpecialMachine>()

fun createRegular(){

    println("slot limit:")
    val slot = readln().toInt()
    println("item limit:")
    val item = readln().toInt()

    val newMachine = VendingMachine(slot, item)
    regMachines.add(newMachine)
}

fun createSpecial(){
    
    println("slot limit:")
    val slot = readln().toInt()
    println("item limit:")
    val item = readln().toInt()
    // println("add-on limit:")
    // val addon = readln().toInt()

    val newMachine = SpecialMachine(slot, item)
    specMachines.add(newMachine)
}

fun createMachine(){

        while (true) {
        println("=== Create a Vending Machine ===")
        println("[R] Regular Vending Machine")
        println("[S] Special Vending Machine")
        println("[X] Exit")
        print("Enter your choice: ")

        val choice = readLine()?.trim()?.uppercase()

        when (choice) {
            "R" -> createRegular()
            "S" -> createSpecial()
            "X" -> return()

            else -> {
                println("Invalid choice. Please enter R, S, or X.")
            }
        }
        println()
    }
}

fun testMachine(){
    //display all machines and have a user choose, then direct to test features or test maintenance
    //iterate through both arrays
}

fun testFeatures(){
    //transaction
}

fun testMaintenance(){
    //everything else
}


fun main() {

    while (true) {
        println("=== Vending Machine Menu ===")
        println("[C] Create a Vending Machine")
        println("[T] Test a Vending Machine")
        println("[X] Exit")
        print("Enter your choice: ")

        val choice = readLine()?.trim()?.uppercase()

        when (choice) {
            "C" -> createMachine()
            "T" -> testMachine()

            "X" -> {
                println("Exiting program. Goodbye!")
                break
            }
            else -> {
                println("Invalid choice. Please enter C, T, or X.")
            }
        }
        println()
    }
}
