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

            // 一人称視点(First Person)なら非表示にする
            if (client.options.getPerspective().isFirstPerson()) return;

            // HUD非表示(F1)じゃなければ描画
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

        // 画面中央ちょい上（頭の位置）
        int centerX = screenWidth / 2;
        int centerY = screenHeight / 2 - 30;

        int textWidth = client.textRenderer.getWidth(name);

        // 個別にパディング設定
        int paddingLeft = 8;
        int paddingRight = 8;
        int paddingTop = 4;
        int paddingBottom = 4;

        int bgLeft = centerX - textWidth / 2 - paddingLeft;
        int bgTop = centerY - paddingTop - 1;
        int bgRight = centerX + textWidth / 2 + paddingRight;
        int bgBottom = centerY + 9 + paddingBottom;

        // 半透明の黒背景
        drawContext.fill(bgLeft, bgTop, bgRight, bgBottom, 0x88000000);

        // 白文字
        drawContext.drawText(
                client.textRenderer,
                Text.of(name),
                centerX - textWidth / 1.5,
                centerY,
                0xFFFFFF,
                false
        );
    }
}
