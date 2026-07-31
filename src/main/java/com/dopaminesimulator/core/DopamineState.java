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
package com.dopaminesimulator.core;

import com.dopaminesimulator.cards.Card;
import com.dopaminesimulator.cards.CardCatalogue;
import com.dopaminesimulator.cards.CardSet;
import com.dopaminesimulator.cards.Rarity;
import com.dopaminesimulator.packs.PackTier;
import com.dopaminesimulator.points.PointSource;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DopamineState
{
	private double points;
	private double lifetimePoints;
	private Map<String, Integer> sourceUpgrades = new HashMap<>();

	private long tick;

	private long lastEarningTick = Long.MIN_VALUE;

	private long totalPacksOpened;

	private int packsSinceLastRare;
	private double lifetimeWeightedXp;
	private Map<String, Integer> cardCounts = new HashMap<>();

	private Map<Rarity, Integer> shards = new EnumMap<>(Rarity.class);

	private Set<CardSet> completedSets = EnumSet.noneOf(CardSet.class);

	public void ensureInitialised()
	{
		if (sourceUpgrades == null)
		{
			sourceUpgrades = new HashMap<>();
		}
		if (cardCounts == null)
		{
			cardCounts = new HashMap<>();
		}
		if (shards == null)
		{
			shards = new EnumMap<>(Rarity.class);
		}
		if (completedSets == null)
		{
			completedSets = EnumSet.noneOf(CardSet.class);
		}
	}
	public boolean isIdle()
	{
		return lastEarningTick == Long.MIN_VALUE
			|| tick - lastEarningTick >= Balance.IDLE_AFTER_TICKS;
	}

	public void addPoints(double amount)
	{
		if (amount <= 0d)
		{
			return;
		}
		points += amount;
		lifetimePoints += amount;
	}
	public boolean spendPoints(double cost)
	{
		if (cost > points)
		{
			return false;
		}
		points -= cost;
		return true;
	}
	public int getSourceUpgradeLevel(PointSource source)
	{
		return sourceUpgrades.getOrDefault(source.name(), 0);
	}
	public void addSourceUpgrades(PointSource source, int levels)
	{
		sourceUpgrades.merge(source.name(), levels, Integer::sum);
	}

	public boolean isSourceUnlocked(PointSource source)
	{
		return source.isUnlockedAt(lifetimePoints);
	}

	public PointSource nextLockedSource()
	{
		for (PointSource source : PointSource.values())
		{
			if (!isSourceUnlocked(source))
			{
				return source;
			}
		}
		return null;
	}
	public boolean isPackUnlocked(PackTier tier)
	{
		return tier.isUnlockedAt(lifetimePoints);
	}

	public int getCopies(String cardId)
	{
		return cardCounts.getOrDefault(cardId, 0);
	}
	public boolean owns(String cardId)
	{
		return getCopies(cardId) > 0;
	}
	public void addCopy(String cardId)
	{
		addCopies(cardId, 1);
	}

	public void addCopies(String cardId, int copies)
	{
		if (copies > 0)
		{
			cardCounts.merge(cardId, copies, Integer::sum);
		}
	}

	public int getShards(Rarity rarity)
	{
		return shards.getOrDefault(rarity, 0);
	}
	public void addShards(Rarity rarity, int count)
	{
		shards.merge(rarity, count, Integer::sum);
	}


	public int getUniqueCardsOwned()
	{
		return (int) cardCounts.values().stream().filter(c -> c != null && c > 0).count();
	}
	public int getStars(String cardId)
	{
		Card card = CardCatalogue.byId(cardId);
		if (card == null)
		{
			return 0;
		}
		return card.getRarity().starsFor(getCopies(cardId));
	}
	public int getTotalStars()
	{
		int total = 0;
		for (Card card : CardCatalogue.all())
		{
			total += getStars(card.getId());
		}
		return total;
	}
	public int getStarsInSet(CardSet set)
	{
		int total = 0;
		for (Card card : CardCatalogue.bySet(set))
		{
			total += getStars(card.getId());
		}
		return total;
	}
	public boolean isSetComplete(CardSet set)
	{
		return CardCatalogue.bySet(set).stream().map(Card::getId).allMatch(this::owns);
	}
}
