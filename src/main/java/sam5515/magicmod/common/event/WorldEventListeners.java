package sam5515.magicmod.common.event;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import sam5515.magicmod.common.lib.MagicUserManager;
import sam5515.magicmod.common.network.MMNetwork;
import sam5515.magicmod.common.network.UpdateManaMessage;

public class WorldEventListeners {
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.ServerTickEvent event){
        if (event.phase == TickEvent.Phase.START){
            MagicUserManager.forEach((playerID, magicUser) -> {
                ServerPlayer player = event.getServer().getPlayerList().getPlayer(playerID);
                if (magicUser.getRechargeRate() > 0){
                    magicUser.attemptRecharge();
                    if (magicUser.hasBeenModified()){
                        MMNetwork.sendToClient(player, new UpdateManaMessage(magicUser));
                    }
                }
            });

        }
    }
}
