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
                if (langIndex == 1) return "MÜZİK";
                return "MUSIC";

            case "SFX":
                if (langIndex == 1) return "EFEKTLER";
                return "SFX";

            case "BRIGHTNESS":
                if (langIndex == 1) return "PARLAKLIK";
                return "BRIGHTNESS";

            case "RESET":
                if (langIndex == 1) return "SIFIRLA";
                return "RESET";

            case "LANGUAGE":
                if (langIndex == 1) return "DİL";
                return "LANGUAGE";

            case "BACK":
                if (langIndex == 1) return "GERİ";
                return "BACK";

            case "DELETE":
                if (langIndex == 1) return "SİL";
                return "DELETE";

            case "CONFIRM DELETE":
                if (langIndex == 1) return "EMİN MİSİN?";
                return "CONFIRM?";

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
                if (langIndex == 1) return "BAŞARIMLAR";
                return "ACHIEVEMENTS";

            case "QUIT GAME":
                if (langIndex == 1) return "OYUNDAN ÇIK";
                return "QUIT GAME";

            case "SAVE & QUIT":
                if (langIndex == 1) return "KAYDET VE ÇIK";
                return "SAVE & QUIT";

            case "CONTROLS":
                if (langIndex == 1) return "--- KONTROLLER ---";
                return "--- CONTROLS ---";

            case "MOVE LEFT":
                if (langIndex == 1) return "SOLA GİT";
                return "MOVE LEFT";

            case "MOVE RIGHT":
                if (langIndex == 1) return "SAĞA GİT";
                return "MOVE RIGHT";

            case "LOOK UP":
                if (langIndex == 1) return "YUKARI BAK";
                return "LOOK UP";

            case "LOOK DOWN":
                if (langIndex == 1) return "AŞAĞI BAK";
                return "LOOK DOWN";

            case "JUMP":
                if (langIndex == 1) return "ZIPLA";
                return "JUMP";

            case "ATTACK (NAIL)":
                if (langIndex == 1) return "SALDIR (ÇİVİ)";
                return "ATTACK (NAIL)";

            case "DASH":
                if (langIndex == 1) return "ATILMA";
                return "DASH";

            case "ABILITIES & SYSTEMS":
                if (langIndex == 1) return "--- YETENEKLER VE SİSTEMLER ---";
                return "--- ABILITIES & SYSTEMS ---";

            case "HEALTH (MASKS):":
                if (langIndex == 1) return "SAĞLIK (MASKELER):";
                return "HEALTH (MASKS):";

            case "DESC_HEALTH":
                if (langIndex == 1) return "ŞÖVALYENİN SAĞLIĞI MASKELERLE TEMSİL EDİLİR.\nHASAR ALMAK BİR MASKEYİ KIRAR. TÜM MASKELER\nKAYBEDİLİRSE ŞÖVALYE YENİLİR.";
                return "THE KNIGHT'S HEALTH IS REPRESENTED BY MASKS.\nTAKING DAMAGE SHATTERS ONE MASK. IF ALL MASKS\nARE LOST, THE KNIGHT IS DEFEATED.";

            case "SOUL VESSEL:":
                if (langIndex == 1) return "RUH KABI:";
                return "SOUL VESSEL:";

            case "DESC_SOUL":
                if (langIndex == 1) return "DÜŞMANLARA ÇİVİ İLE VURMAK RUH TOPLAR.\nRUH, BÜYÜ YAPMAK VEYA KIRILAN MASKELERİ\nİYİLEŞTİRMEK İÇİN KULLANILABİLİR.";
                return "STRIKING ENEMIES WITH THE NAIL GATHERS SOUL.\nSOUL CAN BE USED TO CAST SPELLS OR FOCUS\nTO RESTORE SHATTERED MASKS.";

            case "FOCUS (HEAL):":
                if (langIndex == 1) return "ODAKLANMA (İYİLEŞME):";
                return "FOCUS (HEAL):";

            case "DESC_FOCUS":
                if (langIndex == 1) return "YETERLİ RUHUNUZ VARKEN BİR MASKEYİ İYİLEŞTİRMEK\nİÇİN ODAKLANMA TUŞUNA BASILI TUTUN. ODAKLANIRKEN\nHAREKET EDEMEZSİNİZ.";
                return "HOLD THE FOCUS BUTTON WHILE HAVING ENOUGH SOUL\nTO HEAL ONE MASK. YOU CANNOT MOVE WHILE FOCUSING.";

            case "CHEAT CODES":
                if (langIndex == 1) return "--- HİLE KODLARI ---";
                return "--- CHEAT CODES ---";

            case "GOD MODE:":
                if (langIndex == 1) return "TANRI MODU (ÖLÜMSÜZLÜK):";
                return "GOD MODE (INVINCIBILITY):";

            case "INFINITE SOUL:":
                if (langIndex == 1) return "SINIRSIZ RUH:";
                return "INFINITE SOUL:";

            case "ONE HIT KILL:":
                if (langIndex == 1) return "TEK VURUŞTA ÖLÜM:";
                return "ONE HIT KILL:";

            case "DESC_F1":
                if (langIndex == 1) return "AÇIP KAPATMAK İÇİN OYUN SIRASINDA [ F1 ] TUŞUNA BASIN.";
                return "PRESS [ F1 ] DURING GAMEPLAY TO TOGGLE.";

            case "DESC_F2":
                if (langIndex == 1) return "AÇIP KAPATMAK İÇİN OYUN SIRASINDA [ F2 ] TUŞUNA BASIN.";
                return "PRESS [ F2 ] DURING GAMEPLAY TO TOGGLE.";

            case "DESC_F3":
                if (langIndex == 1) return "AÇIP KAPATMAK İÇİN OYUN SIRASINDA [ F3 ] TUŞUNA BASIN.";
                return "PRESS [ F3 ] DURING GAMEPLAY TO TOGGLE.";

            case "ACH_PROTECTED_TITLE":
                if (langIndex == 1) return "KORUNMUŞ";
                return "PROTECTED";

            case "ACH_PROTECTED_DESC":
                if (langIndex == 1) return "4 Maske Parçası Elde Et";
                return "Acquire 4 Mask Shards";

            case "ACH_MASKED_TITLE":
                if (langIndex == 1) return "MASKELİ";
                return "MASKED";

            case "ACH_MASKED_DESC":
                if (langIndex == 1) return "Tüm Maske Parçalarını Elde Et";
                return "Acquire all Mask Shards";

            case "ACH_CHARMED_TITLE":
                if (langIndex == 1) return "TILSIMLI";
                return "CHARMED";

            case "ACH_CHARMED_DESC":
                if (langIndex == 1) return "İlk Tılsımını Elde Et";
                return "Acquire your first Charm";

            case "ACH_ENCHANTED_TITLE":
                if (langIndex == 1) return "BÜYÜLENMİŞ";
                return "ENCHANTED";

            case "ACH_ENCHANTED_DESC":
                if (langIndex == 1) return "Hallownest Tılsımlarının Yarısını Bul";
                return "Acquire half of Hallownest's Charms";

            case "ZOTE_INTRO_1":
                if (langIndex == 1) return "Ne yaptığını sanıyorsun?! Benimle avımın arasına girmeye cüret mi ediyorsun? Etrafta koşturup ayak altında dolaşmak ve bela çıkarmak senin alışkanlığın mıdır?";
                return "Just what do you think you're doing?! You dare to come between me and my prey? Is it a habit of yours to scurry about, getting in the way and causing bother?";

            case "ZOTE_INTRO_2":
                if (langIndex == 1) return "Şunu bil, seni melez. Ben Yüce Zote, çok ünlü bir şövalyeyim. Yoluma bir daha çıkarsan, silahıma neden 'Hayat Bitiren' dediklerini anlarsın.";
                return "Know this, cur. I am Zote the Mighty, a knight of great renown. Cross me again, and you'll find out why they call my weapon 'Life Ender'.";

            case "ZOTE_EXHAUSTED_1":
                if (langIndex == 1) return "Hala beni ne diye rahatsız ediyorsun? Ben bir Şövalyeyim. Çocukça oyunlarınla ilgilenmiyorum. Dinlenmem gerek.";
                return "What are you still bothering me for? I'm a Knight. I'm not interested in your childish games. I need my rest.";

            case "ZOTE_EXHAUSTED_2":
                if (langIndex == 1) return "Defol! Yoksa çivimi çekeceğim...";
                return "Begone! Lest I draw my nail...";

            case "ZOTE_DREAM":
                if (langIndex == 1) return "Aptal yaratık... yok edilemez bedenimi kemirerek çeneni körelttin. Bu zayıf şeye yenilmene şaşmamalı!";
                return "Foolish beast... you dulled your mandibles by gnawing on my indestructible body. No wonder you were defeated by this weakling!";

            case "PRESS ANY KEY TO RESPAWN" :
                if (langIndex == 1) return "YENİDEN DOĞMAK İÇİN HERHANGİ BİR TUŞA BASIN";
                return "PRESS ANY KEY TO RESPAWN" ;

            case "CHEAT_HEAL":
                if (langIndex == 1) return "ACİL İYİLEŞME";
                return "EMERGENCY HEAL";
            case "CHEAT_SOUL":
                if (langIndex == 1) return "RUH KABINI DOLDUR";
                return "REFILL SOUL VESSEL";
            case "CHEAT_GOD":
                if (langIndex == 1) return "TANRI MODU (ÖLÜMSÜZLÜK)";
                return "GOD MODE (INVINCIBILITY)";
            case "CHEAT_NOCLIP":
                if (langIndex == 1) return "NOCLIP (HAYALET MODU)";
                return "NOCLIP / SPECTATOR MODE";
            case "CHEAT_TP":
                if (langIndex == 1) return "BOSS ARENASINA IŞINLANMA";
                return "BOSS ARENA TELEPORT";
            case "CHEAT_KILL":
                if (langIndex == 1) return "TÜM DÜŞMANLARI ÖLDÜR";
                return "INSTA-KILL ALL ENEMIES";

            case "DESC_CHEAT_HEAL":
                if (langIndex == 1) return "BİR CAN YENİLEMEK İÇİN [ L-CTRL + H ] TUŞLARINA BASIN.";
                return "PRESS [ L-CTRL + H ] TO RECOVER ONE HEALTH MASK.";
            case "DESC_CHEAT_SOUL":
                if (langIndex == 1) return "RUHU TAMAMEN DOLDURMAK İÇİN [ L-CTRL + S ] TUŞLARINA BASIN.";
                return "PRESS [ L-CTRL + S ] TO COMPLETELY REFILL SOUL.";
            case "DESC_CHEAT_GOD":
                if (langIndex == 1) return "ÖLÜMSÜZLÜĞÜ AÇIP KAPATMAK İÇİN [ L-CTRL + G ] TUŞUNA BASIN.";
                return "PRESS [ L-CTRL + G ] TO TOGGLE INVINCIBILITY.";
            case "DESC_CHEAT_NOCLIP":
                if (langIndex == 1) return "UÇMAYI VE DUVARDAN GEÇMEYİ AÇIP KAPATMAK İÇİN [ L-CTRL + N ] TUŞUNA BASIN.";
                return "PRESS [ L-CTRL + N ] TO TOGGLE FLYING AND WALL PASSING.";
            case "DESC_CHEAT_TP":
                if (langIndex == 1) return "BOSS'A ANINDA IŞINLANMAK İÇİN [ L-CTRL + T ] TUŞLARINA BASIN.";
                return "PRESS [ L-CTRL + T ] TO INSTANTLY TELEPORT TO THE BOSS.";
            case "DESC_CHEAT_KILL":
                if (langIndex == 1) return "EKRANDAKİ TÜM DÜŞMANLARI YOK ETMEK İÇİN [ L-CTRL + K ] TUŞUNA BASIN.";
                return "PRESS [ L-CTRL + K ] TO INSTANTLY KILL ALL ENEMIES ON SCREEN.";
            case "ALL_CHEAT":
                if (langIndex == 1)
                    return "HİLELER (L-CTRL BASILI TUTUN):\n[H] İYİLEŞ  |  [S] TAM RUH  |  [G] TANRI MODU\n[N] NOCLIP  |  [T] BOSS IŞINLANMA  |  [K] HERKESİ ÖLDÜR";
                return "CHEATS (HOLD L-CTRL):\n[H] HEAL  |  [S] MAX SOUL  |  [G] GOD MODE\n[N] NOCLIP  |  [T] BOSS TELEPORT  |  [K] KILL ALL";

            case "True Hunter":
                if (langIndex == 1) return "GERÇEK AVCI";
                return "KILLING ALL THE ENEMY'S";
            case "Zote":
                if (langIndex == 1) return "Zote";
                return "TALKING TO ZORE";
            case "Defeat Boss":
                if (langIndex == 1) return "PATRONU YEN";
                return "DEFEATING THE FALSE KNIGHT";
            case "Charmed":
                if (langIndex == 1) return "TILSIMLI";
                return "Equip your first Charm";

            case "Soul Master":
                if (langIndex == 1) return "RUH USTASI";
                return "SOUL MASTER";

            case "Completion":
                if (langIndex == 1) return "OYUNU TAMAMLA";
                return "COMPLETE THE GAME";

            case "Speedrun":
                if (langIndex == 1) return "HIZLI BİTİRME";
                return "DEFEAT THE FALSE KNIGHT IN RECORD TIME";

            case "PRESS ANY KEY TO CONTINUE PLAYING":
                if (langIndex == 1) return "OYNAMAYA DEVAM ETMEK İÇİN HERHANGİ BİR TUŞA BASIN";
                return "PRESS ANY KEY TO CONTINUE PLAYING";

            case "ACHIEVEMENT UNLOCKED":
                if (langIndex == 1) return "BAŞARIM AÇILDI";
                return "ACHIEVEMENT UNLOCKED";

            default:
                return key;
        }
    }
}
