package model

data class Slot(
    var item: Item? = null,
    var quantity: Int = 0,
    var price: Float = 0f,
    var sold: Int = 0
)

open class VendingMachine(

    val slotLimit: Int,
    val itemLimit: Int

) {

    val slots = Array(slotLimit) { Slot() }

    val register = CashRegister()

    var startingInventory: String = ""



    /* ==========================================================
       Slot Operations
       ========================================================== */

    fun setSlot(slotIndex: Int, item: Item, price: Float) {

        require(slotIndex in slots.indices)

        slots[slotIndex].item = item
        slots[slotIndex].price = price
        slots[slotIndex].quantity = 0
        slots[slotIndex].sold = 0
    }


    fun clearSlot(slotIndex: Int) {

        require(slotIndex in slots.indices)

        slots[slotIndex] = Slot()
    }


    fun restockSlot(slotIndex: Int, quantity: Int) {

        require(slotIndex in slots.indices)

        slots[slotIndex].quantity = quantity
        slots[slotIndex].sold = 0
    }


    fun changePrice(slotIndex: Int, price: Float) {

        require(slotIndex in slots.indices)

        slots[slotIndex].price = price
    }



    /* ==========================================================
       Cash Register
       ========================================================== */

    fun replenishCash(

        denomination: Float,
        quantity: Int

    ) {

        register.addCash(
            denomination,
            quantity
        )
    }


    fun collectCash(): Float {

        var total = 0f

        register.getContents().forEach {

            (denomination, quantity) ->

            total += denomination * quantity

            register.removeCash(
                denomination,
                quantity
            )
        }

        return total
    }



    /* ==========================================================
       Transactions
       ========================================================== */

    fun updateRegister(

        deposit: Cash

    ) {

        deposit.forEach {

            (denomination, quantity) ->

            register.addCash(
                denomination,
                quantity
            )
        }
    }


    fun dispenseChange(

        deposit: Cash,
        price: Float

    ): Cash? {

        val deposited =

            deposit.entries.sumOf {

                (denomination, quantity) ->

                (denomination * quantity).toDouble()

            }.toFloat()

        var changeNeeded = deposited - price

        if (changeNeeded < 0f)
            return null

        val hypothetical =
            register.getContents()

        deposit.forEach {

            (denomination, quantity) ->

            hypothetical[denomination] =
                hypothetical.getOrDefault(
                    denomination,
                    0
                ) + quantity
        }

        val change: Cash =
            mutableMapOf()

        for (

            denomination in hypothetical
                .keys
                .sortedDescending()

        ) {

            if (changeNeeded < denomination)
                continue

            val available =
                hypothetical[denomination] ?: 0

            val needed =
                (changeNeeded / denomination).toInt()

            val used =
                minOf(needed, available)

            if (used > 0) {

                change[denomination] = used

                changeNeeded -=
                    denomination * used
            }
        }

        return if (changeNeeded == 0f)
            change
        else
            null
    }



    /* ==========================================================
       Information
       ========================================================== */

    fun getValidSlots(

        deposit: Cash

    ): List<Int> {

        return slots.indices.filter {

            val slot = slots[it]

            slot.item != null &&
            slot.quantity > 0 &&
            dispenseChange(
                deposit,
                slot.price
            ) != null
        }
    }


    fun getTotalSales(): Float {

        return slots.sumOf {

            (it.sold * it.price).toDouble()

        }.toFloat()
    }


    fun getSummary(): List<Slot> {

        return slots.toList()
    }

}