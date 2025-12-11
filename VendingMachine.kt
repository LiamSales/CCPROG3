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


    fun dispenseChange(deposit: Cash, price: Float): Cash? {

        val totalDeposited = deposit.entries.sumOf { (d, q) -> (d * q).toDouble() }.toFloat()

        var changeNeeded = totalDeposited - price

        if (changeNeeded < 0) return null

        val hypothetical = this.register.contents.toMutableMap()
        for ((denom, count) in deposit) {
            hypothetical[denom] = hypothetical.getOrDefault(denom, 0) + count
        }

        // greedy algorithm, I thought of myself but Chatgpt did the implementation here, work on translating thoughts to code!

        val change: Cash = mutableMapOf()

        for (denom in hypothetical.keys.sortedDescending()) {

            if (denom > changeNeeded) continue

            var available = hypothetical[denom]!!
            if (available <= 0) continue

            val needed = (changeNeeded / denom).toInt()
            if (needed <= 0) continue

            val toUse = minOf(needed, available)

            changeNeeded -= denom * toUse

            change[denom] = toUse
        }

        return if (changeNeeded == 0f) change else null
    }



    fun displayValid(deposit: Cash) {
        slots.forEach { s ->
            if (s.quantity > 0 &&
                dispenseChange(deposit, s.price) != null
            ) {
                println("${s.item!!.name} ${s.item!!.calories} kcal — ₱${s.price}")
            }
        }
    }


    fun updateRegister(deposit: Cash) {
        
        // TODO: add deposited money, remove change, finalize sale
    }


    fun replenishCash() {

        val replenishment: Cash = mutableMapOf()
        replenishment.forEach { (denom, _) ->
            print("$denom add amount: ")
            register.addCash(denom, readln().toInt())
        }
    }


    fun collect() {

        var total: Float = 0.0f
        
        register.contents.forEach { ( denom, quantity) ->
            total += quantity.toFloat() * denom
            register.removeCash(denom, quantity)
        } 
        println("Cash collected: $total")
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

                    //user can only pick valid slots anyway (whitelist inputs)

                    val slot = slots[slotChoice]

                    slot.quantity--
                    totalDeposited -= slot.price

                    // is this only a copy or does it directly manipulate the values of the actual slot attribute?

                    //deposit foreach loop subtracting dispenseChange(deposit, slot.price) same indices
                    //difference goes to update register, while deposit becomes equal to dispenseChange

                    println("Dispensed ${slot.item!!.name}")
                }

                3 -> {
                    println("Change: ₱$totalDeposited")
                    //print out deposit, foreach loop showing quantity and amount pairs
                    break
                }
            }
        }
    }

    fun testMaintenance() {
        println("Maintenance menu (TODO)")
    }
}