package tech.encrusted.breadcrumbs;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.Tessellator;
import tech.encrusted.breadcrumbs.config.Settings;
import tech.encrusted.breadcrumbs.config.TrailMode;

//? if <=1.21.4 && >=1.21.2 {
/*import net.minecraft.client.gl.ShaderProgramKeys;*/
//?}
//? if <=1.21.4 {
/*import org.lwjgl.opengl.GL11;*/
//?}
//? if >=1.21.9 {
/*import net.minecraft.util.Identifier;*/
//?}
//? if >=1.20.1 {
import com.mojang.blaze3d.vertex.VertexFormat;
//?}

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
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.io.Console;
import java.util.ArrayList;
import java.util.List;

public class Breadcrumbs implements ClientModInitializer {
    private static boolean enabled = false;
    private static List<Vector3d> positions = new ArrayList<>();
    private static List<Vector3d> points = new ArrayList<>();
    private static KeyBinding keyBinding = KeyBindingHelper.registerKeyBinding(
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
    private final static float saturation = 1f;
    private final static float brightness = 1f;

    @Override
    public void onInitializeClient() {
        AutoConfig.register(Settings.class, Settings.factory);
        Breadcrumbs.settings = AutoConfig.getConfigHolder(Settings.class).getConfig();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (keyBinding.wasPressed()) {
                enabled = !enabled;
                if (enabled) {
                    positions.clear(); // Only reset when starting a new recording, not when stopping
                }
                client.player.sendMessage(Text.of("Recording: " + enabled), false);
            }
        });

        WorldRenderEvents.LAST.register(context -> {
            if (!enabled)
                return;

            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            if (MinecraftClient.getInstance().isPaused())
                return;
            if (player == null) {
                enabled = false;
                return;
            }

            Vec3d temp = player
                    //? if <=1.16.5 {
                        /*.method_30950(MinecraftClient.getInstance().getTickDelta())
                    *///?} else if <=1.20.6 {
                        /*.getLerpedPos(MinecraftClient.getInstance().getTickDelta())*/
                    //?} else if <=1.21.4 {
                        /*.getLerpedPos(MinecraftClient.getInstance().getRenderTickCounter().getTickDelta(false))
                    *///?} else
                        .getLerpedPos(MinecraftClient.getInstance().getRenderTickCounter().getTickProgress(false))
                    .add(0, 0.1, 0); // Add 0.1 to avoid z-fighting with the ground
            Vector3d playerPos = new Vector3d(temp.x, temp.y, temp.z);

            if (settings.removeLoops) {
                detectAndRemoveLoops(playerPos);
            }

            if (positions.size() < 2) { // We need at least 2 points to calculate the distance
                addPosition(playerPos);
                return;
            }

            var pos1 = positions.get(positions.size() - 1);
            var pos2 = positions.get(positions.size() - 2);
            if (pos1.distance(pos2) < Breadcrumbs.settings.segmentLength) {
                positions.remove(pos1);
                addPosition(playerPos);
                return;
            }

            addPosition(playerPos);
        });

        WorldRenderEvents.LAST.register((context) -> {
            int size = points.size();
            if (size == 0)
                return;

            Tessellator tessellator = Tessellator.getInstance();
            Vec3d cameraPos = context.camera().getPos();

            //? if >1.19.2 {
            Matrix4f matrix = context.matrixStack().peek().getPositionMatrix();
            //?} else {
            /*Matrix4f matrix = new Matrix4f();
            matrix.identity();
            *///?}

            //? if <=1.21.4 {
            /*if (settings.renderThroughWalls) {
                RenderSystem.disableDepthTest();
            } else {
                RenderSystem.enableDepthTest();
            }
            *///?}

            //? if <=1.20.6 {
            /*BufferBuilder buf = tessellator.getBuffer();*/
            //?} else {
            BufferBuilder buf;
            //?}

            //? if <=1.16.5 {
            /*RenderSystem.disableTexture();*/
            //?} else if <=1.19.2 {
            /*RenderSystem.setShader(GameRenderer::getPositionColorShader);*/
            //?} else if <=1.21.1 {
            /*RenderSystem.setShader(GameRenderer::getPositionColorProgram);*/
            //?} else if <=1.21.4 {
            /*RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);*/
            //?}

            //? if <=1.21.4 {
            /*GL11.glDisable(GL11.GL_CULL_FACE);
            RenderSystem.enableBlend();
            *///?}

            if (settings.trailMode == TrailMode.LINES) {
                boolean arrows = settings.renderArrows;
                if (arrows) {
                    //? if <=1.16.5 {
                    /*buf.begin(GL11.GL_LINES, VertexFormats.POSITION_COLOR);
                    *///?} else if <=1.20.6 {
                    /*buf.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
                    *///?} else
                    buf = tessellator.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
                } else { // Optimization: using DEBUG_LINE_STRIP instead of DEBUG_LINES to reduce the number of vertices
                    //? if <=1.16.5 {
                    /*buf.begin(GL11.GL_LINE_STRIP, VertexFormats.POSITION_COLOR);
                    *///?} else if <=1.20.6 {
                    /*buf.begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);
                    *///?} else
                    buf = tessellator.begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);
                }

                drawLineTrail(size, arrows, buf, matrix, cameraPos);

                //? if <=1.18.2 {
                /*buf.end();
                *///?} else if <=1.20.6 {
                /*BufferBuilder.BuiltBuffer buffer = buf.end();*/
                //?} else if >=1.21 {
                BuiltBuffer buffer = buf.end();
                //?}
                if (size - (arrows ? 1 : 0) > 0 ) { // This will crash if there are no points
                    //? if <=1.18.2 {
                    /*BufferRenderer.draw(buf);
                    *///?} else if <=1.19.2 {
                    /*BufferRenderer.drawWithShader(buffer);*/
                    //?} else if <=1.21.4 {
                    /*BufferRenderer.drawWithGlobalProgram(buffer);*/
                    //?} else {
                    if (arrows) {
                        if (settings.renderThroughWalls) {
                            RenderHelper.debugLinesNoDepth.draw(buffer);
                        } else {
                            RenderHelper.debugLines.draw(buffer);
                        }
                    } else {
                        if (settings.renderThroughWalls) {
                            RenderHelper.debugLineStripNoDepth.draw(buffer);
                        } else {
                            RenderHelper.debugLineStrip.draw(buffer);
                        }
                    }
                    //?}
                }
                //? if <=1.18.2 {
                /*else {
                    buf.popData();
                }
                *///?}
            } else if (settings.trailMode == TrailMode.THICK) {
                //? if <=1.16.5 {
                /*buf.begin(GL11.GL_TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);
                *///?} else if <=1.20.6 {
                /*buf.begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);*/
                //?} else
                buf = tessellator.begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);

                drawThickTrail(size, buf, matrix, cameraPos);

                //? if <=1.18.2 {
                /*buf.end();
                *///?} else if <=1.20.6 {
                /*BufferBuilder.BuiltBuffer buffer = buf.end();*/
                //?}
                if (size - 1 > 0) {
                    //? if >=1.21 {
                    BuiltBuffer buffer = buf.end();
                    //?}

                    //? if <=1.18.2 {
                    /*BufferRenderer.draw(buf);
                    *///?} else if <=1.19.2 {
                    /*BufferRenderer.drawWithShader(buffer);*/
                    //?} else if <=1.21.4 {
                    /*BufferRenderer.drawWithGlobalProgram(buffer);*/
                    //?} else {
                    if (settings.renderThroughWalls) {
                        RenderHelper.triangleStripNoDepth.draw(buffer);
                    } else {
                        RenderHelper.triangleStrip.draw(buffer);
                    }
                    //?}
                }
                //? if <=1.18.2 {
                /*else {
                    buf.popData();
                }
                *///?}
            }
            //? if <=1.16.5 {
            /*RenderSystem.enableTexture();
            *///?}
            //? if <=1.21.4 {
            /*RenderSystem.disableBlend();
            GL11.glEnable(GL11.GL_CULL_FACE);
            RenderSystem.enableDepthTest();
            *///?}
        });
    }

    private static void drawLineTrail(int size, boolean arrows, BufferBuilder buf, Matrix4f matrix, Vec3d cameraPos) {
        for (int i = 0; i < size - (arrows ? 1 : 0); i++) {
            float[] color;
            if ((positions.size() - 1) * settings.interpolationSteps >= size) {
                color = getColor(i, size + settings.interpolationSteps);
            } else {
                color = getColor(i, size);
            }

            Vector3d pos1 = points.get(i);

            // The first point of the segment
            vertex(buf, matrix, pos1, cameraPos, color);
            if (arrows) {
                // We have to draw the second point of the segment since this isn't a line strip
                Vector3d pos2 = points.get(i + 1);
                vertex(buf, matrix, pos2, cameraPos, color);

                if (i == size - 2 ||
                        (settings.smoothInterpolation && ((i / settings.interpolationSteps) % settings.arrowFrequency == 0 && i % settings.interpolationSteps == 0)) ||
                        (!settings.smoothInterpolation && (i % settings.arrowFrequency == 0))) {
                    // Calculate the pitch and yaw of the current line segment
                    Vector3d dir = new Vector3d(pos2).sub(pos1).normalize();
                    float pitch = (float) Math.asin(-dir.y);
                    float yaw = (float) Math.atan2(dir.x, dir.z);

                    // Calculate the rotation matrix
                    Matrix3f rotation = new Matrix3f();
                    rotation.rotateYXZ(yaw, pitch, 0);

                    if (settings.backwardsArrows) {
                        rotation.rotateY((float) Math.PI);
                    }

                    // Calculate positions for the ends of the arrowhead
                    Vector3d arrowLeft = new Vector3d(-settings.arrowSize, 0, -2 * settings.arrowSize).mul(rotation).add(pos2);
                    Vector3d arrowRight = new Vector3d(settings.arrowSize, 0, -2 * settings.arrowSize).mul(rotation).add(pos2);

                    vertex(buf, matrix, pos2, cameraPos, color);
                    vertex(buf, matrix, arrowLeft, cameraPos, color);

                    vertex(buf, matrix, pos2, cameraPos, color);
                    vertex(buf, matrix, arrowRight, cameraPos, color);
                }
            }
        }
    }

    private static void drawThickTrail(int size, BufferBuilder buf, Matrix4f matrix, Vec3d cameraPos) {
        double oldAngle = 0;
        boolean swapped = false;
        for (int i = 0; i < size - 1; i++) {
            // The gradient looks silly with less than 30 points, and we only want to go from red to blue, not further
            float[] color;
            if ((positions.size() - 1) * settings.interpolationSteps >= size) {
                color = getColor(i, size + settings.interpolationSteps);
            } else {
                color = getColor(i, size);
            }

            Vector3d pos1 = points.get(i);
            Vector3d pos2 = equalify(pos1, points.get(i + 1));

            double angle = Math.atan2(pos2.x - pos1.x, pos2.z - pos1.z);
            double a0 = angle + Math.PI / 2;
            double a1 = angle - Math.PI / 2;

            int arrowFrequency = settings.arrowFrequency; // An arrow frequency of 1 with no interpolation would result in no arrows at all
            arrowFrequency *= settings.smoothInterpolation ? settings.interpolationSteps : 1;
            float thickness = getSegmentThickness(i, arrowFrequency);
            Vector3d p1 = new Vector3d((float) Math.sin(a0), 0, (float) Math.cos(a0)).mul(thickness).add(pos2);
            Vector3d p2 = new Vector3d((float) Math.sin(a1), 0, (float) Math.cos(a1)).mul(thickness).add(pos2);

            if (Math.cos(angle - oldAngle) < 0) {
                swapped = !swapped;
            }

            if (swapped) {
                vertex(buf, matrix, p1, cameraPos, color);
                vertex(buf, matrix, p2, cameraPos, color);
            } else {
                vertex(buf, matrix, p2, cameraPos, color);
                vertex(buf, matrix, p1, cameraPos, color);
            }

            if (i % arrowFrequency == arrowFrequency - 1 && settings.renderArrows) {
                // At the tip/thinnest point of the arrow, put 2 more points to make the base of the next arrow
                p1 = new Vector3d((float) Math.sin(a0), 0, (float) Math.cos(a0)).mul(settings.arrowSize + settings.trailThickness).add(pos2);
                p2 = new Vector3d((float) Math.sin(a1), 0, (float) Math.cos(a1)).mul(settings.arrowSize + settings.trailThickness).add(pos2);
                vertex(buf, matrix, p1, cameraPos, color);
                vertex(buf, matrix, p2, cameraPos, color);
            }

            oldAngle = angle;
        }
    }

    private static Vector3d equalify(Vector3d v1, Vector3d v2) {
        long x1 = Double.doubleToLongBits(v1.x);
        long y1 = Double.doubleToLongBits(v1.y);
        long z1 = Double.doubleToLongBits(v1.z);
        long x2 = Double.doubleToLongBits(v2.x);
        long y2 = Double.doubleToLongBits(v2.y);
        long z2 = Double.doubleToLongBits(v2.z);

        if (Math.abs(x1 - x2) <= 1) {
            v2.x = v1.x;
        }
        if (Math.abs(y1 - y2) <= 1) {
            v2.y = v1.y;
        }
        if (Math.abs(z1 - z2) <= 1) {
            v2.z = v1.z;
        }

        return v2;
    }

    private static void vertex(BufferBuilder buf, Matrix4f matrix, Vector3d pos1, Vec3d cameraPos, float[] color) {
        buf.vertex(
                    //? if <=1.19.2 {
                    /*Meth.toMinecraft(matrix),
                    *///?} else
                    matrix,
                    (float) (pos1.x - cameraPos.x),
                    (float) (pos1.y - cameraPos.y),
                    (float) (pos1.z - cameraPos.z)
                )
                .color(
                     color[0],
                     color[1], color[2], Breadcrumbs.settings.trailOpacity
                )
                //? if <=1.20.6
                /*.next()*/
                ;
    }

    private static float getSegmentThickness(int i, int arrowFrequency) {
        float thickness;
        if (settings.renderArrows) {
            if (settings.backwardsArrows) {
                thickness = (settings.arrowSize + settings.trailThickness) * (i % arrowFrequency) / (arrowFrequency + 1);
            } else {
                thickness = (settings.arrowSize + settings.trailThickness) * (arrowFrequency - i % arrowFrequency) / (arrowFrequency + 1);
            }
        } else {
            thickness = settings.trailThickness;
        }
        return thickness;
    }

    private static float[] getColor(float i, int size) {
        // The gradient looks silly with less than 30 points, and we only want to go from red to blue, not further
        float hue = (i / Math.max(size, 30)) * 0.5f;
        return new Color(Color.HSBtoRGB(hue, saturation, brightness)).getRGBComponents(null);
    }

    private static void detectAndRemoveLoops(Vector3d playerPos) {
        if (positions.size() > 3) { // It's impossible to have a loop with less than 3 line segments (4 points)
            float loopThreshold = 2f;
            int closePointIndex = -1;

            // Check if player is near any previous point (skip the last few points)
            for (int i = 0; i < positions.size() - Math.ceil(Math.max(1, 1f/settings.segmentLength)) * 3; i++) {
                if (playerPos.distance(positions.get(i)) < loopThreshold) {
                    closePointIndex = i;
                    break;
                }
            }

            // If the player is close to a previous point, we have a loop
            if (closePointIndex >= 0) {
                // Remove all points between the found point and the end
                while (positions.size() > closePointIndex + 1) {
                    positions.remove(positions.size() - 1);
                }
            }
        }
    }

    private void addPosition(Vector3d playerPos) {
        positions.add(playerPos);

        if (settings.smoothInterpolation)
            points = CatmullRomSpline.interpolate(positions, settings.interpolationSteps);
        else
            points = positions;
    }


}