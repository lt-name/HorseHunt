package cn.lanink.horsehunt.ui;

import cn.lanink.horsehunt.HorseHunt;
import cn.lanink.horsehunt.room.Room;
import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.element.ElementButtonImageData;
import cn.nukkit.form.element.ElementInput;
import cn.nukkit.form.window.FormWindow;
import cn.nukkit.form.window.FormWindowCustom;
import cn.nukkit.form.window.FormWindowModal;
import cn.nukkit.form.window.FormWindowSimple;

import java.util.HashMap;
import java.util.Map;


public class GuiCreate {

    public static final String PLUGIN_NAME = "§l§7[§1H§2o§3r§4s§5e§6H§au§cn§bt§7]";
    public static final HashMap<Player, HashMap<Integer, GuiType>> UI_CACHE = new HashMap<>();

    /**
     * 显示用户菜单
     * @param player 玩家
     */
    public static void sendUserMenu(Player player) {
        FormWindowSimple simple = new FormWindowSimple(PLUGIN_NAME, "");
        simple.addButton(new ElementButton("§e随机加入房间", new ElementButtonImageData("path", "textures/ui/switch_start_button")));
        simple.addButton(new ElementButton("§e退出当前房间", new ElementButtonImageData("path", "textures/ui/switch_select_button")));
        simple.addButton(new ElementButton("§e查看房间列表", new ElementButtonImageData("path", "textures/ui/servers")));
        showFormWindow(player, simple, GuiType.USER_MENU);
    }

    /**
     * 显示管理菜单
     * @param player 玩家
     */
    public static void sendAdminMenu(Player player) {
        FormWindowSimple simple = new FormWindowSimple(PLUGIN_NAME, "当前设置地图：" + player.getLevel().getName());
        simple.addButton(new ElementButton("§e设置等待出生点", new ElementButtonImageData("path", "textures/ui/World")));
        simple.addButton(new ElementButton("§e添加随机出生点", new ElementButtonImageData("path", "textures/ui/World")));
        simple.addButton(new ElementButton("§e设置时间参数", new ElementButtonImageData("path", "textures/ui/timer")));
        simple.addButton(new ElementButton("§e重载所有房间",  new ElementButtonImageData("path", "textures/ui/refresh_light")));
        simple.addButton(new ElementButton("§c卸载所有房间", new ElementButtonImageData("path", "textures/ui/redX1")));
        showFormWindow(player, simple, GuiType.ADMIN_MENU);
    }

    /**
     * 显示设置时间菜单
     * @param player 玩家
     */
    public static void sendAdminTimeMenu(Player player) {
        FormWindowCustom custom = new FormWindowCustom(PLUGIN_NAME);
        custom.addElement(new ElementInput("等待时间（秒）", "", "60"));
        custom.addElement(new ElementInput("游戏时间（秒）", "", "20"));
        showFormWindow(player, custom, GuiType.ADMIN_TIME_MENU);
    }

    /**
     * 显示房间列表菜单
     * @param player 玩家
     */
    public static void sendRoomListMenu(Player player) {
        FormWindowSimple simple = new FormWindowSimple(PLUGIN_NAME, "");
        for (Map.Entry<String, Room> entry : HorseHunt.getInstance().getRooms().entrySet()) {
            simple.addButton(new ElementButton("§e" + entry.getKey() +
                    "\nPlayer: " + entry.getValue().getPlayers().size() + "/" + entry.getValue().getMaxPlayers(),
                    new ElementButtonImageData("path", "textures/ui/switch_start_button")));
        }
        simple.addButton(new ElementButton("返回", new ElementButtonImageData("path", "textures/ui/cancel")));
        showFormWindow(player, simple, GuiType.ROOM_LIST_MENU);
    }

    /**
     * 加入房间确认(自选)
     * @param player 玩家
     */
    public static void sendRoomJoinOkMenu(Player player, String roomName) {
        if (HorseHunt.getInstance().getRooms().containsKey(roomName.replace("§e", "").trim())) {
            Room room = HorseHunt.getInstance().getRooms().get(roomName.replace("§e", "").trim());
            if (room.getMode() == 2 || room.getMode() == 3) {
                FormWindowModal modal = new FormWindowModal(
                        PLUGIN_NAME, "§a该房间正在游戏中，请稍后", "返回", "返回");
                showFormWindow(player, modal, GuiType.ROOM_JOIN_OK);
            }else if (room.getPlayers().size() >= room.getMaxPlayers()) {
                FormWindowModal modal = new FormWindowModal(
                        PLUGIN_NAME, "§a该房间已满人，请稍后", "返回", "返回");
                showFormWindow(player, modal, GuiType.ROOM_JOIN_OK);
            }else {
                FormWindowModal modal = new FormWindowModal(
                        PLUGIN_NAME, "§l§a确认要加入房间: %name% §l§a？".replace("%name%", "\"" + roomName + "\""), "确定", "返回");
                showFormWindow(player, modal, GuiType.ROOM_JOIN_OK);
            }
        }else {
            FormWindowModal modal = new FormWindowModal(
                    PLUGIN_NAME, "§a该房间不存在！", "返回", "返回");
            showFormWindow(player, modal, GuiType.ROOM_JOIN_OK);
        }
    }

    public static void showFormWindow(Player player, FormWindow window, GuiType guiType) {
        UI_CACHE.computeIfAbsent(player, obj -> new HashMap<>()).put(player.showFormWindow(window), guiType);
    }

}
