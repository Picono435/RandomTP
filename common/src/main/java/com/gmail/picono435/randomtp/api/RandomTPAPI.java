package com.gmail.picono435.randomtp.api;

import com.gmail.picono435.randomtp.RandomTP;
import com.gmail.picono435.randomtp.config.Config;
import com.gmail.picono435.randomtp.config.Messages;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.dimension.DimensionType;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Random;
import java.util.function.Predicate;

public class RandomTPAPI {

    public static void randomTeleport(ServerPlayer player, ServerLevel world) {
        randomTeleport(player, world, null);
    }

    public static void randomTeleport(ServerPlayer player, ServerLevel world, Biome biome) {
        try  {
            Random r = new Random();
            int lowX = ((int)Math.round(Math.abs(player.getX())) + Config.getMinDistance()) * -1;
            int highX = Math.abs((int)Math.round(player.getX()) + Config.getMaxDistance());
            int lowZ = ((int)Math.round(Math.abs(player.getZ())) + Config.getMinDistance()) * -1;
            int highZ = Math.abs((int)Math.round(player.getZ()) + Config.getMaxDistance());
            if(Config.getMaxDistance() == 0) {
                highX = (int) (world.getWorldBorder().getSize() / 2);
                highZ = (int) (world.getWorldBorder().getSize() / 2);
            }
            int x = r.nextInt(highX-lowX) + lowX;
            int y = 50;
            int z = r.nextInt(highZ-lowZ) + lowZ;
            if(biome != null) {
                BlockPos biomePos = findNearestBiome(world, biome, new BlockPos(x, y, z), 6400, 8);
                x = biomePos.getX();
                y = biomePos.getY();
                z = biomePos.getZ();
            }
            int maxTries = Config.getMaxTries();
            while (!isSafe(world, x, y, z) && (maxTries == -1 || maxTries > 0)) {
                y++;
                if(y >= 120 || !isInBiomeWhitelist(getBiomeId(world.getBiome(new BlockPos(x, y, z))).toString())) {
                    x = r.nextInt(highX-lowX) + lowX;
                    y = 50;
                    z = r.nextInt(highZ-lowZ) + lowZ;
                    if(biome != null) {
                        BlockPos biomePos = findNearestBiome(world, biome, new BlockPos(x, y, z), 6400, 8);
                        x = biomePos.getX();
                        y = biomePos.getY();
                        z = biomePos.getZ();
                    }
                    continue;
                }
                if(maxTries > 0){
                    maxTries--;
                }
                if(maxTries == 0) {
                    TextComponent msg = new TextComponent(Messages.getMaxTries().replaceAll("\\{playerName\\}", player.getName().getString()).replaceAll("&", "§"));
                    player.sendMessage(msg);
                    return;
                }
            }

            player.teleportTo(world, x, y, z, player.xRot, player.yRot);
            TextComponent successful = new TextComponent(Messages.getSuccessful().replaceAll("\\{playerName\\}", player.getName().getString()).replaceAll("\\{blockX\\}", "" + (int)player.position().x).replaceAll("\\{blockY\\}", "" + (int)player.position().y).replaceAll("\\{blockZ\\}", "" + (int)player.position().z).replaceAll("&", "§"));
            player.sendMessage(successful);
        } catch(Exception ex) {
            RandomTP.getLogger().info("Error executing command.");
            ex.printStackTrace();
        }
    }

    public static DimensionType getWorld(String world, MinecraftServer server) {
        try {
            ResourceLocation resourcelocation = ResourceLocation.tryParse(Config.getDefaultWorld());
            DimensionType dim = DimensionType.getByName(resourcelocation);
            return dim;
        } catch(Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static boolean checkCooldown(ServerPlayer player, Map<String, Long> cooldowns) {
        int cooldownTime = Config.getCooldown();
        if(cooldowns.containsKey(player.getName().getString())) {
            long secondsLeft = ((cooldowns.get(player.getName().getString())/1000)+cooldownTime) - (System.currentTimeMillis()/1000);
            if(secondsLeft > 0) {
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    public static long getCooldownLeft(ServerPlayer player, Map<String, Long> cooldowns) {
        int cooldownTime = Config.getCooldown();
        long secondsLeft = ((cooldowns.get(player.getName().getString())/1000)+cooldownTime) - (System.currentTimeMillis()/1000);
        return secondsLeft;
    }

    @ExpectPlatform
    public static boolean hasPermission(ServerPlayer player, String permission) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static ResourceLocation getBiomeId(Biome biome) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static Biome getBiomeFromKey(ResourceLocation biome) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean hasPermission(CommandSourceStack source, String permission) {
        throw new AssertionError();
    }

    public static boolean isSafe(ServerLevel world, int newX, int newY, int newZ) {
        if(newX >= world.getWorldBorder().getMaxX() || newZ >= world.getWorldBorder().getMaxZ()) return false;
        if ((isEmpty(world, newX, newY, newZ)) &&
                (!isDangerBlock(world, newX, newY - 1, newZ))) {
            return true;
        }
        return false;
    }

    public static boolean isEmpty(Level world, int newX, int newY, int newZ) {
        if ((world.isEmptyBlock(new BlockPos(newX, newY, newZ))) && (world.isEmptyBlock(new BlockPos(newX, newY + 1, newZ))) &&
                (world.isEmptyBlock(new BlockPos(newX + 1, newY, newZ))) && (world.isEmptyBlock(new BlockPos(newX - 1, newY, newZ))) &&
                (world.isEmptyBlock(new BlockPos(newX, newY, newZ + 1))) && (world.isEmptyBlock(new BlockPos(newX, newY, newZ - 1)))) {
            return true;
        }
        return false;
    }

    public static boolean isDangerBlock(Level world, int newX, int newY, int newZ) {
        for (Block block : getDangerBlocks()) {
            if (block.equals(world.getBlockState(new BlockPos(newX, newY, newZ)).getBlock())) {
                return true;
            }
        }
        return false;
    }

    public static Block[] getDangerBlocks() {
        return new Block[] {Blocks.LAVA, Blocks.WATER, Blocks.AIR};
    }

    private static boolean isInBiomeWhitelist(String biome) {
        //WHITELIST
        if(Config.useBiomeWhitelist()) {
            return Config.getAllowedBiomes().contains(biome);
            //BLACKLIST
        } else {
            return !Config.getAllowedBiomes().contains(biome);
        }
    }

    @Nullable
    public static BlockPos findNearestBiome(ServerLevel world, Biome biome, BlockPos blockPos, int i, int j) {
        return findBiomeHorizontal(world.getChunkSource().getGenerator().getBiomeSource(), blockPos.getX(), blockPos.getY(), blockPos.getZ(), i, j, (biome2) -> {
            return biome2 == biome;
        }, world.random, true);
    }

    @Nullable
    private static BlockPos findBiomeHorizontal(BiomeSource biomeSource, int i, int j, int k, int l, int m, Predicate<Biome> predicate, Random random, boolean bl) {
        int n = i >> 2;
        int o = k >> 2;
        int p = l >> 2;
        int q = j >> 2;
        BlockPos blockPos = null;
        int r = 0;
        int s = bl ? 0 : p;

        for(int t = s; t <= p; t += m) {
            for(int u = -t; u <= t; u += m) {
                boolean bl2 = Math.abs(u) == t;

                for(int v = -t; v <= t; v += m) {
                    if (bl) {
                        boolean bl3 = Math.abs(v) == t;
                        if (!bl3 && !bl2) {
                            continue;
                        }
                    }

                    int w = n + v;
                    int x = o + u;
                    if (predicate.test(biomeSource.getNoiseBiome(w, q, x))) {
                        if (blockPos == null || random.nextInt(r + 1) == 0) {
                            blockPos = new BlockPos(w << 2, j, x << 2);
                            if (bl) {
                                return blockPos;
                            }
                        }

                        ++r;
                    }
                }
            }
        }

        return blockPos;
    }
}
