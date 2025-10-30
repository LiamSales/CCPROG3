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


class VendingMachine (var slotLimit: Int, var itemLimit: Int){

    private val slots = Array(slotLimit) { Slot(null, 0, 0f) }

}