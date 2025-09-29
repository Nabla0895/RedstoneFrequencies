package nabla.redstone_frequencies.component;

import nabla.redstone_frequencies.Redstone_frequencies;
import nabla.redstone_frequencies.component.custom.FrequencySettingsPayload;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModDataComponentTypes {
    public static final ComponentType<FrequencySettingsPayload> FREQUENCY_SETTINGS = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(Redstone_frequencies.MOD_ID, "frequency_settings"),
            ComponentType.<FrequencySettingsPayload>builder()
                    .codec(FrequencySettingsPayload.CODEC)
                    .packetCodec(FrequencySettingsPayload.PACKET_CODEC)
                    .build()
    );

    public static void init() {
        Redstone_frequencies.LOGGER.info("Registering Data Component Types for " + Redstone_frequencies.MOD_ID);
    }
}