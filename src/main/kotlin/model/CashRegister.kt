package model

typealias Cash = MutableMap<Float, Int>

class CashRegister {

    private val contents: Cash = mutableMapOf(

        0.05f to 0,
        0.10f to 0,
        0.25f to 0,

        1.00f to 0,
        5.00f to 0,
        10.00f to 0,
        20.00f to 0,
        50.00f to 0,
        100.00f to 0,
        200.00f to 0,
        500.00f to 0,
        1000.00f to 0

    )

    /**
     * Returns a copy of the register.
     */
    fun getContents(): Cash {

        return contents.toMutableMap()

    }

    /**
     * Quantity of one denomination.
     */
    fun getQuantity(

        denomination: Float

    ): Int {

        return contents.getOrDefault(
            denomination,
            0
        )

    }

    /**
     * Adds cash into the register.
     */
    fun addCash(

        denomination: Float,
        quantity: Int

    ) {

        require(quantity >= 0)

        contents[denomination] =
            contents.getOrDefault(
                denomination,
                0
            ) + quantity

    }

    /**
     * Removes cash from the register.
     */
    fun removeCash(

        denomination: Float,
        quantity: Int

    ) {

        require(quantity >= 0)

        val current =
            contents.getOrDefault(
                denomination,
                0
            )

        contents[denomination] =
            maxOf(
                0,
                current - quantity
            )

    }

    /**
     * Removes every bill and coin.
     */
    fun clear() {

        contents.keys.forEach {

            contents[it] = 0

        }

    }

    /**
     * Total amount currently inside.
     */
    fun getTotalCash(): Float {

        return contents.entries.sumOf {

            (denomination, quantity) ->

            (denomination * quantity).toDouble()

        }.toFloat()

    }

}