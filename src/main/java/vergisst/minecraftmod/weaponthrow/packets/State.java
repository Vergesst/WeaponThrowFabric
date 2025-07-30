package vergisst.minecraftmod.weaponthrow.packets;

public enum State {
    NONE((byte)0),
    START((byte)1),
    DURING((byte)2),
    FINISH((byte)3);

    private byte index;
    private State(byte i) {
        this.index = i;

    }
    public byte toByte() {
        return index;
    }

    public static State fromByte(int index) {
        for(State equipmentslottype : State.values()) {
            if(equipmentslottype.toByte() == index) {
                return equipmentslottype;
            }
        }
        return NONE;

    }
}