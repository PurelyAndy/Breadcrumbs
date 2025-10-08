//~ draw_modes_1
//~ draw_modes_2

package tech.encrusted.breadcrumbs;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.Tessellator;
import tech.encrusted.breadcrumbs.config.Settings;
import tech.encrusted.breadcrumbs.config.TrailMode;

import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.*;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

//? if <=1.21.4 && >=1.21.2 {
/*import net.minecraft.client.gl.ShaderProgramKeys;
*///?}
//? if <=1.21.4 {
import org.lwjgl.opengl.GL11;
//?}
//? if >=1.21.9 {
/*import net.minecraft.util.Identifier;*/
//?}

public class Breadcrumbs implements ClientModInitializer {
    public static final KeyBinding toggleKeyBind = KeyBindingHelper.registerKeyBinding(
            new KeyBinding(
                    "key.breadcrumbs.toggle",
                    InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_BRACKET,
                    //? if <=1.21.8 {
                    "key.breadcrumbs.category"
                    //?} else {
                    /*new KeyBinding.Category(Identifier.of("breadcrumbs", "key.breadcrumbs.category"))
                    *///?}
            )
    );
    public static Settings settings;

    @Override
    public void onInitializeClient() {
        AutoConfig.register(Settings.class, Settings.factory);
        Breadcrumbs.settings = AutoConfig.getConfigHolder(Settings.class).getConfig();

        ClientTickEvents.END_CLIENT_TICK.register(State::update);

        WorldRenderEvents.LAST.register((context) -> {
            int size = State.points.size();
            if (size == 0)
                return;

            Tessellator tessellator = Tessellator.getInstance();
            Vec3d cameraPos = context.camera().getPos();
            Matrix4f matrix = V.positionMatrix(context);

            //? if <=1.20.6 {
            BufferBuilder buf = tessellator.getBuffer();
            //?} else {
            /*BufferBuilder buf;
            *///?}

            //$ set_shader
            RenderSystem.setShader(GameRenderer::getPositionColorShader);

            //? if <=1.21.4 {
            if (settings.renderThroughWalls) {
                RenderSystem.disableDepthTest();
            } else {
                RenderSystem.enableDepthTest();
            }
            GL11.glDisable(GL11.GL_CULL_FACE);
            RenderSystem.enableBlend();
            //?}

            State.trail.render(buf, matrix, cameraPos);

            V.resetRendering();
        });
    }

    private static void drawThickTrail(int size, BufferBuilder buf, Matrix4f matrix, Vec3d cameraPos) {

    }
}