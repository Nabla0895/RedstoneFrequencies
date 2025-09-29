package nabla.redstone_frequencies.block.entity;

import net.minecraft.util.math.BlockPos;

public interface LinkInterface {

    boolean hasLink();
    boolean link (final BlockPos pos);
    boolean unlink();
}
