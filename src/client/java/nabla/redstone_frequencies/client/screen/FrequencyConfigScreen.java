package nabla.redstone_frequencies.client.screen;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import nabla.redstone_frequencies.networking.packet.FrequencyUpdateC2SPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class FrequencyConfigScreen {
    public static Screen create(BlockPos pos, int currentFrequency) {
        ConfigBuilder builder = ConfigBuilder.create().setTitle(Text.literal("Choose Frequency"));
        ConfigCategory category = builder.getOrCreateCategory(Text.literal("Settings"));
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        category.addEntry(entryBuilder.startIntField(Text.literal("Frequency"), currentFrequency)
                .setDefaultValue(0)
                .setTooltip(Text.literal("Choose this Blocks Frequency."))
                .setSaveConsumer(newFrequency -> {
                    ClientPlayNetworking.send(new FrequencyUpdateC2SPacket(pos, newFrequency));
                })
                .build());
        builder.setSavingRunnable(() -> {

        });
    return builder.build();
    }

}
