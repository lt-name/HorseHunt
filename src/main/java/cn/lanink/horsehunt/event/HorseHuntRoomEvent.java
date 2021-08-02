package cn.lanink.horsehunt.event;

import cn.lanink.horsehunt.room.Room;
import cn.nukkit.event.Event;

public abstract class HorseHuntRoomEvent extends Event {

    protected Room room;

    public HorseHuntRoomEvent() {

    }

    public Room getRoom() {
        return this.room;
    }

}
