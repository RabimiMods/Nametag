package com.rabimi.nametag;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.RotationAxis;

public class NametagClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

        WorldRenderEvents.BEFORE_BLOCK_OUTLINE.register((worldRenderContext, blockOutlineContext) -> {

            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player == null) return;

            MatrixStack matrices = worldRenderContext.matrixStack();

            double x = client.player.getX();
            double y = client.player.getY() + 2.9;
            double z = client.player.getZ();

            matrices.push();

            matrices.translate(
                x - worldRenderContext.camera().getPos().x,
                y - worldRenderContext.camera().getPos().y,
                z - worldRenderContext.camera().getPos().z
            );

            // カメラの向きで回転
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-client.getEntityRenderDispatcher().camera.getYaw()));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(client.getEntityRenderDispatcher().camera.getPitch()));

            matrices.scale(-0.025f, -0.025f, 0.025f);

            TextRenderer tr = client.textRenderer;
            VertexConsumerProvider.Immediate vcp = client.getBufferBuilders().getEntityVertexConsumers();

            tr.draw(
                Text.of(client.player.getName().getString()),
                -tr.getWidth(client.player.getName().getString()) / 2f,
                0,
                0xFFFFFF,
                false,
                matrices.peek().getPositionMatrix(),
                vcp,
                TextRenderer.TextLayerType.NORMAL,
                0,
                15728880
            );

            vcp.draw();
            matrices.pop();

            return true;
        });

    }
}