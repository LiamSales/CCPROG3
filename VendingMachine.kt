data class Slot(
    var item: Item? = null,
    var quantity: Int = 0,
    var price: Float = 0f,
    var sold: Int = 0
)

open class VendingMachine(
    val slotLimit: Int,
    val itemLimit: Int
) {

    val slots = Array(slotLimit) { Slot() }
    private val register = CashRegister()
    val startingInventory = StringBuilder()

    fun setSlot(item: Item, price: Float) {
        val i = slots.indexOfFirst { it.item == null }
        if (i == -1) {
            println("No empty slots available.")
            return
        }

        slots[i].item = item
        slots[i].price = price
        slots[i].quantity = 0
        slots[i].sold = 0

        println("Added ${item.name} to slot #${i + 1}")
    }


    fun clearSlot(i: Int) {
        if (i !in slots.indices) return

        slots[i] = Slot()
    }


    fun restockQuantity(i: Int, quantity: Int) {
        if (i !in slots.indices) return
        if (slots[i].item == null) return

        slots[i].quantity = quantity
        slots[i].sold = 0

        startingInventory.clear()
        startingInventory.append("Starting Inventory:\n")

        slots.forEach { slot ->
            slot.item?.let {
                startingInventory.append("${it.name}\t quantity: ${slot.quantity}\n")
            }
        }

        startingInventory.append("\n----------------------------------------\n")
    }


    fun changePrice(i: Int, price: Float) {
        if (i !in slots.indices) return
        if (slots[i].item == null) return

        slots[i].price = price
    }


    fun dispenseChange(deposit: Cash, price: Float): Cash? {

        val totalDeposited =
            deposit.entries.sumOf { (d, q) -> d * q }

        var changeNeeded = totalDeposited - price
        if (changeNeeded < 0f) return null

        val hypothetical = register.contents.toMutableMap()
        deposit.forEach { (d, q) ->
            hypothetical[d] = hypothetical.getOrDefault(d, 0) + q
        }

        val change: Cash = mutableMapOf()

        for (denom in hypothetical.keys.sortedDescending()) {
            if (changeNeeded < denom) continue

            val available = hypothetical[denom] ?: 0
            if (available <= 0) continue

            val needed = (changeNeeded / denom).toInt()
            val toUse = minOf(needed, available)

            if (toUse > 0) {
                change[denom] = toUse
                changeNeeded -= denom * toUse
            }
        }

        return if (changeNeeded == 0f) change else null
    }


    fun displayValid(deposit: Cash) {
        slots.forEachIndexed { i, s ->
            if (s.item != null &&
                s.quantity > 0 &&
                dispenseChange(deposit, s.price) != null
            ) println("[${i + 1}] ${s.item!!.name} ${s.item!!.calories} kcal — ₱${s.price}")
        }
    }


    fun updateRegister(deposit: Cash) {
        deposit.forEach { (denom, quantity) ->
            register.addCash(denom, quantity)
        }
    }


    fun replenishCash() {
        // ⚠ Incomplete: no denominations listed
        // This should iterate over known denominations from CashRegister
        println("Replenish cash not fully implemented.")
    }


    fun collect() {
        var total = 0f

        register.contents.forEach { (denom, quantity) ->
            total += denom * quantity
            register.removeCash(denom, quantity)
        }

        println("Cash collected: ₱$total")
    }


    fun displaySummary() {
        println(startingInventory)

        var totalEarned = 0f
        println("Current Inventory:")
        println("----------------------------------------")

        slots.forEach { slot ->
            slot.item?.let {
                val earned = slot.sold * slot.price
                totalEarned += earned

                println(
                    "${it.name}\t sold: ${slot.sold}\t remaining: ${slot.quantity}\t earned: ₱$earned"
                )
            }
        }

        println("----------------------------------------")
        println("Total earned since restock: ₱$totalEarned")
    }


    fun transaction() {
        val deposit: Cash = mutableMapOf()
        var totalDeposited = 0f

        while (true) {
            println("\nCurrent balance: ₱$totalDeposited")
            displayValid(deposit)

            println("[1] Insert cash")
            println("[2] Choose item")
            println("[3] Cancel / Get change")

            when (readln().toInt()) {

                1 -> {
                    print("Enter denomination: ")
                    val denom = readln().toFloat()

                    deposit[denom] = deposit.getOrDefault(denom, 0) + 1
                    totalDeposited += denom
                }

                2 -> {
                    print("Choose slot #: ")
                    val i = readln().toInt() - 1
                    if (i !in slots.indices) continue

                    val slot = slots[i]
                    if (slot.item == null || slot.quantity <= 0) continue

                    val change = dispenseChange(deposit, slot.price) ?: continue

                    slot.quantity--
                    slot.sold++
                    totalDeposited -= slot.price

                    updateRegister(deposit)

                    deposit.clear()
                    change.forEach { (d, q) ->
                        deposit[d] = q
                    }

                    println("Dispensed ${slot.item!!.name}")
                }

                3 -> {
                    println("Returned change:")
                    deposit.forEach { (d, q) ->
                        println("₱$d x$q")
                    }
                    break
                }
            }
        }
    }


    fun testMaintenance() {
        while (true) {
            println("=== Test Maintenance ===")
            println("[S] Set Slot")
            println("[C] Clear Slot")
            println("[R] Restock Quantity")
            println("[P] Change Price")
            println("[H] Replenish Cash")
            println("[L] Collect Balance")
            println("[D] Display Summary")
            println("[X] Exit")
            print("Enter choice: ")

            when (readLine()?.trim()?.uppercase()) {

                "S" -> {
                    println("[1] Upload item from file")
                    println("[2] Create new item")

                    val item = when (readln().toInt()) {
                        1 -> loadItemFromFile()
                        2 -> createItemAndSave()
                        else -> continue
                    }

                    print("Price: ")
                    val price = inputValidation(0f) as Float
                    setSlot(item, price)
                }

                "C" -> {
                    slots.forEachIndexed { i, s ->
                        s.item?.let {
                            println("[${i + 1}] ${it.name}")
                        }
                    }
                    print("Slot #: ")
                    clearSlot(readln().toInt() - 1)
                }

                "R" -> {
                    print("Slot #: ")
                    val i = readln().toInt() - 1
                    val quantity = inputValidation(0) as Int
                    restockQuantity(i, quantity)
                }

                "P" -> {
                    print("Slot #: ")
                    val i = readln().toInt() - 1
                    val price = inputValidation(0f) as Float
                    changePrice(i, price)
                }

                "H" -> replenishCash()
                "L" -> collect()
                "D" -> displaySummary()
                "X" -> return
            }
        }
    }
}
