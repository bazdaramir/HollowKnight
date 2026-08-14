package com.HollowKnight.model.manager;

import com.HollowKnight.model.enums.Map;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public class AudioManager {
    private static AudioManager instance;

    // menus
    private Music backgroundMusic;
    private Sound hoverSound;
    private Sound clickSound;

    //game background sounds
    private Music Forgotten_crossroads_background;
    private Music Green_path_background;
    private Music DeathPopUp;
    private Music Boss_room;
    private Music BossVictoryMusic;
    // Enemy
    private Sound crawler;
    private Sound lasershoot;
    private Sound crystallgaurdian_run;
    private Music mossfly_fly;
    private Sound huskhornhead_run;
    private Sound huskhornhead_walk;

    // Zote
    private Sound Zote_attack;
    private Sound Zote_talk_1;
    private Sound Zote_talk_2;
    private Sound Zote_talk_3;
    private Sound Zote_talk_4;
    private Sound Zote_talk_5;
    private Sound zote_get_up;
    private Sound Zote_battle_fall;

    // knight
    private Sound slash;
    private Sound enemy_damage;
    private Sound wall_hit;
    private Sound wall_break;
    private Sound jump;
    private Sound falling;
    private Sound Howling_spell;
    private Sound Spirit_spell;
    private Sound Wall_jump;
    private Sound Wall_slide;
    private Sound dash;
    private Sound Double_jump;
    private Sound knight_damage;
    private Sound fireball;
    private Sound knight_death;
    private Sound focus;
    private Sound focusdone;
    private Music run_stone;
    private Music run_grass;
    private Sound fullsoul ;

    //False_knight
    private Sound false_knight_land;
    private Sound false_knight_roll;
    private Sound false_knight_jump;
    private Sound false_knight_damage_armour_final;
    private Sound false_knight_strike_ground;

    private float masterVolume = 1.0f;
    private Preferences prefs;

    private AudioManager() {
        prefs = Gdx.app.getPreferences("HollowKnight_Settings");
        masterVolume = prefs.getInteger("master_volume", 100) / 100f;

        // menus
        backgroundMusic = loadMusic("ui/sound/backgroundsound.wav");
        if (backgroundMusic != null) backgroundMusic.setLooping(true);
        hoverSound = loadSound("ui/sound/ui_button_confirm.wav");
        clickSound = loadSound("ui/sound/button.wav");

        // game background
        Forgotten_crossroads_background = loadMusic("ui/sound/forgotten_crossroads.wav");
        if (Forgotten_crossroads_background != null) Forgotten_crossroads_background.setLooping(true);

        Green_path_background = loadMusic("ui/sound/green_path_background.wav");
        if (Green_path_background != null) Green_path_background.setLooping(true);

        DeathPopUp = loadMusic("ui/sound/cQvNiggcgntbL5GHZoKB+9vVm2onK07o.mp3");
        if (DeathPopUp != null) DeathPopUp.setLooping(true);

        Boss_room = loadMusic("ui/sound/meet_the_grahams.mp3");
        if (Boss_room != null) Boss_room.setLooping(true);

        BossVictoryMusic = loadMusic("ui/sound/we_are_the_champion.mp3");
        if (BossVictoryMusic != null) BossVictoryMusic.setLooping(false);


        //Enemy
        crawler = loadSound("ui/sound/Enemy/crawler.wav");
        lasershoot = loadSound("ui/sound/Enemy/lasershoot.wav");
        crystallgaurdian_run = loadSound("ui/sound/Enemy/crystallgaurdian_run.wav");
        mossfly_fly = loadMusic("ui/sound/Enemy/Mossfly_fly.wav");
        if (mossfly_fly != null) mossfly_fly.setLooping(true);
        huskhornhead_run = loadSound("ui/sound/Enemy/huskhornhead_attack.wav");
        huskhornhead_walk = loadSound("ui/sound/Enemy/huskhornhead_walk.wav");

        //Zote
        Zote_attack = loadSound("ui/sound/Zote/Zote_attack.wav");
        Zote_talk_1 = loadSound("ui/sound/Zote/Zote_01.wav");
        Zote_talk_2 = loadSound("ui/sound/Zote/Zote_02.wav");
        Zote_talk_3 = loadSound("ui/sound/Zote/Zote_03.wav");
        Zote_talk_4 = loadSound("ui/sound/Zote/Zote_04.wav");
        Zote_talk_5 = loadSound("ui/sound/Zote/Zote_05.wav");
        zote_get_up = loadSound("ui/sound/Zote/Zote_get_up.wav");
        Zote_battle_fall = loadSound("ui/sound/Zote/Zote_battle_fall.wav");

        //knight
        slash = loadSound("ui/sound/knight/slash.wav");
        enemy_damage = loadSound("ui/sound/knight/enemy_damage.wav");
        wall_hit = loadSound("ui/sound/breakable_wall_hit.wav");
        wall_break = loadSound("ui/sound/breakable_wall_death.wav");
        jump = loadSound("ui/sound/knight/jump.wav");
        falling = loadSound("ui/sound/knight/falling.wav");
        Howling_spell = loadSound("ui/sound/Knight/Howling_spell.wav");
        Spirit_spell = loadSound("ui/sound/Knight/Spirit_spell.wav");
        Wall_jump = loadSound("ui/sound/knight/Wall_jump.wav");
        Wall_slide = loadSound("ui/sound/Knight/Wall_slide.wav");
        dash = loadSound("ui/sound/knight/dash.wav");
        Double_jump = loadSound("ui/sound/knight/Double_jump.wav");
        knight_damage = loadSound("ui/sound/knight/knight_damage.wav");
        fireball = loadSound("ui/sound/knight/fireball.wav");
        run_grass = loadMusic("ui/sound/knight/run_grass.wav");
        if (run_grass != null) run_grass.setLooping(true);
        run_stone = loadMusic("ui/sound/knight/run_stone.wav");
        if (run_stone != null) run_stone.setLooping(true);
        knight_death = loadSound("ui/sound/knight/knight_death.wav");
        focus = loadSound("ui/sound/Knight/focus_health_charging.wav");
        focusdone = loadSound("ui/sound/Knight/focus_health_heal.wav");
        fullsoul = loadSound("ui/sound/Knight/soul_totem_awake.wav");

        //False_knight
        false_knight_land = loadSound("ui/sound/False_knight/false_knight_land.wav");
        false_knight_roll = loadSound("ui/sound/False_knight/false_knight_roll.wav");
        false_knight_jump = loadSound("ui/sound/False_knight/false_knight_jump.wav");
        false_knight_damage_armour_final = loadSound("ui/sound/False_knight/false_knight_damage_armour_final.wav");
        false_knight_strike_ground = loadSound("ui/sound/False_knight/false_knight_strike_ground.wav");
    }

    private static Sound loadSound(String path) {
        try {
            return Gdx.audio.newSound(Gdx.files.internal(path));
        } catch (Exception e) {
            Gdx.app.error("AudioManager", "Missing sound file, skipping: " + path);
            return null;
        }
    }

    private static Music loadMusic(String path) {
        try {
            return Gdx.audio.newMusic(Gdx.files.internal(path));
        } catch (Exception e) {
            Gdx.app.error("AudioManager", "Missing music file, skipping: " + path);
            return null;
        }
    }

    private static void safePlay(Sound s, float volume) {
        if (s != null) s.play(volume);
    }

    public static AudioManager getInstance() {
        if (instance == null) {
            instance = new AudioManager();
        }
        return instance;
    }
    public void playBossVictoryMusic() {
        if (DeathPopUp !=null)DeathPopUp.pause();
        if (BossVictoryMusic != null) {
            BossVictoryMusic.setVolume(masterVolume*1.4f);
            BossVictoryMusic.play();
        }
    }

    public void playBGM() {
        if (BossVictoryMusic!= null) {
            BossVictoryMusic.pause();
        }
        if (backgroundMusic == null) return;
        if (prefs.getBoolean("music_on", true)) {
            backgroundMusic.setVolume(masterVolume*1.1f);
            backgroundMusic.play();
        } else {
            backgroundMusic.pause();
        }
    }

    public void toggleMusic(boolean isOn) {
        if (Forgotten_crossroads_background != null) Forgotten_crossroads_background.pause();
        if (Green_path_background != null) Green_path_background.pause();
        if (Boss_room!=null) Boss_room.pause();
        if(DeathPopUp!=null) DeathPopUp.pause();

        if (backgroundMusic == null) return;
        if (isOn) {
            backgroundMusic.setVolume(masterVolume);
            backgroundMusic.play();
        } else {
            backgroundMusic.pause();
        }
    }

    public void pausebackGroundMusic() {
        if (backgroundMusic != null) backgroundMusic.pause();
    }
    public void  puaseDeathPopUp(){
        if (DeathPopUp!=null)
            DeathPopUp.pause();
    }

    public void PlauDeathPopUp() {
        if (Forgotten_crossroads_background != null) Forgotten_crossroads_background.pause();
        if (Green_path_background != null) Green_path_background.pause();
        if (Boss_room!=null) Boss_room.pause();
        if (DeathPopUp != null) {
            DeathPopUp.setVolume(masterVolume);
            DeathPopUp.play();
        }
    }

    public void playHoverSound() {
        if (prefs.getBoolean("sfx_on", true)) {
            safePlay(hoverSound, masterVolume * 0.1f);
        }
    }

    public void playClickSound() {
        if (prefs.getBoolean("sfx_on", true)) {
            safePlay(clickSound, masterVolume * 0.7f);
        }
    }

    public void mapSoundHandler(String mapname) {
        if (prefs.getBoolean("music_on", true)) {

            if (Forgotten_crossroads_background != null) Forgotten_crossroads_background.pause();
            if (Green_path_background != null) Green_path_background.pause();
            if (Boss_room!=null) Boss_room.pause();
            if(DeathPopUp!=null) DeathPopUp.pause();

            switch (mapname) {
                case "forgottencrossroads":
                    if (Forgotten_crossroads_background != null) {
                        Forgotten_crossroads_background.setVolume(masterVolume*1.6f);
                        Forgotten_crossroads_background.play();
                    }
                    break;
                case "greenpath":
                    if (Green_path_background != null) {
                        Green_path_background.setVolume(masterVolume*1.6f);
                        Green_path_background.play();
                    }
                    break;
                case "bossroom":
                    if (Boss_room!=null){
                    Boss_room.setVolume(masterVolume*0.3f);
                    Boss_room.play();
                }
                    break;
                default:
                    break;
            }
        }
    }

    public void False_KnightSoundHandler(String sound) {
        if (prefs.getBoolean("sfx_on", true)) {
            switch (sound) {
                case "false_knight_land":
                    safePlay(false_knight_land, masterVolume * 0.8f);
                    break;
                case "false_knight_roll":
                    safePlay(false_knight_roll, masterVolume * 0.8f);
                    break;
                case "false_knight_jump":
                    safePlay(false_knight_jump, masterVolume * 0.8f);
                    break;
                case "false_knight_damage_armour_final":
                    safePlay(false_knight_damage_armour_final, masterVolume * 0.8f);
                    break;
                case "false_knight_strike_ground":
                    safePlay(false_knight_strike_ground, masterVolume * 0.8f);
                    break;
                default:
                    break;
            }
        }
    }

    public void ZoteSoundHandler(String sound) {
        if (prefs.getBoolean("sfx_on", true)) {
            switch (sound) {
                case "attack": safePlay(Zote_attack, masterVolume * 0.8f); break;
                case "0": safePlay(Zote_talk_1, masterVolume * 0.8f); break;
                case "1": safePlay(Zote_talk_2, masterVolume * 0.8f); break;
                case "2": safePlay(Zote_talk_3, masterVolume * 0.8f); break;
                case "3": safePlay(Zote_talk_4, masterVolume * 0.8f); break;
                case "4": safePlay(Zote_talk_5, masterVolume * 0.8f); break;
                case "getup": safePlay(zote_get_up, masterVolume * 0.8f); break;
                case "fall": safePlay(Zote_battle_fall, masterVolume * 0.8f); break;
                default: break;
            }
        }
    }

    public void HuskHornHeadSoundHandler(String sound) {
        if (prefs.getBoolean("sfx_on", true)) {
            switch (sound) {
                case "walk": safePlay(huskhornhead_walk, masterVolume); break;
                case "run": safePlay(huskhornhead_run, masterVolume * 0.9f); break;
                default: break;
            }
        }
    }

    public void CrystallsSoundHandler(String sound) {
        if (prefs.getBoolean("sfx_on", true)) {
            switch (sound) {
                case "laser": safePlay(lasershoot, masterVolume * 0.99f); break;
                case "run": safePlay(crystallgaurdian_run, masterVolume * 0.99f); break;
                default: break;
            }
        }
    }
    public void FullSoulSound(){
        if (prefs.getBoolean("sfx_on", true)) {
            if (fullsoul!=null){
                safePlay(fullsoul,masterVolume * 3.2f);
            }
        }
    }

    public void MossflySoundHandler(String sound) {
        if (prefs.getBoolean("sfx_on", true)) {
            switch (sound) {
                case "fly":
                    if (mossfly_fly != null) {
                        mossfly_fly.setVolume(masterVolume*0.8f);
                        mossfly_fly.play();
                    }
                    break;
            }
        }
    }

    public void MossflyPause() {
        if (mossfly_fly != null && mossfly_fly.isPlaying()) {
            mossfly_fly.pause();
        }
    }

    public void CrawlerSoundHandler(String sound) {
        if (prefs.getBoolean("sfx_on", true)) {
            switch (sound) {
                case "run": safePlay(crawler, masterVolume*0.4f); break;
                default: break;
            }
        }
    }

    public void KnightSoundHandler(String sound, Map mapname) {
        if (prefs.getBoolean("sfx_on", true)) {
            if (run_stone != null) run_stone.pause();
            if (run_grass != null) run_grass.pause();

            switch (sound) {
                case "fireball":
                    safePlay(fireball, masterVolume * 0.8f);
                    break;
                case "run":
                    if (mapname == Map.FORGOTTEN_CROSSROADS) {
                        if (run_stone != null) {
                            run_stone.setVolume(masterVolume * 0.6f);
                            run_stone.play();
                        }
                    } else {
                        if (run_grass != null) {
                            run_grass.setVolume(masterVolume * 0.6f);
                            run_grass.play();
                        }
                    }
                    break;
                case "slash":
                    safePlay(slash, masterVolume * 0.8f);
                    break;
                case "enemy_damage":
                    safePlay(enemy_damage, masterVolume * 0.8f);
                    break;
                case "wall_hit":
                    safePlay(wall_hit, masterVolume * 0.9f);
                    break;
                case "wall_break":
                    safePlay(wall_break, masterVolume * 0.9f);
                    break;
                case "Double_jump":
                    safePlay(Double_jump, masterVolume * 0.8f);
                    break;
                case "knight_death":
                    safePlay(knight_death, masterVolume * 0.8f);
                    break;
                case "knight_damage":
                    safePlay(knight_damage, masterVolume * 0.8f);
                    break;
                case "dash":
                    safePlay(dash, masterVolume * 0.8f);
                    break;
                case "Wall_jump":
                    safePlay(Wall_jump, masterVolume * 0.8f);
                    break;
                case "Wall_slide":
                    safePlay(Wall_slide, masterVolume * 0.8f);
                    break;
                case "Spirit_spell":
                    safePlay(Spirit_spell, masterVolume * 0.8f);
                    break;
                case "Howling_spell":
                    safePlay(Howling_spell, masterVolume * 0.8f);
                    break;
                case "falling":
                    safePlay(falling, masterVolume * 0.8f);
                    break;
                case "jump":
                    safePlay(jump, masterVolume * 0.8f);
                    break;
                    case "focus":
                    if (focus != null) focus.loop(masterVolume *1.8f);
                    break;
                case "focus_stop":
                    if (focus != null) {
                        focus.stop();

                    }
                    break;
                case "focusdone":
                    if (focus != null) {safePlay(focusdone, masterVolume * 1.8f);}
                case "idle":
                    break;
                default:
                    break;
            }
        }
    }

    public void setMasterVolume(float volume) {
        this.masterVolume = volume;
        if (backgroundMusic != null) backgroundMusic.setVolume(volume);
    }
}
