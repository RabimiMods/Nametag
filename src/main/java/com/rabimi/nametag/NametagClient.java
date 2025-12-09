package com.rabimi.nametag;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

public class NametagClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        WorldRenderEvents.AFTER_ENTITIES.register(context -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player == null || client.isPaused()) return;

            var player = client.player;

            // カメラ位置
            Vec3d cam = client.gameRenderer.getCamera().getPos();

            // プレイヤー位置
            double x = player.getX() - cam.x;
            double y = player.getY() - cam.y + 2.2;
            double z = player.getZ() - cam.z;

            var matrices = context.matrixStack();
            var consumers = context.consumers();

            matrices.push();

            matrices.translate(x, y, z);

            // カメラ方向に向ける
            matrices.multiply(client.getEntityRenderDispatcher().getRotation());

            // 大きさ
            float scale = 0.025f;
            matrices.scale(-scale, -scale, scale);

            TextRenderer tr = client.textRenderer;
            String name = player.getName().getString();
            int width = tr.getWidth(name);

            // ここ大事！
            // Debug(F3) や F1 の HUD 非表示とは関係なしに描画される
            tr.draw(
                Text.literal(name),
                -width / 2f,
                0,
                0xFFFFFF,
                false,
                matrices.peek().getPositionMatrix(),
                consumers,
                TextRenderer.TextLayerType.SEE_THROUGH,
                0,
                15728880
            );

            matrices.pop();
        });
    }
}
