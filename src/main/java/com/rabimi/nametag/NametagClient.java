package com.rabimi.nametag;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.RotationAxis;

public class NametagClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

        WorldRenderEvents.BEFORE_BLOCK_OUTLINE.register((worldRenderContext, blockOutlineContext) -> {

            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player == null) return true;

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

            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-client.getEntityRenderDispatcher().camera.getYaw()));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(client.getEntityRenderDispatcher().camera.getPitch()));

            matrices.scale(-0.025f, -0.025f, 0.025f);

            TextRenderer tr = client.textRenderer;
            VertexConsumerProvider.Immediate vcp = client.getBufferBuilders().getEntityVertexConsumers();

            String name = client.player.getName().getString();
            int width = tr.getWidth(name);

            // --- 黒い背景板 ---
            matrices.push();

            float bgLeft = -width / 2f - 2;
            float bgRight = width / 2f + 2;
            float bgTop = -2;
            float bgBottom = tr.fontHeight + 2;

            RenderSystem.disableTexture();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShader(GameRenderer::getPositionColorProgram);

            BufferBuilder buffer = Tessellator.getInstance().getBuffer();
            buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

            buffer.vertex(matrices.peek().getPositionMatrix(), bgLeft, bgBottom, 0).color(0, 0, 0, 150).next();
            buffer.vertex(matrices.peek().getPositionMatrix(), bgRight, bgBottom, 0).color(0, 0, 0, 150).next();
            buffer.vertex(matrices.peek().getPositionMatrix(), bgRight, bgTop, 0).color(0, 0, 0, 150).next();
            buffer.vertex(matrices.peek().getPositionMatrix(), bgLeft, bgTop, 0).color(0, 0, 0, 150).next();

            BufferRenderer.drawWithGlobalProgram(buffer.end());
            RenderSystem.disableBlend();
            RenderSystem.enableTexture();

            matrices.pop();

            // --- 名前の白文字 ---
            tr.draw(
                Text.of(name),
                -width / 2f,
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