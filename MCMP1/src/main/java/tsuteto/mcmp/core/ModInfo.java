package tsuteto.mcmp.core;

import cpw.mods.fml.common.ModMetadata;

public class ModInfo
{
    public static void load(ModMetadata meta)
    {
        meta.modId = Mcmp1Core.modId;
        meta.name = "MCMP-1";
        meta.description = "音楽プレーヤーMCMP-1 (MineCraft Music Player)\n\n" +
                "レコーダーで好きな音源をカセットにダビングしてプレーヤーで聴くスタイルの音楽プレーヤーシリーズ。\nだだっ広いマインクラフトの世界、音楽を持ち歩こう。\n\n" +
                "A music player series allows you to dub the music with the recorder and listen with the player in Minecraft.\nGo out and carry your music around in the wide world.";
        meta.version = "2.0";
        meta.credits = "MP3 decoder: JLayer by javazoom, AAC decoder: JAAD by in-somnia";
        meta.logoFile = "assets/mcmp1/logo.png";
        meta.url = "http://forum.minecraftuser.jp/viewtopic.php?f=13&t=1014";
        meta.updateUrl = "https://dl.dropboxusercontent.com/u/14577828/mcmod/update/mcmp1.json";
        meta.authorList.add("つてと (Tsuteto)");

        meta.autogenerated = false;
    }
}
