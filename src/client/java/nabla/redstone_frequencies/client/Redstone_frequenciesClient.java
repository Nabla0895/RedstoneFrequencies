package nabla.redstone_frequencies.client;

import nabla.redstone_frequencies.client.event.ClientBlockUseHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;

public class Redstone_frequenciesClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        UseBlockCallback.EVENT.register(new ClientBlockUseHandler());
    }
}
