package nabla.redstone_frequencies.client.screen;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import nabla.redstone_frequencies.block.entity.custom.RedstoneReceiverEntity;
import nabla.redstone_frequencies.block.entity.custom.RedstoneTransmitterEntity;
import nabla.redstone_frequencies.networking.packet.FrequencyUpdateC2SPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class FrequencyConfigScreen {
    public static Screen create(BlockPos pos) {
        final AtomicInteger frequency = new AtomicInteger(0);
        final AtomicBoolean isPrivate = new AtomicBoolean(false);
        final AtomicReference<String> ownerName = new AtomicReference<>("Unknown");

        var be = MinecraftClient.getInstance().world.getBlockEntity(pos);
        if (be instanceof RedstoneTransmitterEntity transmitter) {
            frequency.set(transmitter.getFreq());
            isPrivate.set(transmitter.isPrivate());
            ownerName.set(transmitter.getOwnerName());
        } else if (be instanceof RedstoneReceiverEntity receiver) {
            frequency.set(receiver.getFreq());
            isPrivate.set(receiver.isPrivate());
            ownerName.set(receiver.getOwnerName());
        }

        ConfigBuilder builder = ConfigBuilder.create().setTitle(Text.literal("Choose Frequency"));
        ConfigCategory category = builder.getOrCreateCategory(Text.literal("Settings"));
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        category.addEntry(entryBuilder.startTextDescription(
                Text.literal("Block owner: " + ownerName.get())
                ).build());

        category.addEntry(entryBuilder.startIntField(Text.literal("Frequency"), frequency.get())
                .setDefaultValue(0)
                .setSaveConsumer(frequency::set)
                .build());

        category.addEntry(entryBuilder.startBooleanToggle(Text.literal("Private"), isPrivate.get())
                .setDefaultValue(false)
                .setTooltip(Text.literal("If activated, Signal will just be send to Receivers, you placed in the World."))
                .setSaveConsumer(isPrivate::set)
                .build());


        builder.setSavingRunnable(() -> {
            ClientPlayNetworking.send(new FrequencyUpdateC2SPacket(pos, frequency.get(), isPrivate.get()));
        });
    return builder.build();
    }

}
