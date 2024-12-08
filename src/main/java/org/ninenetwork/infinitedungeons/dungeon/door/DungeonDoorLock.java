package org.ninenetwork.infinitedungeons.dungeon.door;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.FileUtil;
import org.mineacademy.fo.region.Region;
import org.mineacademy.fo.remain.CompParticle;
import org.ninenetwork.infinitedungeons.dungeon.Dungeon;
import org.ninenetwork.infinitedungeons.dungeon.DungeonGeneration;
import org.ninenetwork.infinitedungeons.dungeon.instance.DungeonRoomInstance;
import org.ninenetwork.infinitedungeons.dungeon.instance.DungeonRoomPoint;
import org.ninenetwork.infinitedungeons.world.SchematicManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

@Getter
@Setter
public class DungeonDoorLock {

    private ArrayList<DungeonRoomInstance> connectingRooms = new ArrayList<>();

    @Setter
    private ArrayList<Block> fallBlocks = new ArrayList<>();

    private DungeonRoomInstance belongingRoom;

    private Region doorRegion;

    private Location connectionLocation;

    private String directional;

    private boolean isLocked;

    private String unlockGoal;

    private Material unlockMaterial;

    private File schematic;

    protected DungeonDoorLock(Region region, File schematic) {
        this.doorRegion = region;
        this.schematic = schematic;
        this.isLocked = true;
    }

    public void initializeDungeonDoor(Dungeon dungeon, DungeonRoomPoint point, DungeonRoomPoint nextPoint, File schematic, DungeonRoomInstance connector, DungeonRoomInstance connected) {
        setConnectingRooms(connector, connected);
        this.belongingRoom = connector;
        Location midPoint = DungeonGeneration.findMidPoint(point.getCenterLocation(), nextPoint.getCenterLocation());
        Location finalPaste = point.getCenterLocation();
        int orientation = 0;
        if (point.getCenterLocation().getX() == nextPoint.getCenterLocation().getX()) {
            finalPaste = midPoint.clone().add(-2.0, 0.0, -2.0);
            SchematicManager.clearDoorAreas(midPoint, "x");
        } else if (point.getCenterLocation().getZ() == nextPoint.getCenterLocation().getZ()) {
            finalPaste = midPoint.clone().add(0.0, 0.0, -2.0);
            SchematicManager.clearDoorAreas(midPoint, "z");
            orientation = 1;
        }
        SchematicManager.pasteDoor(finalPaste, schematic, orientation);
        ArrayList<Block> fallBlocksSetter = new ArrayList<>();
        for (Block block : this.doorRegion.getBlocks()) {
            if (block.getType() == Material.COAL_BLOCK) {
                fallBlocksSetter.add(block);
            }
        }
        setFallBlocks(fallBlocksSetter);
        dungeon.getBloodRushDoors().add(this);
    }

    public void makeBlocksFall() {
        for (Block block : this.getFallBlocks()) {
            //Remain.spawnFallingBlock(block.getLocation(), block);
            block.setType(Material.AIR);
        }
        /*
        ServerGamePacketListenerImpl ps = ((CraftPlayer) p).getHandle().connection;

        Constructor<FallingBlockEntity> constructor = FallingBlockEntity.class.getDeclaredConstructor(Level.class, double.class, double.class, double.class, BlockState.class);
        constructor.setAccessible(true);
        FallingBlockEntity b = constructor.newInstance(((CraftWorld)p.getWorld()).getHandle(), p.getLocation().getBlockX(), p.getLocation().getBlockY(), p.getLocation().getBlockZ(), Blocks.OAK_PLANKS.defaultBlockState());
        b.setNoGravity(true);
        b.setGlowingTag(true);
        b.getBukkitEntity().setVisibleByDefault(true);
        b.setInvulnerable(true);
        b.setOnGround(false);

        ps.send(new ClientboundAddEntityPacket(b));
        ps.send(new ClientboundSetEntityDataPacket(b.getId(), b.getEntityData().packDirty()));

        fallingBlocks.add(b);
        */

    }

    public void setConnectingRooms(DungeonRoomInstance connector, DungeonRoomInstance connected) {
        ArrayList<DungeonRoomInstance> rooms = new ArrayList<>();
        rooms.add(connector);
        rooms.add(connected);
        this.connectingRooms = rooms;
    }

    public void openDoor(Region region) {
        for (Block block : region.getBlocks()) {
            if (block.getType() == Material.COAL_BLOCK) {
                block.setType(Material.AIR);
                CompParticle.NOTE.spawn(block.getLocation());
            }
        }

    }

    public static void addLockedDungeonDoor(Dungeon dungeon, DungeonRoomPoint point, DungeonRoomPoint nextPoint) {
        if (!Objects.equals(DungeonRoomInstance.getRoomFromPoint(dungeon, point), DungeonRoomInstance.getRoomFromPoint(dungeon, nextPoint))) {
            Location midPoint = DungeonGeneration.findMidPoint(point.getCenterLocation(), nextPoint.getCenterLocation());
            Location firstPoint = null;
            Location secondPoint = null;
            if (point.getCenterLocation().getX() == nextPoint.getCenterLocation().getX()) {
                firstPoint = midPoint.clone().add(2.0, 5.0, 2.0);
                secondPoint = midPoint.clone().add(-2.0, 0.0, -2.0);
            } else if (point.getCenterLocation().getZ() == nextPoint.getCenterLocation().getZ()) {
                firstPoint = midPoint.clone().add(-2.0, 5.0, 2.0);
                secondPoint = midPoint.clone().add(2.0, 0.0, -2.0);
            }
            Region region = new Region(firstPoint, secondPoint);
            File schematic = FileUtil.getFile("DungeonStorage/Schematics/lockdoor.schematic");
            DungeonDoorLock dungeonDoor = new DungeonDoorLock(region, schematic);
            dungeonDoor.initializeDungeonDoor(dungeon, point, nextPoint, schematic, DungeonRoomInstance.getRoomFromPoint(dungeon, point), DungeonRoomInstance.getRoomFromPoint(dungeon, nextPoint));
        } else {
            Common.log("Sent door creation command but was overridden.");
        }
    }

}