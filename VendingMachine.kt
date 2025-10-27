class VendingMachine (var slotLimit: Int, var itemLimit: Int){

    init{
        require(slotLimit >= 10) { "It must have at least 10 slots." }
        require(itemLimit >= 8) {"It must have at least 8 items per slot."}      
    }

    //have it loop, compare with check
    
// add getters and setters, adjust visibility
// learn about packages

// privatize attributes, publicize the methods
// composition or aggregation for item??

    val bank: cash = Array(12) { 0 }
    val slots = arrayOfNulls<Item>(slotLimit)// change to array of pair(items, quantity)
    //var summary: empty at init should have a local copy of restock
    
    fun collectChange(){
    }

    fun payMachine(){
    }

    fun displayBalance(){
    }

    fun addItem(name: String, stock: Int, price: Float, calories: Int){
        //find the first empty slot
        //make sure no duplicates in the name
        //input validation for the rest
    }

    fun restockMachine(){
        //ez add more
        val restockSlots = Array<Int>(slotLimit)
        //rewrite the summary, set this as restock
    }

    fun removeItem(){
        //deletes item
    }

    fun removeStock(){
    }

    fun changePrice(){

    }

    fun transaction(payment: cash){

        //save state of the cash on hand local var
        //compute total

        //add to bank

        //ask what they want, use slot IDs
        //cancelling just gets the equivalent from the local var out from bank subract balance

        //check if difference is positive
        //check if there can be change generated properly
        //cancel if not

        //if yes
        //subtract 1 quantity
        
        //display details, emphasis on calories
        //give change

        // save summary of the transaction

    }

    fun displaySummary(){

    }

}