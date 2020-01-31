package it.uniroma1.lcl.crucy.utils;

/**
 * Created by Daniel
 */

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.ObjectMap;

import it.uniroma1.lcl.crucy.gameplay.audio.MusicAmbient;
import it.uniroma1.lcl.crucy.gameplay.audio.SoundFx;

// Da mergiare e modificare con assetmanager, aggiungere utils per i path

public class AudioManager
{
    private static ObjectMap<MusicAmbient, Music> music;
    private static ObjectMap<SoundFx, Sound> sound;
    private MusicAmbient currentMusic;

    private boolean musicOn;
    private boolean soundOn;
    private boolean vibrationOn;

    private int vibrationTime;


    public AudioManager(boolean initMusicOn, boolean initiSoundOn, boolean vibrationOn) {
        this.musicOn = initMusicOn;
        this.soundOn = initiSoundOn;
        this.vibrationOn = vibrationOn;
        vibrationTime = 500;
        load();
    }

    public void disposeAll() {
        for (Music m : music.values()) m.dispose();
        for (Sound s : sound.values()) s.dispose();
    }

    private void pause() { if (music.get(currentMusic).isPlaying()) music.get(currentMusic).pause(); }

    private void resume() { if (!(music.get(currentMusic).isPlaying()) && musicOn) music.get(currentMusic).play(); }

    public void playSound(SoundFx toPlay) { if(soundOn) sound.get(toPlay).play(); }

    public void stopMusic() { music.get(currentMusic).stop(); }

    public void playMusic(MusicAmbient newM, boolean loop) //boolean loop per modifiche future
    {
        if(currentMusic != null) music.get(currentMusic).stop();
        currentMusic=newM;
        if(musicOn) {
            music.get(currentMusic).play();
            music.get(currentMusic).setLooping(loop);
        }
    }

    private void load() {
        music = new ObjectMap<MusicAmbient, Music>();

        music.put(MusicAmbient.MENU, Loader.getInstance().get("BGMusic/Danger Storm.mp3",Music.class));
        music.put(MusicAmbient.GAME, Loader.getInstance().get("BGMusic/One Sly Move.mp3",Music.class));

        sound = new ObjectMap<SoundFx, Sound>();
        sound.put(SoundFx.SELECT, Loader.getInstance().get("SoundEffects/Select.mp3",Sound.class));
        sound.put(SoundFx.WRONG, Loader.getInstance().get("SoundEffects/Wrong.mp3",Sound.class));
        sound.put(SoundFx.RIGHT, Loader.getInstance().get("SoundEffects/Right.mp3",Sound.class));
        sound.put(SoundFx.TAP, Loader.getInstance().get("SoundEffects/KeyTap.mp3",Sound.class));
        sound.put(SoundFx.CELLBOING, Loader.getInstance().get("SoundEffects/CellBoing.mp3",Sound.class));
        sound.put(SoundFx.SELECTED, Loader.getInstance().get("SoundEffects/WordSelected.mp3",Sound.class));
        sound.put(SoundFx.COINDROP, Loader.getInstance().get("SoundEffects/CoinDrop.mp3",Sound.class));
    }

    public void setMusicOn (boolean b) {
        musicOn = b;
        if(musicOn) resume();
        else pause();
    }

    public void setSoundOn (boolean b) { soundOn = b; }
    public boolean isSoundOn() { return soundOn; }
    public boolean isMusicOn() { return musicOn; }

    public void setVibrationTime(int timeInMillisec) { this.vibrationTime = timeInMillisec; }

    public void vibrate() { vibrate(vibrationTime); }

    private void vibrate(int timeInMillisec)
    {
        if(vibrationOn) Gdx.input.vibrate(timeInMillisec);
    }

    public boolean isVibrationOn() { return vibrationOn; }

    public void switchVibration() {
        if(vibrationOn) vibrationOn = false;
        else vibrationOn = true;

        vibrate();
    }
}
