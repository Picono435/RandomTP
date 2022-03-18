package com.gmail.picono435.randomtp.api;

import com.gmail.picono435.randomtp.RandomTP;
import com.gmail.picono435.randomtp.config.Config;
import com.gmail.picono435.randomtp.config.Messages;
import com.mojang.datafixers.util.Either;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.ResourceOrTagLocationArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.Map;
import java.util.Optional;
import java.util.Random;

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
                ResourceOrTagLocationArgument.Result<Biome> result = new ResourceResult<>(getBiomeResourceKey(biome));
                BlockPos biomePos = world.findNearestBiome(result, new BlockPos(x, y, z), 6400, 8).getFirst();
                x = biomePos.getX();
                y = biomePos.getY();
                z = biomePos.getZ();
            }
            int maxTries = Config.getMaxTries();
            while (!isSafe(world, x, y, z) && (maxTries == -1 || maxTries > 0)) {
                y++;
                if(y >= 120 || !isInBiomeWhitelist(getBiomeId(getBiomeFromKey(world.getBiome(new BlockPos(x, y, z)).unwrapKey().get())).toString())) {
                    x = r.nextInt(highX-lowX) + lowX;
                    y = 50;
                    z = r.nextInt(highZ-lowZ) + lowZ;
                    if(biome != null) {
                        ResourceOrTagLocationArgument.Result<Biome> result = new ResourceResult<>(getBiomeResourceKey(biome));
                        BlockPos biomePos = world.findNearestBiome(result, new BlockPos(x, y, z), 6400, 8).getFirst();
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
                    player.sendMessage(msg, player.getUUID());
                    return;
                }
            }

            player.teleportTo(world, x, y, z, player.getXRot(), player.getYRot());
            TextComponent successful = new TextComponent(Messages.getSuccessful().replaceAll("\\{playerName\\}", player.getName().getString()).replaceAll("\\{blockX\\}", "" + (int)player.position().x).replaceAll("\\{blockY\\}", "" + (int)player.position().y).replaceAll("\\{blockZ\\}", "" + (int)player.position().z).replaceAll("&", "§"));
            player.sendMessage(successful, player.getUUID());
        } catch(Exception ex) {
            RandomTP.getLogger().info("Error executing command.");
            ex.printStackTrace();
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
    public static ResourceLocation getBiomeId(Biome biome) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static ResourceKey<Biome> getBiomeResourceKey(Biome biome) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static Biome getBiomeFromKey(ResourceKey<Biome> biome) {
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

    static record ResourceResult<T>(ResourceKey<T> key) implements ResourceOrTagLocationArgument.Result<T> {
        ResourceResult(ResourceKey<T> key) {
            this.key = key;
        }

        public Either<ResourceKey<T>, TagKey<T>> unwrap() {
            return Either.left(this.key);
        }

        public <E> Optional<ResourceOrTagLocationArgument.Result<E>> cast(ResourceKey<? extends Registry<E>> resourceKey) {
            return this.key.cast(resourceKey).map(ResourceResult::new);
        }

        public boolean test(Holder<T> holder) {
            return holder.is(this.key);
        }

        public String asPrintable() {
            return this.key.location().toString();
        }
    }
}
