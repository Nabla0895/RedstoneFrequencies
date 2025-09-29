package nabla.redstone_frequencies.block.custom;

import com.mojang.serialization.MapCodec;
import nabla.redstone_frequencies.block.entity.ModBlockEntities;
import nabla.redstone_frequencies.block.entity.custom.RedstoneReceiverEntity;
import nabla.redstone_frequencies.component.ModDataComponentTypes;
import nabla.redstone_frequencies.item.ModItems;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
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
    public static final BooleanProperty POWER = BooleanProperty.of("power");

    public RedstoneReceiver(Settings settings) {
        super(settings);
        setDefaultState(this.getDefaultState().with(POWER, false));
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

//    @Override
//    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
//        return validateTicker(type, ModBlockEntities.RECEIVER_BE, (world1, pos, state1, be) -> {
//            if (!world1.isClient() && !be.isRemoved()) {
//                if (world1.isChunkLoaded(pos)) {
//                    be.onServerStart(world1);
//                }
//            }
//        });
//    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.isClient) return ActionResult.SUCCESS;

        if (world.getBlockEntity(pos) instanceof RedstoneReceiverEntity receiver) {
            ItemStack stack = player.getStackInHand(Hand.MAIN_HAND);

            //SET Frequency with Tool
            if (stack.isOf(ModItems.REDSTONE_LINKING_TOOL) && stack.get(ModDataComponentTypes.FREQUENCY) != null) {
                int freq = stack.get(ModDataComponentTypes.FREQUENCY);
                receiver.setFreq(freq);
                player.sendMessage(Text.literal("Set Frequency to:" + freq), true);
                return ActionResult.SUCCESS;
            }

            //Change Frequency by right-clicking without Items
            if (player.isSneaking()) { //Decrease by 1 if sneaking
                int currentFreq = receiver.getFreq();
                receiver.setFreq(currentFreq > 0 ? currentFreq - 1 : 0);
            } else {
                receiver.setFreq(receiver.getFreq() + 1);
            }
            player.sendMessage(Text.literal("Receive-Frequency: " + receiver.getFreq()), true);
        }
        return ActionResult.SUCCESS;
    }

    @Override
    protected int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return this.getStrongRedstonePower(state, world, pos, direction);
    }

    @Override
    protected int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return state.get(POWER) ? 15 : 0;
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
        return null;
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }
}
