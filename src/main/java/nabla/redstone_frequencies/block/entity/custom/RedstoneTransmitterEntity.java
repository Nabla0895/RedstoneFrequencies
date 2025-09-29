package nabla.redstone_frequencies.block.entity.custom;

import nabla.redstone_frequencies.block.custom.RedstoneReceiver;
import nabla.redstone_frequencies.block.entity.LinkInterface;
import nabla.redstone_frequencies.block.entity.ModBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
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

public class RedstoneTransmitterEntity extends BlockEntity implements LinkInterface {
    public RedstoneTransmitterEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.TRANSMITTER_BE, pos, state);
    }

    private BlockPos linkedpos = null;
    private int freq = 0;
    private static final String ID_X = "xLinkedPos";
    private static final String ID_Y = "yLinkedPos";
    private static final String ID_Z = "zLinkedPos";

    @Override
    protected void writeData(WriteView view) {
        super.writeData(view);
        if (linkedpos != null) {
            view.putInt(ID_X, linkedpos.getX());
            view.putInt(ID_Y, linkedpos.getY());
            view.putInt(ID_Z, linkedpos.getZ());
        }
        view.putInt("Frequency", freq);
    }

    @Override
    protected void readData(ReadView view) {
        super.readData(view);
        linkedpos = new BlockPos(view.getInt(ID_X, 0), view.getInt(ID_Y, 0), view.getInt(ID_Z, 0));
        freq = view.getInt("Frequency", 0);
    }

    @Override
    public boolean link(final BlockPos pos) {
        if (pos == null)
            return false;
        this.linkedpos = pos;
        return true;
    }

    @Override
    public boolean unlink() {
        this.linkedpos = null;
        return true;
    }

    public void redstoneUpdate(final boolean enabled) {
        redstoneUpdate(enabled, linkedpos, world);
    }

    public static boolean redstoneUpdate(final boolean enabled, final BlockPos linkedpos,
                                         final World world) {
        if (linkedpos != null) {
            final BlockState state = world.getBlockState(linkedpos);
            if (state.getBlock() instanceof RedstoneReceiver) {
                world.setBlockState(linkedpos, state.with(RedstoneReceiver.POWER, enabled), 3);
            }
        }
        return enabled;
    }

    @Override
    public boolean hasLink() {
        return this.linkedpos != null;
    }

    public BlockPos getReceiverPos() {
        return  this.linkedpos;
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
