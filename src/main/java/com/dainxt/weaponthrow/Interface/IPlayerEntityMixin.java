package com.dainxt.weaponthrow.Interface;

import com.dainxt.weaponthrow.capabilities.PlayerThrowData;

public interface IPlayerEntityMixin {
    void weaponThrow$setThrowPower(PlayerThrowData value);
    PlayerThrowData weaponThrow$getThrowPower();
}

