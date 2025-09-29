package nabla.redstone_frequencies.item.custom;

import nabla.redstone_frequencies.block.ModBlocks;
import nabla.redstone_frequencies.component.ModDataComponentTypes;
import net.minecraft.block.BlockState;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.tooltip.TooltipType;
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
        BlockPos blockPos = context.getBlockPos();
        BlockState blockState = world.getBlockState(blockPos);

        if (blockState.getBlock() == ModBlocks.REDSTONE_RECEIVER) {
            context.getStack().set(ModDataComponentTypes.COORDINATES, context.getBlockPos());
            context.getPlayer().sendMessage(Text.literal("Position saved!"), true);
        }
        return ActionResult.SUCCESS;

    }



    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type) {
        if (stack.get(ModDataComponentTypes.COORDINATES) != null) {
            textConsumer.accept(Text.literal("Linked Receiver: " + stack.get(ModDataComponentTypes.COORDINATES)));
        }
    }


}
