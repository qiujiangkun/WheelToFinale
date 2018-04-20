package top.zhuoxinsocial.server.game;

import io.netty.channel.Channel;
import top.zhuoxinsocial.server.user.Player;

public class UserAction implements GameImp {

    private GameLogic gameLogic;

    public UserAction(GameLogic gameLogic) {
        this.gameLogic = gameLogic;
    }
    @Override
    public void Action(Channel incoming, String s) {
        Player u = gameLogic.getPlayers().getPlayer("Addr", incoming.remoteAddress());
        if (u.isNull())
            return;
        gameLogic.receiveMessage(u, s);
    }

    @Override
    public String getMethodName() {
        return null;
    }
}