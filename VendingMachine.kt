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


    fun canDispenseChange(deposit: Cash, totalDeposited: Float, price: Float): Boolean {

        val hypothetical = this.register.contents.toMutableMap()

        for ((denom, count) in deposit) {
            hypothetical[denom] = (hypothetical[denom] ?: 0) + count
        }

        val change = totalDeposited - price

        if(change <0.0f )
            return false
      
            // AI generated code below, will fix soon
            

   var remaining = price

    // Iterate from largest to smallest denomination
    for (denom in hypothetical.keys.sortedDescending()) {

        // Skip denominations larger than what we need
        if (denom > remaining) continue

        val available = hypothetical[denom] ?: 0
        if (available <= 0) continue

        // Max possible coins/bills we could use
        val needed = (remaining / denom).toInt()

        // How many we actually take (can't exceed available)
        val toUse = minOf(needed, available)

        // Reduce the remaining change
        remaining -= denom * toUse

        // If exact change fulfilled
        if (remaining < 0.009f) return true
    }

    // If loop ends and change is not zero → impossible
    return remaining < 0.009f
}

            
        }
    }


    fun displayValid(deposit: Cash, totalDeposited: Float) {
        slots.forEach { s ->
            if (s.quantity > 0 &&
                canDispenseChange(deposit, totalDeposited, s.price)
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