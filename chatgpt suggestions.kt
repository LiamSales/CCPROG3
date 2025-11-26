var itemList = ArrayList<Item>()        // global item catalog, shared
var regMachines = ArrayList<VendingMachine>()
var specMachines = ArrayList<SpecialMachine>()


// === MACHINE CREATION ===
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


// === TESTING MACHINES ===
fun testMachine() {
    println("\n=== Select a Machine to Test ===")

    if (regMachines.isEmpty() && specMachines.isEmpty()) {
        println("No machines available.")
        return
    }

    var index = 1

    regMachines.forEach {
        println("$index) Regular VM (slots=${it.slotLimit})")
        index++
    }

    specMachines.forEach {
        println("$index) Special VM (slots=${it.slotLimit})")
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


// === MAIN MENU ===
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


// === DATA CLASSES ===
data class Item(val name: String, var calories: Int)

data class Slot(
    var item: Item?,
    var quantity: Int,
    var price: Float
)

data class Transaction(
    val name: String,
    val price: Float,
    val calories: Int,
    val payment: Float,
    val changeGiven: Float
)


// === VENDING MACHINE BASE CLASS ===
open class VendingMachine(val slotLimit: Int, val itemLimit: Int) {

    val slots = Array(slotLimit) { Slot(null, 0, 0f) }
    private val register = CashRegister()
    private val transactions = ArrayList<Transaction>()


    // SAFER setSlot
    fun setSlot(item: Item, price: Float) {
        val i = slots.indexOfFirst { it.item == null }
        if (i == -1) {
            println("No empty slots available.")
            return
        }

        slots[i].item = item
        slots[i].price = price
        println("Added ${item.name} to slot #${i + 1}")
    }


    fun clearSlot(i: Int) {
        slots[i].item = null
        slots[i].quantity = 0
        slots[i].price = 0f
    }


    fun restockQuantity(i: Int, quantity: Int) {
        slots[i].quantity = quantity
    }


    fun changePrice(i: Int, price: Float) {
        slots[i].price = price
    }


    fun canDispenseChange(hypotheticalBalance: Cash, price: Float): Boolean {
        // TODO: implement greedy algorithm
        return false
    }


    fun displayValid(deposit: Cash, totalDeposited: Float) {
        slots.forEach { s ->
            if (s.item != null &&
                s.quantity > 0 &&
                totalDeposited >= s.price
            ) {
                println("${s.item!!.name} ${s.item!!.calories} kcal — ₱${s.price}")
            }
        }
    }


    fun updateRegister(price: Float, deposit: Cash) {
        // TODO: add deposited money, remove change, finalize sale
    }


    fun replenishCash() {
        // TODO: allow maintenance to add bills
    }


    fun collect() {
        println("Cash collected:")
        println(register.getContents())
        // TODO: compute total then clear register
    }


    fun displaySummary() {
        // TODO: show transactions + totals
    }


    // === TRANSACTION LOOP ===
    fun transaction() {
        val deposit: Cash = mutableMapOf()
        var totalDeposited = 0f

        while (true) {
            println("\nCurrent balance: ₱$totalDeposited")
            displayValid(deposit, totalDeposited)

            println("[1] Insert cash")
            println("[2] Choose item")
            println("[3] Dispense change")
            when (readln().toInt()) {

                1 -> {
                    print("Enter denomination: ")
                    val denom = readln().toFloat()
                    deposit[denom] = deposit.getOrDefault(denom, 0) + 1
                    totalDeposited += denom
                }

                2 -> {
                    print("Choose slot #: ")
                    val slotChoice = readln().toInt() - 1

                    if (slotChoice !in slots.indices) {
                        println("Invalid slot.")
                        continue
                    }

                    val slot = slots[slotChoice]

                    if (slot.item == null || slot.quantity <= 0) {
                        println("Slot empty.")
                        continue
                    }

                    if (totalDeposited < slot.price) {
                        println("Insufficient balance.")
                        continue
                    }

                    slot.quantity--
                    totalDeposited -= slot.price

                    println("Dispensed ${slot.item!!.name}")
                }

                3 -> {
                    println("Change: ₱$totalDeposited")
                    // TODO: return actual denominations
                    break
                }
            }
        }
    }

    fun testMaintenance() {
        println("Maintenance menu (TODO)")
    }
}


// === CASH REGISTER ===
typealias Cash = MutableMap<Float, Int>

class CashRegister {
    private val contents: Cash = mutableMapOf(
        0.05f to 0, 0.10f to 0, 0.25f to 0,
        1.00f to 0, 5.00f to 0, 10.00f to 0,
        20.00f to 0, 50.00f to 0, 100.00f to 0,
        200.00f to 0, 500.00f to 0, 1000.00f to 0
    )

    fun addCash(denom: Float, quantity: Int) {
        contents[denom] = contents.getOrDefault(denom, 0) + quantity
    }

    fun removeCash(denom: Float, quantity: Int) {
        contents[denom] = contents.getOrDefault(denom, 0) - quantity
    }

    fun getContents(): Cash = contents
}


// === SPECIAL MACHINE ===
class SpecialMachine(slotLimit: Int, itemLimit: Int)
    : VendingMachine(slotLimit, itemLimit) {

    // TODO: handle add-ons for MCO2
}
