package nabla.redstone_frequencies.block.entity.custom;

import nabla.redstone_frequencies.block.entity.ModBlockEntities;
import nabla.redstone_frequencies.util.FrequencyManager;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class RedstoneTransmitterEntity extends BlockEntity {
    public RedstoneTransmitterEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.TRANSMITTER_BE, pos, state);
    }

    private int freq = 0;


    @Override
    protected void writeData(WriteView view) {
        super.writeData(view);
        view.putInt("Frequency", freq);
    }

    @Override
    protected void readData(ReadView view) {
        super.readData(view);
        freq = view.getInt("Frequency", 0);
    }

    public int getFreq() {
        return this.freq;
    }

    public void setFreq(int freq) {
        this.freq = freq;
        markDirty();
    }

    public void redstoneUpdate(final int power) {
        if (this.world != null && !this.world.isClient()) {
            //Send Signal to FrequencyManager
            FrequencyManager.broadcast((ServerWorld) this.world, this.freq, power);
        }
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return createNbt(registryLookup);
    }
}
