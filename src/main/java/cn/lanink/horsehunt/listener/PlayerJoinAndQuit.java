package cn.lanink.horsehunt.listener;

import cn.lanink.gamecore.utils.SavePlayerInventory;
import cn.lanink.gamecore.utils.Tips;
import cn.lanink.horsehunt.HorseHunt;
import cn.lanink.horsehunt.room.Room;
import cn.lanink.horsehunt.utils.Tools;
import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.event.player.PlayerTeleportEvent;
import cn.nukkit.scheduler.Task;

import java.util.LinkedHashMap;

public class PlayerJoinAndQuit implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player != null && HorseHunt.getInstance().getRooms().containsKey(player.getLevel().getName())) {
            HorseHunt.getInstance().getServer().getScheduler().scheduleDelayedTask(HorseHunt.getInstance(), new Task() {
                @Override
                public void onRun(int i) {
                    if (player.isOnline()) {
                        if (HorseHunt.getInstance().isHasTips()) {
                            Tips.removeTipsConfig(player.getLevel().getName(), player);
                        }
                        Tools.rePlayerState(player ,false);
                        SavePlayerInventory.restore(HorseHunt.getInstance(), player);
                        player.teleport(HorseHunt.getInstance().getServer().getDefaultLevel().getSafeSpawn());
                    }
                }
            }, 1);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (player == null) {
            return;
        }
        for (Room room : HorseHunt.getInstance().getRooms().values()) {
            if (room.isPlaying(player)) {
                room.quitRoom(player);
            }
        }
    }

    @EventHandler
    public void onPlayerTp(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        String fromLevel = event.getFrom().getLevel() == null ? null : event.getFrom().getLevel().getName();
        String toLevel = event.getTo().getLevel()== null ? null : event.getTo().getLevel().getName();
        if (player == null || fromLevel == null || toLevel == null) return;
        if (!fromLevel.equals(toLevel)) {
            LinkedHashMap<String, Room> room =  HorseHunt.getInstance().getRooms();
            if (room.containsKey(fromLevel) && room.get(fromLevel).isPlaying(player)) {
                event.setCancelled(true);
                player.sendMessage("§e >> §c退出房间请使用命令！");
            }else if (!player.isOp() && room.containsKey(toLevel) &&
                    !room.get(toLevel).isPlaying(player)) {
                event.setCancelled(true);
                player.sendMessage("§e >> §c要进入游戏地图，请先加入游戏！");
            }
        }
    }

}
