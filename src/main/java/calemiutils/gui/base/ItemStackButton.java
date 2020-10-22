package calemiutils.gui.base;

import calemiutils.util.helper.ScreenHelper;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;

@OnlyIn(Dist.CLIENT)
public abstract class ItemStackButton extends Button {

    private ScreenRect rect;
    protected final ItemRenderer itemRenderer;

    /**
     * A base button used to render a given ItemStack.
     * @param pressable Called when the button is pressed.
     */
    public ItemStackButton (int x, int y, ItemRenderer itemRender, Button.IPressable pressable) {
        super(x, y, 16, 16, "", pressable);
        rect = new ScreenRect(this.x, this.y, width, height);
        this.itemRenderer = itemRender;
    }

    public abstract ItemStack getRenderedStack();
    public abstract String[] getTooltip();

    public void setRect(ScreenRect rect) {
        this.rect = rect;
        this.x = rect.x;
        this.y = rect.y;
        this.width = rect.width;
        this.height = rect.height;
    }

    @Override
    public void renderButton (int mouseX, int mouseY, float partialTicks) {

        if (this.visible && this.active) {

            isHovered = rect.contains(mouseX, mouseY);

            ScreenHelper.drawItemStack(itemRenderer, getRenderedStack(), rect.x, rect.y);
            ScreenHelper.drawHoveringTextBox(mouseX, mouseY, 150, rect, getTooltip());

            GL11.glColor4f(1, 1, 1, 1);
        }
    }
}
