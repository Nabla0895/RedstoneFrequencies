package nabla.redstone_frequencies.util;

import nabla.redstone_frequencies.block.custom.RedstoneReceiver;
import nabla.redstone_frequencies.block.entity.custom.RedstoneReceiverEntity;
import nabla.redstone_frequencies.block.entity.custom.RedstoneTransmitterEntity;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class FrequencyManager {
    //Saves all Receiver-Positions in World
    private static final Map<World, Map<BlockPos, RedstoneReceiverEntity>> receivers = new ConcurrentHashMap<>();

    //Adds a new Receiver to Map
    public static void addReceiver(RedstoneReceiverEntity receiver) {
        receivers.computeIfAbsent(receiver.getWorld(), k -> new ConcurrentHashMap<>()).put(receiver.getPos(), receiver);
    }

    //Removes a Receiver from Map
    public static void removeReceiver(RedstoneReceiverEntity receiver) {
        if (receiver.getWorld() != null) {
            Map<BlockPos, RedstoneReceiverEntity> worldReceivers = receivers.get(receiver.getWorld());
            if (worldReceivers != null) {
                worldReceivers.remove(receiver.getPos());
            }
        }
    }

    //Send Signal of specific Frequency
    public static void broadcast(ServerWorld world, RedstoneTransmitterEntity transmitter, int power) {
        Map<BlockPos, RedstoneReceiverEntity> worldReceivers = receivers.get(world);
        if (worldReceivers == null) return;

        final int frequency = transmitter.getFreq();
        final boolean isTransmitterPrivate = transmitter.isPrivate();
        final UUID transmitterOwner = transmitter.getOwnerUuid().orElse(null);

        worldReceivers.forEach((pos, receiver) -> {
            if (receiver.getFreq() == frequency) { // 1. Frequency Test - have to be the same
                boolean shouldActivate = false;

                if (isTransmitterPrivate) { // 2. Is transmitter public or private?
                    if (receiver.isPrivate() && Objects.equals(transmitterOwner, receiver.getOwnerUuid().orElse(null))) { // Receiver AND Transmitter has to be private AND has to be the same owner
                        shouldActivate = true;
                    }
                } else { // If Transmitter is public, Receiver also have to be public
                    if (!receiver.isPrivate()) {
                        shouldActivate = true;
                    }
                }

                BlockState currentState = world.getBlockState(pos);
                if (currentState.getBlock() instanceof RedstoneReceiver) {
                    int currentPower = currentState.get(Properties.POWER);
                    int newPower = shouldActivate ? power : 0;

                    if (currentPower != newPower) {
                        world.setBlockState(pos, currentState.with(Properties.POWER, newPower), 3);
                    }
                }
            }
        });
    }

}
