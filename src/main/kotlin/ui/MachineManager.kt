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
                 * IMPORTANT:
                 *
                 * ItemManager.items must already be loaded
                 * before this function is called.
                 */

                loadInventory(
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
     * SAVE ONE MACHINE IMMEDIATELY
     * ==========================================================
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

            if (slotIndex !in machine.slots.indices)
                return@forEach

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

            /*
             * Empty slot.
             */

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

            /*
             * If the item no longer exists,
             * leave the slot empty instead of crashing.
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

                    out.println(
                        "$denomination,$quantity"
                    )
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

        /*
         * Start from an empty register.
         */

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
}