package tech.encrusted.breadcrumbs.styles;

import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import tech.encrusted.breadcrumbs.State;
import tech.encrusted.breadcrumbs.V;

import java.awt.*;
import java.util.List;

import static tech.encrusted.breadcrumbs.Breadcrumbs.settings;

public abstract class Trail {
    public void render(
            //? if <=1.20.6 {
            /*BufferBuilder buf,
            *///?} else {
            Tessellator tessellator,
            //?}
            Matrix4f matrix,
            Vec3d cameraPos) {
        for (List<Vector3d> points : State.points) {
            if (points.size() > 1) {
                BufferBuilder buf = V.begin(tessellator, getDrawMode(), VertexFormats.POSITION_COLOR);
                build(buf, matrix, cameraPos, points);
                draw(buf);
            }
        }
    }

    protected abstract void build(BufferBuilder buf, Matrix4f matrix, Vec3d cameraPos, List<Vector3d> points);

    protected abstract void draw(BufferBuilder buf);

    protected abstract V.DrawMode getDrawMode();

    protected static float[] getColor(int i, int size) {
        if ((State.positions.size() - 1) * settings.interpolationSteps >= size) {
            return gradient(i, size + settings.interpolationSteps);
        } else {
            return gradient(i, size);
        }
    }

    private static float[] gradient(float i, int size) {
        // The gradient looks silly with less than 30 points, and we only want to go from red to blue, not further
        float hue = (i / Math.max(size, 30)) * 0.5f;
        return new Color(Color.HSBtoRGB(hue, 1, 1)).getRGBComponents(null);
    }
}
