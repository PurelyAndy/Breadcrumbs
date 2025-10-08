package tech.encrusted.breadcrumbs.config;

import tech.encrusted.breadcrumbs.styles.LinesTrail;
import tech.encrusted.breadcrumbs.styles.ThickTrail;
import tech.encrusted.breadcrumbs.styles.Trail;

public enum TrailMode {
    LINES(new LinesTrail()),
    THICK(new ThickTrail());

    public final Trail trail;
    TrailMode(Trail trail) {
        this.trail = trail;
    }
}
