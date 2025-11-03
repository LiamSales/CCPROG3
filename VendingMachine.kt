
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


class VendingMachine (val slotLimit: Int, val itemLimit: Int){

    private val slots = Array(slotLimit) { Slot(null, 0, 0f) }
    private val register = CashRegister()
    private val transactions = ArrayList<Transaction>()

    //should only be available to the user if there is an available slot
    fun setSlot(item: Item, price: Float) {

        val i = this.slots.indexOfFirst { it.item == null }

        slots[i].item = item
        slots[i].price = price
        println("Added ${item.name} to slot #${i + 1}.")

    }

    fun clearSlot(i: Int){

        this.slots[i].item = null
        this.slots[i].quantity = 0
        this.slots[i].price = 0f
    
    }

    fun restockQuantity(i: Int, quantity: Int){
        this.slots[i].quantity = quantity
    }

fun transaction() {

    val deposit: Cash = mutableMapOf()
    var totalDeposited = 0

    while (true) {
        println("Current balance: ₱$totalDeposited")
        println("[1] Insert cash")
        println("[2] Choose item")
        println("[3] Cancel and refund")
        print("Select option: ")

        when (readln().toInt()) {
            1 -> {
                print("Enter denomination: ")
                val denom = readln().toInt()
                print("Enter quantity: ")
                val qty = readln().toInt()
                deposit[denom] = (deposit[denom] ?: 0) + qty
                totalDeposited += denom * qty
                println("Deposited ₱${denom * qty}. New balance: ₱$totalDeposited")
                //fix this such that its incrememntal
            }

            2 -> {
                println("Select slot number:")
                val choice = readln().toInt() - 1

                val slot = slots[choice]
                val item = slot.item


                //check for available change

                slot.quantity--
                val change = totalDeposited - slot.price.toInt()

                transactions.add(Transaction(
                    item.name, slot.price, item.calories, totalDeposited.toFloat(), change.toFloat()
                ))

                break
            }

            3 -> {
                //dispense change
                break
            }

            else -> println("Invalid option. Try again.")
        }
    }
}

        
    }

    fun collect(){
        this.register
    }

}