package openmods.gui.component.page;

import net.minecraft.client.Minecraft;
import net.minecraft.util.StatCollector;

import org.apache.commons.lang3.StringEscapeUtils;

import openmods.gui.component.GuiComponentLabel;

public class TitledPage extends PageBase {

    public TitledPage(String title, String content) {
        String translatedTitle = StatCollector.translateToLocal(title);
        String translatedContent = StringEscapeUtils.unescapeJava(StatCollector.translateToLocal(content));
        int x = (getWidth() - Minecraft.getMinecraft().fontRenderer.getStringWidth(translatedTitle)) / 2;

        addComponent(new GuiComponentLabel(x, 12, translatedTitle).setScale(BookScaleConfig.getPageTitleScale()));

        final GuiComponentLabel lblContent = new GuiComponentLabel(0, 35, getWidth() - 20, 300, translatedContent);

        lblContent.setScale(BookScaleConfig.getPageContentScale());
        lblContent.setAdditionalLineHeight(BookScaleConfig.getTitlePageSeparator());

        lblContent.setX((getWidth() - lblContent.getWidth()) / 2);
        addComponent(lblContent);
    }
}
