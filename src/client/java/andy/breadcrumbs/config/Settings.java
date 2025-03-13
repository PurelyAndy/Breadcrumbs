package andy.breadcrumbs.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.serializer.ConfigSerializer;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;


@Config(name = "breadcrumbs")
public class Settings implements ConfigData {
    public boolean renderThroughWalls = true;
    public float segmentLength = 0.25f;
    public boolean smoothInterpolation = true;
    public int interpolationSteps = 7;
    public TrailMode trailMode = TrailMode.LINES;
    public float trailThickness = 0.25f;
    public float trailOpacity = 1f;
    public boolean renderArrows = true;
    public boolean backwardsArrows = false;
    public float arrowSize = 0.2f;
    public int arrowFrequency = 5;
    public boolean removeLoops = false;

    public static ConfigSerializer.Factory<Settings> factory = (Config config, Class<Settings> clazz) -> new Toml4jConfigSerializer<>(config, clazz) {
        public Settings deserialize() {
            try {
                return super.deserialize();
            } catch (SerializationException e) {
                return createDefault();
            }
        }
    };
}
