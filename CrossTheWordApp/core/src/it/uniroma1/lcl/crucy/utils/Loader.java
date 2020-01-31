package it.uniroma1.lcl.crucy.utils;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import it.uniroma1.lcl.crucy.menu.login.Textures;


/**
 * Created by Daniel on 04/03/2017.
 */

public class Loader {

    public AssetManager manager;
    private static Loader instance;

    public static Loader getInstance()
    {
        if (instance == null) { instance = new Loader(); }
        return instance;
    }

    public  boolean updateLoader(int f) {  return manager.update(f); }
    public  float percent() { return manager.getProgress(); }

    public  <T extends Disposable> T get(String path, Class<T> classType) {  return manager.get(path, classType); }

    private List<AssetWrapper> createAssetsWrapper() {

        return Arrays.asList(
        new AssetWrapper("BGMusic/Danger Storm.mp3", Music.class),
        new AssetWrapper("BGMusic/One Sly Move.mp3", Music.class),
        new AssetWrapper("BGTextures/loading.png",Texture.class),
        new AssetWrapper("Boxes/SelectedBlock/block_red_active.png", Texture.class),
        new AssetWrapper("Boxes/SelectedBlock/block_yellow_active.png", Texture.class),
        new AssetWrapper("Boxes/SelectedBlock/block_green_active.png", Texture.class),
        new AssetWrapper("Boxes/SelectedBlock/block_selection_Yellow.png", Texture.class),
        new AssetWrapper("Boxes/SelectedBlock/block_selection_Red.png", Texture.class),
        new AssetWrapper("Boxes/SelectedBlock/block_selection_Green.png", Texture.class),
        new AssetWrapper("Boxes/Block.png", Texture.class),
        new AssetWrapper("Boxes/block_large_fill.png", Texture.class),
        new AssetWrapper("Boxes/Checked_Block.png", Texture.class),
        new AssetWrapper("Boxes/Checked_Block_yellow.png", Texture.class),
        new AssetWrapper("Boxes/Normal_Block.png", Texture.class),
        new AssetWrapper("Boxes/Selected_Block_Domain.png", Texture.class),
        new AssetWrapper("Boxes/Selected_Block_Red.png", Texture.class),
        new AssetWrapper("Boxes/Selected_Block_Yellow.png", Texture.class),
        new AssetWrapper("Boxes/Selected_Block_Green.png", Texture.class),
        new AssetWrapper("Boxes/White_Block.png", Texture.class),
        new AssetWrapper("Boxes/White_Block_Small.png", Texture.class),
        new AssetWrapper("Boxes/Wrong_Block.png", Texture.class),
        new AssetWrapper("Boxes/new_crossword.png", Texture.class),
        new AssetWrapper("Boxes/new_crossword_pressed.png", Texture.class),
        new AssetWrapper("Boxes/new_crossword_small.png", Texture.class),
        new AssetWrapper("Boxes/new_crossword_pressed_small.png", Texture.class),

        new AssetWrapper("Buttons/dialog_button_pop.png",Texture.class),
        new AssetWrapper("Buttons/dialog_button_pop_hover.png",Texture.class),
        new AssetWrapper("Buttons/dialog_button_pop_active.png",Texture.class),
        new AssetWrapper("Buttons/menu_button_pop.png",Texture.class),
        new AssetWrapper("Buttons/menu_button_pop_2d.png",Texture.class),
        new AssetWrapper("Buttons/menu_button_pop_hover.png",Texture.class),
        new AssetWrapper("Buttons/menu_button_pop_active.png",Texture.class),
        new AssetWrapper("Buttons/menu_button_pop_active_green.png",Texture.class),
        new AssetWrapper("Buttons/scoreLabel.png", Texture.class),
        new AssetWrapper("Buttons/moneyLabel.png", Texture.class),

        new AssetWrapper("Popup/button_up.png", Texture.class),
        new AssetWrapper("Popup/button_down.png", Texture.class),

        new AssetWrapper("Started/started_small.png", Texture.class),
        new AssetWrapper("Started/started_small_down.png", Texture.class),
        new AssetWrapper("Started/mini_cross3.png", Texture.class),
        new AssetWrapper("Started/started_easy_icon2.png", Texture.class),
        new AssetWrapper("Started/started_medium_icon2.png", Texture.class),
        new AssetWrapper("Started/started_hard_icon2.png", Texture.class),


        new AssetWrapper("Buttons/diamondLabel.png", Texture.class),
        new AssetWrapper("Buttons/xpLabelbar.png", Texture.class),
        new AssetWrapper("Buttons/xpLabel.png", Texture.class),

        new AssetWrapper("Font/font.png", Texture.class),
        new AssetWrapper("Font/fontWhite.png", Texture.class),
        new AssetWrapper("Font/font2.png", Texture.class),
        new AssetWrapper("Font/fontBonusValue.png", Texture.class),
        new AssetWrapper("Font/fontDef.png", Texture.class),
        new AssetWrapper("Font/fontLoading.png", Texture.class),
        new AssetWrapper("Font/keyboardFont.png", Texture.class),
        new AssetWrapper("Font/moneyFont.png", Texture.class),
        new AssetWrapper("Font/oswald-64.png", Texture.class),
        new AssetWrapper("Font/scoreFont.png", Texture.class),
        new AssetWrapper("Font/font.fnt", BitmapFont.class),
        new AssetWrapper("Font/fontWhite.fnt", BitmapFont.class),
        new AssetWrapper("Font/font2.fnt", BitmapFont.class),
        new AssetWrapper("Font/fontBonusValue.fnt", BitmapFont.class),
        new AssetWrapper("Font/fontDef.fnt", BitmapFont.class),
        new AssetWrapper("Font/fontBold/noto.fnt", BitmapFont.class),
                new AssetWrapper("Font/whiteFont/regular.fnt", BitmapFont.class),
                new AssetWrapper("Font/regularFont/regular.fnt", BitmapFont.class),
        new AssetWrapper("Font/fontLoading.fnt", BitmapFont.class),
        new AssetWrapper("Font/keyboardFont.fnt", BitmapFont.class),
        new AssetWrapper("Font/moneyFont.fnt", BitmapFont.class),
        new AssetWrapper("Font/oswald-64.fnt", BitmapFont.class),
        new AssetWrapper("Font/scoreFont.fnt", BitmapFont.class),
        new AssetWrapper("Font/crucy_font.fnt", BitmapFont.class),
        new AssetWrapper("Font/crucy_font.png", Texture.class),

        new AssetWrapper("MainMenu/Settings.png", Texture.class),
        new AssetWrapper("MainMenu/ladback.png", Texture.class),
        new AssetWrapper("Font/backfont.png", Texture.class),
        new AssetWrapper("Font/backfont.fnt", BitmapFont.class),

        new AssetWrapper("MainMenu/Login.png", Texture.class),
        new AssetWrapper("MainMenu/Play.png", Texture.class),
        new AssetWrapper("MainMenu/Cup.png", Texture.class),
        new AssetWrapper("MainMenu/Off.png", Texture.class),
        new AssetWrapper("MainMenu/tableLeaderboard.png", Texture.class),

        new AssetWrapper("background/white.png", Texture.class),
        new AssetWrapper("SoundEffects/Select.mp3", Sound.class),
        new AssetWrapper("SoundEffects/Wrong.mp3", Sound.class),
        new AssetWrapper("SoundEffects/Right.mp3", Sound.class),
        new AssetWrapper("SoundEffects/KeyTap.mp3", Sound.class),
        new AssetWrapper("SoundEffects/CellBoing.mp3", Sound.class),
        new AssetWrapper("SoundEffects/WordSelected.mp3", Sound.class),
        new AssetWrapper("SoundEffects/CoinDrop.mp3", Sound.class),

        new AssetWrapper("WordTexture/word_highlight_green.png", Texture.class),
        new AssetWrapper("WordTexture/word_highlight_red.png", Texture.class),
        new AssetWrapper("WordTexture/word_highlight_yellow.png", Texture.class),

        new AssetWrapper("social/default_pic2.png", Texture.class),
        new AssetWrapper("social/signin_fb.png", Texture.class),
        new AssetWrapper("social/signout_fb.png", Texture.class),
        new AssetWrapper("social/signin_clicked_fb.png", Texture.class),
        new AssetWrapper("social/signout_clicked_fb.png", Texture.class),
        new AssetWrapper("social/profile_picture_label.png", Texture.class),
        new AssetWrapper("social/profile_picture_label2.png", Texture.class),
        new AssetWrapper("social/profile_picture_mask.png", Texture.class),
        new AssetWrapper("textfield/cursor.png",Texture.class),
        new AssetWrapper("textfield/cursorT.png",Texture.class),
        new AssetWrapper("textfield/selection.png",Texture.class),

        new AssetWrapper("BGTextures/mask.png",Texture.class),

        new AssetWrapper("AOnMenu.png", Texture.class),
        new AssetWrapper("AOffMenu.png", Texture.class),
        new AssetWrapper("arrow.png", Texture.class),
        new AssetWrapper("audioOn.png", Texture.class),
        new AssetWrapper("audioOff.png", Texture.class),
        new AssetWrapper("vibrationOn.png", Texture.class),
        new AssetWrapper("vibrationOff.png", Texture.class),

        new AssetWrapper("back.png", Texture.class),
        new AssetWrapper("back_arrow_Blue.png", Texture.class),
        new AssetWrapper("back_arrow_white.png", Texture.class),
        new AssetWrapper("bbonus.png", Texture.class),
        new AssetWrapper("bg2.png",Texture.class),
        new AssetWrapper("bg.png",Texture.class),
        new AssetWrapper("bg3.png",Texture.class),
        new AssetWrapper("bg4.png",Texture.class),

        new AssetWrapper("Block_Invisible.png",Texture.class),
        new AssetWrapper("bonus.png", Texture.class),
        new AssetWrapper("bonus_auto.png", Texture.class),
        new AssetWrapper("bonus_keyboard.png", Texture.class),
        new AssetWrapper("bonus_letters.png", Texture.class),
        new AssetWrapper("bonus_letters2.png", Texture.class),
        new AssetWrapper("cancel.jpg", Texture.class),
        new AssetWrapper("Coins.png", Texture.class),
        new AssetWrapper("Diamonds.png", Texture.class),
        new AssetWrapper("app_logo.png",Texture.class),

        new AssetWrapper("Crucy_logo.png",Texture.class),
        new AssetWrapper("cup.png",Texture.class),
        new AssetWrapper("definition_label.jpg",Texture.class),
        new AssetWrapper("definition_label_easy.png",Texture.class),
        new AssetWrapper("definition_label_medium.png",Texture.class),
        new AssetWrapper("definition_label_hard.png",Texture.class),
        new AssetWrapper("EffectOn.png", Texture.class),
        new AssetWrapper("EffectOff.png", Texture.class),
        new AssetWrapper("exit.png", Texture.class),
        new AssetWrapper("freccia_dx.png",Texture.class),
        new AssetWrapper("freccia_destra.jpg",Texture.class),
        new AssetWrapper("Keyboard Background.jpg",Texture.class),
        new AssetWrapper("keyboard_200.png",Texture.class),
        new AssetWrapper("keyboard_200_down.png",Texture.class),
        new AssetWrapper("keyboard_200_yellow_down.png",Texture.class),
        new AssetWrapper("keyboard_back.png",Texture.class),
        new AssetWrapper("keyboard_backDown.png",Texture.class),
        new AssetWrapper("keyboard_button.png",Texture.class),
        new AssetWrapper("knob_scroll.png",Texture.class),
        new AssetWrapper("knob_scroll2.png",Texture.class),
        new AssetWrapper("know_scroll2.png",Texture.class),
        new AssetWrapper("knob_scroll_little.png",Texture.class),


        new AssetWrapper("main.png",Texture.class),
        new AssetWrapper("menu.png", Texture.class),
        new AssetWrapper("scroll_horizontal.png", Texture.class),
        new AssetWrapper("scroll_horizontal2.png", Texture.class),
        new AssetWrapper("topvanish.png", Texture.class),

        new AssetWrapper("settings.png", Texture.class),
        new AssetWrapper("sfxOn.png", Texture.class),
        new AssetWrapper("sfxOff.png", Texture.class),
        new AssetWrapper("tap.png", Texture.class),
        new AssetWrapper("tap2.png", Texture.class),

        new AssetWrapper("spritesheet/spriteLoad.png", Texture.class),
        new AssetWrapper("background/Loadingcross.png", Texture.class),
        new AssetWrapper("Font/font3.png", Texture.class),
        new AssetWrapper("Font/font3.fnt", BitmapFont.class),
        new AssetWrapper("Font/silverFont/regular.fnt", BitmapFont.class),
        new AssetWrapper("Font/numFont.png", Texture.class),
        new AssetWrapper("Font/numFont.fnt", BitmapFont.class),
        new AssetWrapper("Font/levelFont.png", Texture.class),
        new AssetWrapper("Font/levelFont.fnt", BitmapFont.class),

        new AssetWrapper("MainMenu/gear.png", Texture.class),
        new AssetWrapper("MainMenu/shadow_lvl_gear.png", Texture.class),
        new AssetWrapper("MainMenu/lock.png", Texture.class),

        new AssetWrapper("MainMenu/bluer.png", Texture.class),


        new AssetWrapper("gold.png", Texture.class),
        new AssetWrapper("diam.png", Texture.class),


        new AssetWrapper("Boxes/btile.png", Texture.class),
        new AssetWrapper("Boxes/empty.png", Texture.class),

        new AssetWrapper("Boxes/btile_pressed.png", Texture.class),
        new AssetWrapper("MainMenu/freep.png", Texture.class),
        new AssetWrapper("MainMenu/rank.png", Texture.class),

        new AssetWrapper("MainMenu/base2_exp.png", Texture.class),
        new AssetWrapper("MainMenu/indicator_exp.png", Texture.class),
        new AssetWrapper("MainMenu/top2_exp.png", Texture.class),
        new AssetWrapper("MainMenu/top_exp_green.png", Texture.class),
        new AssetWrapper("MainMenu/top_exp_yellow.png", Texture.class),
        new AssetWrapper("MainMenu/top_exp_red.png", Texture.class),

        new AssetWrapper("Boxes/btile_crossword_white.png", Texture.class),
        new AssetWrapper("Boxes/btile_crossword_white_checked.png", Texture.class),

        new AssetWrapper("Font/sml.png", Texture.class),
        new AssetWrapper("Font/sml.fnt", BitmapFont.class),

        new AssetWrapper("MainMenu/tutorialTap.png", Texture.class),
        new AssetWrapper("MainMenu/tutorialDoubleTap.png", Texture.class),
        new AssetWrapper("MainMenu/tutorialPress.png", Texture.class),
        new AssetWrapper("MainMenu/tutorialMove.png", Texture.class),
        new AssetWrapper("MainMenu/tutorialZoom.png", Texture.class),

        new AssetWrapper("backgroundGestureTutorial.png", Texture.class),
        new AssetWrapper("Boxes/oscurato_block.png", Texture.class),
        new AssetWrapper("Boxes/minilock.png", Texture.class),
        new AssetWrapper("info.png",Texture.class),
        new AssetWrapper("shader.png", Texture.class),

        new AssetWrapper("Difficulty/easy.png", Texture.class),
        new AssetWrapper("Difficulty/easy_60.png", Texture.class),
        new AssetWrapper("Difficulty/medium.png", Texture.class),
        new AssetWrapper("Difficulty/medium_60.png", Texture.class),
        new AssetWrapper("Difficulty/hard.png", Texture.class),
        new AssetWrapper("Difficulty/hard_60.png", Texture.class),
        new AssetWrapper("Loghi/babelscape+sapienza_NERO2.png", Texture.class));
        }


    public  void loadTexture() {
        manager = new AssetManager();
        List<AssetWrapper> assets = createAssetsWrapper();
        TextureLoader.TextureParameter textureParameter = new TextureLoader.TextureParameter();
        textureParameter.minFilter = Texture.TextureFilter.MipMap;
        textureParameter.magFilter = Texture.TextureFilter.Nearest;
        textureParameter.genMipMaps = true;

        for(AssetWrapper as : assets) {
            if (as.getType().equals(Texture.class))
                this.manager.load(as.getPath(), as.getType(), textureParameter);
            else this.manager.load(as.getPath(), as.getType());

        }
    }

    public  void dispose() {  manager.dispose(); }
    
}
