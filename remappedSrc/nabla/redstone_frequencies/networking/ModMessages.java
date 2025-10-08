package nabla.redstone_frequencies.networking;

import nabla.redstone_frequencies.networking.packet.FrequencyUpdateC2SPacket;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class ModMessages {

    public static void registerC2SPackets() {
        PayloadTypeRegistry.playC2S().register(FrequencyUpdateC2SPacket.ID, FrequencyUpdateC2SPacket.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(FrequencyUpdateC2SPacket.ID, FrequencyUpdateC2SPacket::receive);
    }

    public static void registerS2CPackets() {
        // Leer für zukünftige Nutzung
    }
}