data class Item(
    val name: String,
    var calories: Int
)


data class Slot(
    var item: Item?,
    var quantity: Int,
    var price: Float
    )

data class Transaction(
    val name: String,
    val price: Float,
    val calories: Int,
    val payment: Float,
    val changeGiven: Float
)


class VendingMachine (val slotLimit: Int, val itemLimit: Int){

    val slots = Array(slotLimit) { Slot(null, 0, 0f) }
    val register = CashRegister()
    val transactions = ArrayList<Transaction>()


}