package com.rabimi.nametag;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public class NametagClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player == null) return;

            // F1で消えるのを防ぐ
            if (!client.options.hudHidden) {
                drawNameTag(drawContext);
            }
        });
    }

    private void drawNameTag(DrawContext drawContext) {
        MinecraftClient client = MinecraftClient.getInstance();
        String name = client.player.getName().getString();

        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();

        int centerX = screenWidth / 2;
        int centerY = screenHeight / 2 - 40; // 頭上っぽい位置にちょい上

        int textWidth = client.textRenderer.getWidth(name);
        int padding = 4;

        int bgLeft = centerX - textWidth / 2 - padding;
        int bgTop = centerY - padding - 1;
        int bgRight = centerX + textWidth / 2 + padding;
        int bgBottom = centerY + 9 + padding;

        // 黒背景（半透明）
        drawContext.fill(bgLeft, bgTop, bgRight, bgBottom, 0x88000000);

        // 白文字
        drawContext.drawText(
                client.textRenderer,
                Text.of(name),
                centerX - textWidth / 2,
                centerY,
                0xFFFFFF,
                false
        );
    }
}