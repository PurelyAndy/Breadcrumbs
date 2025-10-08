//~ draw_modes_1
//~ draw_modes_2

package tech.encrusted.breadcrumbs;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.text.MutableText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3d;
import org.joml.Matrix4f;

//? if <=1.16.5 {
/*import org.lwjgl.opengl.GL11;
*///?}
//? if <=1.21.4 {
import net.minecraft.client.render.VertexFormat;
import org.lwjgl.opengl.GL11;
//?} else {
/*import com.mojang.blaze3d.vertex.VertexFormat;
*///?}

//? if >1.18.2 {
/*import net.minecraft.client.render.BuiltBuffer;*/
//?}

public class V {
    public enum DrawMode {
        LINES(VertexFormat.DrawMode.DEBUG_LINES),
        LINE_STRIP(VertexFormat.DrawMode.DEBUG_LINE_STRIP),
        TRIANGLES(VertexFormat.DrawMode.TRIANGLES),
        TRIANGLE_STRIP(VertexFormat.DrawMode.TRIANGLE_STRIP);

        public final
        //$ draw_mode
        VertexFormat.DrawMode
        mode;

        DrawMode(
                //$ draw_mode
                VertexFormat.DrawMode
                mode
        ) {
            this.mode = mode;
        }
    }
    public static
    //? if <=1.20.6 {
    void
    //?} else {
    /*net.minecraft.client.render.BufferBuilder
    *///?}
    begin(
    //? if <=1.20.6 {
    net.minecraft.client.render.BufferBuilder
    //?} else {
    /*net.minecraft.client.render.Tessellator
    *///?}
    obj,
    DrawMode mode,
    VertexFormat format
    ) {
        //? if >=1.21 {
        /*return
        *///?}
        obj.begin(mode.mode, format);
    }

    public static void vertex(BufferBuilder buf, Matrix4f matrix, Vector3d pos1, Vec3d cameraPos, float[] color) {
        //? if <=1.19.2 {
        net.minecraft.util.math.Matrix4f identity = new net.minecraft.util.math.Matrix4f();
        identity.loadIdentity();
        //?}
        buf.vertex(
            //? if <=1.19.2 {
            identity,
            //?} else {
            /*matrix,*/
            //?}
            (float) (pos1.x - cameraPos.x),
            (float) (pos1.y - cameraPos.y),
            (float) (pos1.z - cameraPos.z)
        )
        .color(
            color[0],
            color[1], color[2], Breadcrumbs.settings.trailOpacity
        )
        //? if <=1.20.6
        .next()
        ;
    }

    public static Vec3d lerpedPos(ClientPlayerEntity player) {
        return player
                //? if <=1.16.5 {
                /*.method_30950(MinecraftClient.getInstance().getTickDelta())
                 *///?} else if <=1.20.6 {
                .getLerpedPos(MinecraftClient.getInstance().getTickDelta())
                 //?} else if <=1.21.4 {
                /*.getLerpedPos(MinecraftClient.getInstance().getRenderTickCounter().getTickDelta(false))
                 *///?} else
                /*.getLerpedPos(MinecraftClient.getInstance().getRenderTickCounter().getTickProgress(false))*/
                ;
    }

    public static Matrix4f positionMatrix(WorldRenderContext context) {
        //? if <=1.19.2 {
        return null;
        //?} else {
        /*return context.matrixStack().peek().getPositionMatrix();*/
        //?}
    }

    public static void resetRendering() {
        //? if <=1.16.5 {
        /*RenderSystem.enableTexture();*/
        //?}
        //? if <=1.21.4 {
        RenderSystem.disableBlend();
        GL11.glEnable(GL11.GL_CULL_FACE);
        RenderSystem.enableDepthTest();
        //?}
    }

    public static
    //? if <=1.18.2 {
    BufferBuilder
    //?} else if <=1.20.6 {
    /*BufferBuilder.BuiltBuffer*/
    //?} else {
    /*BuiltBuffer
    *///?}
    endBuffer(BufferBuilder buf) {
        //? if <=1.18.2 {
        buf.end();
        return buf;
        //?} else {
        /*return buf.end();
        *///?}
    }

    public static void draw(
    //? if <=1.18.2 {
    BufferBuilder
    //?} else if <=1.20.6 {
    /*BufferBuilder.BuiltBuffer*/
    //?} else {
    /*BuiltBuffer
    *///?}
    buffer
    ) {
        //? if <=1.18.2 {
        BufferRenderer.draw(buffer);
        //?} else if <=1.19.2 {
        /*BufferRenderer.drawWithShader(buffer);*/
        //?} else if <=1.21.4 {
        /*BufferRenderer.drawWithGlobalProgram(buffer);
        *///?} else {
        /*you stupid idiot you broke stonecutter somehow*/
        //?}
    }

    public static MutableText translatableText(String key) {
        //? if <=1.18.2 {
        return new TranslatableText(key);
        //?} else {
        /*return Text.translatable(key);
         *///?}
    }
}
