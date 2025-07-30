package vergisst.minecraftmod.weaponthrow.Interface;

import vergisst.minecraftmod.weaponthrow.capabilities.PlayerThrowData;

public interface IPlayerEntityMixin {
    void weaponThrow$setThrowPower(PlayerThrowData value);
    PlayerThrowData weaponThrow$getThrowPower();
}

