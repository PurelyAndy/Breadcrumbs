package tech.encrusted.breadcrumbs;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import org.apache.logging.log4j.core.lookup.Interpolator;
import org.joml.Vector3d;
import tech.encrusted.breadcrumbs.styles.Trail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static tech.encrusted.breadcrumbs.Breadcrumbs.settings;

public class State {
    private static boolean enabled = false;
    public static List<List<Vector3d>> positions = new ArrayList<>() {{
        add(new ArrayList<>());
    }};
    public static List<List<Vector3d>> points = new ArrayList<>() {{
        add(new ArrayList<>());
    }};

    public static void tick(MinecraftClient client) {
        while (Breadcrumbs.toggleKeyBind.wasPressed()) {
            enabled = !enabled;
            if (enabled) {
                positions.clear();
                positions.add(new ArrayList<>());
                points.clear();
                points.add(new ArrayList<>());
            }
            client.player.sendMessage(Text.of("Recording: " + enabled), false);
        }
    }
    public static void updatePosition(WorldRenderContext unused) {
        if (!enabled)
            return;

        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;
        if (client.isPaused())
            return;
        if (player == null) {
            enabled = false;
            return;
        }

        Vec3d temp = V.lerpedPos(player).add(0, 0.1, 0); // Add 0.1 to avoid z-fighting with the ground
        Vector3d playerPos = new Vector3d(temp.x, temp.y, temp.z);

        if (settings.removeLoops) {
            detectAndRemoveLoops(playerPos);
        }

        if (positions.getLast().size() < 2) { // We need at least 2 points to calculate the distance
            addPosition(playerPos);
            return;
        }

        var pos1 = positions.getLast().getLast();
        var pos2 = positions.getLast().get(positions.getLast().size() - 2);
        if (pos1.distance(pos2) < settings.segmentLength) {
            positions.getLast().remove(pos1);
            addPosition(playerPos);
            return;
        }

        addPosition(playerPos);
    }

    private static void detectAndRemoveLoops(Vector3d playerPos) {
        // It's impossible to have a loop with less than 3 line segments (4 points)
        if (positions.stream().mapToInt(List::size).sum() > 3) {
            float loopThreshold = Math.max(2f, settings.segmentLength * 0.5f);
            int closePointIndex = -1;
            int closePointPortionIndex = -1;

            // Check if player is near any previous point (skip the last few points)
            for (int i = 0; i < positions.size(); i++) {
                var portion = positions.get(i);
                for (int j = 0; j < portion.size() - Math.ceil(Math.max(1, 1f / settings.segmentLength)) * 3; j++) {
                    if (playerPos.distance(portion.get(j)) < loopThreshold) {
                        closePointPortionIndex = i;
                        closePointIndex = j;
                        break;
                    }
                }
            }

            // If the player is close to a previous point, we have a loop
            if (closePointIndex >= 0) {
                // Remove all points between the found point and the end
                while (positions.size() > closePointPortionIndex + 2) {
                    positions.removeLast();
                }
                var portion = positions.getLast();
                while (portion.size() > closePointIndex + 1) {
                    portion.removeLast();
                }
            }
        }
    }

    private static void addPosition(Vector3d playerPos) {
        List<Vector3d> lastPortion = positions.getLast();
        if (!lastPortion.isEmpty()) {
            Vector3d last = lastPortion.getLast();
            if (last.distance(playerPos) > settings.segmentLength * 4) {
                lastPortion = new ArrayList<>();
                positions.add(lastPortion);
            }
        }
        lastPortion.add(playerPos);

        if (settings.smoothInterpolation) {
            points = new ArrayList<>(positions.size());
            for (int i = 0, positionsSize = positions.size(); i < positionsSize; i++) {
                List<Vector3d> portion = positions.get(i);
                if (i == points.size()) {
                    points.add(new ArrayList<>());
                }
                points.set(i, CatmullRomSpline.interpolate(portion, settings.interpolationSteps));
            }
        } else {
            points = positions;
        }
    }

    public static Trail getTrail() {
        return settings.trailMode.trail;
    }
}
