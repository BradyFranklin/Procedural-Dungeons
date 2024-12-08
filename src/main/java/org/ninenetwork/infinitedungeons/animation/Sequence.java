package org.ninenetwork.infinitedungeons.animation;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.mineacademy.fo.model.SimpleHologramStand;

import java.util.HashMap;
import java.util.Map;

public abstract class Sequence {

    private static final Map<String, Sequence> byName = new HashMap<>();

    public static final Sequence CRATE_DROP = null;

    @Setter(AccessLevel.PROTECTED)
    private int sceneDelayTicks = 20;

    @Getter(value = AccessLevel.PROTECTED)
    private Location lastLocation;

    private SimpleHologramStand lastStand;

    protected Sequence(String name) {
        byName.put(name, this);
    }

    public final void start(Location location) {
        this.lastLocation = location;
        this.onStart();
    }

    protected abstract void onStart();

    protected final void removeLast() {
        if (this.lastStand != null) {
            this.lastStand.remove();
        }


    }

}