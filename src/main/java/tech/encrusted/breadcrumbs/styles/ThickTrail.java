package tech.encrusted.breadcrumbs.styles;

import net.minecraft.client.render.BufferBuilder;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import tech.encrusted.breadcrumbs.RenderHelper;
import tech.encrusted.breadcrumbs.State;
import tech.encrusted.breadcrumbs.V;

import java.util.List;

import static tech.encrusted.breadcrumbs.Breadcrumbs.settings;

public class ThickTrail extends Trail {
    @Override
    public void build(BufferBuilder buf, Matrix4f matrix, Vec3d cameraPos, List<Vector3d> points) {
        int size = points.size();

        double oldAngle = 0;
        boolean swapped = false;

        for (int i = 0; i < size - 1; i++) {
            float[] color = getColor(i, size);

            Vector3d pos1 = points.get(i);
            Vector3d pos2 = equalify(pos1, points.get(i + 1));

            double angle = Math.atan2(pos2.x - pos1.x, pos2.z - pos1.z);
            double a0 = angle + Math.PI / 2;
            double a1 = angle - Math.PI / 2;

            // An arrow frequency of 1 with no interpolation would result in no arrows at all
            int arrowFrequency = settings.arrowFrequency * (settings.smoothInterpolation ? settings.interpolationSteps : 1);
            float thickness = getSegmentThickness(i, arrowFrequency);
            Vector3d p1 = new Vector3d((float) Math.sin(a0), 0, (float) Math.cos(a0)).mul(thickness).add(pos2);
            Vector3d p2 = new Vector3d((float) Math.sin(a1), 0, (float) Math.cos(a1)).mul(thickness).add(pos2);

            if (Math.cos(angle - oldAngle) < 0) {
                swapped = !swapped;
            }

            if (swapped) {
                V.vertex(buf, matrix, p1, cameraPos, color);
                V.vertex(buf, matrix, p2, cameraPos, color);
            } else {
                V.vertex(buf, matrix, p2, cameraPos, color);
                V.vertex(buf, matrix, p1, cameraPos, color);
            }

            if (i % arrowFrequency == arrowFrequency - 1 && settings.renderArrows) {
                // At the tip/thinnest point of the arrow, put 2 more points to make the base of the next arrow
                p1 = new Vector3d((float) Math.sin(a0), 0, (float) Math.cos(a0)).mul(settings.arrowSize + settings.trailThickness).add(pos2);
                p2 = new Vector3d((float) Math.sin(a1), 0, (float) Math.cos(a1)).mul(settings.arrowSize + settings.trailThickness).add(pos2);
                V.vertex(buf, matrix, p1, cameraPos, color);
                V.vertex(buf, matrix, p2, cameraPos, color);
            }

            oldAngle = angle;
        }
    }

    @Override
    public void draw(BufferBuilder buf) {
        var buffer = V.endBuffer(buf);
        //? if <=1.21.4 {
        /*V.draw(buffer);
        *///?} else {
        if (settings.renderThroughWalls) {
            RenderHelper.triangleStripNoDepth.draw(buffer);
        } else {
            RenderHelper.triangleStrip.draw(buffer);
        }
        //?}
    }

    @Override
    public V.DrawMode getDrawMode() {
        return V.DrawMode.TRIANGLE_STRIP;
    }

    private static Vector3d equalify(Vector3d v1, Vector3d v2) {
        long x1 = Double.doubleToLongBits(v1.x);
        long y1 = Double.doubleToLongBits(v1.y);
        long z1 = Double.doubleToLongBits(v1.z);
        long x2 = Double.doubleToLongBits(v2.x);
        long y2 = Double.doubleToLongBits(v2.y);
        long z2 = Double.doubleToLongBits(v2.z);

        if (Math.abs(x1 - x2) == 1) {
            v2.x = v1.x;
        }
        if (Math.abs(y1 - y2) == 1) {
            v2.y = v1.y;
        }
        if (Math.abs(z1 - z2) == 1) {
            v2.z = v1.z;
        }

        return v2;
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
}
