package cn.lanink.horsehunt.listener;

import cn.lanink.horsehunt.HorseHunt;
import cn.lanink.horsehunt.event.HorseHuntPlayerDeathEvent;
import cn.lanink.horsehunt.room.PlayerStatus;
import cn.lanink.horsehunt.room.Room;
import cn.lanink.horsehunt.utils.Tools;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDeathEvent;
import cn.nukkit.event.entity.EntityVehicleExitEvent;
import cn.nukkit.event.player.PlayerChatEvent;
import cn.nukkit.event.player.PlayerCommandPreprocessEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.player.PlayerRespawnEvent;
import cn.nukkit.item.Item;
import cn.nukkit.nbt.tag.CompoundTag;
import nukkitcoders.mobplugin.entities.animal.walking.Horse;

import java.util.Map;
import java.util.Random;

/**
 * 游戏监听器（nk事件）
 * @author lt_name
 */
public class PlayerGameListener implements Listener {

    private final HorseHunt horseHunt;

    public PlayerGameListener(HorseHunt horseHunt) {
        this.horseHunt = horseHunt;
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Horse) {
            Horse horse = (Horse) event.getEntity();
            Room room = this.horseHunt.getRooms().get(horse.getLevel().getName());
            if (room == null) {
                return;
            }
            String name = horse.namedTag.getString("HorseHuntPlayerName");

            Server.getInstance().getPluginManager().callEvent(
                    new HorseHuntPlayerDeathEvent(room, Server.getInstance().getPlayer(name)));

            EntityDamageEvent cause = horse.getLastDamageCause();
            if (cause instanceof EntityDamageByEntityEvent) {
                EntityDamageByEntityEvent ev = (EntityDamageByEntityEvent) cause;
                if (ev.getDamager() instanceof Player) {
                    Tools.sendMessage(room, ev.getDamager().getName() + " 杀了 " + name + "的马");
                }
            }
        }
    }

    @EventHandler
    public void onEntityVehicleExit(EntityVehicleExitEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            Room room = this.horseHunt.getRooms().get(player.getLevel().getName());
            if (room == null || !room.isPlaying(player)) {
                return;
            }
            if (event.getVehicle().getHeight() > 0 &&
                    room.getMode() == 2 &&
                    room.getPlayers(player) == PlayerStatus.SURVIVE) {
                //TODO Fix
                //event.setCancelled(true);
                Server.getInstance().getScheduler().scheduleDelayedTask(this.horseHunt,
                        () -> event.getVehicle().mountEntity(player), 1);
            }
        }
    }

    /**
     * 实体受到另一实体伤害事件
     * @param event 事件
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player damager = (Player) event.getDamager();
            Room room = this.horseHunt.getRooms().get(damager.getLevel().getName());
            if (room == null || !room.isPlaying(damager)) {
                return;
            }

            if (event.getEntity() instanceof Player) {
                event.setCancelled(true);
            }
        }
    }

    /**
     * 实体受到伤害事件
     * @param event 事件
     */
    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            Room room = this.horseHunt.getRooms().get(player.getLevel().getName());
            if (room == null) return;
            if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
                if (room.getMode() == 2) {
                    Server.getInstance().getPluginManager().callEvent(new HorseHuntPlayerDeathEvent(room, player));
                }else {
                    player.teleport(room.getWaitSpawn());
                }
                event.setCancelled(true);
            }
        }
    }

    /**
     * 玩家点击事件
     * @param event 事件
     */
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Item item = event.getItem();
        if (player == null || item == null) {
            return;
        }
        Room room = this.horseHunt.getRooms().getOrDefault(player.getLevel().getName(), null);
        if (room == null || !room.isPlaying(player)) {
            return;
        }
        if (event.getAction() == PlayerInteractEvent.Action.LEFT_CLICK_BLOCK) {
            event.setCancelled(true);
            player.setAllowModifyWorld(false);
        }
        if (room.getMode() == 1) {
            if (!item.hasCompoundTag()) return;
            CompoundTag tag = item.getNamedTag();
            if (tag.getBoolean("isHorseHuntItem") && tag.getInt("HorseHuntType") == 10) {
                event.setCancelled(true);
                room.quitRoom(player);
            }
        }
    }

    /**
     * 玩家重生事件
     * @param event 事件
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        for (Room room : this.horseHunt.getRooms().values()) {
            if (room.isPlaying(player)) {
                if (room.getMode() == 2) {
                    event.setRespawnPosition(room.getRandomSpawn().get(new Random().nextInt(room.getRandomSpawn().size())));
                }else {
                    event.setRespawnPosition(room.getWaitSpawn());
                }
                break;
            }
        }
    }

    /**
     * 玩家执行命令事件
     * @param event 事件
     */
    @EventHandler
    public void onCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (player == null || event.getMessage() == null) return;
        Room room = this.horseHunt.getRooms().get(player.getLevel().getName());
        if (room == null || !room.isPlaying(player)) {
            return;
        }
        if (event.getMessage().startsWith(this.horseHunt.getCmdUser(), 1) ||
                event.getMessage().startsWith(this.horseHunt.getCmdAdmin(), 1)) {
            return;
        }
        event.setCancelled(true);
        player.sendMessage("§e >> §c游戏中无法使用其他命令");
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(PlayerChatEvent event) {
        Player player = event.getPlayer();
        if (player == null || event.getMessage() == null) return;
        Room room = this.horseHunt.getRooms().getOrDefault(player.getLevel().getName(), null);
        if (room == null || !room.isPlaying(player)) {
            for (Room r : this.horseHunt.getRooms().values()) {
                for (Player p : r.getPlayers().keySet()) {
                    event.getRecipients().remove(p);
                }
            }
            return;
        }
        if (room.getPlayers(player) == PlayerStatus.DEATH) {
            String message = "§7[§cDeath§7]§r " + player.getName() + " §b>>>§r " + event.getMessage();
            for (Map.Entry<Player, PlayerStatus> entry : room.getPlayers().entrySet()) {
                if (entry.getValue() == PlayerStatus.DEATH) {
                    entry.getKey().sendMessage(message);
                }
            }
        }else {
            String message = "§7[§aRoom§7]§r " + player.getName() + " §b>>>§r " + event.getMessage();
            room.getPlayers().keySet().forEach(p -> p.sendMessage(message));
        }
        event.setMessage("");
        event.setCancelled(true);
    }

}
