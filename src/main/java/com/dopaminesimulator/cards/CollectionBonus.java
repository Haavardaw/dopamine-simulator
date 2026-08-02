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
package com.dopaminesimulator.cards;

import com.dopaminesimulator.core.DopamineState;
import com.dopaminesimulator.points.PointSource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Which cards make which source earn more.
 *
 * <p>A source reads several sets, grouped by what the cards are about rather
 * than where they were swept from: the combat skills feed Combat, the gathering
 * and artisan skills feed Experience, the places you can reach feed Travel.
 *
 * <p>Every set belongs to exactly one source. It used to be one set each, which
 * left the twenty three skill sets, two thirds of the catalogue, feeding
 * nothing at all.
 */
public final class CollectionBonus
{
	private static final double PER_ROOT_STAR = 0.04d;

	private static final Map<PointSource, List<CardSet>> SETS = new EnumMap<>(PointSource.class);
	private static final Map<CardSet, PointSource> OWNER = new EnumMap<>(CardSet.class);

	static
	{
		feed(PointSource.EXPERIENCE, CardSet.SKILLS, CardSet.MINING, CardSet.SMITHING,
			CardSet.FISHING, CardSet.WOODCUTTING, CardSet.FIREMAKING, CardSet.CRAFTING,
			CardSet.FLETCHING, CardSet.RUNECRAFT, CardSet.CONSTRUCTION);
		feed(PointSource.COMBAT, CardSet.BOSSES, CardSet.ATTACK, CardSet.STRENGTH,
			CardSet.DEFENCE, CardSet.RANGED, CardSet.MAGIC);
		feed(PointSource.RECOVERY, CardSet.SLAYER, CardSet.COOKING, CardSet.HERBLORE,
			CardSet.FARMING);
		feed(PointSource.TRAVEL, CardSet.DIARIES, CardSet.AGILITY, CardSet.SAILING,
			CardSet.AREAS);
		feed(PointSource.WEALTH, CardSet.ITEMS, CardSet.THIEVING, CardSet.HUNTER);
		feed(PointSource.CLICK, CardSet.MINIGAMES);
		feed(PointSource.SUFFERING, CardSet.PRAYER);
		feed(PointSource.IDLING, CardSet.QUESTS);
	}

	private CollectionBonus()
	{
	}

	private static void feed(PointSource source, CardSet... sets)
	{
		List<CardSet> list = new ArrayList<>();
		for (CardSet set : sets)
		{
			list.add(set);
			OWNER.put(set, source);
		}
		SETS.put(source, Collections.unmodifiableList(list));
	}

	/** The sets a source reads. Never empty. */
	public static List<CardSet> setsFor(PointSource source)
	{
		return SETS.getOrDefault(source, Collections.singletonList(CardSet.QUESTS));
	}

	/** The set a source is named for, where only one will fit. */
	public static CardSet setFor(PointSource source)
	{
		return setsFor(source).get(0);
	}

	public static PointSource sourceFor(CardSet set)
	{
		return OWNER.getOrDefault(set, PointSource.IDLING);
	}

	public static double multiplierFor(DopamineState state, PointSource source)
	{
		long stars = 0;
		double collections = 1d;
		for (CardSet set : setsFor(source))
		{
			stars += state.getWeightedStarsInSet(set);
			collections *= CardCollection.multiplierFor(state, set);
		}
		return (1d + Math.sqrt(stars) * PER_ROOT_STAR) * collections;
	}

	public static int percentFor(DopamineState state, PointSource source)
	{
		return (int) Math.round((multiplierFor(state, source) - 1d) * 100d);
	}
}
