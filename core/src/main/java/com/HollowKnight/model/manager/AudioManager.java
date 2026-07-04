package com.HollowKnight.model.manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public class AudioManager {
    private static AudioManager instance;
    // همه صدا های داخل بازی مونده
    // ولی سختی زیادی نداره صرفا باید لود بشه و وقتی که نیاز شد یبار پلی بشه
    // سخت نیست یکم زمان شاید ببره
    // میزارم اخرش همه چی تموم شد صدا هارو اضافه میکنم

    private Music backgroundMusic;
    private Sound hoverSound;
    private Sound clickSound;

    private float masterVolume = 1.0f;
    private Preferences prefs;

    private AudioManager() {
        prefs = Gdx.app.getPreferences("HollowKnight_Settings");
        masterVolume = prefs.getInteger("master_volume", 100) / 100f;

        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("ui/sound/backgroundsound.wav"));
        backgroundMusic.setLooping(true);

        hoverSound = Gdx.audio.newSound(Gdx.files.internal("ui/sound/ui_button_confirm.wav"));
        clickSound = Gdx.audio.newSound(Gdx.files.internal("ui/sound/button.wav"));
    }

    public static AudioManager getInstance() {
        if (instance == null) {
            instance = new AudioManager();
        }
        return instance;
    }

    public void playBGM() {
        if (prefs.getBoolean("music_on", true)) {
            backgroundMusic.setVolume(masterVolume);
            backgroundMusic.play();
        } else {
            backgroundMusic.pause();
        }
    }

    public void toggleMusic(boolean isOn) {
        if (isOn) {
            backgroundMusic.setVolume(masterVolume);
            backgroundMusic.play();
        } else {
            backgroundMusic.pause();
        }
    }

    public void playHoverSound() {
        if (prefs.getBoolean("sfx_on", true)) {
            hoverSound.play(masterVolume*0.1f);
        }
    }

    public void playClickSound() {
        if (prefs.getBoolean("sfx_on", true)) {
            clickSound.play(masterVolume*0.7f);
        }
    }

    public void setMasterVolume(float volume) {
        this.masterVolume = volume;
        backgroundMusic.setVolume(volume);
    }
}
