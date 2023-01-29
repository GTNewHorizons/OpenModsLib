package openmods;

import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

import openmods.config.gui.OpenModsConfigScreen;

import com.google.common.collect.ImmutableSet;
import cpw.mods.fml.client.IModGuiFactory;

public class GuiFactory implements IModGuiFactory {

    public static class ConfigScreen extends OpenModsConfigScreen {

        public ConfigScreen(GuiScreen parent) {
            super(parent, OpenMods.MODID, "OpenModsLib");
        }
    }

    @Override
    public void initialize(Minecraft minecraftInstance) {}

    @Override
    public Class<? extends GuiScreen> mainConfigGuiClass() {
        return ConfigScreen.class;
    }

    @Override
    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
        return ImmutableSet.of();
    }

    @Override
    public RuntimeOptionGuiHandler getHandlerFor(RuntimeOptionCategoryElement element) {
        return null;
    }
}
