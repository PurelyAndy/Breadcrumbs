package tech.encrusted.breadcrumbs.styles;

import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import tech.encrusted.breadcrumbs.State;
import tech.encrusted.breadcrumbs.V;

import static tech.encrusted.breadcrumbs.Breadcrumbs.settings;

public class LinesTrail extends Trail {
    @Override
    public void build(BufferBuilder buf, Matrix4f matrix, Vec3d cameraPos) {
        int size = State.points.size();

        for (int i = 0; i < size - (settings.renderArrows ? 1 : 0); i++) {
            float[] color = getColor(i, size);

            Vector3d pos1 = State.points.get(i);

            // The first point of the segment
            V.vertex(buf, matrix, pos1, cameraPos, color);
            if (settings.renderArrows) {
                // We have to draw the second point of the segment since this isn't a line strip
                Vector3d pos2 = State.points.get(i + 1);
                V.vertex(buf, matrix, pos2, cameraPos, color);

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

                    V.vertex(buf, matrix, pos2, cameraPos, color);
                    V.vertex(buf, matrix, arrowLeft, cameraPos, color);

                    V.vertex(buf, matrix, pos2, cameraPos, color);
                    V.vertex(buf, matrix, arrowRight, cameraPos, color);
                }
            }
        }
    }

    @Override
    public void draw(BufferBuilder buf) {
        var buffer = V.endBuffer(buf);

        if (State.points.size() > 1) {
            //? if <=1.21.4 {
            V.draw(buffer);
            //?} else {
            /*if (arrows) {
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
            *///?}
        }
        //? if <=1.18.2 {
        else {
            buf.popData();
        }
        //?}
    }

    @Override
    public V.DrawMode getDrawMode() {
        return settings.renderArrows ? V.DrawMode.LINES : V.DrawMode.LINE_STRIP;
    }
}
