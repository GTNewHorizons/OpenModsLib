package openmods.calc.command;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.KeyBinding;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CalcKey {

    private final KeyBinding keyCalc = new KeyBinding(
            "openmodslib.key.calc",
            Keyboard.KEY_EQUALS,
            "openmodslib.key.category");

    public CalcKey() {
        ClientRegistry.registerKeyBinding(keyCalc);
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        handleInput();
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onMouseInput(InputEvent.MouseInputEvent event) {
        if (Mouse.getEventButton() >= 0) {
            handleInput();
        }
    }

    private void handleInput() {
        if (keyCalc.isPressed() && Minecraft.getMinecraft().currentScreen == null) {
            Minecraft.getMinecraft().displayGuiScreen(new GuiChat("= "));
        }
    }
}
