package cn.lanink.horsehunt.room;

import cn.lanink.gamecore.utils.SavePlayerInventory;
import cn.lanink.gamecore.utils.Tips;
import cn.lanink.horsehunt.HorseHunt;
import cn.lanink.horsehunt.tasks.WaitTask;
import cn.lanink.horsehunt.utils.Tools;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.utils.Config;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author lt_name
 */
public class Room {

    public int waitTime, gameTime;
    private int mode; //0未初始化 1等待 2游戏 3胜利结算
    private String level, waitSpawn;

    private final int setWaitTime;
    private final int setGameTime;

    @Getter
    private int minPlayers = 2;
    @Getter
    private int maxPlayers = 16;

    private final HashMap<Player, PlayerStatus> players = new HashMap<>();

    private final ArrayList<Position> randomSpawn = new ArrayList<>();

    public Player victoryPlayer;
    
    /**
     * @param config 配置文件
     */
    public Room(Config config) {
        this.setWaitTime = config.getInt("waitTime", 120);
        this.setGameTime = config.getInt("gameTime", 20);
        this.waitSpawn = config.getString("waitSpawn", null);
        this.level = config.getString("World", null);
        if (this.getLevel() == null && !Server.getInstance().loadLevel(this.level)) {
            throw new RuntimeException(this.level + " 地图加载失败");
        }
        for (String string : config.getStringList("randomSpawn")) {
            String[] s = string.split(":");
            this.randomSpawn.add(new Position(
                    Integer.parseInt(s[0]),
                    Integer.parseInt(s[1]),
                    Integer.parseInt(s[2]),
                    this.getLevel()));
        }
        this.initTime();
        this.mode = 0;
    }
    
    /**
     * 初始化task
     */
    public void initTask() {
        this.setMode(1);
        Server.getInstance().getScheduler().scheduleRepeatingTask(
                HorseHunt.getInstance(), new WaitTask(HorseHunt.getInstance(), this), 20);
    }

    /**
     * 初始化倒计时时间
     */
    protected void initTime() {
        this.waitTime = this.setWaitTime;
        this.gameTime = this.setGameTime;
    }

    public int getMode() {
        return this.mode;
    }

    /**
     * 设置房间状态
     * @param mode 状态
     */
    public void setMode(int mode) {
        this.mode = mode;
    }

    /**
     * 结束房间
     */
    public void endGame() {
        this.mode = 0;
        if (!this.players.isEmpty()) {
            Iterator<Map.Entry<Player, PlayerStatus>> it = this.players.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<Player, PlayerStatus> entry = it.next();
                it.remove();
                this.quitRoom(entry.getKey());
            }
        }
        this.initTime();
        this.victoryPlayer = null;
        Tools.cleanEntity(this.getLevel());
    }
    
    /**
     * 加入房间
     *
     * @param player 玩家
     */
    public void joinRoom(Player player) {
        if (this.players.size() < this.getMaxPlayers()) {
            if (this.mode == 0) {
                this.initTask();
            }
            this.addPlaying(player);
            Tools.rePlayerState(player, true);
            SavePlayerInventory.save(HorseHunt.getInstance(), player);
            player.teleport(this.getWaitSpawn());
            Tools.giveItem(player, 10);
            if (HorseHunt.getInstance().isHasTips()) {
                Tips.closeTipsShow(this.level, player);
            }
        }
    }

    /**
     * 退出房间
     *
     * @param player 玩家
     */
    public void quitRoom(Player player) {
        this.players.remove(player);
        if (HorseHunt.getInstance().isHasTips()) {
            Tips.removeTipsConfig(this.level, player);
        }
        if (player.riding != null) {
            player.riding.dismountEntity(player);
        }
        player.teleport(Server.getInstance().getDefaultLevel().getSafeSpawn());
        Tools.rePlayerState(player, false);
        SavePlayerInventory.restore(HorseHunt.getInstance(), player);
        HorseHunt.getInstance().getIScoreboard().closeScoreboard(player);
        player.sendMessage("§a你已退出房间");
    }

    /**
     * 获取玩家是否在房间内
     * @param player 玩家
     * @return 是否在房间
     */
    public boolean isPlaying(Player player) {
        return this.players.containsKey(player);
    }

    /**
     * 获取玩家列表
     * @return 玩家列表
     */
    public HashMap<Player, PlayerStatus> getPlayers() {
        return this.players;
    }

    /**
     * 获取玩家身份
     * @param player 玩家
     * @return 玩家身份
     */
    public PlayerStatus getPlayers(Player player) {
        return this.players.get(player);
    }
    
    /**
     * 记录在游戏内的玩家
     * @param player 玩家
     */
    public void addPlaying(Player player) {
        if (!this.players.containsKey(player)) {
            this.addPlaying(player, PlayerStatus.SURVIVE);
        }
    }
    
    /**
     * 记录在游戏内的玩家
     * @param player 玩家
     * @param mode 身份
     */
    public void addPlaying(Player player, PlayerStatus mode) {
        this.players.put(player, mode);
    }
    
    /**
     * 获取随机出生点
     * @return 随机出生点列表
     */
    public ArrayList<Position> getRandomSpawn() {
        return this.randomSpawn;
    }
    
    /**
     * 获取设置的等待时间
     * @return 等待时间
     */
    public int getSetWaitTime() {
        return this.setWaitTime;
    }

    /**
     * 获取设置的游戏时间
     * @return 游戏时间
     */
    public int getSetGameTime() {
        return this.setGameTime;
    }

    /**
     * 获取世界
     * @return 世界
     */
    public Level getLevel() {
        return Server.getInstance().getLevelByName(this.level);
    }

    /**
     * 获取等待出生点
     * @return 出生点
     */
    public Position getWaitSpawn() {
        String[] s = this.waitSpawn.split(":");
        return new Position(Integer.parseInt(s[0]),
                Integer.parseInt(s[1]),
                Integer.parseInt(s[2]),
                this.getLevel());
    }

}
