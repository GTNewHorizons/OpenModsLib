package openmods.gui.component;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import com.google.common.collect.Lists;

import openmods.gui.component.page.BookScaleConfig;
import openmods.gui.listener.IMouseDownListener;
import openmods.utils.render.FakeIcon;

public class GuiComponentBook extends BaseComposite {

    private static final ResourceLocation PAGETURN = new ResourceLocation("openmodslib", "pageturn");

    private GuiComponentSpriteButton imgPrev;
    private GuiComponentSpriteButton imgNext;
    private GuiComponentLabel pageNumberLeft;
    private GuiComponentLabel pageNumberRight;

    public static IIcon iconPageLeft = FakeIcon.createSheetIcon(211, 0, -211, 180);
    public static IIcon iconPageRight = FakeIcon.createSheetIcon(0, 0, 211, 180);
    public static IIcon iconPrev = FakeIcon.createSheetIcon(57, 226, 18, 10);
    public static IIcon iconNext = FakeIcon.createSheetIcon(57, 213, 18, 10);
    public static IIcon iconPrevHover = FakeIcon.createSheetIcon(80, 226, 18, 10);
    public static IIcon iconNextHover = FakeIcon.createSheetIcon(80, 213, 18, 10);

    private static final ResourceLocation texture = new ResourceLocation("openmodslib:textures/gui/book.png");

    public List<BaseComponent> pages;

    private int index = 0;

    public GuiComponentBook() {
        super(0, 0);

        GuiComponentSprite imgLeftBackground = new GuiComponentSprite(0, 0, iconPageLeft, texture);
        GuiComponentSprite imgRightBackground = new GuiComponentSprite(0, 0, iconPageRight, texture);
        imgRightBackground.setX(iconPageRight.getIconWidth());

        imgPrev = new GuiComponentSpriteButton(24, 158, iconPrev, iconPrevHover, texture);
        imgPrev.setListener(new IMouseDownListener() {

            @Override
            public void componentMouseDown(BaseComponent component, int x, int y, int button) {
                prevPage();
            }
        });
        imgNext = new GuiComponentSpriteButton(380, 158, iconNext, iconNextHover, texture);
        imgNext.setListener(new IMouseDownListener() {

            @Override
            public void componentMouseDown(BaseComponent component, int x, int y, int button) {
                nextPage();
            }
        });

        final float scalePageNumber = BookScaleConfig.getPageNumberScale();
        pageNumberLeft = new GuiComponentLabel(85, 163, 100, 10, "XXX");
        pageNumberLeft.setScale(scalePageNumber);
        pageNumberRight = new GuiComponentLabel(295, 163, 100, 10, "XXX");
        pageNumberRight.setScale(scalePageNumber);

        addComponent(imgLeftBackground);
        addComponent(imgRightBackground);
        addComponent(imgPrev);
        addComponent(imgNext);
        addComponent(pageNumberLeft);
        addComponent(pageNumberRight);

        pages = Lists.newArrayList();

    }

    public int getNumberOfPages() {
        return pages.size();
    }

    @Override
    public int getWidth() {
        return iconPageRight.getIconWidth() * 2;
    }

    @Override
    public int getHeight() {
        return iconPageRight.getIconHeight();
    }

    public void addPage(BaseComponent page) {
        addComponent(page);
        page.setEnabled(false);
        pages.add(page);
    }

    public void enablePages() {
        int i = 0;
        for (BaseComponent page : pages) {
            final boolean isLeft = i == index;
            final boolean isRight = i == index + 1;

            if (isLeft) {
                page.setEnabled(true);
                page.setX(20);

            } else if (isRight) {
                page.setEnabled(true);
                page.setX(10 + iconPageRight.getIconWidth());
            } else {
                page.setEnabled(false);
            }
            i++;
        }

        int totalPageCount = i % 2 == 0 ? i : i + 1;

        imgNext.setEnabled(index < pages.size() - 2);
        imgPrev.setEnabled(index > 0);
        pageNumberLeft
                .setText(StatCollector.translateToLocalFormatted("openblocks.misc.page", index + 1, totalPageCount));
        pageNumberRight
                .setText(StatCollector.translateToLocalFormatted("openblocks.misc.page", index + 2, totalPageCount));
    }

    @Override
    public void renderComponentBackground(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY) {}

    public void changePage(int newPage) {
        newPage &= ~1;
        if (newPage != index) {
            index = newPage;
            enablePages();
            playPageTurnSound();
        }
    }

    private static void playPageTurnSound() {
        Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.func_147674_a(PAGETURN, 1.0f));
    }

    public IMouseDownListener createBookmarkListener(final int index) {
        return new IMouseDownListener() {

            @Override
            public void componentMouseDown(BaseComponent component, int x, int y, int button) {
                changePage(index);
            }
        };
    }

    public void prevPage() {
        if (index > 0) changePage(index - 2);
    }

    public void nextPage() {
        if (index < pages.size() - 2) changePage(index + 2);
    }

    public void firstPage() {
        changePage(0);
    }

    public void lastPage() {
        changePage(getNumberOfPages() - 1);
    }
}
