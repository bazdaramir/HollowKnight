package com.HollowKnight.model.manager;

import com.HollowKnight.model.Charm;
import com.badlogic.gdx.utils.Array;

public class CharmManager {
    public static final int MAX_NOTCHES = 3;
    private final AchievementManager achievementManager = new AchievementManager();
    private Array<Charm> charms;
    private int usedNotches = 0;

    public CharmManager() {
        charms = new Array<>();
        charms.add(new Charm("Soul Catcher", "Increases Soul received per successful Nail hit.", "ui/charms/SoulCatcher.png"));
        charms.add(new Charm("Dashmaster", "Reduces the cooldown of the Dash ability.", "ui/charms/Dashmaster.png"));
        charms.add(new Charm("Unbreakable Strength", "Increases Nail damage.", "ui/charms/UnbreakableStrength.png"));
        charms.add(new Charm("Quick Slash", "Increases attack speed (reduces attack cooldown).", "ui/charms/QuickSlash.png"));
        charms.add(new Charm("Quick Focus", "Increases the healing speed of the Focus ability.", "ui/charms/QuickFocus.png"));
        charms.add(new Charm("Heavy Blow", "Increases the knockback force applied to enemies.", "ui/charms/HeavyBlow.png"));
        charms.add(new Charm("Sharp Shadow", "Damages enemies when dashing and increases dash length by 20%.", "ui/charms/SharpShadow.png"));
    }

    // ویدهارت جایزه اتاق مخفیه، تا وقتی پیدا نشده اصلا تو لیست تلسم ها نیست
    public void unlockVoidHeart() {
        for (Charm charm : charms) {
            if (charm.getName().equals("Void Heart")) return;
        }
        charms.add(new Charm("Void Heart", "Increases spell damage by 50% and changes spell animations.", "ui/charms/VoidHeart.png"));
    }

    public Array<Charm> getCharms() {
        return charms;
    }

    public int getUsedNotches() {
        return usedNotches;
    }

    public boolean toggleCharm(Charm charm) {
        if (charm.isEquipped()) {
            charm.setEquipped(false);
            usedNotches--;
            return true;
        } else {
            if (usedNotches < MAX_NOTCHES) {
                charm.setEquipped(true);
                usedNotches++;
                achievementManager.unlockCharmed();
                return true;
            }
            return false;
        }
    }

    public boolean isEquipped(String charmName) {
        for (Charm c : charms) {
            if (c.getName().equalsIgnoreCase(charmName)) {
                return c.isEquipped();
            }
        }
        return false;
    }
}
