package com.HollowKnight.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class Translator {

    public static final String[] LANGUAGES = {"ENGLISH","TURKISH"};

    public static String getText(String key) {
        Preferences prefs = Gdx.app.getPreferences("HollowKnight_Settings");
        int langIndex = prefs.getInteger("language_id", 0);
        switch (key) {
            case "SETTINGS":
                if (langIndex == 1) return "AYARLAR";
                return "SETTINGS";

            case "VOLUME":
                if (langIndex == 1) return "SES";
                return "VOLUME";

            case "MUSIC":
                if (langIndex == 1) return "MÜZIK";
                return "MUSIC";

            case "SFX":
                if (langIndex == 1) return "EFEKTLER";
                return "SFX";

            case "BRIGHTNESS":
                if (langIndex == 1) return "PARLAKLIK";
                return "BRIGHTNESS";

            case "RESET":
                if (langIndex == 1) return "SESI";
                return "RESET";

            case "LANGUAGE":
                if (langIndex == 1) return "DIL";
                return "LANGUAGE";

            case "BACK":
                if (langIndex == 1) return "GERI";
                return "BACK";

            case "ON":
                if (langIndex == 1) return "AÇIK";
                return "ON";

            case "OFF":
                if (langIndex == 1) return "KAPALI";
                return "OFF";
            case "START GAME":
                if (langIndex == 1) return "OYUNA BAŞLA";
                return "START GAME";

            case "GUIDE":
                if (langIndex == 1) return "REHBER";
                return "GUIDE";

            case "ACHIEVEMENTS":
                if (langIndex == 1) return "BAŞARILAR";
                return "ACHIEVEMENTS";

            case "QUIT GAME":
                if (langIndex == 1) return "ÇIKIŞ";
                return "QUIT GAME";
            case "CONTROLS":
                if (langIndex == 1) return "--- KONTROLLER ---";
                return "--- CONTROLS ---";

            case "MOVE LEFT":
                if (langIndex == 1) return "SOLA GIT";
                return "MOVE LEFT";

            case "MOVE RIGHT":
                if (langIndex == 1) return "SAGA GIT";
                return "MOVE RIGHT";

            case "LOOK UP":
                if (langIndex == 1) return "YUKARI BAK";
                return "LOOK UP";

            case "LOOK DOWN":
                if (langIndex == 1) return "ASAGI BAK";
                return "LOOK DOWN";

            case "JUMP":
                if (langIndex == 1) return "ZIPLA";
                return "JUMP";

            case "ATTACK (NAIL)":
                if (langIndex == 1) return "SALDIRI (CIVI)";
                return "ATTACK (NAIL)";

            case "DASH":
                if (langIndex == 1) return "ATILMA";
                return "DASH";

            case "ABILITIES & SYSTEMS":
                if (langIndex == 1) return "--- YETENEKLER VE SISTEMLER ---";
                return "--- ABILITIES & SYSTEMS ---";

            case "HEALTH (MASKS):":
                if (langIndex == 1) return "SAGLIK (MASKELER):";
                return "HEALTH (MASKS):";

            case "DESC_HEALTH":
                if (langIndex == 1) return "SOVALYENIN SAGLIGI MASKELERLE TEMSIL EDILIR.\nHASAR ALMAK BIR MASKEYI KIRAR. TUM MASKELER\nKAYBEDILIRSE SOVALYE YENILIR.";
                return "THE KNIGHT'S HEALTH IS REPRESENTED BY MASKS.\nTAKING DAMAGE SHATTERS ONE MASK. IF ALL MASKS\nARE LOST, THE KNIGHT IS DEFEATED.";

            case "SOUL VESSEL:":
                if (langIndex == 1) return "RUH KABI:";
                return "SOUL VESSEL:";

            case "DESC_SOUL":
                if (langIndex == 1) return "DUSMANLARA CIVI ILE VURMAK RUH TOPLAR.\nRUH, BUYU YAPMAK VEYA KIRILAN MASKELERI\nIYILESTIRMEK ICIN KULLANILABILIR.";
                return "STRIKING ENEMIES WITH THE NAIL GATHERS SOUL.\nSOUL CAN BE USED TO CAST SPELLS OR FOCUS\nTO RESTORE SHATTERED MASKS.";

            case "FOCUS (HEAL):":
                if (langIndex == 1) return "ODAKLANMA (IYILESME):";
                return "FOCUS (HEAL):";

            case "DESC_FOCUS":
                if (langIndex == 1) return "YETERLI RUHUNUZ VARKEN BIR MASKEYI IYILESTIRMEK\nICIN ODAKLANMA TUSUNA BASILI TUTUN. ODAKLANIRKEN\nHAREKET EDEMEZSINIZ.";
                return "HOLD THE FOCUS BUTTON WHILE HAVING ENOUGH SOUL\nTO HEAL ONE MASK. YOU CANNOT MOVE WHILE FOCUSING.";

            case "CHEAT CODES":
                if (langIndex == 1) return "--- HILE KODLARI ---";
                return "--- CHEAT CODES ---";

            case "GOD MODE:":
                if (langIndex == 1) return "TANRI MODU (OLUMSUZLUK):";
                return "GOD MODE (INVINCIBILITY):";

            case "INFINITE SOUL:":
                if (langIndex == 1) return "SINIRSIZ RUH:";
                return "INFINITE SOUL:";

            case "ONE HIT KILL:":
                if (langIndex == 1) return "TEK VURUSTA OLUM:";
                return "ONE HIT KILL:";

            case "DESC_F1":
                if (langIndex == 1) return "ACIP KAPATMAK ICIN OYUN SIRASINDA [ F1 ] TUSUNA BASIN.";
                return "PRESS [ F1 ] DURING GAMEPLAY TO TOGGLE.";

            case "DESC_F2":
                if (langIndex == 1) return "ACIP KAPATMAK ICIN OYUN SIRASINDA [ F2 ] TUSUNA BASIN.";
                return "PRESS [ F2 ] DURING GAMEPLAY TO TOGGLE.";

            case "DESC_F3":
                if (langIndex == 1) return "ACIP KAPATMAK ICIN OYUN SIRASINDA [ F3 ] TUSUNA BASIN.";
                return "PRESS [ F3 ] DURING GAMEPLAY TO TOGGLE.";
            case "ACH_PROTECTED_TITLE":
                if (langIndex == 1) return "KORUNMUS";
                return "PROTECTED";

            case "ACH_PROTECTED_DESC":
                if (langIndex == 1) return "4 Maske Parcasi Elde Et";
                return "Acquire 4 Mask Shards";

            case "ACH_MASKED_TITLE":
                if (langIndex == 1) return "MASKELI";
                return "MASKED";

            case "ACH_MASKED_DESC":
                if (langIndex == 1) return "Tum Maske Parcalarini Elde Et";
                return "Acquire all Mask Shards";

            case "ACH_CHARMED_TITLE":
                if (langIndex == 1) return "TILSIMLI";
                return "CHARMED";

            case "ACH_CHARMED_DESC":
                if (langIndex == 1) return "Ilk Tilsimini Elde Et";
                return "Acquire your first Charm";

            case "ACH_ENCHANTED_TITLE":
                if (langIndex == 1) return "BUYULENMIS";
                return "ENCHANTED";

            case "ACH_ENCHANTED_DESC":
                if (langIndex == 1) return "Hallownest Tilsimlarinin Yarisini Bul";
                return "Acquire half of Hallownest's Charms";
            default:
                return key;
        }
    }
}
