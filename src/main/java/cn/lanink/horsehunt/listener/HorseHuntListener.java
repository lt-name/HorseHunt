package cn.lanink.horsehunt.listener;

import cn.lanink.horsehunt.HorseHunt;
import cn.lanink.horsehunt.event.HorseHuntPlayerDeathEvent;
import cn.lanink.horsehunt.event.HorseHuntRoomEndEvent;
import cn.lanink.horsehunt.event.HorseHuntRoomStartEvent;
import cn.lanink.horsehunt.room.PlayerStatus;
import cn.lanink.horsehunt.room.Room;
import cn.lanink.horsehunt.tasks.game.TimeTask;
import cn.lanink.horsehunt.tasks.game.TipsTask;
import cn.lanink.horsehunt.utils.Tools;
import cn.nukkit.AdventureSettings;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.inventory.PlayerInventory;
import cn.nukkit.item.Item;
import cn.nukkit.level.Sound;
import cn.nukkit.level.particle.HugeExplodeSeedParticle;
import cn.nukkit.nbt.tag.CompoundTag;
import nukkitcoders.mobplugin.entities.animal.walking.Horse;

import java.util.Map;


/**
 * 游戏监听器（插件事件）
 * @author lt_name
 */
public class HorseHuntListener implements Listener {

    private final HorseHunt horseHunt;

    public HorseHuntListener(HorseHunt horseHunt) {
        this.horseHunt = horseHunt;
    }

    /**
     * 房间开始事件
     * @param event 事件
     */
    @EventHandler
    public void onRoomStart(HorseHuntRoomStartEvent event) {
        Room room = event.getRoom();
        int x = 0;
        for (Map.Entry<Player, PlayerStatus> entry : room.getPlayers().entrySet()) {
            Tools.rePlayerState(entry.getKey(), true);
            PlayerInventory inventory = entry.getKey().getInventory();
            inventory.clearAll();
            entry.getKey().getUIInventory().clearAll();
            if (x >= room.getRandomSpawn().size()) {
                x = 0;
            }
            entry.getKey().teleport(room.getRandomSpawn().get(x));

            inventory.addItem(
                    Item.get(276),
                    Item.get(261),
                    Item.get(262, 0, 64)
            );

            CompoundTag nbt = Entity.getDefaultNBT(entry.getKey());
            nbt.putString("HorseHuntPlayerName", entry.getKey().getName());
            Horse horse = new Horse(entry.getKey().chunk, nbt);
            horse.spawnToAll();
            horse.setSaddled(true);
            Server.getInstance().getScheduler().scheduleDelayedTask(this.horseHunt,
                    () -> horse.mountEntity(entry.getKey()), 1);
            x++;
        }
        room.setMode(2);
        Server.getInstance().getScheduler().scheduleRepeatingTask(
                this.horseHunt, new TimeTask(this.horseHunt, room), 20);
        Server.getInstance().getScheduler().scheduleRepeatingTask(
                this.horseHunt, new TipsTask(this.horseHunt, room), 18, true);
    }

    @EventHandler
    public void onRoomEnd(HorseHuntRoomEndEvent event) {
        Room room = event.getRoom();
        if (room.getPlayers().size() > 0) {
            for (Player player : room.getPlayers().keySet()) {
                if (player == room.victoryPlayer) {
                    Tools.cmd(player, this.horseHunt.getConfig().getStringList("胜利执行命令"));
                }else {
                    Tools.cmd(player, this.horseHunt.getConfig().getStringList("失败执行命令"));
                }
            }
        }
    }

    /**
     * 玩家死亡事件（游戏中死亡）
     * @param event 事件
     */
    @EventHandler(ignoreCancelled = true)
    public void onPlayerDeath(HorseHuntPlayerDeathEvent event) {
        Player player = event.getPlayer();
        Room room = event.getRoom();
        player.getInventory().clearAll();
        player.setAllowModifyWorld(true);
        player.setAdventureSettings((new AdventureSettings(player)).set(AdventureSettings.Type.ALLOW_FLIGHT, true));
        player.setGamemode(3);
        room.addPlaying(player, PlayerStatus.DEATH);
        Tools.setPlayerInvisible(player, true);
        player.getLevel().addParticle(new HugeExplodeSeedParticle(player));
        Tools.playSound(room, Sound.RANDOM_EXPLODE);
    }

}
