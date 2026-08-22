package ui

import model.Item
import model.SpecialMachine
import model.VendingMachine
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object MachineManager {

    data class MachineEntry(
        val folder: File,
        val machine: VendingMachine
    )

    val machines =
        mutableListOf<MachineEntry>()

    /*
     * ==========================================================
     * LOAD ALL MACHINES
     * ==========================================================
     */

    fun loadMachines() {

        machines.clear()

        val root =
            File("data/machines")

        root.mkdirs()

        root.listFiles()
            ?.filter {
                it.isDirectory &&
                it.name.startsWith("machine_")
            }
            ?.sortedBy {
                it.name
            }
            ?.forEach { folder ->

                val info =
                    File(folder, "info.csv")

                if (!info.exists())
                    return@forEach

                val lines =
                    info.readLines()

                if (lines.size < 2)
                    return@forEach

                val slotLimit =
                    lines[0].trim().toIntOrNull()
                        ?: return@forEach

                val itemLimit =
                    lines[1].trim().toIntOrNull()
                        ?: return@forEach

                val machine: VendingMachine =

                    if (lines.size >= 3) {

                        val addOnLimit =
                            lines[2].trim().toIntOrNull()
                                ?: 0

                        SpecialMachine(
                            slotLimit,
                            itemLimit,
                            addOnLimit
                        )

                    } else {

                        VendingMachine(
                            slotLimit,
                            itemLimit
                        )
                    }

                loadInventory(
                    folder,
                    machine
                )

                loadAddOns(
                    folder,
                    machine
                )

                loadRegister(
                    folder,
                    machine
                )

                machines.add(
                    MachineEntry(
                        folder,
                        machine
                    )
                )
            }
    }

    /*
     * ==========================================================
     * SAVE ONE MACHINE
     * ==========================================================
     */

    fun saveMachine(
        entry: MachineEntry
    ) {

        saveInventory(
            entry.folder,
            entry.machine
        )

        saveAddOns(
            entry.folder,
            entry.machine
        )

        saveRegister(
            entry.folder,
            entry.machine
        )
    }

    /*
     * ==========================================================
     * SAVE ALL MACHINES
     * ==========================================================
     */

    fun saveMachines() {

        machines.forEach { entry ->

            saveMachine(entry)

        }
    }

    /*
     * ==========================================================
     * INVENTORY
     * ==========================================================
     */

    private fun saveInventory(
        folder: File,
        machine: VendingMachine
    ) {

        val file =
            File(folder, "inventory.csv")

        file.parentFile?.mkdirs()

        file.printWriter().use { out ->

            machine.slots.forEachIndexed { index, slot ->

                val itemName =
                    slot.item?.name ?: ""

                out.println(
                    "$index," +
                    "$itemName," +
                    "${slot.quantity}," +
                    "${slot.price}," +
                    "${slot.sold}"
                )
            }
        }
    }

    private fun loadInventory(
        folder: File,
        machine: VendingMachine
    ) {

        val file =
            File(folder, "inventory.csv")

        if (!file.exists())
            return

        file.readLines().forEach { line ->

            if (line.isBlank())
                return@forEach

            val parts =
                line.split(",")

            if (parts.size < 5)
                return@forEach

            val slotIndex =
                parts[0].toIntOrNull()
                    ?: return@forEach

            if (
                slotIndex !in
                machine.slots.indices
            ) {
                return@forEach
            }

            val itemName =
                parts[1]

            val quantity =
                parts[2].toIntOrNull()
                    ?: return@forEach

            val price =
                parts[3].toFloatOrNull()
                    ?: return@forEach

            val sold =
                parts[4].toIntOrNull()
                    ?: return@forEach

            if (itemName.isBlank()) {

                machine.clearSlot(
                    slotIndex
                )

                return@forEach
            }

            val item =
                ItemManager.items.find {
                    it.name == itemName
                }

            if (item == null)
                return@forEach

            val slot =
                machine.slots[slotIndex]

            slot.item =
                item

            slot.quantity =
                quantity

            slot.price =
                price

            slot.sold =
                sold
        }
    }

    /*
     * ==========================================================
     * SPECIAL MACHINE ADD-ONS
     * ==========================================================
     */

    private fun saveAddOns(
        folder: File,
        machine: VendingMachine
    ) {

        val special =
            machine as? SpecialMachine
                ?: return

        val file =
            File(folder, "addons.csv")

        file.parentFile?.mkdirs()

        file.printWriter().use { out ->

            special.getAddOnSlots()
                .forEachIndexed { index, slot ->

                    val itemName =
                        slot.item?.name ?: ""

                    out.println(
                        "$index," +
                        "$itemName," +
                        "${slot.quantity}," +
                        "${slot.price}," +
                        "${slot.sold}"
                    )
                }
        }
    }

    private fun loadAddOns(
        folder: File,
        machine: VendingMachine
    ) {

        val special =
            machine as? SpecialMachine
                ?: return

        val file =
            File(folder, "addons.csv")

        if (!file.exists())
            return

        file.readLines().forEach { line ->

            if (line.isBlank())
                return@forEach

            val parts =
                line.split(",")

            if (parts.size < 5)
                return@forEach

            val index =
                parts[0].toIntOrNull()
                    ?: return@forEach

            if (
                index !in
                special.getAddOnSlots().indices
            ) {
                return@forEach
            }

            val itemName =
                parts[1]

            val quantity =
                parts[2].toIntOrNull()
                    ?: return@forEach

            val price =
                parts[3].toFloatOrNull()
                    ?: return@forEach

            val sold =
                parts[4].toIntOrNull()
                    ?: return@forEach

            val slot =
                special.getAddOnSlots()[index]

            if (itemName.isBlank()) {

                special.clearAddOnSlot(
                    index
                )

                return@forEach
            }

            val item =
                ItemManager.items.find {
                    it.name == itemName
                }

            if (item == null)
                return@forEach

            slot.item =
                item

            slot.quantity =
                quantity

            slot.price =
                price

            slot.sold =
                sold
        }
    }

    /*
     * ==========================================================
     * REGISTER
     * ==========================================================
     */

    private fun saveRegister(
        folder: File,
        machine: VendingMachine
    ) {

        val file =
            File(folder, "register.csv")

        file.parentFile?.mkdirs()

        file.printWriter().use { out ->

            machine.register
                .getContents()
                .forEach { (denomination, quantity) ->

                    if (quantity > 0) {

                        out.println(
                            "$denomination,$quantity"
                        )
                    }
                }
        }
    }

    private fun loadRegister(
        folder: File,
        machine: VendingMachine
    ) {

        val file =
            File(folder, "register.csv")

        if (!file.exists())
            return

        machine.register.clear()

        file.readLines().forEach { line ->

            if (line.isBlank())
                return@forEach

            val parts =
                line.split(",")

            if (parts.size < 2)
                return@forEach

            val denomination =
                parts[0].toFloatOrNull()
                    ?: return@forEach

            val quantity =
                parts[1].toIntOrNull()
                    ?: return@forEach

            if (quantity < 0)
                return@forEach

            machine.register.addCash(
                denomination,
                quantity
            )
        }
    }

    /*
     * ==========================================================
     * TRANSACTION LOG
     * ==========================================================
     */

    fun saveTransaction(
        entry: MachineEntry,
        baseItem: String,
        basePrice: Float,
        addOns: List<Pair<String, Float>>,
        totalPrice: Float,
        cashInserted: Float,
        change: Float
    ) {

        val file =
            File(
                entry.folder,
                "transactions.csv"
            )

        file.parentFile?.mkdirs()

        val isNew =
            !file.exists() ||
            file.length() == 0L

        file.appendText(
            buildString {

                if (isNew) {

                    append(
                        "timestamp," +
                        "base_item," +
                        "base_price," +
                        "addons," +
                        "addon_total," +
                        "total_price," +
                        "cash_inserted," +
                        "change"
                    )

                    append("\n")
                }

                val timestamp =
                    LocalDateTime.now()
                        .format(
                            DateTimeFormatter.ofPattern(
                                "yyyy-MM-dd HH:mm:ss"
                            )
                        )

                val addonNames =
                    addOns.joinToString(";") {
                        escapeCsv(it.first)
                    }

                val addonTotal =
                    addOns.sumOf {
                        it.second.toDouble()
                    }.toFloat()

                append(
                    escapeCsv(timestamp)
                )

                append(",")

                append(
                    escapeCsv(baseItem)
                )

                append(",")

                append(
                    "%.2f".format(basePrice)
                )

                append(",")

                append(
                    escapeCsv(addonNames)
                )

                append(",")

                append(
                    "%.2f".format(addonTotal)
                )

                append(",")

                append(
                    "%.2f".format(totalPrice)
                )

                append(",")

                append(
                    "%.2f".format(cashInserted)
                )

                append(",")

                append(
                    "%.2f".format(change)
                )

                append("\n")
            }
        )
    }

    /*
     * ==========================================================
     * CSV ESCAPING
     * ==========================================================
     */

    private fun escapeCsv(
        value: String
    ): String {

        if (
            value.contains(",") ||
            value.contains("\"") ||
            value.contains("\n")
        ) {

            return "\"" +
                value.replace(
                    "\"",
                    "\"\""
                ) +
                "\""
        }

        return value
    }
}