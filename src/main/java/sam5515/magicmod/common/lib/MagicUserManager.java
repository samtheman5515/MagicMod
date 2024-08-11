package sam5515.magicmod.common.lib;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import sam5515.magicmod.MagicMod;
import sam5515.magicmod.common.api.MagicUser;
import sam5515.magicmod.common.network.MMNetwork;
import sam5515.magicmod.common.network.SyncMagicUserMessage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;

public class MagicUserManager {
    private static final Map<UUID, MagicUser> users = new HashMap<>();
    public static MagicUser getUser(UUID playerID){
        return users.get(playerID);
    }
    public static MagicUser getUser(Player player){
        return getUser(player.getUUID());
    }
    public static void forEach(BiConsumer<UUID, MagicUser> consumer){
        users.forEach(consumer);
    }
    public static void sync(UUID playerID, MagicUser magicUser){
        users.put(playerID, magicUser);
    }
    public static void register(IEventBus bus){
        bus.addListener(MagicUserManager::onServerStopping);
        bus.addListener(MagicUserManager::onServerStarting);
        bus.addListener(MagicUserManager::onPlayerLoggedIn);
    }
    private static void save(Path outputDir){
        users.forEach((uuid, magicUser) -> {
            Path userDataPath = outputDir.resolve(uuid.toString() + ".dat");
            CompoundTag nbt = magicUser.serializeNBT();
            try{
                NbtIo.write(nbt, userDataPath.toFile());
            } catch(IOException e){
                MagicMod.LOGGER.warn("Could not save user data " + userDataPath, e);
            }
        });
    }
    private static void loadMagicUserData(Path userDataPath){
        String fileName = userDataPath.getFileName().toString();
        int index = fileName.indexOf('.');
        if (index != -1){
            String playerIDstr = fileName.substring(0, index);
            String fileExt = fileName.substring(index);
            if (fileExt.equals(".dat")){
                try{
                    UUID playerID = UUID.fromString(playerIDstr);
                    CompoundTag nbt = NbtIo.read(userDataPath.toFile());
                    MagicUser magicUser = new MagicUser();
                    magicUser.deserializeNBT(nbt);
                    users.put(playerID, magicUser);
                }catch (IllegalArgumentException e){
                    MagicMod.LOGGER.warn("Invalid file name " + fileName, e);
                }catch (IOException e){
                    MagicMod.LOGGER.warn("Could not read user data file " + fileName, e);
                }
            }
        }
    }
    private static final LevelResource magicUserDataDir = new LevelResource("magicuserdata");
    private static void onServerStarting(ServerStartingEvent event){
        Path dataDir = event.getServer().getWorldPath(magicUserDataDir);
        try{
            Files.createDirectories(dataDir);
            users.clear();
            try(var stream = Files.walk(dataDir, 1)){
                stream.forEach(MagicUserManager::loadMagicUserData);
            }
        } catch (IOException e){
            MagicMod.LOGGER.warn("Could not load user data", e);
        }
    }
    private static void onServerStopping(ServerStoppingEvent event){
        Path dataDir = event.getServer().getWorldPath(magicUserDataDir);
        try{
            Files.createDirectories(dataDir);
            save(dataDir);
        } catch (IOException e){
            MagicMod.LOGGER.warn("Could not save user data", e);
        }
    }
    private static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event){
        MagicUser magicUser= users.computeIfAbsent(event.getEntity().getUUID(), playerID -> new MagicUser());
        MMNetwork.sendToClient((ServerPlayer) event.getEntity(), new SyncMagicUserMessage(magicUser));
    }
}
