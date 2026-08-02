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

/**
 * What a finished card pays out in, and what buys a card you choose.
 *
 * <p>The collection had three answers to "what does a duplicate do": it advanced
 * the star track, it granted a shard of its rarity, and separately a wildcard
 * could add a star outright. Shards then bought copies, which is what the
 * duplicate would have done anyway, so the loop fed itself.
 *
 * <p>One answer now. A copy advances the card. Once the card is finished the
 * copy becomes dust instead, and dust buys a copy of whichever card you point it
 * at. That is the only way in the game to choose, packs and banners deciding for
 * you, so a shelf of finished commons turns into progress on the one legendary
 * you are missing.
 */
public final class Dust
{
	/** A wildcard added a star outright, so it converts to a decent lump. */
	public static final int PER_WILDCARD = 40;

	private Dust()
	{
	}

	/**
	 * What one further copy of an already finished card is worth.
	 *
	 * <p>Scaled by rarity because a spare legendary is a far scarcer thing to
	 * hold than a spare common, and pouring both into the same pot at the same
	 * rate would make the legendary feel wasted.
	 */
	public static int fromOverflow(Rarity rarity)
	{
		switch (rarity)
		{
			case COMMON:
				return 1;
			case UNCOMMON:
				return 2;
			case RARE:
				return 4;
			case EPIC:
				return 9;
			default:
				return 20;
		}
	}

	/**
	 * What one copy of a chosen card costs.
	 *
	 * <p>Set against {@link #fromOverflow} so that finishing a card and pouring
	 * its overflow back in buys roughly a tenth of another card of the same
	 * rarity per copy: enough that duplicates always mean something, not so much
	 * that dust outruns opening packs.
	 */
	public static int costPerCopy(Rarity rarity)
	{
		return fromOverflow(rarity) * 10;
	}

	/** What it costs to take a card the whole way with dust alone. */
	public static long costToMax(Rarity rarity)
	{
		return (long) costPerCopy(rarity) * rarity.copiesForMaxStars();
	}
}
