package nabla.redstone_frequencies.block;

import nabla.redstone_frequencies.Redstone_frequencies;
import nabla.redstone_frequencies.block.custom.RedstoneReceiver;
import nabla.redstone_frequencies.block.custom.RedstoneTransmitter;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public class ModBlocks {

    public static void init() {
        Redstone_frequencies.LOGGER.info("Registrering Mod Blocks for " + Redstone_frequencies.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register(entries -> {
            entries.add(ModBlocks.REDSTONE_TRANSMITTER);
            entries.add(ModBlocks.REDSTONE_RECEIVER);
        });

    }

    private static Block register(String name, Function<AbstractBlock.Settings, Block> blockFactory, AbstractBlock.Settings settings, boolean shouldRegisterItem) {

        RegistryKey<Block> blockKey = keyOfBlock(name);

        Block block = blockFactory.apply(settings.registryKey(blockKey));


        if (shouldRegisterItem) {

            RegistryKey<Item> itemKey = keyOfItem(name);

            BlockItem blockItem = new BlockItem(block, new Item.Settings().registryKey(itemKey).useBlockPrefixedTranslationKey());
            Registry.register(Registries.ITEM, itemKey, blockItem);
        }

        return Registry.register(Registries.BLOCK, blockKey, block);
    }

    private static RegistryKey<Block> keyOfBlock(String name) {
        return RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(Redstone_frequencies.MOD_ID, name));
    }

    private static RegistryKey<Item> keyOfItem(String name) {
        return RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Redstone_frequencies.MOD_ID, name));
    }

    public static final Block REDSTONE_TRANSMITTER = register(
            "redstone_transmitter",
            RedstoneTransmitter::new,
            AbstractBlock.Settings.create().sounds(BlockSoundGroup.STONE).strength(0.3F, 4.5F),
            true
    );

    public static final Block REDSTONE_RECEIVER = register(
            "redstone_receiver",
            RedstoneReceiver::new,
            AbstractBlock.Settings.create().sounds(BlockSoundGroup.STONE).strength(0.3F, 4.5F),
            true
    );
}
