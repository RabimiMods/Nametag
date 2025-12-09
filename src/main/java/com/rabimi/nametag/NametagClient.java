package com.rabimi.nametag;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

public class NametagClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        WorldRenderEvents.AFTER_ENTITIES.register(context -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player == null) return;

            var player = client.player;

            // カメラの位置
            Vec3d cam = client.gameRenderer.getCamera().getPos();

            // プレイヤーの位置
            double x = player.getX() - cam.x;
            double y = player.getY() - cam.y + 2.2;
            double z = player.getZ() - cam.z;

            var matrices = context.matrixStack();
            VertexConsumerProvider consumers = context.consumers();

            matrices.push();

            matrices.translate(x, y, z);

            // カメラの向きに合わせる
            matrices.multiply(client.getEntityRenderDispatcher().getRotation());

            // 文字の大きさ
            float scale = 0.025f;
            matrices.scale(-scale, -scale, scale);

            TextRenderer tr = client.textRenderer;
            String name = player.getName().getString();
            int width = tr.getWidth(name);

            tr.draw(
                Text.literal(name),
                -width / 2f,
                0,
                0xFFFFFF,
                false,
                matrices.peek().getPositionMatrix(),
                consumers,
                TextRenderer.TextLayerType.NORMAL,
                0,
                15728880
            );

            matrices.pop();
        });
    }
}
