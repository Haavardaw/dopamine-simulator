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
import com.dopaminesimulator.cards.CardSet;
import com.dopaminesimulator.cards.Rarity;
import com.dopaminesimulator.core.Balance;
import com.dopaminesimulator.core.DopamineState;
import com.dopaminesimulator.core.Reward;
import com.dopaminesimulator.core.RewardQueue;
import java.util.Random;

public class CollectionService
{
	private final Random random;

	public CollectionService()
	{
		this(new Random());
	}

	public CollectionService(Random random)
	{
		this.random = random;
	}

	private static final int EPIC_SHARDS_PER_LEGENDARY_DUPE = 3;
	private static final int SHARDS_PER_DUPE = 1;
	public boolean grant(DopamineState state, Card card, RewardQueue rewards)
	{
		return grant(state, card, rewards, false);
	}

	public boolean grant(DopamineState state, Card card, RewardQueue rewards, boolean fromFusion)
	{
		return grant(state, card, rewards, fromFusion, 1);
	}

	public boolean grant(DopamineState state, Card card, RewardQueue rewards, boolean fromFusion,
		int copies)
	{
		int granted = Math.max(1, copies);
		rollShiny(state, card, rewards);
		rollGilded(state, card, rewards);
		if (state.owns(card.getId()))
		{
			int starsBefore = state.getStars(card.getId());
			state.addCopies(card.getId(), granted);
			int starsAfter = state.getStars(card.getId());
			if (card.getRarity() == Rarity.LEGENDARY)
			{
				state.addShards(Rarity.EPIC, EPIC_SHARDS_PER_LEGENDARY_DUPE * granted);
			}
			else
			{
				state.addShards(card.getRarity(), SHARDS_PER_DUPE * granted);
			}
			if (starsAfter > starsBefore)
			{
				rewards.push(Reward.starUp(card, starsAfter).withCopies(granted));
			}
			else
			{
				rewards.push(Reward.duplicate(card,
					card.getRarity() == Rarity.LEGENDARY
						? EPIC_SHARDS_PER_LEGENDARY_DUPE
						: SHARDS_PER_DUPE).withCopies(granted));
			}
			return false;
		}
		state.addCopies(card.getId(), granted);
		rewards.push((fromFusion ? Reward.fusion(card) : Reward.newCard(card)).withCopies(granted));
		checkSetCompletion(state, card.getSet(), rewards);
		return true;
	}
	private void rollShiny(DopamineState state, Card card, RewardQueue rewards)
	{
		if (state.isShiny(card.getId()))
		{
			return;
		}

		if (random.nextInt(Balance.SHINY_ONE_IN) == 0 && state.makeShiny(card.getId()))
		{
			rewards.push(Reward.shiny(card));
		}
	}

	private void rollGilded(DopamineState state, Card card, RewardQueue rewards)
	{
		if (state.isGilded(card.getId()))
		{
			return;
		}

		if (random.nextInt(Balance.GILDED_ONE_IN) == 0 && state.makeGilded(card.getId()))
		{
			rewards.push(Reward.gilded(card));
		}
	}

	private void checkSetCompletion(DopamineState state, CardSet set, RewardQueue rewards)
	{
		if (state.getCompletedSets().contains(set))
		{
			return;
		}
		if (state.isSetComplete(set))
		{
			state.getCompletedSets().add(set);
			rewards.push(Reward.setComplete(set));
		}
	}
}
