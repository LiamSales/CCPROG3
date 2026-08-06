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
     * Load All Machines
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
                    lines[0].trim().toInt()

                val itemLimit =
                    lines[1].trim().toInt()

                val machine: VendingMachine =

                    if (lines.size >= 3) {

                        SpecialMachine(

                            slotLimit,

                            itemLimit,

                            lines[2].trim().toInt()

                        )

                    }

                    else {

                        VendingMachine(

                            slotLimit,

                            itemLimit

                        )

                    }

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
     * Save All Machines
     * ==========================================================
     */

    fun saveMachines() {

        machines.forEach {

            saveMachine(it)

        }

    }

    private fun saveMachine(

        entry: MachineEntry

    ) {

        val folder =
            entry.folder

        val machine =
            entry.machine


        /*
         * info.csv
         */

        val info =
            File(folder, "info.csv")

        info.printWriter().use { out ->

            out.println(machine.slotLimit)

            out.println(machine.itemLimit)

            if (machine is SpecialMachine) {

                out.println(

                    machine.getAddOnSlotCount()

                )

            }

        }


        /*
         * inventory.csv
         */

        val inventory =
            File(folder, "inventory.csv")

        inventory.printWriter().use { out ->

            machine.slots.forEachIndexed {

                index,
                slot ->

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


        /*
         * register.csv
         */

        val register =
            File(folder, "register.csv")

        register.printWriter().use { out ->

            machine.register

                .getContents()

                .forEach {

                    (denomination, quantity) ->

                    out.println(

                        "$denomination,$quantity"

                    )

                }

        }

    }


    /*
     * ==========================================================
     * Load Inventory
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

        val slot =
            parts[0].toInt()

        // Ignore invalid slot numbers
        if (slot !in machine.slots.indices)
            return@forEach

        val itemName =
            parts[1]

        val quantity =
            parts[2].toInt()

        val price =
            parts[3].toFloat()

        val sold =
            parts[4].toInt()

        val item =
            ItemManager.items.find {

                it.name == itemName

            }

        val currentSlot =
            machine.slots[slot]

        currentSlot.item =
            item

        currentSlot.quantity =
            quantity

        currentSlot.price =
            price

        currentSlot.sold =
            sold

    }

}

    /*
     * ==========================================================
     * Load Register
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

            machine.register.addCash(

                parts[0].toFloat(),

                parts[1].toInt()

            )

        }

    }

}