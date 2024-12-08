package org.ninenetwork.infinitedungeons.map;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.*;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.transform.AffineTransform;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.util.SideEffectSet;
import com.sk89q.worldedit.util.io.Closer;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.FileUtil;
import org.mineacademy.fo.Valid;
import org.mineacademy.fo.model.ChunkedTask;
import org.ninenetwork.infinitedungeons.dungeon.Dungeon;
import org.ninenetwork.infinitedungeons.settings.Settings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SchematicManager {

    public static void save(org.mineacademy.fo.region.Region region, File schematicFile) {
        Valid.checkBoolean(region.isWhole(), "Cannot save region that lacks primary or secondary point: " + region);

        try (Closer closer = Closer.create()) {
            final com.sk89q.worldedit.regions.Region weRegion = toWorldEditRegion(region);
            final EditSession session = createEditSession(region.getWorld());

            final Clipboard clipboard = new BlockArrayClipboard(weRegion);
            final ForwardExtentCopy copy = new ForwardExtentCopy(session, weRegion, clipboard, weRegion.getMinimumPoint());

            Operations.complete(copy);

            final FileOutputStream fos = new FileOutputStream(schematicFile);
            final FileOutputStream out = closer.register(fos);
            final ClipboardWriter writer = closer.register(BuiltInClipboardFormat.SPONGE_SCHEMATIC.getWriter(out));

            writer.write(clipboard);

        } catch (final Throwable t) {
            t.printStackTrace();
        }
    }

    /*
     * Utility to convert the given region into a worldedit region
     */
    private static com.sk89q.worldedit.regions.Region toWorldEditRegion(org.mineacademy.fo.region.Region region) {
        final BlockVector3 min = toWorldEditVector(region.getPrimary());
        final BlockVector3 max = toWorldEditVector(region.getSecondary());

        return new CuboidRegion(min, max);
    }

    /**
     * Paste the schematic file content starting from the given location
     * This pastes all blocks at once.
     *
     * @param to
     * @param schematicFile
     */
    public static void paste(Location to, File schematicFile, int orientation) {
        try (EditSession session = createEditSession(to.getWorld())) {
            double x = 0.0;
            double z = 0.0;
            if (orientation == 1) {
                x = 30.0;
                z = 0;
            } else if (orientation == 2) {
                x = 30.0;
                z = 30.0; //
            } else if (orientation == 3) {
                x = 0;
                z = 30.0; //
            }
            final Clipboard clipboard = loadSchematic(schematicFile);
            BlockVector3 blockVector3 = BlockVector3.at(clipboard.getOrigin().x(), clipboard.getOrigin().y(), clipboard.getOrigin().z());
            Location location = to.clone();
            location.setX(to.getX() + (x));
            location.setY(to.getY());
            location.setZ(to.getZ() + (z));
            ClipboardHolder holder = new ClipboardHolder(clipboard);
            if (orientation != 0) {
                holder.setTransform(new AffineTransform().rotateY(-(orientation * 90)));
            }
            holder.getClipboard().setOrigin(blockVector3);
            final Operation operation = holder
                    .createPaste(session)
                    .ignoreAirBlocks(true)
                    .to(toWorldEditVector(location))
                    .build();

            Common.log("Original Location: " + to.getX() + ", " + to.getZ());
            Common.log("Pasted to " + location.getX() + ", " + location.getZ());

            Operations.complete(operation);

        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public static void pasteDoor(Location to, File schematicFile, int orientation) {

        try (EditSession session = createEditSession(to.getWorld())) {
            double x = 0.0;
            double z = 0.0;
            if (orientation == 1) {
                x = 2.0;
                z = 0;
            }
            final Clipboard clipboard = loadSchematic(schematicFile);
            BlockVector3 blockVector3 = BlockVector3.at(clipboard.getOrigin().x(), clipboard.getOrigin().y(), clipboard.getOrigin().z());
            Location location = to.clone();
            location.setX(to.getX() + (x));
            location.setY(to.getY());
            location.setZ(to.getZ() + (z));
            ClipboardHolder holder = new ClipboardHolder(clipboard);
            if (orientation != 0) {
                holder.setTransform(new AffineTransform().rotateY(-(orientation * 90)));
            }
            holder.getClipboard().setOrigin(blockVector3);
            final Operation operation = holder
                    .createPaste(session)
                    .ignoreAirBlocks(true)
                    .to(toWorldEditVector(location))
                    .build();

            Common.log("Original Location: " + to.getX() + ", " + to.getZ());
            Common.log("Pasted to " + location.getX() + ", " + location.getZ());

            Operations.complete(operation);

        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public static void resetToAir(Dungeon dungeon) {
        Region region = toWorldEditRegion(dungeon.getRegion());
        try (EditSession session = createEditSession(Bukkit.getWorld(Settings.PluginServerSettings.DUNGEON_WORLD_NAME))) {
            BlockType air = BlockTypes.AIR;
            BlockState state = air.getDefaultState();
            session.replaceBlocks(region, (Set<BaseBlock>) null,state);
        } catch (MaxChangedBlocksException e) {
            throw new RuntimeException(e);
        }
    }

    /*
    public static void paste(Location to, File schematicFile, int orientation) {

        try (EditSession session = createEditSession(to.getWorld())) {
            double x = 0.0;
            double z = 0.0;
            if (orientation == 1) {
                x = 30.0;
                z = 0;
            } else if (orientation == 2) {
                x = 30.0;
                z = 30;
            } else if (orientation == 3) {
                x = 0;
                z = 30;
            }
            final Clipboard clipboard = loadSchematic(schematicFile);
            BlockVector3 blockVector3 = BlockVector3.at(clipboard.getOrigin().getX(), clipboard.getOrigin().getY(), clipboard.getOrigin().getZ());
            Location location = to.clone().add(to.getX() + (x), to.getY(), to.getZ() + (z));
            ClipboardHolder holder = new ClipboardHolder(clipboard);
            if (orientation != 0) {
                holder.setTransform(new AffineTransform().rotateY(-(orientation * 90)));
            }
            holder.getClipboard().setOrigin(blockVector3);
            final Operation operation = holder
                    .createPaste(session)
                    .ignoreAirBlocks(true)
                    .to(toWorldEditVector(location))
                    .build();

            Common.log("Original Location: " + to.getX() + ", " + to.getZ());
            Common.log("Pasted to " + location.getX() + ", " + location.getZ());

            Operations.complete(operation);

        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
    */

    /**
     * Place the given schematic file one-by-one to fit the given region,
     * this uses a chunked task for maximum performance.
     *
     * @param region
     * @param schematicFile
     */
    public static void paste(org.mineacademy.fo.region.Region region, File schematicFile) {

        Common.runAsync(() -> {
            final Clipboard clipboard = loadSchematic(schematicFile);
            final List<BlockVector3> worldEditBlocks = new ArrayList<>();

            for (final Block block : region.getBlocks())
                worldEditBlocks.add(toWorldEditVector(block.getLocation()));

            final EditSession session = createEditSession(region.getWorld());

            // Restore each block individually, in chunks
            final ChunkedTask task = new ChunkedTask(50_000) {

                @Override
                protected void onProcess(int index) {
                    final BlockVector3 vector = worldEditBlocks.get(index);
                    final BaseBlock copy = clipboard.getFullBlock(vector);

                    try {
                        if (copy != null) {
                            session.setBlock(vector, copy);

                            Operations.completeBlindly(session.commit());
                        }

                    } catch (final MaxChangedBlocksException ex) {
                        ex.printStackTrace();
                    }
                }

                @Override
                protected boolean canContinue(int index) {
                    return index < worldEditBlocks.size();
                }
            };

            // Run the chain on the main thread for safety
            Common.runLater(task::startChain);
        });
    }

    /*
     * Load schematic from the given file
     */
    private static Clipboard loadSchematic(File file) {

        try {
            final ClipboardFormat format = ClipboardFormats.findByFile(file);
            Valid.checkNotNull(format, "Null schematic file format " + file + " (file corrupted or WorldEdit outdated - or too new?)!");

            final ClipboardReader reader = format.getReader(new FileInputStream(file));
            final Clipboard schematic = reader.read();
            Valid.checkNotNull(schematic, "Failed to read schematic from " + file);

            return schematic;

        } catch (final Throwable t) {
            throw new RuntimeException(t);
        }
    }

    /*
     * Create a new edit session
     */
    private static EditSession createEditSession(org.bukkit.World bukkitWorld) {
        final BukkitWorld world = new BukkitWorld(bukkitWorld);
        final EditSession session = WorldEdit.getInstance().newEditSession(world);

        session.setSideEffectApplier(SideEffectSet.defaults());

        return session;
    }

    /*
     * Convert location to block vector
     */
    private static BlockVector3 toWorldEditVector(@NonNull Location location) {
        return BlockVector3.at(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

}