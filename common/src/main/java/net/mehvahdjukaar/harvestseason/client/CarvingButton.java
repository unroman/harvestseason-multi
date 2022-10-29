package net.mehvahdjukaar.harvestseason.client;


import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;


public class CarvingButton extends GuiComponent implements Widget, GuiEventListener, NarratableEntry {
    public final int u;
    public final int v;
    public final int x;
    public final int y;
    public static final int WIDTH = 6;
    protected boolean isHovered;
    protected boolean carved = false;
    protected boolean focused;

    private final IDraggable onDragged;
    private final IPressable onPress;

    public CarvingButton(int centerX, int centerY, int u, int v, IPressable pressedAction,
                         IDraggable dragAction) {
        this.x = centerX - ((8 - u) * WIDTH);
        this.y = centerY - ((-v) * WIDTH);
        this.u = u;
        this.v = v;
        this.onPress = pressedAction;
        this.onDragged = dragAction;
    }

    public void setCarved(boolean carved) {
        this.carved = carved;
    }

    public boolean getCarved() {
        return carved;
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {


        //soboolean wasHovered = this.isHovered();
    }


    public void renderButton(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks, Material material) {
        this.isHovered = this.isMouseOver(mouseX, mouseY);

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, new ResourceLocation("block/pumpkin_side"));
        TextureAtlasSprite sprite = material.sprite();
        RenderSystem.setShaderTexture(0, sprite.atlas().location());

        // sprite.getU0(), sprite.getU1(), sprite.getV0(), sprite.getV1();

        RenderSystem.setShaderColor(1, 1, 1, 1.0F);
        int width = (int) (sprite.getWidth() / (sprite.getU1() - sprite.getU0()));
        int height = (int) (sprite.getHeight() / (sprite.getV1() - sprite.getV0()));
        blit(matrixStack, this.x, this.y, WIDTH, WIDTH, sprite.getU(u) * width, height * sprite.getV(v), 1, 1,
                width, height);

    }

    public void renderTooltip(PoseStack matrixStack) {
        //maybe remove this
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        RenderSystem.setShaderColor(0.5f, 0.5f, 0.5f, 1);

        blit(matrixStack, this.x - 1, this.y - 1, 16 * WIDTH, 0, WIDTH + 2, WIDTH + 2, 32 * WIDTH, 16 * WIDTH);
        //this.renderButton(matrixStack);
    }

    //toggle
    public void onClick(double mouseX, double mouseY) {
        this.carved = !this.carved;
        this.onPress.onPress(this.u, this.v, this.carved);

    }

    public void onRelease(double mouseX, double mouseY) {
    }

    //set
    public void onDrag(double mouseX, double mouseY, boolean on) {
        this.carved = on;
        this.onPress.onPress(this.u, this.v, this.carved);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.isValidClickButton(button)) {
            boolean flag = this.isMouseOver(mouseX, mouseY);
            if (flag) {
                this.playDownSound(Minecraft.getInstance().getSoundManager());
                this.onClick(mouseX, mouseY);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (this.isValidClickButton(button)) {
            this.onRelease(mouseX, mouseY);
            return true;
        } else {
            return false;
        }
    }

    protected boolean isValidClickButton(int button) {
        return button == 0;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {

        if (this.isValidClickButton(button)) {
            this.onDragged.onDragged(mouseX, mouseY, this.carved);
            return true;
        } else {
            return false;
        }
    }

    public boolean isHovered() {
        return this.isHovered || this.focused;
    }

    @Override
    public boolean changeFocus(boolean focus) {
        this.focused = !this.focused;
        //this.onFocusedChanged(this.focused);
        return this.focused;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseX >= this.x && mouseY >= this.y && mouseX < (this.x + WIDTH) && mouseY < (this.y + WIDTH);
    }


    public void playDownSound(SoundManager handler) {
        handler.play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    @Override
    public NarrationPriority narrationPriority() {
        return NarrationPriority.NONE;
    }

    @Override
    public void updateNarration(NarrationElementOutput pNarrationElementOutput) {
    }

    public interface IPressable {
        void onPress(int x, int y, boolean on);
    }

    public interface IDraggable {
        void onDragged(double mouseX, double mouseY, boolean on);
    }

}

