package cn.lanink.horsehunt.utils;

import cn.lanink.horsehunt.HorseHunt;
import cn.lanink.horsehunt.room.Room;
import cn.nukkit.AdventureSettings;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.command.ConsoleCommandSender;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityFirework;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemFirework;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.level.Sound;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.PlaySoundPacket;
import cn.nukkit.utils.DyeColor;
import org.jetbrains.annotations.NotNull;

import java.util.List;


public class Tools {

    public static void sendMessage(@NotNull Room room, String message) {
        for (Player player : room.getPlayers().keySet()) {
            player.sendMessage(message);
        }
    }

    public static void cmd(@NotNull Player player, @NotNull List<String> cmds) {
        if (cmds.isEmpty()) {
            return;
        }
        for (String s : cmds) {
            String[] cmd = s.split("&");
            if ((cmd.length > 1) && (cmd[1].equals("con"))) {
                Server.getInstance().dispatchCommand(new ConsoleCommandSender(), cmd[0].replace("@p", player.getName()));
            } else {
                Server.getInstance().dispatchCommand(player, cmd[0].replace("@p", player.getName()));
            }
        }
    }

    public static void giveItem(Player player, int i) {
        switch (i) {
            case 10:
                Item item = Item.get(324, 0, 1);
                item.setNamedTag(new CompoundTag()
                        .putBoolean("isHorseHuntItem", true)
                        .putInt("HorseHuntType", 10));
                item.setCustomName("退出房间\n手持点击即可退出房间");
                player.getInventory().setItem(8, item);
                break;
            //TODO
        }
    }

    /**
     * 设置玩家是否隐身
     *
     * @param player 玩家
     * @param invisible 是否隐身
     */
    public static void setPlayerInvisible(Player player, boolean invisible) {
        player.setDataFlag(0, 5, invisible);
    }

    /**
     * 重置玩家状态
     *
     * @param player 玩家
     * @param joinRoom 是否为加入房间
     */
    public static void rePlayerState(Player player, boolean joinRoom) {
        player.removeAllEffects();
        player.setHealth(player.getMaxHealth());
        player.getFoodData().setLevel(player.getFoodData().getMaxLevel());
        player.setNameTag(player.getName());
        player.setGamemode(0);
        if (joinRoom) {
            player.setAllowModifyWorld(false);
        }else {
            setPlayerInvisible(player, false);
        }
        player.getAdventureSettings().set(AdventureSettings.Type.FLYING, false);
        player.getAdventureSettings().set(AdventureSettings.Type.ALLOW_FLIGHT, false);
        player.getAdventureSettings().update();
    }

    /**
     * 播放声音
     *
     * @param room 房间
     * @param sound 声音
     */
    public static void playSound(@NotNull Room room, @NotNull Sound sound) {
        for (Player player : room.getPlayers().keySet()) {
            playSound(player, sound);
        }
    }
    
    /**
     * 播放声音
     *
     * @param player 玩家
     * @param sound 声音
     */
    public static void playSound(@NotNull Player player, @NotNull Sound sound) {
        PlaySoundPacket packet = new PlaySoundPacket();
        packet.name = sound.getSound();
        packet.volume = 1.0F;
        packet.pitch = 1.0F;
        packet.x = player.getFloorX();
        packet.y = player.getFloorY();
        packet.z = player.getFloorZ();
        player.dataPacket(packet);
    }

    /**
     * 清理实体
     * @param level 世界
     */
    public static void cleanEntity(@NotNull Level level) {
        if (level.getEntities() != null && level.getEntities().length > 0) {
            for (Entity entity : level.getEntities()) {
                if (!(entity instanceof Player)) {
                    entity.close();
                }
            }
        }
    }

    /**
     * 获取底部 Y
     *
     * @param player 玩家
     * @return Y
     */
    public static double getFloorY(@NotNull Player player) {
        for (int y = 0; y < 10; y++) {
            Level level = player.getLevel();
            Block block = level.getBlock(player.getFloorX(), player.getFloorY() - y, player.getFloorZ());
            if (block.getId() != 0) {
                if (block.getBoundingBox() != null) {
                    return block.getBoundingBox().getMaxY() + 0.2;
                }
                return block.getMinY() + 0.2;
            }
        }
        return player.getFloorY();
    }

    /**
     * 生成随机烟花
     * GitHub：https://github.com/PetteriM1/FireworkShow
     *
     * @param position 位置
     */
    public static void spawnFirework(@NotNull Position position) {
        Level level = position.getLevel();
        ItemFirework item = new ItemFirework();
        CompoundTag tag = new CompoundTag();
        CompoundTag ex = new CompoundTag();
        ex.putByteArray("FireworkColor",new byte[]{
                (byte) DyeColor.values()[HorseHunt.RANDOM.nextInt(ItemFirework.FireworkExplosion.ExplosionType.values().length)].getDyeData()
        });
        ex.putByteArray("FireworkFade",new byte[0]);
        ex.putBoolean("FireworkFlicker",HorseHunt.RANDOM.nextBoolean());
        ex.putBoolean("FireworkTrail",HorseHunt.RANDOM.nextBoolean());
        ex.putByte("FireworkType",ItemFirework.FireworkExplosion.ExplosionType.values()
                [HorseHunt.RANDOM.nextInt(ItemFirework.FireworkExplosion.ExplosionType.values().length)].ordinal());
        tag.putCompound("Fireworks",(new CompoundTag("Fireworks")).putList(new ListTag<CompoundTag>("Explosions").add(ex)).putByte("Flight",1));
        item.setNamedTag(tag);
        CompoundTag nbt = new CompoundTag();
        nbt.putList(new ListTag<DoubleTag>("Pos")
                .add(new DoubleTag("",position.x+0.5D))
                .add(new DoubleTag("",position.y+0.5D))
                .add(new DoubleTag("",position.z+0.5D))
        );
        nbt.putList(new ListTag<DoubleTag>("Motion")
                .add(new DoubleTag("",0.0D))
                .add(new DoubleTag("",0.0D))
                .add(new DoubleTag("",0.0D))
        );
        nbt.putList(new ListTag<FloatTag>("Rotation")
                .add(new FloatTag("",0.0F))
                .add(new FloatTag("",0.0F))

        );
        nbt.putCompound("FireworkItem", NBTIO.putItemHelper(item));
        EntityFirework entity = new EntityFirework(level.getChunk((int)position.x >> 4, (int)position.z >> 4), nbt);
        entity.spawnToAll();
    }

}
