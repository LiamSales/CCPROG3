class VendingMachine (var slotLimit: Int, var itemLimit: Int){

    init{
        require(slotLimit >= 10) { "It must have at least 10 slots." }
        require(itemLimit >= 8) {"It must have at least 8 items per slot."}      
    }
    
    val bank: cash = Array(12) { 0 }
    val slots = arrayOfNulls<Item>(slotLimit)
    
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

    fun replenish(newStock: Int){
        //ez add more
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
        
        //give change

        // save summary of the transaction

    }

    fun displaySummary(){

    }

}