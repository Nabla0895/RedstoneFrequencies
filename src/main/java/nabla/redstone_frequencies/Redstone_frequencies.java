package nabla.redstone_frequencies;

import nabla.redstone_frequencies.block.ModBlocks;
import nabla.redstone_frequencies.block.entity.ModBlockEntities;
import nabla.redstone_frequencies.component.ModDataComponentTypes;
import nabla.redstone_frequencies.item.ModItems;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Redstone_frequencies implements ModInitializer {
    public static final String MOD_ID = "redstone_frequencies";


    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {

        ModBlocks.init();
        ModItems.init();
        ModBlockEntities.init();
        ModDataComponentTypes.init();
        LOGGER.info("If you can read this, " + MOD_ID + " is loaded successfully! Have fun!");

    }
}