package ui

import model.Item
import model.SpecialMachine
import model.VendingMachine
import java.io.File

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

                /*
                 * ItemManager must already be loaded.
                 */
                loadInventory(
                    folder,
                    machine
                )

                loadRegister(
                    folder,
                    machine
                )

                loadAddOns(
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
     *
     * This is the ONLY function controllers should call
     * when they want to persist a machine.
     */

    fun saveMachine(
        entry: MachineEntry
    ) {

        saveInventory(
            entry.folder,
            entry.machine
        )

        saveRegister(
            entry.folder,
            entry.machine
        )

        saveAddOns(
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
     * SAVE CURRENT MACHINE
     * ==========================================================
     *
     * Useful from MaintenanceController/TestController.
     */

    fun saveMachine(
        folder: File,
        machine: VendingMachine
    ) {

        saveInventory(
            folder,
            machine
        )

        saveRegister(
            folder,
            machine
        )

        saveAddOns(
            folder,
            machine
        )
    }

    /*
     * ==========================================================
     * INVENTORY SAVE
     * ==========================================================
     *
     * Format:
     *
     * slot,itemName,quantity,price,sold
     *
     * Example:
     *
     * 0,Coke,5,25.0,2
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

    /*
     * ==========================================================
     * INVENTORY LOAD
     * ==========================================================
     */

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
                parts[0].trim().toIntOrNull()
                    ?: return@forEach

            if (slotIndex !in machine.slots.indices)
                return@forEach

            val itemName =
                parts[1].trim()

            val quantity =
                parts[2].trim().toIntOrNull()
                    ?: return@forEach

            val price =
                parts[3].trim().toFloatOrNull()
                    ?: return@forEach

            val sold =
                parts[4].trim().toIntOrNull()
                    ?: return@forEach

            /*
             * Empty slot.
             */

            if (itemName.isBlank()) {

                machine.clearSlot(
                    slotIndex
                )

                return@forEach
            }

            val item: Item? =
                ItemManager.items.find {
                    it.name == itemName
                }

            /*
             * Item no longer exists.
             * Leave slot empty.
             */

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
     * REGISTER SAVE
     * ==========================================================
     *
     * Format:
     *
     * denomination,quantity
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

    /*
     * ==========================================================
     * REGISTER LOAD
     * ==========================================================
     */

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
                parts[0].trim().toFloatOrNull()
                    ?: return@forEach

            val quantity =
                parts[1].trim().toIntOrNull()
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
     * ADD-ON SAVE
     * ==========================================================
     *
     * Format:
     *
     * slot,itemName,quantity,price,sold
     *
     * Only SpecialMachine has add-ons.
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

    /*
     * ==========================================================
     * ADD-ON LOAD
     * ==========================================================
     */

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
                parts[0].trim().toIntOrNull()
                    ?: return@forEach

            if (
                index !in
                special.getAddOnSlots().indices
            )
                return@forEach

            val itemName =
                parts[1].trim()

            val quantity =
                parts[2].trim().toIntOrNull()
                    ?: return@forEach

            val price =
                parts[3].trim().toFloatOrNull()
                    ?: return@forEach

            val sold =
                parts[4].trim().toIntOrNull()
                    ?: return@forEach

            /*
             * Empty add-on slot.
             */

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
                    ?: return@forEach

            val slot =
                special.getAddOnSlot(index)
                    ?: return@forEach

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
}