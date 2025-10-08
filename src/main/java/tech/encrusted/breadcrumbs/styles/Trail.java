package tech.encrusted.breadcrumbs.styles;

import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import tech.encrusted.breadcrumbs.State;
import tech.encrusted.breadcrumbs.V;

import java.awt.*;

import static tech.encrusted.breadcrumbs.Breadcrumbs.settings;

public abstract class Trail {
    public void render(BufferBuilder buf, Matrix4f matrix, Vec3d cameraPos) {
        buf = V.begin(tessellator, getDrawMode(), VertexFormats.POSITION_COLOR);
        build(buf, matrix, cameraPos);
        draw(buf);
    }
    protected abstract void build(BufferBuilder buf, Matrix4f matrix, Vec3d cameraPos);
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
