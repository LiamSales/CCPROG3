typealias Cash = MutableMap<Int,Int>

class CashRegister {

    companion object {
        val DENOMINATIONS = listOf(5, 10, 25, 100, 500, 1000, 2000, 5000, 10000, 20000, 50000, 100000)
    }// doesnt belong to an instance but to the class itself?
    // so i dont need to create an object i can just rawdog this?
    

    val cash: Cash = DENOMINATIONS.associateWith { 0 }.toMutableMap()

    fun add(denomination: Int, quantity: Int) {
    }

    fun remove(denomination: Int, quantity: Int) {

    }

    fun collect(){
        
    }
}
