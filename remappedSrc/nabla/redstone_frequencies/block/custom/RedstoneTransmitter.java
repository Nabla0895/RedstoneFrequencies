package nabla.redstone_frequencies.block.custom;

import com.mojang.serialization.MapCodec;
import nabla.redstone_frequencies.block.entity.custom.RedstoneTransmitterEntity;
import nabla.redstone_frequencies.component.ModDataComponentTypes;
import nabla.redstone_frequencies.item.ModItems;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
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
    public static final IntProperty POWER = Properties.POWER;

    public RedstoneTransmitter(Settings settings) {
        super(settings);
        setDefaultState(this.getDefaultState().with(POWER, 0));
    }


    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.isClient()) {
            return player.getStackInHand(Hand.MAIN_HAND).isEmpty() ? ActionResult.SUCCESS : ActionResult.PASS;
        }

        if (world.getBlockEntity(pos) instanceof RedstoneTransmitterEntity transmitter) {
            ItemStack stack = player.getStackInHand(Hand.MAIN_HAND);

            if (stack.isOf(ModItems.REDSTONE_LINKING_TOOL)) {
                if (stack.contains(ModDataComponentTypes.FREQUENCY_SETTINGS)) {
                    var settings = stack.get(ModDataComponentTypes.FREQUENCY_SETTINGS);
                    if (settings != null) {
                        if (transmitter.getOwnerUuid().isEmpty() || transmitter.getOwnerUuid().get().equals(player.getUuid()) || player.hasPermissionLevel(2)) {
                            transmitter.setFreq(settings.frequency());
                            transmitter.setPrivate(settings.isPrivate());
                            player.sendMessage(Text.literal("Settings pasted to Transmitter!"), true);
                            world.playSound(null, pos, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.BLOCKS, 0.5F, 1.5F);
                            world.updateListeners(pos, state, state, 3);
                        } else {
                            player.sendMessage(Text.literal("You are not owner of this block!"), true);
                        }
                    }
                }
                return ActionResult.CONSUME;
            }
        }
        return ActionResult.PASS;
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (!oldState.isOf(state.getBlock()) && !world.isClient()) {
            updateState(world, pos, state);
        }
    }

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, @Nullable WireOrientation wireOrientation, boolean notify) {
        if (!world.isClient()) {
            updateState(world, pos, state);
        }
    }

    private void updateState(World world, BlockPos pos, BlockState state) {
        int incomingPower = world.getReceivedRedstonePower(pos);
        if (incomingPower != state.get(POWER)) {
            BlockState newState = state.with(POWER, incomingPower);
            world.setBlockState(pos, newState, 2);

            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof RedstoneTransmitterEntity transmitter) {
                transmitter.redstoneUpdate(incomingPower);
            }
        }
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if (!world.isClient() && placer instanceof PlayerEntity) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof RedstoneTransmitterEntity transmitterEntity) {
                transmitterEntity.setOwnerUuid(placer.getUuid());
                transmitterEntity.setOwnerName(placer.getName().getString());
            }
        }
        super.onPlaced(world, pos, state, placer, itemStack);
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
