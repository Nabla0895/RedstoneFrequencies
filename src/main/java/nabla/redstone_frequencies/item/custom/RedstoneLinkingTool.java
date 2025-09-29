package nabla.redstone_frequencies.item.custom;

import nabla.redstone_frequencies.block.ModBlocks;
import nabla.redstone_frequencies.block.entity.custom.RedstoneReceiverEntity;
import nabla.redstone_frequencies.block.entity.custom.RedstoneTransmitterEntity;
import nabla.redstone_frequencies.component.ModDataComponentTypes;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.function.Consumer;

public class RedstoneLinkingTool extends Item {


    public RedstoneLinkingTool(Settings settings) {
        super(settings);
    }


    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        if (world.isClient) return ActionResult.SUCCESS;

        BlockEntity be = world.getBlockEntity(context.getBlockPos());
        ItemStack stack = context.getStack();

        //Copy Frequency from Transmitter or Receiver
        if (be instanceof RedstoneTransmitterEntity transmitter) {
            stack.set(ModDataComponentTypes.FREQUENCY, transmitter.getFreq());
            context.getPlayer().sendMessage(Text.literal("Frequency: " + transmitter.getFreq()), true);
            world.playSound(null, context.getBlockPos(), SoundEvents.ITEM_INK_SAC_USE, SoundCategory.BLOCKS, 0.2F, 2F);
        } else if (be instanceof RedstoneReceiverEntity receiver) {
            stack.set(ModDataComponentTypes.FREQUENCY, receiver.getFreq());
            context.getPlayer().sendMessage(Text.literal("Frequency: " + receiver.getFreq()), true);
            world.playSound(null, context.getBlockPos(), SoundEvents.ITEM_INK_SAC_USE, SoundCategory.BLOCKS, 0.2F, 2F);
        }
        return ActionResult.SUCCESS;
    }



    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type) {
        if (stack.get(ModDataComponentTypes.FREQUENCY) != null) {
            textConsumer.accept(Text.literal("Saved Frequency: " + stack.get(ModDataComponentTypes.FREQUENCY)));
        } else {
            textConsumer.accept(Text.literal("No frequency saved."));
        }
    }


}
