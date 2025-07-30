package com.dainxt.weaponthrow.handler;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import com.dainxt.weaponthrow.config.WeaponThrowConfig;

public class ConfigRegistry {

    public static ConfigHolder<WeaponThrowConfig> COMMON;

    public static void registerConfig() {
        COMMON = AutoConfig.register(WeaponThrowConfig.class, GsonConfigSerializer::new);
    }
}
