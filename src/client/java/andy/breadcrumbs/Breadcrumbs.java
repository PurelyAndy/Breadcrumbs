package andy.breadcrumbs;

import andy.breadcrumbs.config.Settings;
import andy.breadcrumbs.config.TrailMode;
import com.mojang.blaze3d.systems.RenderSystem;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.*;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix3f;
import org.joml.Matrix3fc;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Breadcrumbs implements ClientModInitializer {
    private static boolean enabled = false;
    private static List<Vector3f> positions = new ArrayList<>();
    private static KeyBinding keyBinding = KeyBindingHelper.registerKeyBinding(
            new KeyBinding(
                    "key.breadcrumbs.toggle",
                    InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_BRACKET,
                    "key.breadcrumbs.category"
            )
    );
    public static Settings settings;

    @Override
    public void onInitializeClient() {
        AutoConfig.register(Settings.class, Settings.factory);
        Breadcrumbs.settings = AutoConfig.getConfigHolder(Settings.class).getConfig();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (keyBinding.wasPressed()) {
                enabled = !enabled;
                if (enabled) {
                    positions.clear();
                }
                client.player.sendMessage(Text.literal("Recording: " + enabled));
            }
        });

        WorldRenderEvents.START.register(context -> {
            if (!enabled)
                return;

            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            if (MinecraftClient.getInstance().isPaused())
                return;
            if (player == null) {
                enabled = false;
                return;
            }

            Vector3f playerPos = player.getLerpedPos(MinecraftClient.getInstance().getRenderTickCounter().getTickDelta(false)).toVector3f();

            if (positions.size() < 2) {
                positions.add(playerPos);
                return;
            }
            var pos1 = positions.get(positions.size() - 1);
            var pos2 = positions.get(positions.size() - 2);
            if (pos1.distance(pos2) < Breadcrumbs.settings.segmentDistance) {
                positions.remove(pos1);
                positions.add(playerPos);
                return;
            }

            positions.add(playerPos);
        });

        WorldRenderEvents.LAST.register((context) -> {
            Matrix4f matrix = context.matrixStack().peek().getPositionMatrix();
            if (settings.renderThroughWalls) {
                RenderSystem.disableDepthTest();
            }
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buf;
            Vec3d cameraPos = context.camera().getPos();
            RenderSystem.setShader(GameRenderer::getPositionColorProgram);
            float cx = (float) cameraPos.x;
            float cy = (float) cameraPos.y;
            float cz = (float) cameraPos.z;

            List<Vector3f> points;
            if (settings.smoothInterpolation)
                points = CatmullRomSpline.interpolate(positions, settings.interpolationSteps);
            else
                points = positions;

            int size = points.size();
            float saturation = 1f;
            float brightness = 1f;

            GL11.glDisable(GL11.GL_CULL_FACE);
            RenderSystem.enableBlend();

            if (settings.trailMode == TrailMode.LINES) {
                boolean arrows = settings.renderArrows;
                if (arrows)
                    buf = tessellator.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
                else
                    buf = tessellator.begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);

                GL11.glEnable(GL11.GL_LINE_SMOOTH);
                GL11.glLineWidth(5f);

                for (int i = 0; i < size - (arrows ? 1 : 0); i++) {
                    float hue = ((float) i / Math.max(size, 30)) * 0.5f;
                    float[] color = new Color(Color.HSBtoRGB(hue, saturation, brightness)).getRGBComponents(null);

                    Vector3f pos1 = points.get(i);

                    buf.vertex(matrix, pos1.x - cx, pos1.y - cy, pos1.z - cz).color(color[0], color[1], color[2], Breadcrumbs.settings.trailOpacity);
                    if (arrows) {
                        Vector3f pos2 = points.get(i + 1);
                        buf.vertex(matrix, pos2.x - cx, pos2.y - cy, pos2.z - cz).color(color[0], color[1], color[2], Breadcrumbs.settings.trailOpacity);

                        if (i == size - 2 ||
                                (settings.smoothInterpolation && ((i / settings.interpolationSteps) % settings.arrowFrequency == 0 && i % settings.interpolationSteps == 0)) ||
                                (!settings.smoothInterpolation && (i % settings.arrowFrequency == 0))) {
                            //Calculate the pitch and yaw of the current line segment
                            Vector3f dir = new Vector3f(pos2).sub(pos1).normalize();
                            float pitch = (float) Math.asin(-dir.y);
                            float yaw = (float) Math.atan2(dir.x, dir.z);

                            //Calculate the rotation matrix
                            Matrix3f rotation = new Matrix3f();
                            rotation.rotateYXZ(yaw, pitch, 0);

                            if (settings.backwardsArrows) {
                                rotation.rotateY((float) Math.PI);
                            }

                            //Calculate the arrow points
                            Vector3f arrowLeft = new Vector3f(-settings.arrowSize, 0, -2 * settings.arrowSize).mul(rotation).add(pos2);
                            Vector3f arrowRight = new Vector3f(settings.arrowSize, 0, -2 * settings.arrowSize).mul(rotation).add(pos2);

                            buf.vertex(matrix, pos2.x - cx, pos2.y - cy, pos2.z - cz).color(color[0], color[1], color[2], Breadcrumbs.settings.trailOpacity);
                            buf.vertex(matrix, arrowLeft.x - cx, arrowLeft.y - cy, arrowLeft.z - cz).color(color[0], color[1], color[2], Breadcrumbs.settings.trailOpacity);

                            buf.vertex(matrix, pos2.x - cx, pos2.y - cy, pos2.z - cz).color(color[0], color[1], color[2], Breadcrumbs.settings.trailOpacity);
                            buf.vertex(matrix, arrowRight.x - cx, arrowRight.y - cy, arrowRight.z - cz).color(color[0], color[1], color[2], Breadcrumbs.settings.trailOpacity);
                        }
                    }
                }

                GL11.glDisable(GL11.GL_LINE_SMOOTH);
                if (size - (arrows ? 1 : 0) > 0 ) {
                    BufferRenderer.drawWithGlobalProgram(buf.end());
                }
            } else if (settings.trailMode == TrailMode.THICK) {
                buf = tessellator.begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);

                for (int i = 0; i < size - 1; i++) {
                    float hue = ((float) i / Math.max(size, 30)) * 0.5f;
                    float[] color = new Color(Color.HSBtoRGB(hue, saturation, brightness)).getRGBComponents(null);

                    Vector3f pos1 = points.get(i);
                    Vector3f pos2 = points.get(i + 1);

                    float a0 = (float) Math.atan2(pos2.x - pos1.x, pos2.z - pos1.z) + (float) Math.PI / 2;
                    float a1 = (float) Math.atan2(pos2.x - pos1.x, pos2.z - pos1.z) - (float) Math.PI / 2;

                    float thickness;
                    if (settings.renderArrows) {
                        int arrowFrequency = settings.arrowFrequency * settings.interpolationSteps;
                        if (settings.backwardsArrows) {
                            thickness = (settings.arrowSize + settings.trailThickness) * (i % arrowFrequency) / arrowFrequency;
                        } else {
                            thickness = (settings.arrowSize + settings.trailThickness) * (arrowFrequency - i % arrowFrequency) / arrowFrequency;
                        }
                    } else {
                        thickness = settings.trailThickness;
                    }
                    Vector3f p1 = new Vector3f((float) Math.sin(a0), 0, (float) Math.cos(a0)).mul(thickness).add(pos2);
                    Vector3f p2 = new Vector3f((float) Math.sin(a1), 0, (float) Math.cos(a1)).mul(thickness).add(pos2);

                    buf.vertex(matrix, p1.x - cx, p1.y - cy, p1.z - cz).color(color[0], color[1], color[2], settings.trailOpacity);
                    buf.vertex(matrix, p2.x - cx, p2.y - cy, p2.z - cz).color(color[0], color[1], color[2], settings.trailOpacity);
                }

                if (size - 1 > 0) {
                    BufferRenderer.drawWithGlobalProgram(buf.end());
                }
            }
            RenderSystem.disableBlend();
            GL11.glEnable(GL11.GL_CULL_FACE);
            if (settings.renderThroughWalls) {
                RenderSystem.enableDepthTest();
            }
        });
    }


}