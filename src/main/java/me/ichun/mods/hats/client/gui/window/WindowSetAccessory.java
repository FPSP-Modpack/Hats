package me.ichun.mods.hats.client.gui.window;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import me.ichun.mods.hats.client.gui.WorkspaceHats;
import me.ichun.mods.hats.client.gui.window.element.ElementHatRender;
import me.ichun.mods.hats.client.gui.window.element.ElementHatsScrollView;
import me.ichun.mods.hats.common.Hats;
import me.ichun.mods.hats.common.hats.HatHandler;
import me.ichun.mods.ichunutil.client.gui.bns.window.Fragment;
import me.ichun.mods.ichunutil.client.gui.bns.window.Window;
import me.ichun.mods.ichunutil.client.gui.bns.window.constraint.Constraint;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.View;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.element.Element;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.element.ElementScrollBar;
import me.ichun.mods.ichunutil.client.render.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.MathHelper;

import javax.annotation.Nonnull;

public class WindowSetAccessory extends Window<WorkspaceHats>
{
    private final @Nonnull ElementHatRender<?> parentElement;

    public WindowSetAccessory(WorkspaceHats parent, @Nonnull ElementHatRender<?> parentElement)
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

        setView(new ViewSetAccessory(this));
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

    public static class ViewSetAccessory extends View<WindowSetAccessory>
    {
        public int age;
        public float renderTick = 0F;

        public ViewSetAccessory(@Nonnull WindowSetAccessory parent)
        {
            super(parent, "hats.gui.window.hat.personaliser");

            int padding = 0;

            ElementScrollBar<?> sv = new ElementScrollBar<>(this, ElementScrollBar.Orientation.VERTICAL, 0.6F);
            sv.constraints().top(this, Constraint.Property.Type.TOP, padding)
                    .bottom(this, Constraint.Property.Type.BOTTOM, padding) // 10 + 20 + 10, bottom + button height + padding
                    .right(this, Constraint.Property.Type.RIGHT, padding);
            elements.add(sv);

            ElementHatsScrollView list = new ElementHatsScrollView(this).setScrollVertical(sv);
            list.constraints().top(this, Constraint.Property.Type.TOP, padding + 1)
                    .bottom(this, Constraint.Property.Type.BOTTOM, padding + 1)
                    .left(this, Constraint.Property.Type.LEFT, padding + 1)
                    .right(sv, Constraint.Property.Type.LEFT, 2 + 1);
            elements.add(list);

            ElementHatRender<?> hat = new ElementHatRender<>(list, parent.parentElement.hatDetails, btn -> {
                ElementHatsScrollView scrollView = (ElementHatsScrollView)btn.parentFragment;
                if(btn.toggleState) //we're selected
                {
                    for(Element<?> element : scrollView.elements)
                    {
                        if(element != btn)
                        {
                            ((ElementHatRender<?>)element).toggleState = false;
                        }
                    }

                    HatHandler.assignSpecificHat(parentFragment.parent.hatEntity, btn.hatDetails);
                }
            }, btn -> {
            }
            );
            hat.setToggled(false);
            hat.setSize(50, 70);
            list.addElement(hat);
        }

        @Override
        public void tick()
        {
            super.tick();

            age++;
        }

        @Override
        public void resize(Minecraft mc, int width, int height)
        {
            super.resize(mc, width, height);
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
            int singProg = (int)(hatsListPadding * prog);
            int doubProg = (int)((hatsListPadding * 2) * prog);

            //RENDER the hat border first

            int otherRenderHatsListPadding = 3;
            int hatViewWidth = parentFragment.parentElement.getWidth();
            int hatViewX = parentFragment.parentElement.posX;
            int targetElementX = ((WindowHatsList.ViewHatsList)parentFragment.parent.windowHatsList.getCurrentView()).list.getLeft() + otherRenderHatsListPadding;

            parentFragment.parentElement.setPosX((int)(hatViewX + (targetElementX - parentFragment.parentElement.getLeft()) * prog));
            parentFragment.parentElement.setWidth((int)(parentFragment.parentElement.width + (parentFragment.parentElement.getMinWidth() - parentFragment.parentElement.width) * prog));

            parentFragment.parentElement.posX -= singProg;
            parentFragment.parentElement.width += doubProg;
            parentFragment.parentElement.posY -= singProg;
            parentFragment.parentElement.height += doubProg;

            if(renderMinecraftStyle())
            {
                int width = parentFragment.parentElement.width;
                int height = parentFragment.parentElement.height;

                RenderSystem.enableAlphaTest();
                //draw the corners
                bindTexture(Fragment.VANILLA_TABS);

                //fill space
                RenderHelper.draw(stack, parentFragment.parentElement.getLeft() + 4, parentFragment.parentElement.getTop() + 4, width - 8, height - 8, 0, 4D/256D, 24D/256D, 36D/256D, 60D/256D); //fill space

                //draw borders
                RenderHelper.draw(stack, parentFragment.parentElement.getLeft(), parentFragment.parentElement.getTop() + 4, 4, height - 8, 0, 0D/256D, 4D/256D, 36D/256D, 60D/256D); //left border
                RenderHelper.draw(stack, parentFragment.parentElement.getLeft() + 4, parentFragment.parentElement.getTop(), width - 8, 4, 0, 4D/256D, 24D/256D, 32D/256D, 36D/256D); //top border
                RenderHelper.draw(stack, parentFragment.parentElement.getRight() - 4, parentFragment.parentElement.getTop() + 4, 4, height - 8, 0, 24D/256D, 28D/256D, 36D/256D, 60D/256D); //right border
                RenderHelper.draw(stack, parentFragment.parentElement.getLeft() + 4, parentFragment.parentElement.getBottom() - 4, width - 8, 4, 0, 4D/256D, 24D/256D, 124D/256D, 128D/256D); //bottom left

                //draw corners
                RenderHelper.draw(stack, parentFragment.parentElement.getLeft(), parentFragment.parentElement.getTop(), 4, 4, 0, 0D/256D, 4D/256D, 32D/256D, 36D/256D); //top left
                RenderHelper.draw(stack, parentFragment.parentElement.getRight() - 4, parentFragment.parentElement.getTop(), 4, 4, 0, 24D/256D, 28D/256D, 32D/256D, 36D/256D); //top right
                RenderHelper.draw(stack, parentFragment.parentElement.getLeft(), parentFragment.parentElement.getBottom() - 4, 4, 4, 0, 0D/256D, 4D/256D, 124D/256D, 128D/256D); //bottom left
                RenderHelper.draw(stack, parentFragment.parentElement.getRight() - 4, parentFragment.parentElement.getBottom() - 4, 4, 4, 0, 24D/256D, 28D/256D, 124D/256D, 128D/256D); //bottom left
            }
            else
            {
                parentFragment.parentElement.fill(stack, getTheme().windowBorder, 0);
                parentFragment.parentElement.fill(stack, getTheme().windowBackground, 3);
            }

            parentFragment.parentElement.height -= doubProg;
            parentFragment.parentElement.posY += singProg;
            parentFragment.parentElement.width -= doubProg;
            parentFragment.parentElement.posX += singProg;

            parentFragment.parentElement.setWidth(hatViewWidth);
            parentFragment.parentElement.setPosX(hatViewX);
            //end render the hat border first

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

            float prog = 1F;
            if(age <= Hats.configClient.guiAnimationTime)
            {
                prog = (float)Math.sin(Math.toRadians(MathHelper.clamp(((age + partialTick) / Hats.configClient.guiAnimationTime), 0F, 1F) * 90F));
            }

            int hatViewWidth = parentFragment.parentElement.getWidth();
            int hatViewLeft = parentFragment.parentElement.getLeft();
            int targetElementX = ((WindowHatsList.ViewHatsList)parentFragment.parent.windowHatsList.getCurrentView()).list.getLeft() + hatsListPadding;
            int hatViewTop = parentFragment.parentElement.getTop();

            parentFragment.parentElement.setLeft((int)(hatViewLeft + (targetElementX - hatViewLeft) * prog));
            parentFragment.parentElement.setWidth((int)(parentFragment.parentElement.width + (parentFragment.parentElement.getMinWidth() - parentFragment.parentElement.width) * prog));
            parentFragment.parentElement.setTop(hatViewTop);

            //This is in relation of the new parentElementPosition
            int maxHeight = parentFragment.parent.windowHatsList.height - (parentFragment.parent.windowHatsList.borderSize.get() * 2);
            int idealHeight = 70 + 8; //70 + 2x padding //TODO make the scrollbar completely optional so that we can just not have it.
            int targetHeight = Math.min(idealHeight, maxHeight);

            int targetX = parentFragment.parentElement.getRight() + (hatsListPadding * 4);
            int targetWidth = 60 + (idealHeight > maxHeight ? 14 : 0); //50 width + 2x list padding and border (5 x 2) + padding to scroll (4) + scrolll (14) + 2x padding (6)//TODO update this? Calculate number of accessories to fit on screen and if we need a scroll bar or not

            parentFragment.posX = (int)(parentFragment.parentElement.getLeft() + (targetX - parentFragment.parentElement.getLeft()) * prog);
            parentFragment.width = (int)(parentFragment.parentElement.width + (targetWidth - parentFragment.parentElement.width) * prog);

            parentFragment.height = (int)(parentFragment.parentElement.height + (targetHeight - parentFragment.parentElement.height) * prog);
            parentFragment.resize(getWorkspace().getMinecraft(), parentFragment.width, parentFragment.height);

            renderTick = partialTick;

            //We're using RenderSystem instead of MatrixStack because of the entity render
            RenderSystem.translatef(0F, 0F, 20F);
            stack.push();

            stack.translate(0F, 0F, 10F);

            super.render(stack, mouseX, mouseY, partialTick);

            stack.pop();

            Fragment<?> fragment = parentFragment.parentElement.parentFragment;
            parentFragment.parentElement.parentFragment = this;

            parentFragment.parentElement.parentFragment.setScissor();

            //We're using RenderSystem instead of MatrixStack because of the entity render
            RenderSystem.translatef(0F, 0F, 20F);

            parentFragment.parentElement.setLeft((int)(hatViewLeft + (targetElementX - hatViewLeft) * prog));
            parentFragment.parentElement.setTop(hatViewTop);

            parentFragment.parentElement.render(stack, mouseX, mouseY, partialTick);

            parentFragment.parentElement.parentFragment = fragment;
            parentFragment.parentElement.setWidth(hatViewWidth);
            parentFragment.parentElement.setLeft(hatViewLeft);
            parentFragment.parentElement.setTop(hatViewTop);

            resetScissorToParent();
        }

        @Override
        public void setScissor()
        {
            int hatRend = 50 + 12 + 3 + 6; //element + borders + padding + our border
            RenderHelper.startGlScissor(getLeft() - hatRend, getTop() - 6, width + hatRend + 6, height + 12);
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
