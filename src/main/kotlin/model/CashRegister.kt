package model

typealias Cash = MutableMap<Float, Int>

class CashRegister {

    private val contents: Cash = mutableMapOf(
        0.05f to 0, 0.10f to 0, 0.25f to 0,
        1.00f to 0, 5.00f to 0, 10.00f to 0,
        20.00f to 0, 50.00f to 0, 100.00f to 0,
        200.00f to 0, 500.00f to 0, 1000.00f to 0
    )

    fun getContents(): Cash {
        return contents.toMutableMap()
    }

    fun addCash(denom: Float, quantity: Int) {
        if (quantity <= 0) return
        contents[denom] = contents.getOrDefault(denom, 0) + quantity
    }

    fun removeCash(denom: Float, quantity: Int) {
        if (quantity <= 0) return
        val current = contents.getOrDefault(denom, 0)
        contents[denom] = maxOf(0, current - quantity)
    }
}

