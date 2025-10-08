package tech.encrusted.breadcrumbs;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3d;
import tech.encrusted.breadcrumbs.styles.Trail;

import java.util.ArrayList;
import java.util.List;

import static tech.encrusted.breadcrumbs.Breadcrumbs.settings;

public class State {
    private static boolean enabled = false;
    public static final List<Vector3d> positions = new ArrayList<>();
    public static List<Vector3d> points = new ArrayList<>();
    private static Trail trail;

    public static void tick(MinecraftClient client) {
        while (Breadcrumbs.toggleKeyBind.wasPressed()) {
            enabled = !enabled;
            if (enabled) {
                positions.clear(); // Only reset when starting a new recording, not when stopping
            }
            client.player.sendMessage(Text.of("Recording: " + enabled), false);
        }
    }
    public static void updatePosition(WorldRenderContext unused) {
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

        if (positions.size() < 2) { // We need at least 2 points to calculate the distance
            addPosition(playerPos);
            return;
        }

        var pos1 = positions.get(positions.size() - 1);
        var pos2 = positions.get(positions.size() - 2);
        if (pos1.distance(pos2) < settings.segmentLength) {
            positions.remove(pos1);
            addPosition(playerPos);
            return;
        }

        addPosition(playerPos);
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

    private static void addPosition(Vector3d playerPos) {
        positions.add(playerPos);

        if (settings.smoothInterpolation)
            points = CatmullRomSpline.interpolate(positions, settings.interpolationSteps);
        else
            points = positions;
    }

    public static Trail getTrail() {
        if (trail == null) {
            trail = settings.trailMode.trail;
        }
        return trail;
    }

    public static void setTrail(Trail trail) {
        State.trail = trail;
    }
}
