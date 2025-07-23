package vergisst.minecraftmod.weaponthrowlite.packets

public enum class PacketState(private val index: Byte) {
    NONE(0.toByte()),
    START(1.toByte()),
    DURING(2.toByte()),
    FINISH(3.toByte());

    fun toByte(): Byte {
        return index
    }

    companion object {
        fun fromByte(index: Int): PacketState {
            for (equipmentslottype in entries) {
                if (equipmentslottype.toByte().toInt() == index) {
                    return equipmentslottype
                }
            }
            return PacketState.NONE
        }
    }
}