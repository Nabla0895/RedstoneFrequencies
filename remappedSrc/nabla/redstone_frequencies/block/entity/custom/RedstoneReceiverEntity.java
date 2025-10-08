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
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class RedstoneReceiverEntity extends BlockEntity{
    public RedstoneReceiverEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.RECEIVER_BE, pos, state);
    }

    private int freq = 0;
    private boolean isPrivate = false;
    private UUID ownerUuid;
    private String ownerName = "Unknown";

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
        markDirty();
    }

    public Optional<UUID> getOwnerUuid() {
        return Optional.ofNullable(ownerUuid);
    }

    public void setOwnerUuid(UUID ownerUuid) {
        this.ownerUuid = ownerUuid;
        markDirty();
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
        markDirty();
    }

    @Override
    protected void writeData(WriteView view) {
        super.writeData(view);
        view.putInt("Frequency", freq);
        view.putBoolean("IsPrivate", isPrivate);
        if (ownerUuid != null) {
            view.putString("Owner", ownerUuid.toString());
        }
        view.putString("OwnerName", ownerName);
    }

    @Override
    protected void readData(ReadView view) {
        super.readData(view);
        this.freq = view.getInt("Frequency", 0);
        this.isPrivate = view.getBoolean("IsPrivate", false);
        if (view.contains("Owner")) {
            String ownerString = view.getString("Owner", null);
            if (ownerString != null && !ownerString.isEmpty()) {
                this.ownerUuid = UUID.fromString(ownerString);
            }
        }
        this.ownerName = view.getString("OwnerName", "Unknown");
    }

    @Override
    public void setWorld(World world) {
        super.setWorld(world);
        if (world != null && !world.isClient()) {
            FrequencyManager.addReceiver(this);
        }
    }

    @Override
    public void markRemoved() {
        if (world != null && !world.isClient()) {
            FrequencyManager.removeReceiver(this);
        }
        super.markRemoved();
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

    public int getFreq() {
        return this.freq;
    }

    public void setFreq(int freq) {
        this.freq = freq;
        markDirty();
    }


}
