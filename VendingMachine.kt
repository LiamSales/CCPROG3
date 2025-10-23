class VendingMachine (var slotLimit: Int, var itemLimit: Int){

    init{
        require(slotLimit >= 10, "It must have at least 10 slots.")
        require(itemLimit >= 8, "It must have at least 8 items per slot.")        
    }

    var change: Float = 0.0f
    val slots: Array<Item>(slotLimit) = emptyArray()


    fun collectChange(){
        this.change = 0.0f
    }

    fun payMachine(cash: Float){
        change+=cash
    }

    fun displayBalance(){
        println("change")
    }

    


}