package me.ichun.mods.hats.client.gui.window;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import me.ichun.mods.hats.client.gui.WorkspaceHats;
import me.ichun.mods.hats.client.gui.window.element.ElementHatRender;
import me.ichun.mods.hats.common.Hats;
import me.ichun.mods.ichunutil.client.gui.bns.window.Fragment;
import me.ichun.mods.ichunutil.client.gui.bns.window.Window;
import me.ichun.mods.ichunutil.client.gui.bns.window.constraint.Constraint;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.View;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.element.ElementButtonTextured;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.element.ElementPadding;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.element.ElementScrollBar;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.element.ElementTextWrapper;
import me.ichun.mods.ichunutil.client.render.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

import javax.annotation.Nonnull;

public class WindowSetColouriser extends Window<WorkspaceHats>
{
    private final @Nonnull ElementHatRender<?> parentElement;

    public WindowSetColouriser(WorkspaceHats parent, @Nonnull ElementHatRender<?> parentElement)
    {
        super(parent);

        setBorderSize(() -> 0);

        disableBringToFront();
        disableDocking();
        disableDockStacking();
        disableUndocking();
        disableDrag();
        disableDragResize();
        disableTitle();
        isNotUnique();

        this.parentElement = parentElement;

        setView(new ViewSetColouriser(this));
    }

    @Override
    public void setScissor()
    {
        currentView.setScissor();
    }
    @Override
    public void renderBackground(MatrixStack stack){}//No BG

    @Override
    public int getMinWidth()
    {
        return parentElement.getMinWidth();
    }

    @Override
    public int getMinHeight()
    {
        return parentElement.getMinHeight();
    }

    public static class ViewSetColouriser extends View<WindowSetColouriser>
    {
        public static ResourceLocation TEX_RESET = new ResourceLocation("hats", "textures/icon/reset.png");

        public ElementButtonTextured<?> confirm;
        public ElementButtonTextured<?> reset;
        public int age;
        public float renderTick = 0F;

        public ViewSetColouriser(@Nonnull WindowSetColouriser parent)
        {
            super(parent, "hats.gui.window.hat.colorizer");

            ElementPadding padding = new ElementPadding(this, parentFragment.parentElement.getMinWidth(), parentFragment.parentElement.getMinHeight());
            padding.constraints().top(this, Constraint.Property.Type.TOP, 0).right(this, Constraint.Property.Type.RIGHT, 0);
            elements.add(padding);

            confirm = new ElementButtonTextured<>(this, WindowHatOptions.ViewHatOptions.TEX_COLOURISE, btn -> {
            }); //TODO finalise and send?
            confirm.setTooltip(I18n.format("gui.ok"));
            confirm.setSize(20, 20);
            confirm.constraints().right(padding, Constraint.Property.Type.LEFT, 3).top(padding, Constraint.Property.Type.TOP, 0);
            elements.add(confirm);

            reset = new ElementButtonTextured<>(this, TEX_RESET, btn -> {
                //TODO this
            });
            reset.setTooltip(I18n.format("hats.gui.button.reset"));
            reset.setSize(20, 20);
            reset.constraints().right(padding, Constraint.Property.Type.LEFT, 3).bottom(padding, Constraint.Property.Type.BOTTOM, 0);
            elements.add(reset);

            int edgePadding = 3;
            ElementScrollBar<?> svR = new ElementScrollBar<>(this, ElementScrollBar.Orientation.HORIZONTAL, 0.1F); //100%?
            svR.constraints().top(padding, Constraint.Property.Type.TOP, 1).right(confirm, Constraint.Property.Type.LEFT, edgePadding).left(this, Constraint.Property.Type.LEFT, edgePadding);
            svR.setId("colouriserR");
            svR.setScrollProg(1F); //TODO set this
            elements.add(svR);

            ElementTextWrapper textR = new ElementTextWrapper(this);
            textR.setText("R");
            textR.constraints().bottom(svR, Constraint.Property.Type.BOTTOM, 0).left(svR, Constraint.Property.Type.LEFT, 1);
            elements.add(textR);

            ElementScrollBar<?> svG = new ElementScrollBar<>(this, ElementScrollBar.Orientation.HORIZONTAL, 0.1F); //100%?
            svG.constraints().top(svR, Constraint.Property.Type.BOTTOM, 4).right(confirm, Constraint.Property.Type.LEFT, edgePadding).left(this, Constraint.Property.Type.LEFT, edgePadding);
            svG.setId("colouriserG");
            svG.setScrollProg(1F); //TODO set this
            elements.add(svG);

            ElementTextWrapper textG = new ElementTextWrapper(this);
            textG.setText("G");
            textG.constraints().bottom(svG, Constraint.Property.Type.BOTTOM, 0).left(svG, Constraint.Property.Type.LEFT, 1);
            elements.add(textG);

            ElementScrollBar<?> svB = new ElementScrollBar<>(this, ElementScrollBar.Orientation.HORIZONTAL, 0.1F); //100%?
            svB.constraints().top(svG, Constraint.Property.Type.BOTTOM, 4).right(confirm, Constraint.Property.Type.LEFT, edgePadding).left(this, Constraint.Property.Type.LEFT, edgePadding);
            svB.setId("colouriserB");
            svB.setScrollProg(1F); //TODO set this
            elements.add(svB);

            ElementTextWrapper textB = new ElementTextWrapper(this);
            textB.setText("B");
            textB.constraints().bottom(svB, Constraint.Property.Type.BOTTOM, 0).left(svB, Constraint.Property.Type.LEFT, 1);
            elements.add(textB);

            ElementScrollBar<?> svA = new ElementScrollBar<>(this, ElementScrollBar.Orientation.HORIZONTAL, 0.1F); //100%?
            svA.constraints().top(svB, Constraint.Property.Type.BOTTOM, 4).right(confirm, Constraint.Property.Type.LEFT, edgePadding).left(this, Constraint.Property.Type.LEFT, edgePadding);
            svA.setId("colouriserA");
            svA.setScrollProg(1F); //TODO set this
            elements.add(svA);

            ElementTextWrapper textA = new ElementTextWrapper(this);
            textA.setText("A");
            textA.constraints().bottom(svA, Constraint.Property.Type.BOTTOM, 0).left(svA, Constraint.Property.Type.LEFT, 1);
            elements.add(textA);
        }

        @Override
        public void tick()
        {
            super.tick();

            age++;
        }

        @Override
        public void renderBackground(MatrixStack stack)
        {
            float prog = 1F;
            if(age <= Hats.configClient.guiAnimationTime)
            {
                prog = (float)Math.sin(Math.toRadians(MathHelper.clamp(((age + renderTick) / Hats.configClient.guiAnimationTime), 0F, 1F) * 90F));
            }

            int hatsListPadding = 6;
            float singProg = hatsListPadding * prog;
            float doubProg = (hatsListPadding * 2) * prog;

            posX -= singProg;
            width += doubProg;
            posY -= singProg;
            height += doubProg;

            if(renderMinecraftStyle())
            {
                RenderSystem.enableAlphaTest();
                //draw the corners
                bindTexture(Fragment.VANILLA_TABS);

                //fill space
                RenderHelper.draw(stack, getLeft() + 4, getTop() + 4, width - 8, height - 8, 0, 4D/256D, 24D/256D, 36D/256D, 60D/256D); //fill space

                //draw borders
                RenderHelper.draw(stack, getLeft(), getTop() + 4, 4, height - 8, 0, 0D/256D, 4D/256D, 36D/256D, 60D/256D); //left border
                RenderHelper.draw(stack, getLeft() + 4, getTop(), width - 8, 4, 0, 4D/256D, 24D/256D, 32D/256D, 36D/256D); //top border
                RenderHelper.draw(stack, getRight() - 4, getTop() + 4, 4, height - 8, 0, 24D/256D, 28D/256D, 36D/256D, 60D/256D); //right border
                RenderHelper.draw(stack, getLeft() + 4, getBottom() - 4, width - 8, 4, 0, 4D/256D, 24D/256D, 124D/256D, 128D/256D); //bottom left

                //draw corners
                RenderHelper.draw(stack, getLeft(), getTop(), 4, 4, 0, 0D/256D, 4D/256D, 32D/256D, 36D/256D); //top left
                RenderHelper.draw(stack, getRight() - 4, getTop(), 4, 4, 0, 24D/256D, 28D/256D, 32D/256D, 36D/256D); //top right
                RenderHelper.draw(stack, getLeft(), getBottom() - 4, 4, 4, 0, 0D/256D, 4D/256D, 124D/256D, 128D/256D); //bottom left
                RenderHelper.draw(stack, getRight() - 4, getBottom() - 4, 4, 4, 0, 24D/256D, 28D/256D, 124D/256D, 128D/256D); //bottom left
            }
            else
            {
                fill(stack, getTheme().windowBorder, 0);
                fill(stack, getTheme().windowBackground, 3);
            }

            height -= doubProg;
            posY += singProg;
            width -= doubProg;
            posX += singProg;
        }

        @Override
        public void render(MatrixStack stack, int mouseX, int mouseY, float partialTick)
        {
            int hatsListPadding = 3;
            int targetX = ((WindowHatsList.ViewHatsList)parentFragment.parent.windowHatsList.getCurrentView()).list.getLeft() + hatsListPadding;
            int targetWidth = ((WindowHatsList.ViewHatsList)parentFragment.parent.windowHatsList.getCurrentView()).list.width - hatsListPadding - hatsListPadding;

            float prog = 1F;
            if(age <= Hats.configClient.guiAnimationTime)
            {
                prog = (float)Math.sin(Math.toRadians(MathHelper.clamp(((age + partialTick) / Hats.configClient.guiAnimationTime), 0F, 1F) * 90F));
            }

            parentFragment.posX = (int)(parentFragment.parentElement.getLeft() + (targetX - parentFragment.parentElement.getLeft()) * prog);
            parentFragment.width = (int)(parentFragment.parentElement.width + (targetWidth - parentFragment.parentElement.width) * prog);
            parentFragment.resize(getWorkspace().getMinecraft(), parentFragment.width, parentFragment.height);

            reset.posX = (int)(confirm.posX + (24 * (1F - prog)));

            renderTick = partialTick;
            stack.push();

            stack.translate(0F, 0F, 375F);

            super.render(stack, mouseX, mouseY, partialTick);

            stack.pop();

            int hatViewWidth = parentFragment.parentElement.getWidth();
            int hatViewLeft = parentFragment.parentElement.getLeft();
            int targetElementX = ((WindowHatsList.ViewHatsList)parentFragment.parent.windowHatsList.getCurrentView()).list.getRight() - hatsListPadding - parentFragment.parentElement.getMinWidth();
            int hatViewTop = parentFragment.parentElement.getTop();

            Fragment<?> fragment = parentFragment.parentElement.parentFragment;
            parentFragment.parentElement.parentFragment = this;

            parentFragment.parentElement.parentFragment.setScissor();

            //We're using RenderSystem instead of MatrixStack because of the entity render
            RenderSystem.translatef(0F, 0F, 40F);

            parentFragment.parentElement.setLeft((int)(hatViewLeft + (targetElementX - hatViewLeft) * prog));
            parentFragment.parentElement.setWidth((int)(parentFragment.parentElement.width + (parentFragment.parentElement.getMinWidth() - parentFragment.parentElement.width) * prog));
            parentFragment.parentElement.setTop(hatViewTop);

            parentFragment.parentElement.render(stack, mouseX, mouseY, partialTick);

            parentFragment.parentElement.parentFragment = fragment;
            parentFragment.parentElement.setTop(hatViewTop);
            parentFragment.parentElement.setWidth(hatViewWidth);
            parentFragment.parentElement.setLeft(hatViewLeft);

            resetScissorToParent();
        }

        @Override
        public void setScissor()
        {
            RenderHelper.startGlScissor(getLeft() - 21, getTop() - 6, width + 21 + 6, height + 12);
        }

        @Override
        public int getMinWidth()
        {
            return parentFragment.getMinWidth();
        }

        @Override
        public int getMinHeight()
        {
            return parentFragment.getMinHeight();
        }
    }
}
