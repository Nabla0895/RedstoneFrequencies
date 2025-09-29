package nabla.redstone_frequencies.block.custom;

import com.mojang.serialization.MapCodec;
import nabla.redstone_frequencies.block.entity.custom.RedstoneTransmitterEntity;
import nabla.redstone_frequencies.component.ModDataComponentTypes;
import nabla.redstone_frequencies.item.ModItems;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.block.WireOrientation;
import org.jetbrains.annotations.Nullable;

public class RedstoneTransmitter extends BlockWithEntity implements BlockEntityProvider {
    public static final MapCodec<RedstoneTransmitter> CODEC = RedstoneTransmitter.createCodec(RedstoneTransmitter::new);
    public RedstoneTransmitter(Settings settings) {
        super(settings);
    }


    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }


    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, @Nullable WireOrientation wireOrientation, boolean notify) {
        if (!world.isClient) {
            final BlockEntity entity = world.getBlockEntity(pos);
            if (entity instanceof RedstoneTransmitterEntity) {
                final RedstoneTransmitterEntity emitter = (RedstoneTransmitterEntity) entity;
                emitter.redstoneUpdate(world.isReceivingRedstonePower(pos));
            }
        }
    }



    @Override
    protected ActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            if (stack.getItem() == ModItems.REDSTONE_LINKING_TOOL) {
                final BlockEntity entity = world.getBlockEntity(pos);
                if (entity instanceof RedstoneTransmitterEntity) {
                    final RedstoneTransmitterEntity emitter = (RedstoneTransmitterEntity) entity;
                    final BlockPos linkedPos = emitter.getReceiverPos();
                    final BlockPos recPos = stack.get(ModDataComponentTypes.COORDINATES);
                    if (linkedPos == null && recPos == null) {
                        player.sendMessage(Text.literal("Not linked!"), true);
                        return ActionResult.SUCCESS;
                    }
                    if (player.isSneaking() && linkedPos != null) {
                        if (emitter.unlink()) {
                            player.sendMessage(Text.literal("Deleted Link!"), true);
                            return ActionResult.SUCCESS;
                        }
                    }
                    if (!player.isSneaking() && recPos != null)
                        if (emitter.link(recPos)) {
                            player.sendMessage(Text.literal("Sucessfully linked to: " + emitter.getReceiverPos().toString()), true);
                            stack.set(ModDataComponentTypes.COORDINATES, null);
                            return ActionResult.SUCCESS;
                        }
                    if (!player.isSneaking() && linkedPos != null && recPos == null) {
                        player.sendMessage(Text.literal("Linked Receiver: " + emitter.getReceiverPos().toString()), true);
                        return ActionResult.SUCCESS;
                    }
                }
            }

        }
        return ActionResult.FAIL;
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient) {
            final BlockEntity entity = world.getBlockEntity(pos);
            if (entity instanceof RedstoneTransmitterEntity) {
                final RedstoneTransmitterEntity emitter = (RedstoneTransmitterEntity) entity;
                final BlockPos linkedPos = emitter.getReceiverPos();
                if (linkedPos != null) {
                    player.sendMessage(Text.literal("Linked to: " + emitter.getReceiverPos().toString()), false);
                    return ActionResult.SUCCESS;
                } else {
                    player.sendMessage(Text.literal("Unlinked!"), false);
                    return ActionResult.SUCCESS;
                }
            }
        }
        return ActionResult.FAIL;
    }

    @Override
    protected boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new RedstoneTransmitterEntity(pos, state);
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }
}
