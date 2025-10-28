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


class VendingMachine (var slotLimit: Int, var itemLimit: Int){

    init {
        require(slotLimit >= 8) { "Machine must have at least 8 slots." }
        require(itemLimit >= 10) { "Each slot must hold at least 10 items." }
    }

//have it loop, compare with check
    
// add getters and setters, adjust visibility
// learn about packages

// privatize attributes, publicize the methods
// composition or aggregation for item??

    private val slots = Array(slotLimit) { Slot(null, 0, 0f) }

    //var summary: empty at init should have a local copy of restock


    fun addItem(item: Item, price: Float, quantity: Int ) {
        
        // 1. Validate input

        val i = slots.indexOfFirst { it.item == null }

        slots[i].item = item
        slots[i].price = price
        slots[i].quantity = quantity

        // do we have a separate function in a different class for creating an item?
        // can items be used in different machines?

        //where should create item be?
    }

    /** Removes an item completely from a slot. */
    fun removeItem(slotIndex: Int) {
        // TODO:
        // 1. Validate index
        // 2. Clear item and reset stock
    }

    /** Reduces stock of a specific item. */
    fun removeStock(slotIndex: Int, amount: Int) {
        // TODO:
        // 1. Validate index and sufficient stock
        // 2. Subtract amount
    }

    /** Updates the price of a given item. */
    fun changePrice(slotIndex: Int, newPrice: Float) {
        // TODO:
        // 1. Validate slotIndex
        // 2. Update price
    }

    /** Restocks items in the vending machine. */
    fun restockMachine() {
        // TODO:
        // 1. Iterate all slots
        // 2. Allow user input for new stock per slot
        // 3. Record restock summary (start/end inventory)
    }

    /** Displays total balance (in pesos). */
    fun displayBalance() {
        val total = bank.entries.sumOf { it.key * it.value } / 100.0
        println("Machine Balance: ₱%.2f".format(total))
    }

    /** Handles user payment and transaction process. */
    fun transaction(payment: cash) {
        // TODO:
        // 1. Compute total payment
        // 2. Ask for desired item
        // 3. Check sufficient payment and stock
        // 4. Check if change can be produced
        // 5. Update stock, add to transactions, and print result
    }

    /** Returns change in denominations, if possible. */
    fun collectChange(amount: Int): cash {
        // TODO:
        // 1. Compute which denominations to use (greedy algorithm)
        // 2. Check if enough bills/coins exist
        // 3. Deduct from bank
        // 4. Return change map
        return mutableMapOf()
    }

    /** Collects all cash from the machine (for operator). */
    fun collectMoney() {
        // TODO:
        // 1. Display breakdown
        // 2. Clear bank contents
    }

    /** Displays transaction summary. */
    fun displaySummary() {
        // TODO:
        // 1. Print each item sold and total revenue
        // 2. Include calories and sales info since last restock
    }

    /** Helper to print available items and slots. */
    fun displaySlots() {
        println("=== ITEMS IN MACHINE ===")
        for (i in slots.indices) {
            val slot = slots[i]
            if (slot.item != null)
                println("[${i + 1}] ${slot.item!!.name} - (${slot.quantity} pcs, ${slot.item!!.calories} cal)")
            else
                println("[${i + 1}] <Empty>")
        }
    }
}