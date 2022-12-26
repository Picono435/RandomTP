package com.gmail.picono435.randomtp.api;

import com.gmail.picono435.randomtp.RandomTP;
import com.gmail.picono435.randomtp.config.Config;
import com.gmail.picono435.randomtp.config.Messages;
import com.mojang.datafixers.util.Pair;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;

import java.util.Map;
import java.util.Random;

public class RandomTPAPI {

    public static void randomTeleport(ServerPlayer player, ServerLevel world) {
        randomTeleport(player, world, null);
    }

    public static void randomTeleport(ServerPlayer player, ServerLevel world, ResourceKey<Biome> biomeResourceKey) {
        try  {
            Random random = new Random();
            BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
            Pair<Integer, Integer> boundsX = null;
            Pair<Integer, Integer> boundsZ = null;
            if(biomeResourceKey == null) {
                boundsX = generateBounds(world, player, true);
                boundsZ = generateBounds(world, player, false);

                int x = random.ints(boundsX.getFirst(), boundsX.getSecond()).findAny().getAsInt();
                if((world.getWorldBorder().getMaxX() > 0 && world.getWorldBorder().getMinX() < 0) || (world.getWorldBorder().getMaxX() < 0 && world.getWorldBorder().getMinX() > 0)) {
                    if(random.nextInt(2) == 1) x = x * -1;
                }
                int z = random.ints(boundsZ.getFirst(), boundsZ.getSecond()).findAny().getAsInt();
                if((world.getWorldBorder().getMaxZ() > 0 && world.getWorldBorder().getMinZ() < 0) || (world.getWorldBorder().getMaxZ() < 0 && world.getWorldBorder().getMinZ() > 0)) {
                    if(random.nextInt(2) == 1) z = z * -1;
                }

                mutableBlockPos.setX(x);
                mutableBlockPos.setY(50);
                mutableBlockPos.setZ(z);
            } else {
                BlockPos biomePos = world.findNearestBiome(world.getServer().registryAccess().registry(Registry.BIOME_REGISTRY).get().get(biomeResourceKey), new BlockPos(player.getX(), player.getY(), player.getZ()), 6400, 8);
                if(biomePos == null) {
                    TextComponent msg = new TextComponent(Messages.getMaxTries().replaceAll("\\{playerName\\}", player.getName().getString()).replaceAll("&", "§"));
                    player.sendMessage(msg, player.getUUID());
                    return;
                }
                mutableBlockPos.setX(biomePos.getX());
                mutableBlockPos.setY(50);
                mutableBlockPos.setZ(biomePos.getZ());
                if(!world.getWorldBorder().isWithinBounds(mutableBlockPos)) {
                    TextComponent msg = new TextComponent(Messages.getMaxTries().replaceAll("\\{playerName\\}", player.getName().getString()).replaceAll("&", "§"));
                    player.sendMessage(msg, player.getUUID());
                    return;
                }
            }
            int maxTries = Config.getMaxTries();
            int y = mutableBlockPos.getY();
            while (!isSafe(world, mutableBlockPos) && (maxTries == -1 || maxTries > 0)) {
                y++;
                mutableBlockPos.setY(y);
                if(mutableBlockPos.getY() >= 200 || !isInBiomeWhitelist(world.getServer().registryAccess().registry(Registry.BIOME_REGISTRY).get().getKey(world.getBiome(mutableBlockPos.immutable())))) {
                    if(biomeResourceKey != null) {
                        BlockPos biomePos = world.findNearestBiome(world.getServer().registryAccess().registry(Registry.BIOME_REGISTRY).get().get(biomeResourceKey), new BlockPos(player.getX(), player.getY(), player.getZ()), 6400, 8);
                        if(biomePos == null) {
                            TextComponent msg = new TextComponent(Messages.getMaxTries().replaceAll("\\{playerName\\}", player.getName().getString()).replaceAll("&", "§"));
                            player.sendMessage(msg, player.getUUID());
                            return;
                        }
                        mutableBlockPos.setX(biomePos.getX());
                        mutableBlockPos.setY(50);
                        mutableBlockPos.setZ(biomePos.getZ());
                        if(!world.getWorldBorder().isWithinBounds(mutableBlockPos)) {
                            TextComponent msg = new TextComponent(Messages.getMaxTries().replaceAll("\\{playerName\\}", player.getName().getString()).replaceAll("&", "§"));
                            player.sendMessage(msg, player.getUUID());
                            return;
                        }
                        continue;
                    }
                    int x = random.ints(boundsX.getFirst(), boundsX.getSecond()).findAny().getAsInt();
                    if((world.getWorldBorder().getMaxX() > 0 && world.getWorldBorder().getMinX() < 0) || (world.getWorldBorder().getMaxX() < 0 && world.getWorldBorder().getMinX() > 0)) {
                        if(random.nextInt(2) == 1) x = x * -1;
                    }
                    int z = random.ints(boundsZ.getFirst(), boundsZ.getSecond()).findAny().getAsInt();
                    if((world.getWorldBorder().getMaxZ() > 0 && world.getWorldBorder().getMinZ() < 0) || (world.getWorldBorder().getMaxZ() < 0 && world.getWorldBorder().getMinZ() > 0)) {
                        if(random.nextInt(2) == 1) z = z * -1;
                    }

                    mutableBlockPos.setX(x);
                    mutableBlockPos.setY(50);
                    mutableBlockPos.setZ(z);
                    continue;
                }
                if(maxTries > 0){
                    maxTries--;
                }
                if(maxTries == 0) {
                    TextComponent msg = new TextComponent(Messages.getMaxTries().replaceAll("\\{playerName\\}", player.getName().getString()).replaceAll("&", "§"));
                    player.sendMessage(msg, player.getUUID());
                    return;
                }
            }

            player.teleportTo(world, mutableBlockPos.getX(), mutableBlockPos.getY(), mutableBlockPos.getZ(), player.xRot, player.yRot);
            TextComponent successful = new TextComponent(Messages.getSuccessful().replaceAll("\\{playerName\\}", player.getName().getString()).replaceAll("\\{blockX\\}", "" + (int)player.position().x).replaceAll("\\{blockY\\}", "" + (int)player.position().y).replaceAll("\\{blockZ\\}", "" + (int)player.position().z).replaceAll("&", "§"));
            player.sendMessage(successful, player.getUUID());
        } catch(Exception ex) {
            RandomTP.getLogger().info("Error executing command.");
            ex.printStackTrace();
        }
    }

    private static Pair<Integer, Integer> generateBounds(ServerLevel world, Player player, boolean XorZ) {
        if(XorZ) {
            // Calculating bounds for coordinates X
            double maxWorldBorderX = world.getWorldBorder().getMaxX();
            double minWorldBorderX = world.getWorldBorder().getMinX();
            int maxDistanceX = (int) (Config.getMaxDistance() == 0 ? Math.abs(maxWorldBorderX) + 2 : player.getX() >= 0 ? Config.getMaxDistance() + Math.round(player.getX()) : Config.getMaxDistance() - Math.round(player.getX()));
            int minDistanceX = (int) (player.getX() >= 0 ? Config.getMinDistance() + player.getX() : Config.getMinDistance() - player.getX());
            int highX;
            if(Math.abs(maxDistanceX) >= Math.abs(maxWorldBorderX)) {
                highX = (int) maxWorldBorderX;
            } else {
                highX = maxDistanceX;
            }
            if(highX == maxDistanceX * -1 || highX == maxWorldBorderX * -1) highX = highX * -1;
            if(minDistanceX >= highX) minDistanceX = highX >= 0 ? highX - 10 : highX + 10;
            int lowX;
            if(Math.abs(minDistanceX) >= Math.abs(minWorldBorderX)) {
                lowX = minDistanceX;
            } else {
                lowX = (int) minWorldBorderX;
            }
            return new Pair<>(lowX, highX);
        } else {
            // Calculating bounds for coordinates Z
            double maxWorldBorderZ = world.getWorldBorder().getMaxZ();
            double minWorldBorderZ = world.getWorldBorder().getMinZ();
            int maxDistanceZ = (int) (Config.getMaxDistance() == 0 ? Math.abs(maxWorldBorderZ) + 2 : player.getZ() >= 0 ? Config.getMaxDistance() + Math.round(player.getZ()) : Config.getMaxDistance() - Math.round(player.getZ()));
            int minDistanceZ = (int) (player.getZ() >= 0 ? Config.getMinDistance() + player.getZ() : Config.getMinDistance() - player.getZ());
            int highZ;
            if(Math.abs(maxDistanceZ) >= Math.abs(maxWorldBorderZ)) {
                highZ = (int) maxWorldBorderZ;
            } else {
                highZ = maxDistanceZ;
            }
            if(highZ == maxDistanceZ * -1 || highZ == maxWorldBorderZ * -1) highZ = highZ * -1;
            if(minDistanceZ >= highZ) minDistanceZ = highZ >= 0 ? highZ - 10 : highZ + 10;
            int lowZ;
            if(Math.abs(minDistanceZ) >= Math.abs(minWorldBorderZ)) {
                lowZ = minDistanceZ;
            } else {
                lowZ = (int) minWorldBorderZ;
            }
            return new Pair<>(lowZ, highZ);
        }
    }

    public static ServerLevel getWorld(String world, MinecraftServer server) {
        try {
            ResourceLocation resourcelocation = ResourceLocation.tryParse(world);
            ResourceKey<Level> registrykey = ResourceKey.create(Registry.DIMENSION_REGISTRY, resourcelocation);
            ServerLevel worldTo = server.getLevel(registrykey);
            return worldTo;
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
    public static boolean hasPermission(CommandSourceStack source, String permission) {
        throw new AssertionError();
    }

    public static boolean isSafe(ServerLevel world, BlockPos.MutableBlockPos mutableBlockPos) {
        if (isEmpty(world, mutableBlockPos) && !isDangerBlocks(world, mutableBlockPos)) {
            return true;
        }
        return false;
    }

    public static boolean isEmpty(ServerLevel world, BlockPos.MutableBlockPos mutableBlockPos) {
        if (world.isEmptyBlock(mutableBlockPos.offset(0, 1, 0)) && world.isEmptyBlock(mutableBlockPos)) {
            return true;
        }
        return false;
    }

    public static boolean isDangerBlocks(ServerLevel world, BlockPos.MutableBlockPos mutableBlockPos) {
        if(isDangerBlock(world, mutableBlockPos) && isDangerBlock(world, mutableBlockPos.offset(0, 1, 0)) &&
                isDangerBlock(world, mutableBlockPos.offset(0, -1, 0))) {
            return true;
        }
        if(world.getBlockState(mutableBlockPos.offset(0, -1, 0)).getBlock() != Blocks.AIR) {
            return false;
        }
        return true;
    }

    public static boolean isDangerBlock(ServerLevel world, BlockPos mutableBlockPos) {
        return world.getBlockState(mutableBlockPos).getBlock() instanceof LiquidBlock;
    }

    private static boolean isInBiomeWhitelist(ResourceLocation biome) {
        //WHITELIST
        if(Config.useBiomeWhitelist()) {
            if(biome == null) {
                return false;
            }
            return Config.getAllowedBiomes().contains(biome.toString());
            //BLACKLIST
        } else {
            if(biome == null) {
                return true;
            }
            return !Config.getAllowedBiomes().contains(biome.toString());
        }
    }
}