package tech.encrusted.breadcrumbs;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class CatmullRomSpline {
    public static List<Vector3f> interpolate(List<Vector3f> positions, int segments) {
        int size = positions.size();
        if (positions.isEmpty()) {
            return new ArrayList<>(0);
        }
        List<Vector3f> result = new ArrayList<>((size - 1) * segments + 1);

        result.add(new Vector3f(positions.get(0)));

        if (size < 2) {
            return result;
        }

        int offset = 1;
        if (positions.get(positions.size() - 1).distance(positions.get(positions.size() - 2)) < 0.26) {
            offset = 2;
        }
        for (int i = 0; i < size - offset; i++) {
            Vector3f p0 = i > 0 ? positions.get(i - 1) : positions.get(0);
            Vector3f p1 = positions.get(i);
            Vector3f p2 = positions.get(i + 1);
            Vector3f p3 = (i < size - 2) ? positions.get(i + 2) : new Vector3f(p2).sub(p1).add(p2);

            int startJ = (i == 0) ? 1 : 0;

            for (int j = startJ; j < segments; j++) {
                float t = j / (float) segments;
                float t2 = t * t;
                float t3 = t2 * t;

                float x = 0.5f * ((2 * p1.x) +
                        (-p0.x + p2.x) * t +
                        (2 * p0.x - 5 * p1.x + 4 * p2.x - p3.x) * t2 +
                        (-p0.x + 3 * p1.x - 3 * p2.x + p3.x) * t3);

                float y = 0.5f * ((2 * p1.y) +
                        (-p0.y + p2.y) * t +
                        (2 * p0.y - 5 * p1.y + 4 * p2.y - p3.y) * t2 +
                        (-p0.y + 3 * p1.y - 3 * p2.y + p3.y) * t3);

                float z = 0.5f * ((2 * p1.z) +
                        (-p0.z + p2.z) * t +
                        (2 * p0.z - 5 * p1.z + 4 * p2.z - p3.z) * t2 +
                        (-p0.z + 3 * p1.z - 3 * p2.z + p3.z) * t3);

                result.add(new Vector3f(x, y, z));
            }
        }

        result.add(new Vector3f(positions.get(size - 1)));

        return result;
    }
}