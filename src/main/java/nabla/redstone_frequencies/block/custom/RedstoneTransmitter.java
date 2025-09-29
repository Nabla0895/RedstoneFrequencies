package nabla.redstone_frequencies.block.custom;

import com.mojang.serialization.MapCodec;
import nabla.redstone_frequencies.block.entity.custom.RedstoneTransmitterEntity;
import nabla.redstone_frequencies.component.ModDataComponentTypes;
import nabla.redstone_frequencies.item.ModItems;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
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
    public static final BooleanProperty POWER = BooleanProperty.of("power");

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
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.isClient) return ActionResult.SUCCESS;

        if (world.getBlockEntity(pos) instanceof RedstoneTransmitterEntity transmitter) {
            ItemStack stack = player.getStackInHand(Hand.MAIN_HAND);

            //SET Frequency with Tool
            if (stack.isOf(ModItems.REDSTONE_LINKING_TOOL) && stack.get(ModDataComponentTypes.FREQUENCY) != null) {
                int freq = stack.get(ModDataComponentTypes.FREQUENCY);
                transmitter.setFreq(freq);
                player.sendMessage(Text.literal("Set Frequency to:" + freq), true);
                return ActionResult.SUCCESS;
            }

            //Change Frequency by right-clicking without item
            if (player.isSneaking()) { //Decrease by 1 if sneaking
                int currentFreq = transmitter.getFreq();
                transmitter.setFreq(currentFreq > 0 ? currentFreq - 1 : 0);
            } else { //Increase by 1 without sneaking
                transmitter.setFreq(transmitter.getFreq() +1);
            }
            player.sendMessage(Text.literal("Send-Frequency: " + transmitter.getFreq()), true);
        }
        return ActionResult.SUCCESS;
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

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(POWER);
    }

}
