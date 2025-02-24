package info.u_team.u_team_core.util;

import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import info.u_team.u_team_core.api.gui.BackgroundColorProvider;
import info.u_team.u_team_core.api.gui.PerspectiveRenderable;
import info.u_team.u_team_core.api.gui.ScaleProvider;
import info.u_team.u_team_core.api.gui.TextProvider;
import info.u_team.u_team_core.api.gui.TextureProvider;
import info.u_team.u_team_core.api.gui.TooltipRenderable;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.Mth;

public class WidgetUtil {
	
	public static boolean isHovered(AbstractWidget widget) {
		return widget.isHovered;
	}
	
	public static <T extends AbstractWidget & PerspectiveRenderable & BackgroundColorProvider> void renderButtonLikeWidget(T widget, TextureProvider textureProvider, PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
		RenderUtil.drawContinuousTexturedBox(poseStack, widget.x, widget.y, textureProvider.getU(), textureProvider.getV(), widget.width, widget.height, textureProvider.getWidth(), textureProvider.getHeight(), 2, 3, 2, 2, widget.getBlitOffset(), textureProvider.getTexture(), widget.getCurrentBackgroundColor(poseStack, mouseY, mouseY, partialTicks));
		
		widget.renderBackground(poseStack, mouseX, mouseY, partialTicks);
		widget.renderForeground(poseStack, mouseX, mouseY, partialTicks);
	}
	
	public static <T extends AbstractWidget & TextProvider> void renderText(T widget, PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
		final var font = widget.getCurrentTextFont();
		
		var message = widget.getCurrentText();
		if (message != TextComponent.EMPTY) {
			final var messageWidth = font.width(message);
			final var ellipsisWidth = font.width("...");
			
			if (messageWidth > widget.width - 6 && messageWidth > ellipsisWidth) {
				message = new TextComponent(font.substrByWidth(message, widget.width - 6 - ellipsisWidth).getString() + "...");
			}
			
			final float xStart = (widget.x + (widget.width / 2) - messageWidth / 2);
			final float yStart = (widget.y + (widget.height - 8) / 2);
			
			font.drawShadow(poseStack, message, xStart, yStart, widget.getCurrentTextColor(poseStack, mouseX, mouseY, partialTicks).getColorARGB());
		}
	}
	
	public static <T extends AbstractWidget & TextProvider & ScaleProvider> void renderScaledText(T widget, PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
		final var scale = widget.getCurrentScale(poseStack, mouseX, mouseY, partialTicks);
		
		if (scale == 1) {
			renderText(widget, poseStack, mouseX, mouseY, partialTicks);
		} else {
			final var font = widget.getCurrentTextFont();
			
			var message = widget.getCurrentText();
			if (message != TextComponent.EMPTY) {
				final var messageWidth = Mth.ceil(scale * font.width(message));
				final var ellipsisWidth = Mth.ceil(scale * font.width("..."));
				
				if (messageWidth > widget.width - 6 && messageWidth > ellipsisWidth) {
					message = new TextComponent(font.substrByWidth(message, widget.width - 6 - ellipsisWidth).getString() + "...");
				}
				
				final var positionFactor = 1 / scale;
				
				final var xStart = (widget.x + (widget.width / 2) - messageWidth / 2) * positionFactor;
				final var yStart = (widget.y + ((int) (widget.height - 8 * scale)) / 2) * positionFactor;
				
				poseStack.pushPose();
				poseStack.scale(scale, scale, 0);
				font.drawShadow(poseStack, message, xStart, yStart, widget.getCurrentTextColor(poseStack, mouseX, mouseY, partialTicks).getColorARGB());
				poseStack.popPose();
			}
		}
	}
	
	public static void renderTooltips(List<Widget> widgets, PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
		widgets.forEach(widget -> {
			if (widget instanceof TooltipRenderable tooltipRenderable) {
				tooltipRenderable.renderToolTip(poseStack, mouseX, mouseY, partialTicks);
			}
		});
	}
}
