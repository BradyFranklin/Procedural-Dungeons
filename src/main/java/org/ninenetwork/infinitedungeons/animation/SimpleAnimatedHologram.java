package org.ninenetwork.infinitedungeons.animation;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.model.SimpleHologramStand;
import org.mineacademy.fo.region.Region;
import org.mineacademy.fo.remain.CompMaterial;

@Getter
@Setter
public class SimpleAnimatedHologram extends SimpleHologramStand {

    private boolean animated = true;

    private boolean rotatingUpAndDown = true;

    private boolean rotatingToSides = true;

    private double verticalMovementThreshold = 0.25;

    private boolean motionDown = true;

    public SimpleAnimatedHologram(Location spawnLocation, ItemStack item) {
        super(spawnLocation, item);
    }

    @Override
    protected void onTick() {
        Location location = this.getLocation();

        if (this.isAnimated()) {

            if (this.isRotatingUpAndDown()) {
                final double y = location.getY();
                final double lastY = getLastTeleportLocation().getY();
                if (y < lastY - this.verticalMovementThreshold) {
                    this.motionDown = false;
                }
                if (y > lastY + this.verticalMovementThreshold) {
                    this.motionDown = true;
                }
                location.subtract(0, 0.01 * (this.motionDown ? 1 : -1), 0);
            }

            if (this.isRotatingToSides()) {
                location.setYaw(location.getYaw() + 7 * (this.motionDown ? -1 : 1));
            }

            this.getEntity().teleport(location);
        }
    }

}