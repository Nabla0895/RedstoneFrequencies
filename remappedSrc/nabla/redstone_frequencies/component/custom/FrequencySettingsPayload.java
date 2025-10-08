package nabla.redstone_frequencies.component.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public record FrequencySettingsPayload(int frequency, boolean isPrivate) {
    public static final Codec<FrequencySettingsPayload> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("frequency").forGetter(FrequencySettingsPayload::frequency),
                    Codec.BOOL.fieldOf("is_private").forGetter(FrequencySettingsPayload::isPrivate)
            ).apply(instance, FrequencySettingsPayload::new)
    );

    public static final PacketCodec<ByteBuf, FrequencySettingsPayload> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.VAR_INT, FrequencySettingsPayload::frequency,
            PacketCodecs.BOOLEAN, FrequencySettingsPayload::isPrivate,
            FrequencySettingsPayload::new
    );
}