package nabla.redstone_frequencies.block.custom;

import com.mojang.serialization.MapCodec;
import nabla.redstone_frequencies.block.entity.ModBlockEntities;
import nabla.redstone_frequencies.block.entity.custom.RedstoneReceiverEntity;
import nabla.redstone_frequencies.block.entity.custom.RedstoneTransmitterEntity;
import nabla.redstone_frequencies.component.ModDataComponentTypes;
import nabla.redstone_frequencies.item.ModItems;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class RedstoneReceiver extends BlockWithEntity implements BlockEntityProvider {
    public static final MapCodec<RedstoneReceiver> CODEC = RedstoneReceiver.createCodec(RedstoneReceiver::new);
    public static final IntProperty POWER = Properties.POWER;

    public RedstoneReceiver(Settings settings) {
        super(settings);
        setDefaultState(this.getDefaultState().with(POWER, 0));
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!(player instanceof ServerPlayerEntity)) {
            return player.getStackInHand(Hand.MAIN_HAND).isEmpty() ? ActionResult.SUCCESS : ActionResult.PASS;
        }

        if (world.getBlockEntity(pos) instanceof RedstoneReceiverEntity receiver) {
            ItemStack stack = player.getStackInHand(Hand.MAIN_HAND);

        if (stack.isOf(ModItems.REDSTONE_LINKING_TOOL)) {
            if (stack.contains(ModDataComponentTypes.FREQUENCY_SETTINGS)) {
                var settings = stack.get(ModDataComponentTypes.FREQUENCY_SETTINGS);
                if (settings != null) {
                    if (receiver.getOwnerUuid().isEmpty() || receiver.getOwnerUuid().get().equals(player.getUuid()) || player.hasPermissionLevel(2)) {
                        receiver.setFreq(settings.frequency());
                        receiver.setPrivate(settings.isPrivate());
                        player.sendMessage(Text.literal("Settings pasted to Receiver!"), true);
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
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if (!world.isClient() && placer instanceof PlayerEntity) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof RedstoneReceiverEntity receiverEntity) {
                receiverEntity.setOwnerUuid(placer.getUuid());
                receiverEntity.setOwnerName(placer.getName().getString());
            }
        }
        super.onPlaced(world, pos, state, placer, itemStack);
    }

    @Override
    protected int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return state.get(POWER);
    }

    @Override
    protected int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return state.get(POWER);
    }

    @Override
    protected boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(POWER);
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new RedstoneReceiverEntity(pos, state);
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }
}
