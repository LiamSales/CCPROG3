typealias Cash = MutableMap<Float,Int>

class CashRegister {

    val contents: Cash = mutableMapOf(

        0.05f to 0, 0.10f to 0, 0.25f to 0,

        1.00f to 0, 5.00f to 0, 10.00f to 0, 20.00f to 0,

        50.00f to 0, 100.00f to 0, 200.00f to 0, 500.00f to 0, 1000.00f to 0
    )

    fun addCash(denom: Float, quanitity: Int){

    }

    fun removeCash(denom: Float, quanitity: Int){

    }

    // i think these should be the only methods? rest of the logic should be in the other class

}
