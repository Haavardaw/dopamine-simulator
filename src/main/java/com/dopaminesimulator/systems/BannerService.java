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

import com.dopaminesimulator.cards.Card;
import com.dopaminesimulator.cards.CardCatalogue;
import com.dopaminesimulator.cards.CardSet;
import com.dopaminesimulator.cards.Rarity;
import com.dopaminesimulator.core.DopamineState;
import com.dopaminesimulator.core.RewardQueue;
import com.dopaminesimulator.packs.PackTier;
import java.util.List;
import java.util.Random;

public class BannerService
{
	public static final double PULL_COST = 300_000d;
	public static final int HARD_PITY = 100;
	public static final int SOFT_PITY_FROM = 75;
	public static final double BASE_RATE = 0.006d;
	public static final double SOFT_PITY_STEP = 0.035d;
	private static final int FEATURED_COPIES = 40;

	private final Random random;
	private final PackService packs;
	private final CollectionService collection;

	public BannerService(Random random, PackService packs, CollectionService collection)
	{
		this.random = random;
		this.packs = packs;
		this.collection = collection;
	}

	public Card featured(DopamineState state)
	{
		String id = state.getBannerCardId();
		Card card = id == null ? null : CardCatalogue.byId(id);
		if (card == null)
		{
			card = roll(state);
		}
		return card;
	}

	public double rateAt(int pity)
	{
		if (pity >= HARD_PITY - 1)
		{
			return 1d;
		}
		if (pity < SOFT_PITY_FROM)
		{
			return BASE_RATE;
		}
		return Math.min(1d, BASE_RATE + (pity - SOFT_PITY_FROM + 1) * SOFT_PITY_STEP);
	}

	public boolean canPull(DopamineState state)
	{
		return state.getPoints() >= PULL_COST;
	}

	public Card pull(DopamineState state, CardSet targetSet, RewardQueue rewards)
	{
		if (!canPull(state) || !state.spendPoints(PULL_COST))
		{
			return null;
		}

		Card prize = featured(state);
		state.setBannerPulls(state.getBannerPulls() + 1);
		packs.openFree(state, PackTier.PRISMATIC, targetSet, 1, rewards);

		if (random.nextDouble() < rateAt(state.getBannerPity()))
		{
			state.setBannerPity(0);
			collection.grant(state, prize, rewards, false, FEATURED_COPIES);
			roll(state);
			return prize;
		}

		state.setBannerPity(state.getBannerPity() + 1);
		return null;
	}

	public Card roll(DopamineState state)
	{
		List<Card> pool = CardCatalogue.byRarity(Rarity.LEGENDARY);
		Card chosen = pool.get(random.nextInt(pool.size()));
		state.setBannerCardId(chosen.getId());
		return chosen;
	}
}
