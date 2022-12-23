package com.gmail.picono435.randomtp.api;

import com.gmail.picono435.randomtp.RandomTP;
import com.gmail.picono435.randomtp.config.Config;
import com.gmail.picono435.randomtp.config.Messages;
import com.mojang.datafixers.util.Pair;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.*;
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

import java.util.*;

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
                if(random.nextInt(2) == 1) x = x * -1;
                int z = random.ints(boundsZ.getFirst(), boundsZ.getSecond()).findAny().getAsInt();
                if(random.nextInt(2) == 1) z = z * -1;

                mutableBlockPos.setX(x);
                mutableBlockPos.setY(50);
                mutableBlockPos.setZ(z);
            } else {
                Pair<BlockPos, Holder<Biome>> pair = world.findClosestBiome3d(biomeHolder -> biomeHolder.is(biomeResourceKey), mutableBlockPos.immutable(), 6400, 32, 64);
                if(pair == null) {
                    System.out.println("Could not find biome sadje :(");
                    Component msg = Component.literal(Messages.getMaxTries().replaceAll("\\{playerName\\}", player.getName().getString()).replaceAll("&", "§"));
                    player.sendSystemMessage(msg, false);
                    return;
                }
                mutableBlockPos.setX(pair.getFirst().getX());
                mutableBlockPos.setY(50);
                mutableBlockPos.setZ(pair.getFirst().getZ());
                System.out.println("FOOOOOOOUND biome trying positions");
            }
            int maxTries = Config.getMaxTries();
            int y = mutableBlockPos.getY();
            while (!isSafe(world, mutableBlockPos) && (maxTries == -1 || maxTries > 0)) {
                System.out.println(mutableBlockPos.getX() + " " + mutableBlockPos.getY() + " " + mutableBlockPos.getZ());
                y++;
                mutableBlockPos.setY(y);
                if(mutableBlockPos.getY() >= 200 || !isInBiomeWhitelist(world.getBiome(mutableBlockPos.immutable()).unwrapKey().get().location())) {
                    if(biomeResourceKey != null) {
                        Pair<BlockPos, Holder<Biome>> pair = world.findClosestBiome3d(biomeHolder -> biomeHolder.is(biomeResourceKey), mutableBlockPos.immutable(), 6400, 32, 64);
                        if(pair == null) {
                            System.out.println("Could not find biome sadje :(");
                            Component msg = Component.literal(Messages.getMaxTries().replaceAll("\\{playerName\\}", player.getName().getString()).replaceAll("&", "§"));
                            player.sendSystemMessage(msg, false);
                            return;
                        }
                        mutableBlockPos.setX(pair.getFirst().getX());
                        mutableBlockPos.setY(50);
                        mutableBlockPos.setZ(pair.getFirst().getZ());
                        continue;
                    }
                    int x = random.ints(boundsX.getFirst(), boundsX.getSecond()).findAny().getAsInt();
                    if(random.nextInt(2) == 1) x = x * -1;
                    int z = random.ints(boundsZ.getFirst(), boundsZ.getSecond()).findAny().getAsInt();
                    if(random.nextInt(2) == 1) z = z * -1;

                    mutableBlockPos.setX(x);
                    y = 50;
                    mutableBlockPos.setY(50);
                    mutableBlockPos.setZ(z);
                    continue;
                }
                if(maxTries > 0){
                    maxTries--;
                }
                if(maxTries == 0) {
                    Component msg = Component.literal(Messages.getMaxTries().replaceAll("\\{playerName\\}", player.getName().getString()).replaceAll("&", "§"));
                    player.sendSystemMessage(msg, false);
                    return;
                }
            }

            player.teleportTo(world, mutableBlockPos.getX(), mutableBlockPos.getY(), mutableBlockPos.getZ(), player.getXRot(), player.getYRot());
            Component successful = Component.literal(Messages.getSuccessful().replaceAll("\\{playerName\\}", player.getName().getString()).replaceAll("\\{blockX\\}", "" + (int)player.position().x).replaceAll("\\{blockY\\}", "" + (int)player.position().y).replaceAll("\\{blockZ\\}", "" + (int)player.position().z).replaceAll("&", "§"));
            player.sendSystemMessage(successful, false);
        } catch(Exception ex) {
            RandomTP.getLogger().info("Error executing command.");
            ex.printStackTrace();
        }
    }

    private static Pair<Integer, Integer> generateBounds(ServerLevel world, Player player, boolean XorZ) {
        int maxDistance = (int) Math.round(Config.getMaxDistance() == 0 ? (world.getWorldBorder().getSize() / 2) : Config.getMaxDistance());
        if(XorZ) {
            // Calculating bounds for coordinates X
            int highX = maxDistance + Math.abs(player.getBlockX());
            if(highX > Math.abs(world.getWorldBorder().getCenterX()) + (world.getWorldBorder().getSize() / 2)) {
                highX = (int) Math.round(world.getWorldBorder().getCenterX() + (world.getWorldBorder().getSize() / 2));
            }
            int lowX = Config.getMinDistance() + Math.abs(player.getBlockX());
            if(lowX > Math.abs(world.getWorldBorder().getCenterX()) + (world.getWorldBorder().getSize() / 2)) {
                lowX = (int) Math.round(world.getWorldBorder().getCenterX() + (world.getWorldBorder().getSize() / 2)) - 10;
            }
            return new Pair<>(lowX, highX);
        } else {
            // Calculating bounds for coordinate Z
            int highZ = maxDistance + Math.abs(player.getBlockZ());
            if(highZ > Math.abs(world.getWorldBorder().getCenterZ()) + (world.getWorldBorder().getSize() / 2)) {
                highZ = (int) Math.round(world.getWorldBorder().getCenterZ() + (world.getWorldBorder().getSize() / 2));
            }
            int lowZ = Config.getMinDistance() + Math.abs(player.getBlockZ());
            if(lowZ > Math.abs(world.getWorldBorder().getCenterZ()) + (world.getWorldBorder().getSize() / 2)) {
                lowZ = (int) Math.round(world.getWorldBorder().getCenterZ() + (world.getWorldBorder().getSize() / 2)) - 10;
            }
            return new Pair<>(lowZ, highZ);
        }
    }

    public static ServerLevel getWorld(String world, MinecraftServer server) {
        try {
            ResourceLocation resourcelocation = ResourceLocation.tryParse(world);
            ResourceKey<Level> registrykey = ResourceKey.create(Registries.DIMENSION, resourcelocation);
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
