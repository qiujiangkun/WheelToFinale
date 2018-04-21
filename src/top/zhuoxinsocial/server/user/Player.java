package top.zhuoxinsocial.server.user;


import io.netty.channel.*;
import top.zhuoxinsocial.server.room.Room;

import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Player {

    private final Map<String, Object> values = new HashMap<>();

    public int getUid() {
        return (int) get("uid");
    }

    public String getNickname() {
        return (String) get("nickname");
    }

    public String getEmail() {
        return (String) get("email");
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Player)
            return Objects.equals(get("uid"), ((Player) obj).get("uid"));
        return false;
    }

    public boolean equalPass(String password) {
        return Objects.equals(get("password"), password);
    }


    public Player bindChannel(Channel channel) {
        this.channel = channel;
        this.addr = channel.remoteAddress();
        return this;
    }

    public void enterRoom(Room room) {
        nowRoom = room;
    }

    public Room getNowRoom() {
        return nowRoom;
    }

    public Channel getChannel() {
        return channel;
    }

    public void sendMessage(String s) {
        channel.writeAndFlush(s);
    }

    public SocketAddress getAddr() {
        return addr;
    }

    private Channel channel;
    private SocketAddress addr;
    private Room nowRoom;

    public int size() {
        return values.size();
    }

    public boolean isEmpty() {
        return values.isEmpty();
    }

    public boolean containsKey(String key) {
        return values.containsKey(key);
    }

    public Object get(String key) {
        return values.get(key);
    }

    public Player set(String key, Object value) {
        values.put(key, value);
        return this;
    }

    public void clear() {
        values.clear();
    }

}
