package nabla.redstone_frequencies.networking.packet;

import nabla.redstone_frequencies.Redstone_frequencies;
import nabla.redstone_frequencies.block.entity.custom.RedstoneReceiverEntity;
import nabla.redstone_frequencies.block.entity.custom.RedstoneTransmitterEntity;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public record FrequencyUpdateC2SPacket(BlockPos blockPos, int frequency, boolean isPrivate) implements CustomPayload {
    public static final CustomPayload.Id<FrequencyUpdateC2SPacket> ID = new CustomPayload.Id<>(Identifier.of(Redstone_frequencies.MOD_ID, "frequency_update"));

    public static final PacketCodec<RegistryByteBuf, FrequencyUpdateC2SPacket> CODEC = PacketCodec.of(
            (value, buf) -> {
                buf.writeBlockPos(value.blockPos());
                buf.writeInt(value.frequency());
                buf.writeBoolean(value.isPrivate());
            },
            (buf) -> new FrequencyUpdateC2SPacket(buf.readBlockPos(), buf.readInt(), buf.readBoolean())
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void receive(FrequencyUpdateC2SPacket payload, ServerPlayNetworking.Context context) {
        ServerPlayerEntity player = context.player();
        World world = player.getWorld();

        context.server().execute(() -> {
            BlockPos pos = payload.blockPos();
            BlockState state = world.getBlockState(pos);
            boolean hasPermission = player.hasPermissionLevel(2);

            if(world.getBlockEntity(payload.blockPos()) instanceof RedstoneTransmitterEntity be) {
                if (be.getOwnerUuid().isEmpty() || be.getOwnerUuid().get().equals(player.getUuid()) || hasPermission) {
                    be.setFreq(payload.frequency());
                    be.setPrivate(payload.isPrivate());
                    world.updateListeners(pos, state, state, 3);
                }
            } else if(world.getBlockEntity(payload.blockPos()) instanceof RedstoneReceiverEntity be) {
                if (be.getOwnerUuid().isEmpty() || be.getOwnerUuid().get().equals(player.getUuid()) || hasPermission) {
                    be.setFreq(payload.frequency());
                    be.setPrivate(payload.isPrivate());
                    world.updateListeners(pos, state, state, 3);
                }
            }
        });
    }
}
