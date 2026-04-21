package com.azr.client.modules.combat;

import com.azr.client.Module;
import com.azr.client.ModuleCategory;

import java.lang.reflect.*;
import java.util.*;

/**
 * KillAura - Anarchy modu
 * Mobs ve oyunculari dahil tum LivingEntityleri hedef alir.
 * Reflection ile intermediary MC isimlerini kullanir:
 *   class_310 (MinecraftClient), field_1724 (player), field_1687 (world),
 *   field_1761 (interactionManager), method_18112 (getEntities),
 *   method_2918 (attackEntity), method_6104 (swingHand), etc.
 */
public final class AnarchyKillAura extends Module {

    // Intermediary isimler (KillAura.class + AutoMace.class bytecodeundan alindi)
    private static final String MC_CLASS        = "net.minecraft.class_310";
    private static final String MC_INSTANCE     = "method_1551";   // MinecraftClient.getInstance()
    private static final String F_PLAYER        = "field_1724";    // mc.player  (class_746)
    private static final String F_WORLD         = "field_1687";    // mc.world   (class_638)
    private static final String F_INTERACTION   = "field_1761";    // mc.interactionManager (class_636)
    private static final String M_GET_ENTITIES  = "method_18112";  // world.getEntities() Iterable
    private static final String C_LIVING        = "net.minecraft.class_1309";  // LivingEntity
    private static final String M_IS_ALIVE      = "method_5805";   // isAlive() Z
    private static final String M_SQ_DIST       = "method_5858";   // squaredDistanceTo(Entity) D
    private static final String M_ATTACK        = "method_2918";   // interactionManager.attackEntity(Player,Entity)
    private static final String M_SWING         = "method_6104";   // player.swingHand(Hand)
    private static final String C_HAND          = "net.minecraft.class_1268";
    private static final String F_MAIN_HAND     = "field_5808";    // Hand.MAIN_HAND

    // Ayarlar
    private float range    = 6.0f;   // Hedefleme mesafesi (blok)
    private long  attackDelay = 50L; // ms (50ms = 20 CPS, anarchy)

    // Reflection cache
    private boolean initialized = false;
    private Method  mcGetInstance;
    private Field   fPlayer, fWorld, fInteraction;
    private Method  mGetEntities;
    private Class<?>livingEntityClass;
    private Method  mIsAlive, mSqDist, mAttack, mSwing;
    private Object  mainHandValue;
    private long    lastAttack = 0L;

    public AnarchyKillAura() {
        super("Kill Aura", "Anarchy: Tum varliklari, her esyayla vurur.", -1, ModuleCategory.COMBAT);
    }

    public float getRange()       { return range; }
    public void  setRange(float r){ range = Math.max(1f, Math.min(10f, r)); }

    public long getAttackDelay()        { return attackDelay; }
    public void setAttackDelay(long ms) { attackDelay = Math.max(20L, ms); }

    @Override
    public void onTick() {
        if (!isEnabled()) return;

        long now = System.currentTimeMillis();
        if (now - lastAttack < attackDelay) return;

        try {
            if (!initialized) init();
            if (!initialized) return;

            Object mc     = mcGetInstance.invoke(null);
            if (mc == null) return;

            Object player = fPlayer.get(mc);
            Object world  = fWorld.get(mc);
            Object im     = fInteraction.get(mc);
            if (player == null || world == null || im == null) return;

            // Tum entityleri al
            Iterable<?> entities = (Iterable<?>) mGetEntities.invoke(world);
            if (entities == null) return;

            List<Object> targets = new ArrayList<>();
            double rangeSq = range * range;

            for (Object e : entities) {
                if (!livingEntityClass.isInstance(e)) continue;
                if (e == player) continue;
                Boolean alive = (Boolean) mIsAlive.invoke(e);
                if (!alive) continue;
                Double sq = (Double) mSqDist.invoke(player, e);
                if (sq > rangeSq) continue;
                targets.add(e);
            }

            if (targets.isEmpty()) return;

            // En yakini sec
            targets.sort((a, b) -> {
                try {
                    double da = (Double) mSqDist.invoke(player, a);
                    double db = (Double) mSqDist.invoke(player, b);
                    return Double.compare(da, db);
                } catch (Exception ex) { return 0; }
            });

            Object target = targets.get(0);

            // Vur: interactionManager.attackEntity(player, target)
            mAttack.invoke(im, player, target);
            // El sallia: player.swingHand(Hand.MAIN_HAND)
            mSwing.invoke(player, mainHandValue);

            lastAttack = now;

        } catch (Exception ignored) { }
    }

    private void init() {
        try {
            Class<?> mcClass = Class.forName(MC_CLASS);
            mcGetInstance = findMethod(mcClass, MC_INSTANCE);
            if (mcGetInstance == null) return;

            Object mc = mcGetInstance.invoke(null);
            if (mc == null) return;

            fPlayer      = findField(mcClass, F_PLAYER);
            fWorld       = findField(mcClass, F_WORLD);
            fInteraction = findField(mcClass, F_INTERACTION);
            if (fPlayer == null || fWorld == null || fInteraction == null) return;

            livingEntityClass = Class.forName(C_LIVING);

            // world.getEntities()
            mGetEntities = findMethod(fWorld.getType(), M_GET_ENTITIES);
            if (mGetEntities == null) return;

            // isAlive()
            mIsAlive = findMethod(livingEntityClass, M_IS_ALIVE);
            if (mIsAlive == null) return;

            // squaredDistanceTo(Entity) -- on player class
            mSqDist = findMethod(fPlayer.getType(), M_SQ_DIST);
            if (mSqDist == null) return;

            // attackEntity(Player, Entity) -- on interactionManager class
            mAttack = findMethod(fInteraction.getType(), M_ATTACK);
            if (mAttack == null) return;

            // swingHand(Hand)
            mSwing = findMethod(fPlayer.getType(), M_SWING);
            if (mSwing == null) return;

            // Hand.MAIN_HAND
            Class<?> handClass = Class.forName(C_HAND);
            Field handField = handClass.getField(F_MAIN_HAND);
            mainHandValue = handField.get(null);
            if (mainHandValue == null) return;

            initialized = true;

        } catch (Exception e) {
            initialized = false;
        }
    }

    // ----- Yardimci reflection metodlari -----

    private static Method findMethod(Class<?> clazz, String name) {
        if (clazz == null) return null;
        for (Class<?> c = clazz; c != null && c != Object.class; c = c.getSuperclass()) {
            for (Method m : c.getDeclaredMethods()) {
                if (m.getName().equals(name)) {
                    m.setAccessible(true);
                    return m;
                }
            }
        }
        return null;
    }

    private static Field findField(Class<?> clazz, String name) {
        if (clazz == null) return null;
        for (Class<?> c = clazz; c != null && c != Object.class; c = c.getSuperclass()) {
            for (Field f : c.getDeclaredFields()) {
                if (f.getName().equals(name)) {
                    f.setAccessible(true);
                    return f;
                }
            }
        }
        return null;
    }
}
