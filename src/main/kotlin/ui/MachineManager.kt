package ui

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

                    } else {

                        VendingMachine(

                            slotLimit,
                            itemLimit

                        )

                    }

                machines.add(

                    MachineEntry(
                        folder,
                        machine
                    )

                )

            }

    }

}