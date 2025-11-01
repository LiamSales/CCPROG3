
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

    val slots = Array(slotLimit) { Slot(null, 0, 0f) }
    val register = CashRegister()
    val transactions = ArrayList<Transaction>()

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


    fun transaction(deposit: Cash){
        

    }

}