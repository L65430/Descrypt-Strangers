package com.example.administrator.finalprocject.Info;

/**
 * Created by L on 2016/11/9.
 */
public class Gameinfo {
    String bestcode;
    String gamename;
    int gameicon;

    public Gameinfo(String bestcode,String gamename,int gameicon)
    {
        this.bestcode=bestcode;
        this.gamename=gamename;
        this.gameicon=gameicon;
    }

    public String getbestcode()
    {
        return bestcode;
    }
    public String getGamename()
    {
        return gamename;
    }
    public int getGameicon()
    {
        return gameicon;
    }

    public void setbestcode(String bestcode)
    {
        this.bestcode=bestcode;
    }

    public void setGamename(String gamename)
    {
        this.gamename=gamename;
    }

    public void setGameicon(int gameicon)
    {
        this.gameicon=gameicon;
    }

}
