package model

class SpecialMachine(
    slotLimit: Int,
    itemLimit: Int,
    addOnLimit: Int
) : VendingMachine(slotLimit, itemLimit) {

    /*
        Separate storage for add-ons.

        Items become add-ons simply
        because they are placed here.
    */
    private val addOnSlots =
        Array(addOnLimit) { Slot() }


    /*
        Returns all add-on slots.
    */
    fun getAddOnSlots(): Array<Slot> {
        return addOnSlots
    }


    /*
        Returns one add-on slot.
    */
    fun getAddOnSlot(index: Int): Slot? {

        if (index !in addOnSlots.indices)
            return null

        return addOnSlots[index]
    }


    /*
        Places an item into an
        add-on slot.
    */
    fun setAddOnSlot(
        index: Int,
        item: Item,
        price: Float
    ) {

        if (index !in addOnSlots.indices)
            return

        addOnSlots[index].item = item
        addOnSlots[index].price = price
        addOnSlots[index].quantity = 0
        addOnSlots[index].sold = 0
    }


    /*
        Clears an add-on slot.
    */
    fun clearAddOnSlot(index: Int) {

        if (index !in addOnSlots.indices)
            return

        addOnSlots[index] = Slot()
    }


    /*
        Restocks an add-on.
    */
    fun restockAddOn(
        index: Int,
        quantity: Int
    ) {

        if (index !in addOnSlots.indices)
            return

        addOnSlots[index].quantity = quantity
        addOnSlots[index].sold = 0
    }


    /*
        Adds more stock.
    */
    fun addAddOnStock(
        index: Int,
        quantity: Int
    ) {

        if (index !in addOnSlots.indices)
            return

        addOnSlots[index].quantity += quantity
    }


    /*
        Changes add-on price.
    */
    fun changeAddOnPrice(
        index: Int,
        price: Float
    ) {

        if (index !in addOnSlots.indices)
            return

        addOnSlots[index].price = price
    }


    /*
        Decreases quantity after
        a successful purchase.
    */
    fun dispenseAddOn(index: Int): Boolean {

        if (index !in addOnSlots.indices)
            return false

        val slot = addOnSlots[index]

        if (slot.item == null)
            return false

        if (slot.quantity <= 0)
            return false

        slot.quantity--
        slot.sold++

        return true
    }


    /*
        Removes everything from all
        add-on slots.
    */
    fun clearAllAddOns() {

        for (i in addOnSlots.indices) {
            addOnSlots[i] = Slot()
        }
    }


    /*
        Number of available add-on slots.
    */
    fun getAddOnSlotCount(): Int {
        return addOnSlots.size
    }
}