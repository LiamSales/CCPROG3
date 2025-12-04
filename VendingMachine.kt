data class Item(
    val name: String,
    var calories: Int
)

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

open class VendingMachine(val slotLimit: Int, val itemLimit: Int) {

    val slots = Array(slotLimit) { Slot(null, 0, 0f) }
    private val register = CashRegister()
    private val transactions = ArrayList<Transaction>()


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


    fun DispenseChange(deposit: Cash, price: Float):  Cash? {

        val hypothetical = this.register.contents.toMutableMap()

        for ((denom, count) in deposit) {
            hypothetical[denom] = (hypothetical[denom] ?: 0) + count
        }

        var change: Cash = mutableMapOf()

        //return the change in cash
        //if change is negative/not possible, return null

        for (denom in hypothetical.keys.sortedDescending()) {

            if (denom > change) continue

            val available = hypothetical[denom] ?: 0
            if (available <= 0) continue

            val needed = (change / denom).toInt()

            val toUse = minOf(needed, available)

            change -= denom * toUse
        }

        return change
}



    fun displayValid(deposit: Cash) {
        slots.forEach { s ->
            if (s.quantity > 0 &&
                DispenseChange(deposit, s.price) != null
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


    fun transaction() {
        val deposit: Cash = mutableMapOf()
        var totalDeposited = 0f

        while (true) {
            println("\nCurrent balance: ₱$totalDeposited")
            displayValid(deposit)

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