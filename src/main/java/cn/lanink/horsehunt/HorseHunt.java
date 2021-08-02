package cn.lanink.horsehunt;

import cn.lanink.gamecore.scoreboard.ScoreboardUtil;
import cn.lanink.gamecore.scoreboard.base.IScoreboard;
import cn.lanink.horsehunt.command.AdminCommand;
import cn.lanink.horsehunt.command.UserCommand;
import cn.lanink.horsehunt.listener.HorseHuntListener;
import cn.lanink.horsehunt.listener.PlayerGameListener;
import cn.lanink.horsehunt.listener.PlayerJoinAndQuit;
import cn.lanink.horsehunt.listener.RoomLevelProtection;
import cn.lanink.horsehunt.room.Room;
import cn.lanink.horsehunt.ui.GuiListener;
import cn.lanink.horsehunt.utils.MetricsLite;
import cn.nukkit.level.Level;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;

import java.io.File;
import java.util.*;

/**
 * HotPotato
 * @author lt_name
 */
public class HorseHunt extends PluginBase {

    public static final String VERSION = "?";
    public static boolean debug = false;
    public static final SplittableRandom RANDOM = new SplittableRandom();
    private static HorseHunt horseHunt;

    private Config config;
    
    private final HashMap<String, Config> roomConfigs = new HashMap<>();
    private final LinkedHashMap<String, Room> rooms = new LinkedHashMap<>();
    
    private String cmdUser;
    private String cmdAdmin;
    
    private IScoreboard iScoreboard;
    
    private boolean hasTips = false;

    public static HorseHunt getInstance() {
        return horseHunt;
    }
    
    @Override
    public void onLoad() {
        horseHunt = this;
    
        File file1 = new File(this.getDataFolder() + "/Rooms");
        File file2 = new File(this.getDataFolder() + "/PlayerInventory");
        if (!file1.exists() && !file1.mkdirs()) {
            this.getLogger().error("Rooms 文件夹初始化失败");
        }
        if (!file2.exists() && !file2.mkdirs()) {
            this.getLogger().error("PlayerInventory 文件夹初始化失败");
        }
        
        this.saveDefaultConfig();
        this.config = new Config(getDataFolder() + "/config.yml", 2);
    }
    
    @Override
    public void onEnable() {
        this.getLogger().info("§e插件开始加载！本插件是免费哒~如果你花钱了，那一定是被骗了~");
        this.getLogger().info("此插件创意来自： 君邪 ");
        this.getLogger().info("§l§e版本: " + VERSION);
        
        //加载计分板
        this.iScoreboard = ScoreboardUtil.getScoreboard();

        //检查Tips
        try {
            Class.forName("tip.Main");
            this.hasTips = true;
        } catch (Exception ignored) {

        }
        
        this.cmdUser = this.config.getString("插件命令", "HorseHunt");
        this.cmdAdmin = this.config.getString("管理命令", "HorseHuntAdmin");
        this.getServer().getCommandMap().register("", new UserCommand(this.cmdUser));
        this.getServer().getCommandMap().register("", new AdminCommand(this.cmdAdmin));
    
        this.getServer().getPluginManager().registerEvents(new PlayerJoinAndQuit(), this);
        this.getServer().getPluginManager().registerEvents(new RoomLevelProtection(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerGameListener(this), this);
        this.getServer().getPluginManager().registerEvents(new HorseHuntListener(this), this);
        this.getServer().getPluginManager().registerEvents(new GuiListener(this), this);

        this.loadRooms();
        
        try {
            new MetricsLite(this, 12309);
        } catch (Exception ignored) {
        
        }
        this.getLogger().info("§e插件加载完成！欢迎使用！");
    }
    
    @Override
    public void onDisable() {
        this.unloadRooms();
        this.getLogger().info("§c插件卸载完成！");
    }

    public IScoreboard getIScoreboard() {
        return this.iScoreboard;
    }

    public boolean isHasTips() {
        return this.hasTips;
    }

    @Override
    public Config getConfig() {
        return this.config;
    }

    public LinkedHashMap<String, Room> getRooms() {
        return this.rooms;
    }

    public Config getRoomConfig(Level level) {
        return this.getRoomConfig(level.getName());
    }

    public Config getRoomConfig(String level) {
        if (this.roomConfigs.containsKey(level)) {
            return this.roomConfigs.get(level);
        }
        Config config = new Config(getDataFolder() + "/Rooms/" + level + ".yml", 2);
        this.roomConfigs.put(level, config);
        return config;
    }

    /**
     * 加载所有房间
     */
    private void loadRooms() {
        this.getLogger().info("§e开始加载房间");
        File[] s = new File(getDataFolder() + "/Rooms").listFiles();
        if (s != null) {
            for (File file1 : s) {
                String[] fileName = file1.getName().split("\\.");
                if (fileName.length > 0) {
                    Config config = getRoomConfig(fileName[0]);
                    if (config.getInt("waitTime", 0) == 0 ||
                            config.getInt("gameTime", 0) == 0 ||
                            config.getString("waitSpawn", "").trim().equals("") ||
                            config.getStringList("randomSpawn").size() == 0 ||
                            config.getString("World", "").trim().equals("")) {
                        this.getLogger().warning("§c房间：" + fileName[0] + " 配置不完整，加载失败！");
                        continue;
                    }
                    Room room = new Room(config);
                    this.rooms.put(fileName[0], room);
                    this.getLogger().info("§a房间：" + fileName[0] + " 已加载！");
                }
            }
        }
        this.getLogger().info("§e房间加载完成！当前已加载 " + this.rooms.size() + " 个房间！");
    }

    /**
     * 卸载所有房间
     */
    public void unloadRooms() {
        if (!this.rooms.isEmpty()) {
            Iterator<Map.Entry<String, Room>> it = this.rooms.entrySet().iterator();
            while(it.hasNext()){
                Map.Entry<String, Room> entry = it.next();
                entry.getValue().endGame();
                this.getLogger().info("§c房间：" + entry.getKey() + " 已卸载！");
                it.remove();
            }
        }
        this.rooms.clear();
        this.roomConfigs.clear();
    }

    /**
     * 重载所有房间
     */
    public void reLoadRooms() {
        this.unloadRooms();
        this.loadRooms();
    }

    public String getCmdUser() {
        return this.cmdUser;
    }

    public String getCmdAdmin() {
        return this.cmdAdmin;
    }

}
