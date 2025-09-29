package nabla.redstone_frequencies.util;

import nabla.redstone_frequencies.block.custom.RedstoneReceiver;
import nabla.redstone_frequencies.block.entity.custom.RedstoneReceiverEntity;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Map;
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
    public static void broadcast(ServerWorld world, int freq, int powered) {
        Map<BlockPos, RedstoneReceiverEntity> worldReceivers = receivers.get(world);
        if (worldReceivers == null) return;

        worldReceivers.forEach((pos, receiver) -> {
            if (receiver.getFreq() == freq) {
                BlockState state = world.getBlockState(pos);
                if (state.getBlock() instanceof RedstoneReceiver) {
                    world.setBlockState(pos, state.with(RedstoneReceiver.POWER, powered), 3);
                }
            }
        });
    }

}
