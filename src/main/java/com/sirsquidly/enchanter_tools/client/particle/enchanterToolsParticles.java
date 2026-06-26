package com.sirsquidly.enchanter_tools.client.particle;

/**
 * This enum just simplifies writing particles.
* */
public enum enchanterToolsParticles
{
    BRAZIER_EMBER,
    BRAZIER_FLAME_FADE,
    BRAZIER_SMOKE,
    BRAZIER_RUNE_BURN;

    public int getId() { return this.ordinal(); }

    public static enchanterToolsParticles fromId(int id) { return values()[id]; }
}