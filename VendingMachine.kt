class VendingMachine (var slotLimit: Int, var itemLimit: Int){

    init{
        require(slotLimit >= 10) { "It must have at least 10 slots." }
        require(itemLimit >= 8) {"It must have at least 8 items per slot."}      
    }
    
    val cash = Array(12) { 0 }
    val slots = arrayOfNulls<Item>(slotLimit)
    var cashAccepted = Array(12) { 0 }

    
//accept cash first, then have user select item

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
        //different from item being 0
    }

    fun removeStock(){

    }

    fun changePrice(){

    }

    fun displaySummary(){

    }

}