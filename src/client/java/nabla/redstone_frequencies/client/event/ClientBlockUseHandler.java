package nabla.redstone_frequencies.client.event;

import nabla.redstone_frequencies.block.ModBlocks;
import nabla.redstone_frequencies.block.entity.custom.RedstoneReceiverEntity;
import nabla.redstone_frequencies.block.entity.custom.RedstoneTransmitterEntity;
import nabla.redstone_frequencies.client.screen.FrequencyConfigScreen;
import nabla.redstone_frequencies.item.ModItems;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ClientBlockUseHandler implements UseBlockCallback {
    @Override
    public ActionResult interact(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
        BlockPos pos = hitResult.getBlockPos();
        ItemStack stack = player.getStackInHand(hand);

        if (hand == Hand.MAIN_HAND && stack.isEmpty()) {
            if (world.getBlockState(pos).isOf(ModBlocks.REDSTONE_TRANSMITTER) ||
                    world.getBlockState(pos).isOf(ModBlocks.REDSTONE_RECEIVER)) {


                MinecraftClient.getInstance().execute(() ->
                        MinecraftClient.getInstance().setScreen(FrequencyConfigScreen.create(pos))
                );


                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.PASS;
    }
}