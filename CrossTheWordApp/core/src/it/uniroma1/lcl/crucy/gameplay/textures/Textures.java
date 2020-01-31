package it.uniroma1.lcl.crucy.gameplay.textures;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

import it.uniroma1.lcl.crucy.utils.Loader;

/**
 * Created by bejgu on 13/09/2016.
 *
 * Classe che gestisce tutte le texture del cruciverba
 */
public class Textures
{

    public static Texture blockTexture ;
    public static Texture portalTexture;
    public static Texture normalTexture ;
    public static Texture passageTexture ;
    public static Texture selectedTextureRed;
    public static Texture selectedTextureGreen;
    public static Texture selectedTextureYellow;
    public static Texture indicatorTexture;
    public static Texture indicatorTextureRed;
    public static Texture indicatorTextureYellow;
    public static Texture indicatorTextureGreen;
    public static Texture keyboardBackgroundTexture;
    public static Texture keyboardBack;
    public static Texture AOnMenuTexture;
    public static Texture AOffMenuTexture;
    public static Texture effectOn;
    public static Texture effectOff;
    public static Texture mainTexture;
    public static Texture exitTexture;
    public static Texture definitionLabelTexture;
    public static Texture definitionLabelTextureEasy;
    public static Texture definitionLabelTextureMedium;
    public static Texture definitionLabelTextureHard;
    public static Texture menuTexture;
    public static Texture dialogUpTexture;
    public static Texture dialogOverTexture;
    public static Texture dialogDownTexture;
    public static Texture bonusTexture;
    public static Texture buttonTexture;
    public static Texture buttonTextureDown;
    public static Texture wrongWordTexture;
    public static Texture bonus;
    public static Texture backArrow;
    public static Texture bonusAuto;
    public static Texture bonusKeyboard;
    public static Texture bonusLetters;
    public static Texture bonusLetters2;
    public static Texture coin;
    public static Texture isActualIndicatorYellow;
    public static Texture isActualIndicatorRed;
    public static Texture isActualIndicatorGreen;
    public static Texture cancel;
    public static Texture boxOsc;
    public static Texture boxOscLock;


    public static Texture labelScore;
    public static Texture labelXP;
    public static Texture xpBar;
    public static Texture labelMoney;
    public static Texture labelDiamond;
    public static Texture diamond;


    public static BitmapFont font;
    public static BitmapFont font2;
    public static BitmapFont font3;
    public static BitmapFont font4;
    public static BitmapFont fontKeyboard;
    public static BitmapFont fontScore;
    public static BitmapFont fontBonusValue;
    public static BitmapFont fontMoney;


    public static void loadTextures()
    {

        blockTexture = Loader.getInstance().get("Boxes/Block.png", Texture.class);
        normalTexture = Loader.getInstance().get("Boxes/Normal_Block.png", Texture.class);
        boxOsc = Loader.getInstance().get("Boxes/oscurato_block.png", Texture.class);
        boxOscLock = Loader.getInstance().get("Boxes/minilock.png", Texture.class);

        selectedTextureRed = Loader.getInstance().get("Boxes/SelectedBlock/block_red_active.png", Texture.class);
        selectedTextureYellow = Loader.getInstance().get("Boxes/SelectedBlock/block_yellow_active.png", Texture.class);
        selectedTextureGreen = Loader.getInstance().get("Boxes/SelectedBlock/block_green_active.png", Texture.class);

        isActualIndicatorYellow = Loader.getInstance().get("Boxes/SelectedBlock/block_selection_Yellow.png", Texture.class);
        isActualIndicatorRed = Loader.getInstance().get("Boxes/SelectedBlock/block_selection_Red.png", Texture.class);
        isActualIndicatorGreen= Loader.getInstance().get("Boxes/SelectedBlock/block_selection_Green.png", Texture.class);

        indicatorTextureRed = Loader.getInstance().get("Boxes/Selected_Block_Red.png", Texture.class);
        indicatorTextureYellow = Loader.getInstance().get("Boxes/Selected_Block_Yellow.png", Texture.class);
        indicatorTextureGreen = Loader.getInstance().get("Boxes/Selected_Block_Green.png", Texture.class);

        keyboardBackgroundTexture = Loader.getInstance().get("Keyboard Background.jpg", Texture.class);
        keyboardBack =Loader.getInstance().get("keyboard_back.png", Texture.class);
        AOnMenuTexture = Loader.getInstance().get("AOnMenu.png", Texture.class);
        AOffMenuTexture =Loader.getInstance().get("AOffMenu.png", Texture.class);
        effectOn =Loader.getInstance().get("EffectOn.png", Texture.class);
        effectOff =Loader.getInstance().get("EffectOff.png", Texture.class);
        mainTexture =Loader.getInstance().get("main.png", Texture.class);
        exitTexture =Loader.getInstance().get("exit.png", Texture.class);
        definitionLabelTexture =Loader.getInstance().get("definition_label.jpg", Texture.class);
        definitionLabelTextureEasy = Loader.getInstance().get("definition_label_easy.png", Texture.class);
        definitionLabelTextureMedium = Loader.getInstance().get("definition_label_medium.png", Texture.class);
        definitionLabelTextureHard = Loader.getInstance().get("definition_label_hard.png", Texture.class);
        menuTexture =Loader.getInstance().get("menu.png", Texture.class);
        dialogUpTexture = Loader.getInstance().get("Buttons/dialog_button_pop.png", Texture.class);;
        dialogOverTexture =Loader.getInstance().get("Buttons/dialog_button_pop_hover.png", Texture.class);
        dialogDownTexture =Loader.getInstance().get("Buttons/dialog_button_pop_active.png", Texture.class);
        bonusTexture =Loader.getInstance().get("bonus.png", Texture.class);
        buttonTexture= Loader.getInstance().get("keyboard_200.png", Texture.class);
        buttonTextureDown= Loader.getInstance().get("keyboard_200_down.png", Texture.class);
        bonus =Loader.getInstance().get("bbonus.png", Texture.class);
        backArrow = Loader.getInstance().get("arrow.png", Texture.class);
        bonusAuto = Loader.getInstance().get("bonus_auto.png", Texture.class);
        bonusKeyboard = Loader.getInstance().get("bonus_keyboard.png", Texture.class);
        bonusLetters = Loader.getInstance().get("bonus_letters.png", Texture.class);
        bonusLetters2 = Loader.getInstance().get("bonus_letters2.png", Texture.class);
        cancel = Loader.getInstance().get("cancel.jpg", Texture.class);

        coin = Loader.getInstance().get("Coins.png", Texture.class);
        diamond = Loader.getInstance().get("Diamonds.png", Texture.class);

        wrongWordTexture = Loader.getInstance().get("Boxes/Wrong_Block.png", Texture.class);

        labelScore = Loader.getInstance().get("Buttons/scoreLabel.png", Texture.class);
        xpBar = Loader.getInstance().get("Buttons/xpLabelbar.png", Texture.class);
        labelXP = Loader.getInstance().get("Buttons/xpLabel.png", Texture.class);

        labelMoney = Loader.getInstance().get("Buttons/moneyLabel.png", Texture.class);
        labelDiamond = Loader.getInstance().get("Buttons/diamondLabel.png", Texture.class);


        font =  Loader.getInstance().get("Font/fontDef.fnt", BitmapFont.class);
        font2 =  Loader.getInstance().get("Font/font2.fnt", BitmapFont.class);
        font3 =  Loader.getInstance().get("Font/fontBold/noto.fnt", BitmapFont.class);
        font4 =  Loader.getInstance().get("Font/regularFont/regular.fnt", BitmapFont.class);

        fontKeyboard =  Loader.getInstance().get("Font/keyboardFont.fnt", BitmapFont.class);
        fontScore =  Loader.getInstance().get("Font/scoreFont.fnt", BitmapFont.class);
        fontBonusValue =  Loader.getInstance().get("Font/fontBonusValue.fnt", BitmapFont.class);
        fontMoney =  Loader.getInstance().get("Font/moneyFont.fnt", BitmapFont.class);


    }

}
