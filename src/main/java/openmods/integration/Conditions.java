package openmods.integration;

import cpw.mods.fml.common.Loader;
import openmods.conditions.ICondition;
import openmods.reflection.SafeClassLoad;

public class Conditions extends openmods.conditions.Conditions {

    public static ICondition classExists(String clsName) {
        final SafeClassLoad cls = new SafeClassLoad(clsName);
        return new ICondition() {

            @Override
            public boolean check() {
                return cls.tryLoad();
            }
        };
    }

    public static ICondition modLoaded(final String modName) {
        return new ICondition() {

            @Override
            public boolean check() {
                return Loader.isModLoaded(modName);
            }
        };
    }

}
