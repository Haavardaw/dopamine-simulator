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
import com.dopaminesimulator.feats.FeatTrack;
import com.dopaminesimulator.packs.PackTier;
import com.dopaminesimulator.points.PointSource;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashSet;
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

	private Set<String> shinyCards = new LinkedHashSet<>();

	private Set<String> gildedCards = new LinkedHashSet<>();

	private Set<CardSet> completedSets = EnumSet.noneOf(CardSet.class);

	private Map<FeatTrack, Long> featProgress = new EnumMap<>(FeatTrack.class);

	private Set<String> npcsKilled = new LinkedHashSet<>();

	private Set<String> skillsMaxed = new LinkedHashSet<>();

	private Set<String> achievements = new LinkedHashSet<>();

	private int passSeason = 1;

	private double passXp;

	private boolean passPremium;

	private Set<Integer> passClaimedFree = new LinkedHashSet<>();

	private Set<Integer> passClaimedPremium = new LinkedHashSet<>();

	public void ensureInitialised()
	{
		if (sourceUpgrades == null)
		{
			sourceUpgrades = new HashMap<>();
		}
		if (shinyCards == null)
		{
			shinyCards = new LinkedHashSet<>();
		}
		if (gildedCards == null)
		{
			gildedCards = new LinkedHashSet<>();
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
		if (featProgress == null)
		{
			featProgress = new EnumMap<>(FeatTrack.class);
		}
		if (npcsKilled == null)
		{
			npcsKilled = new LinkedHashSet<>();
		}
		if (skillsMaxed == null)
		{
			skillsMaxed = new LinkedHashSet<>();
		}
		if (achievements == null)
		{
			achievements = new LinkedHashSet<>();
		}
		if (passClaimedFree == null)
		{
			passClaimedFree = new LinkedHashSet<>();
		}
		if (passClaimedPremium == null)
		{
			passClaimedPremium = new LinkedHashSet<>();
		}
		if (passSeason < 1)
		{
			passSeason = 1;
		}
	}

	public void addPassXp(double amount)
	{
		if (amount > 0d)
		{
			passXp += amount;
		}
	}

	public boolean isPassTierClaimed(int tier, boolean premium)
	{
		return (premium ? passClaimedPremium : passClaimedFree).contains(tier);
	}

	public boolean claimPassTier(int tier, boolean premium)
	{
		return (premium ? passClaimedPremium : passClaimedFree).add(tier);
	}

	public void startNextSeason()
	{
		passSeason++;
		passXp = 0d;
		passPremium = false;
		passClaimedFree.clear();
		passClaimedPremium.clear();
	}

	public boolean hasAchievement(String id)
	{
		return achievements.contains(id);
	}

	public boolean awardAchievement(String id)
	{
		return achievements.add(id);
	}

	public void resetFeats()
	{
		featProgress.clear();
		npcsKilled.clear();
		skillsMaxed.clear();
		achievements.clear();
	}

	public long getFeatProgress(FeatTrack track)
	{
		Long value = featProgress.get(track);
		return value == null ? 0L : value;
	}

	public void addFeatProgress(FeatTrack track, long amount)
	{
		if (amount <= 0L)
		{
			return;
		}
		featProgress.merge(track, amount, Long::sum);
	}

	public void raiseFeatProgress(FeatTrack track, long value)
	{
		if (value > getFeatProgress(track))
		{
			featProgress.put(track, value);
		}
	}

	public boolean recordNpcKilled(String name)
	{
		if (name == null || name.isEmpty())
		{
			return false;
		}
		return npcsKilled.add(name);
	}

	public boolean recordSkillMaxed(String skill)
	{
		return skill != null && !skill.isEmpty() && skillsMaxed.add(skill);
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
			total += starValue(card.getId());
		}
		return total;
	}

	public int starValue(String cardId)
	{
		int stars = getStars(cardId);
		if (isShiny(cardId))
		{
			stars *= Balance.SHINY_STAR_MULTIPLIER;
		}
		if (isGilded(cardId))
		{
			stars += stars * Balance.GILDED_STAR_BONUS_PERCENT / 100;
		}
		return stars;
	}

	public boolean isGilded(String cardId)
	{
		return gildedCards.contains(cardId);
	}

	public boolean makeGilded(String cardId)
	{
		return gildedCards.add(cardId);
	}

	public int getGildedCount()
	{
		return gildedCards.size();
	}

	public boolean isShiny(String cardId)
	{
		return shinyCards.contains(cardId);
	}

	public boolean makeShiny(String cardId)
	{
		return shinyCards.add(cardId);
	}

	public int getShinyCount()
	{
		return shinyCards.size();
	}
	public boolean isSetComplete(CardSet set)
	{
		return CardCatalogue.bySet(set).stream().map(Card::getId).allMatch(this::owns);
	}
}
