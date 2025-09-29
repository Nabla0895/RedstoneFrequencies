package nabla.redstone_frequencies.item.custom;

import nabla.redstone_frequencies.block.entity.custom.RedstoneReceiverEntity;
import nabla.redstone_frequencies.block.entity.custom.RedstoneTransmitterEntity;
import nabla.redstone_frequencies.component.ModDataComponentTypes;
import nabla.redstone_frequencies.component.custom.FrequencySettingsPayload;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import java.util.function.Consumer;

public class RedstoneLinkingTool extends Item {
    public RedstoneLinkingTool(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        if (user.isSneaking()) {
            stack.remove(ModDataComponentTypes.FREQUENCY_SETTINGS); // Clear saved Settings

            if (world.isClient) {
                user.sendMessage(Text.literal("Tool reset successfully!"), true);
            }
            world.playSound(user, user.getBlockPos(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 0.5F, 0.8F);

            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        if (world.isClient) return ActionResult.SUCCESS;

        BlockEntity be = world.getBlockEntity(context.getBlockPos());
        ItemStack stack = context.getStack();
        PlayerEntity player = context.getPlayer();

        // Case 1: Copy from Block
        if (be instanceof RedstoneTransmitterEntity transmitter) {
            FrequencySettingsPayload payload = new FrequencySettingsPayload(transmitter.getFreq(), transmitter.isPrivate());
            stack.set(ModDataComponentTypes.FREQUENCY_SETTINGS, payload);
            player.sendMessage(Text.literal("Copied settings! (F: " + payload.frequency() + ", P: " + payload.isPrivate() + ")"), true);
            world.playSound(null, context.getBlockPos(), SoundEvents.UI_BUTTON_CLICK.value(), SoundCategory.BLOCKS, 0.5F, 1.5F);
            return ActionResult.SUCCESS;
        }
        if (be instanceof RedstoneReceiverEntity receiver) {
            FrequencySettingsPayload payload = new FrequencySettingsPayload(receiver.getFreq(), receiver.isPrivate());
            stack.set(ModDataComponentTypes.FREQUENCY_SETTINGS, payload);
            player.sendMessage(Text.literal("Copied settings! (F: " + payload.frequency() + ", P: " + payload.isPrivate() + ")"), true);
            world.playSound(null, context.getBlockPos(), SoundEvents.UI_BUTTON_CLICK.value(), SoundCategory.BLOCKS, 0.5F, 1.5F);
            return ActionResult.SUCCESS;
        }

        // Case 2: Paste on a Block
        FrequencySettingsPayload savedSettings = stack.get(ModDataComponentTypes.FREQUENCY_SETTINGS);
        if (savedSettings != null && be instanceof RedstoneTransmitterEntity transmitter) {
            transmitter.setFreq(savedSettings.frequency());
            transmitter.setPrivate(savedSettings.isPrivate());
            //player.sendMessage(Text.literal("Paste successfully!"), true);
            world.playSound(null, context.getBlockPos(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.BLOCKS, 0.5F, 1.5F);
            // Client-Update auslösen!
            ((ServerWorld) world).getChunkManager().markForUpdate(context.getBlockPos());
            return ActionResult.SUCCESS;
        }
        if (savedSettings != null && be instanceof RedstoneReceiverEntity receiver) {
            receiver.setFreq(savedSettings.frequency());
            receiver.setPrivate(savedSettings.isPrivate());
            //player.sendMessage(Text.literal("Paste successfully!"), true);
            world.playSound(null, context.getBlockPos(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.BLOCKS, 0.5F, 1.5F);
            // Client-Update auslösen!
            ((ServerWorld) world).getChunkManager().markForUpdate(context.getBlockPos());
            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type) {
        FrequencySettingsPayload settings = stack.get(ModDataComponentTypes.FREQUENCY_SETTINGS);
        if (settings != null) {
            textConsumer.accept(Text.literal("Saved frequency: " + settings.frequency()));
            textConsumer.accept(Text.literal("Mode: " + (settings.isPrivate() ? "Privat" : "Öffentlich")));
        } else {
            textConsumer.accept(Text.literal("No settings saved."));
        }
    }
}