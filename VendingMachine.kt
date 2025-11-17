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

//slot quanitity should be public

class VendingMachine (val slotLimit: Int, val itemLimit: Int){

    public val slots = Array(slotLimit) { Slot(null, 0, 0f) }
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
        //save to transaction
    }
    
    fun changePrice(i: Int, price: Float){

    }


    fun displayValid(totalDeposited: Float){
        //deals with register
        //now just the big part
    }

    fun replenishCash(){

    }

    fun collect(){

    }

    fun displaySummary(){

    }

    fun transaction() {

        val deposit: Cash = mutableMapOf()
        var totalDeposited = 0.0f

        while (true) {
            println("Current balance: ₱$totalDeposited")
            displayValid(totalDeposited)
            println("[1] Insert cash")
            println("[2] Choose item")
            println("[3] Dispense Change")
            print("Select option: ")

            when (readln().toInt()) {
                1 -> {
                    
                    print("Enter denomination: ")
                    val denom = readln().toInt()
                    // no quantities they just keep adding one by one manually

                    //deposit[denom] incrememnts ++

                    totalDeposited += denom //faulty needs fixing

                    println("Deposited ₱$denom.")

                }

                2 -> {
                    //assume machine only lets users interact with possible options (input validation)

                    val choice = readln().toInt()

                    slots[choice].quantity--

                    //update register

                    totalDeposited-=slots[choice].price

                    //update display
                    //save to transaction

                }

                3 -> {
                    println(totalDeposited)
                    break //are we breaking out of the while (true?)
                }
            }
        }
    }    
}
