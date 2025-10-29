typealias cash = MutableMap<Int,Int>
// can i hardcode the denominations here globally?

data class Item(
    val name: String,
    var calories: Int
)

//the whole thing isnt hardcoded, items are saved in a txt file
//do i save the machines as well?

fun validateInput(input: String?, max: Int): String{
    var current = input
    do {
        if (current != null && current.length <= max && current.isNotEmpty())
            return current
        println("Input can only be up to $max characters long. Please try again:")
        current = readlnOrNull()
    } while(true)
}
//have chatgpt proofread whether null check is actually neessary, workaround needed and if i can just do readln instead of readlnOrNull such that user really cant place null


fun validateInput(input: Int): Int{
    return 0
}

fun validateInput(input: Float): Float{
    return 0f
}


//can this have input validation? limited characters for string; non negative calories

fun createRegular(){

}

fun createSpecial(){

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

}

fun testFeatures(){
    //automatic check
}

fun testMaintenance(){
    // no distinction
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
