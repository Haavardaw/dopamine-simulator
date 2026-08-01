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

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Views over {@link Cards}. The catalogue used to build its contents here, from a
 * mix of hand-written lists and loops over the client's own enums; the cards
 * themselves now live in {@link Cards} so that referring to one is a compile-time
 * reference and its save id no longer depends on its display name.
 */
public final class CardCatalogue
{
	private static final List<Card> CARDS;
	private static final Map<String, Card> BY_ID;
	private static final Map<Rarity, List<Card>> BY_RARITY;
	private static final Map<CardSet, List<Card>> BY_SET;

	static
	{
		List<Card> cards = new ArrayList<>(Cards.values().length);
		Map<String, Card> byId = new HashMap<>();
		Map<Rarity, List<Card>> byRarity = new EnumMap<>(Rarity.class);
		Map<CardSet, List<Card>> bySet = new EnumMap<>(CardSet.class);
		for (Rarity rarity : Rarity.values())
		{
			byRarity.put(rarity, new ArrayList<>());
		}
		for (CardSet set : CardSet.values())
		{
			bySet.put(set, new ArrayList<>());
		}

		for (Cards entry : Cards.values())
		{
			Card card = entry.getCard();
			cards.add(card);
			if (byId.put(card.getId(), card) != null)
			{
				throw new IllegalStateException("Duplicate card id: " + card.getId());
			}
			byRarity.get(card.getRarity()).add(card);
			bySet.get(card.getSet()).add(card);
		}

		CARDS = Collections.unmodifiableList(cards);
		BY_ID = Collections.unmodifiableMap(byId);
		BY_RARITY = Collections.unmodifiableMap(byRarity);
		BY_SET = Collections.unmodifiableMap(bySet);
	}

	private CardCatalogue()
	{
	}

	public static List<Card> all()
	{
		return CARDS;
	}

	public static Card byId(String id)
	{
		return BY_ID.get(id);
	}

	public static List<Card> byRarity(Rarity rarity)
	{
		return Collections.unmodifiableList(BY_RARITY.get(rarity));
	}

	public static List<Card> bySet(CardSet set)
	{
		return Collections.unmodifiableList(BY_SET.get(set));
	}

	public static List<Card> bySetAndRarity(CardSet set, Rarity rarity)
	{
		List<Card> matching = new ArrayList<>();
		for (Card card : BY_SET.get(set))
		{
			if (card.getRarity() == rarity)
			{
				matching.add(card);
			}
		}
		return matching;
	}

	public static int size()
	{
		return CARDS.size();
	}
}
