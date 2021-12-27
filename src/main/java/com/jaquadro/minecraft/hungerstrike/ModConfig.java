package com.jaquadro.minecraft.hungerstrike;

import net.minecraftforge.common.ForgeConfigSpec;

public class ModConfig
{
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final General GENERAL = new General(BUILDER);
    public static final ForgeConfigSpec spec = BUILDER.build();

    public static class General {
        public final ForgeConfigSpec.ConfigValue<Mode> mode;
        public final ForgeConfigSpec.ConfigValue<Double> foodHealFactor;
        public final ForgeConfigSpec.ConfigValue<Integer> foodStackSize;
        public final ForgeConfigSpec.ConfigValue<Boolean> hideHungerBar;
        public final ForgeConfigSpec.ConfigValue<Integer> hungerBaseline;

        public General(ForgeConfigSpec.Builder builder) {
            builder.push("General");

            mode = builder
                .comment("Mode can be set to NONE, LIST, or ALL",
                    "- NONE: Hunger Strike is disabled for all players.",
                    "- LIST: Hunger Strike is enabled for players added",
                    "        in-game with /hungerstrike command.",
                    "- ALL:  Hunger Strike is enabled for all players.")
                .defineEnum("mode", Mode.ALL);

            foodHealFactor = builder
                .comment("How to translate food points into heart points when consuming food.",
                    "At the default value of 0.5, food fills your heart bar at half the rate it would fill hunger.")
                .define("foodHealFactor", 0.5);

            foodStackSize = builder
                .comment("Globally overrides the maximum stack size of food items.",
                    "This property affects all Vanilla and Mod food items that derive from ItemFood.",
                    "Set to -1 to retain the default stack size of each food item.  Note: This will affect the entire server, not just players on hunger strike.",
                    "WARNING: Setting this property may result in unexpected behavior with other mods.")
                .defineInRange("maxFoodStackSize", -1, -1, 64);

            hideHungerBar = builder
                .comment("Controls whether or not the hunger bar is hidden for players on hunger strike.",
                    "If the hunger bar is left visible, it will remain filled at half capacity, except when certain potion effects are active like hunger and regeneration.")
                .define("hideHungerBar", true);

            hungerBaseline = builder
                .comment("The default hunger level when no status effects are active.",
                    "Valid range is [1 - 20], with 20 being fully filled, and 10 being half-filled.  The default value is 10, which disables health regen but allows sprinting.")
                .defineInRange("hungerBaseline", 10, 1, 20);

            builder.pop();
        }
    }

    public enum Mode {
        NONE,
        LIST,
        ALL;

        public static Mode fromValueIgnoreCase (String value) {
            if (value.compareToIgnoreCase("NONE") == 0)
                return Mode.NONE;
            else if (value.compareToIgnoreCase("LIST") == 0)
                return Mode.LIST;
            else if (value.compareToIgnoreCase("ALL") == 0)
                return Mode.ALL;

            return Mode.LIST;
        }
    }
}
