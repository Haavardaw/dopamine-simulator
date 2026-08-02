/*
 * Copyright (c) 2026, Zoinkwiz <https://github.com/Zoinkwiz>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.dopaminesimulator.points;

import java.util.Random;
import lombok.Getter;
import net.runelite.api.gameval.ItemID;

/**
 * What turns up on the coin when a surge lands.
 *
 * <p>A surge used to be one thing: clicks paid five times as much for five
 * seconds. It was worth catching but it was worth catching in exactly the same
 * way every time, so after the first few there was nothing to read and nothing
 * to decide. Seven dishes instead, each pulling a different lever, so seeing
 * which one landed is the point rather than a formality.
 *
 * <p>Weights are out of a hundred. The dull dependable one is common, the ones
 * that pay in something you cannot otherwise buy are not.
 */
@Getter
public enum GnomeFood
{
	/**
	 * The plain one. Pays as though you had got a couple of hundred clicks in
	 * for nothing, which is a lot early and a pleasant nothing later, because the
	 * click payout deliberately grows slower than everything else.
	 */
	CHOCCHIP_CRUNCHIES("Chocchip crunchies", ItemID.CHOCCHIP_CRUNCHIES, 30, 0L,
		"A free fistful of clicks."),

	/** The old surge, kept, because a frenzy is worth having among the rest. */
	SPICY_CRUNCHIES("Spicy crunchies", ItemID.SPICY_CRUNCHIES, 20, 10_000L,
		"Clicks pay five times as much."),

	/** The one that pays you for playing rather than for clicking. */
	WORM_HOLE("Worm hole", ItemID.WORM_HOLE, 16, 60_000L,
		"Everything earns four times as much."),

	/** Dust is otherwise only what a finished card overflows, which is slow. */
	TOAD_CRUNCHIES("Toad crunchies", ItemID.TOAD_CRUNCHIES, 12, 0L,
		"A handful of dust."),

	/** The pass moves on its own clock, so a shove along it is worth something. */
	FRUIT_BLAST("Fruit blast", ItemID.FRUIT_BLAST, 10, 0L,
		"A push up the pass."),

	/** Cards, without paying for them. */
	TANGLED_TOADS_LEGS("Tangled toads' legs", ItemID.TANGLED_TOADS_LEGS, 7, 0L,
		"A pack, on the house."),

	/** The one worth hoping for. Pays anywhere from nothing much to a great deal. */
	WIZARD_BLIZZARD("Wizard blizzard", ItemID.WIZARD_BLIZZARD, 5, 0L,
		"Anything from a little to a lot.");

	/**
	 * Clicks pay this much more while spicy crunchies are up.
	 *
	 * <p>Ten seconds of it is worth a few minutes of ordinary income, but only to
	 * somebody actually at the keyboard for the ten seconds, which is the one
	 * dish that should ask for that.
	 */
	public static final double FRENZY_MULTIPLIER = 5d;

	/**
	 * Everything earns this much more while a worm hole is open. A minute of it
	 * comes to three minutes of ordinary income, which puts it level with the
	 * frenzy without needing you to do anything.
	 */
	public static final double WORM_HOLE_MULTIPLIER = 4d;

	/**
	 * Chocchip crunchies pay as though you had clicked this many times. Worth a
	 * couple of minutes early and under a minute late, because the click payout
	 * deliberately grows slower than income does.
	 */
	public static final int CRUNCHIES_CLICKS = 60;

	/** Fruit blast is worth about a tier of an early pass. */
	public static final double FRUIT_BLAST_PASS_XP = 30d;

	private final String displayName;
	private final int itemId;
	private final int weight;
	private final long durationMs;
	private final String blurb;

	private static final int TOTAL_WEIGHT;

	static
	{
		int total = 0;
		for (GnomeFood food : values())
		{
			total += food.weight;
		}
		TOTAL_WEIGHT = total;
	}

	GnomeFood(String displayName, int itemId, int weight, long durationMs, String blurb)
	{
		this.displayName = displayName;
		this.itemId = itemId;
		this.weight = weight;
		this.durationMs = durationMs;
		this.blurb = blurb;
	}

	/** Whether this one hangs around or is over the moment you catch it. */
	public boolean lasts()
	{
		return durationMs > 0L;
	}

	public double clickMultiplier()
	{
		return this == SPICY_CRUNCHIES ? FRENZY_MULTIPLIER : 1d;
	}

	public double incomeMultiplier()
	{
		return this == WORM_HOLE ? WORM_HOLE_MULTIPLIER : 1d;
	}

	/** How often this one comes up, as a percentage, for the sake of showing it. */
	public int chancePercent()
	{
		return (int) Math.round(100d * weight / TOTAL_WEIGHT);
	}

	public static GnomeFood roll(Random random)
	{
		int pick = random.nextInt(TOTAL_WEIGHT);
		for (GnomeFood food : values())
		{
			pick -= food.weight;
			if (pick < 0)
			{
				return food;
			}
		}
		return CHOCCHIP_CRUNCHIES;
	}
}
