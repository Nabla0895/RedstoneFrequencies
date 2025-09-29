package nabla.redstone_frequencies.block.entity;

import nabla.redstone_frequencies.Redstone_frequencies;
import nabla.redstone_frequencies.block.ModBlocks;
import nabla.redstone_frequencies.block.entity.custom.RedstoneTransmitterEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {
    public static final BlockEntityType<RedstoneTransmitterEntity> TRANSMITTER_BE = Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(Redstone_frequencies.MOD_ID, "transmitter_be"), FabricBlockEntityTypeBuilder.create(RedstoneTransmitterEntity::new, ModBlocks.REDSTONE_TRANSMITTER).build());

    public static void init() {
        Redstone_frequencies.LOGGER.info("Registrering Block Entities for " + Redstone_frequencies.MOD_ID);
    }
}
