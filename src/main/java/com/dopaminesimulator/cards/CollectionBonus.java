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

public final class CollectionBonus
{
	private static final double PER_ROOT_STAR = 0.04d;
	private CollectionBonus()
	{
	}
	public static CardSet setFor(PointSource source)
	{
		switch (source)
		{
			case EXPERIENCE:
				return CardSet.SKILLS;
			case COMBAT:
				return CardSet.BOSSES;
			case WEALTH:
				return CardSet.ITEMS;
			case TRAVEL:
				return CardSet.DIARIES;
			case RECOVERY:
				return CardSet.SLAYER;
			case CLICK:
				return CardSet.MINIGAMES;
			default:
				return CardSet.QUESTS;
		}
	}
	public static PointSource sourceFor(CardSet set)
	{
		for (PointSource source : PointSource.values())
		{
			if (setFor(source) == set)
			{
				return source;
			}
		}
		return PointSource.IDLING;
	}
	public static double multiplierFor(DopamineState state, PointSource source)
	{
		CardSet set = setFor(source);
		return (1d + Math.sqrt(state.getStarsInSet(set)) * PER_ROOT_STAR)
			* CardCollection.multiplierFor(state, set);
	}
	public static int percentFor(DopamineState state, PointSource source)
	{
		return (int) Math.round((multiplierFor(state, source) - 1d) * 100d);
	}
}
