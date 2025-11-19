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
        this.slots[i].price = price

    }

    fun canDispenseChange(deposit: Cash): Boolean{
        return true // ok this we need math, think of somehting on paper first
    }

    fun displayValid(deposit: Cash, totalDeposited: Float){

        slots.forEach { i -> 
            if(totalDeposited >= i.price && canDispenseChange(deposit)){
                println(i.item!!.name +" "+ i.item!!.calories +" "+ i.price)
            }
        }

    }

    fun updateRegister(price: Float, deposit: Cash){

    }

    fun replenishCash(){

    }

    fun collect(){

    }

    fun displaySummary(){

    }

    fun transaction() {

        val deposit: Cash = mutableMapOf() // set it to empty
        var totalDeposited = 0.0f // just to see if enough and thats it but we mostly work in deposit

        while (true) {
            println("Current balance: ₱$totalDeposited")
            displayValid(deposit, totalDeposited)
            println("[1] Insert cash")
            println("[2] Choose item")
            println("[3] Dispense Change")
            print("Select option: ")

            when (readln().toInt()) {
                1 -> {
                    
                    val denom = readln().toFloat()

                    // print("Enter denomination: ")
                    // val denom = readln().toInt()

                    // deposit[denom] ++

                    totalDeposited += denom

                    println("Deposited ₱$denom.")

                }

                2 -> {
                    //assume machine only lets users interact with possible options (input validation)

                    val choice = readln().toInt()

                    slots[choice].quantity--

                    //--
                    //update register

                    //puts the money in the machine
                    //compute exact change in denominations

                    //replaces the denom value so the cash on hand has the change alr in computation or whatever
                    //--

                    totalDeposited-=slots[choice].price

                    //update display
                    //save to transaction

                }

                3 -> {

                    println(totalDeposited)
                    //give out the change in animation?

                    break
                }
            }
        }
    }    
}
