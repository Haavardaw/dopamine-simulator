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
package com.dopaminesimulator.systems;

import com.dopaminesimulator.cards.CardSet;
import com.dopaminesimulator.core.DopamineState;
import com.dopaminesimulator.core.RewardQueue;
import com.dopaminesimulator.incremental.Milestones;
import com.dopaminesimulator.packs.PackTier;
import com.dopaminesimulator.points.GnomeFood;
import java.util.Random;

/**
 * Hands out what a dish is worth.
 *
 * <p>The two that last do nothing here: they are read off the clock while they
 * run. Everything else pays out once, at the moment it lands.
 *
 * <p>The sums are written against what the account currently earns rather than
 * as flat numbers, so a dish is worth about the same share of an hour at the
 * start as it is five hundred hours in. A flat number would be a windfall on
 * the first day and beneath noticing by the second week.
 */
public class GnomeFoodService
{
	/** A free pack is the best one costing less than this much of an hour. */
	private static final double PACK_BUDGET_HOURS = 0.08d;

	/** Dust, per milestone reached, so it keeps pace with the cards you hold. */
	private static final int DUST_PER_MILESTONE = 6;

	/** What a wizard blizzard pays, as a share of an hour, at worst and at best. */
	private static final double BLIZZARD_MIN_HOURS = 0.01d;
	private static final double BLIZZARD_MAX_HOURS = 0.25d;

	private final Random random;
	private final PackService packs;

	public GnomeFoodService(Random random, PackService packs)
	{
		this.random = random;
		this.packs = packs;
	}

	/**
	 * Pays out a dish that lands all at once.
	 *
	 * @param hourlyIncome what everything but clicking currently earns
	 * @param clickPayout  what one click is currently worth
	 * @return a line describing what happened, for the reward feed
	 */
	public String apply(DopamineState state, GnomeFood food, double hourlyIncome,
						double clickPayout, RewardQueue rewards)
	{
		switch (food)
		{
			case CHOCCHIP_CRUNCHIES:
			{
				double points = clickPayout * GnomeFood.CRUNCHIES_CLICKS;
				state.addPoints(points);
				return GnomeFood.CRUNCHIES_CLICKS + " clicks, free";
			}
			case TOAD_CRUNCHIES:
			{
				int dust = DUST_PER_MILESTONE
					* (1 + Milestones.reached(state.getLifetimePoints()));
				state.addDust(dust);
				return dust + " dust";
			}
			case FRUIT_BLAST:
			{
				state.addPassXp(GnomeFood.FRUIT_BLAST_PASS_XP);
				return (long) GnomeFood.FRUIT_BLAST_PASS_XP + " pass XP";
			}
			case TANGLED_TOADS_LEGS:
			{
				PackTier tier = freePackTier(state, hourlyIncome);
				if (tier == null)
				{
					return null;
				}
				packs.openFree(state, tier, CardSet.QUESTS, 1, rewards);
				return "a free " + tier.getDisplayName();
			}
			case WIZARD_BLIZZARD:
			{
				double share = BLIZZARD_MIN_HOURS
					+ random.nextDouble() * (BLIZZARD_MAX_HOURS - BLIZZARD_MIN_HOURS);
				double points = hourlyIncome * share;
				state.addPoints(points);
				return String.format("%.0f minutes of income", share * 60d);
			}
			default:
				// spicy crunchies and the worm hole are read off the clock instead
				return null;
		}
	}

	/**
	 * The best pack a free one can be. Bounded by what the account earns rather
	 * than always being the top tier, which at Mythic prices would be worth
	 * sixteen hours of income and make everything else pointless.
	 */
	PackTier freePackTier(DopamineState state, double hourlyIncome)
	{
		double budget = Math.max(PackTier.SCRAP.getCost(), hourlyIncome * PACK_BUDGET_HOURS);
		PackTier best = null;
		for (PackTier tier : PackTier.values())
		{
			if (tier.isUnlockedAt(state.getLifetimePoints()) && tier.getCost() <= budget)
			{
				best = tier;
			}
		}
		return best == null ? PackTier.SCRAP : best;
	}
}
