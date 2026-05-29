package model

class SpecialMachine(slotLimit: Int, itemLimit: Int, AddOnLimit: Int)
    : VendingMachine(slotLimit, itemLimit) {

    
    private val addOnSlots = Array(AddOnLimit) { Slot() }

    //getter and setters for addOnSlots

    //after picking an item for transaction, it prompts, do you want to add on 
    //shows a list of addOns, should not be possible to get add ons before the base item

    //can add edit, then will show preparation phase

    //edit the item class to have a preparation attribute/method
    //class overloading??

}