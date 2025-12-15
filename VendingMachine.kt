data class Slot(
    var item: Item?,
    var quantity: Int = 0,
    var price: Float,
    var sold: Int = 0
)

open class VendingMachine(val slotLimit: Int, val itemLimit: Int) {

    val slots = Array(slotLimit) { Slot(null, 0, 0f, 0) }
    private val register = CashRegister()
    val startingInventory = StringBuilder()

    fun setSlot(item: Item, price: Float) {
        val i = slots.indexOfFirst { it.item == null }
        if (i == -1) {
            println("No empty slots available.")
            return
        }

        println(slots.count{it.item==null}.toString() + "available slots")

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

        startingInventory.clear()

        startingInventory.append("Starting Inventory: \n")

        slots.forEach { slot ->
            val item = slot.item
            if (item != null) {
                slot.sold = 0
                startingInventory.append("${item.name}\t quantity: ${slot.quantity}\n")
            }
        }

        startingInventory.append("\n ---------------------------------------- \n")

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

        deposit.forEach { (denom, quantity) ->
            register.addCash(denom, quantity)
        }
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


        val currentInventory = StringBuilder()
        var totalEarned: Float = 0.0f

        println(startingInventory.toString())

        currentInventory.append("\n ---------------------------------------- \n")
        currentInventory.append("Current Inventory: \n")

        slots.forEach { slot ->
            val item = slot.item
            if (item != null && slot.sold > 0) {
                val amountCollected = slot.sold.toFloat() * slot.price

                println("${item.name}\t quantity sold: ${slot.sold} \t amount collected: PhP${amountCollected}\n")
                totalEarned+=amountCollected

                currentInventory.append("${item.name}\t quantity: ${slot.quantity}\n")
            }
        }

        println(currentInventory.toString())
        println("Total amount earned since restock: $totalEarned")

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
                    val slot = slots[slotChoice]

                    slot.quantity--
                    totalDeposited -= slot.price

                    val change = dispenseChange(deposit, slot.price) ?: mutableMapOf()

                    //study why i cant reassign directly
                    deposit.forEach { (denom, quantity) ->
                        deposit[denom] = quantity - (change[denom] ?: 0)
                    }

                    updateRegister(deposit)

                    deposit.forEach { (denom, _) ->
                        deposit[denom] = (change[denom] ?: 0)
                    }

                    println("Dispensed ${slot.item!!.name}")

                    slot.sold++

                }

                3 -> {
                    println("Change: ₱$totalDeposited")

                    deposit.forEach{ (denom, quantity) ->
                        println("$denom: $quantity")
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
            println("[H] Relenish Cash")
            println("[L] Collect Balance")
            println("[D] Display Summary")    
            println("[X] Exit")
            print("Enter choice: ")


            when (readLine()?.trim()?.uppercase()) {
                "S" ->{
                    // create item or add existing item
                    
                    val item: Item
                    val price: Float = inputValidation(readln().toFloat()).toFloat()

                    setSlot(item, price)
                }
                "C" ->{}
                "R" ->{}
                "P" ->{}
                "H" -> replenishCash()
                "L" -> collect()
                "D" -> displaySummary()
                "X" -> return
                else -> println("Invalid choice.")
            }
            println()
        }
    }