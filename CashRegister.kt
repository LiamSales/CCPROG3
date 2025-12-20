typealias Cash = MutableMap<Float, Int>

class CashRegister {

    private val contents: Cash = mutableMapOf(
        0.05f to 0, 0.10f to 0, 0.25f to 0,
        1.00f to 0, 5.00f to 0, 10.00f to 0,
        20.00f to 0, 50.00f to 0, 100.00f to 0,
        200.00f to 0, 500.00f to 0, 1000.00f to 0
    )

    fun getContents(): Cash{
    
    }

    fun addCash(denom: Float, quantity: Int) {
        contents[denom] = contents.getOrDefault(denom, 0) + quantity
    }

    fun removeCash(denom: Float, quantity: Int) {
        contents[denom] = contents.getOrDefault(denom, 0) - quantity
    }

