class VendingMachine (var slotLimit: Int, var itemLimit: Int){

    init{
        require(slotLimit >= 10, "It must have at least 10 slots.")
        require(itemLimit >= 8, "It must have at least 8 items per slot.")        
    }

    var pesos: Array<Int>(9) = emptyArray()
    var centavos: Array<Int>(3) = emptyArray()
    val slots: Array<Item>(slotLimit) = emptyArray()


    fun collectChange(){
        pesos = emptyArray()
        centavos = emptyArray()
    }

    fun payMachine(){
    }

    fun displayBalance(){
    }






}