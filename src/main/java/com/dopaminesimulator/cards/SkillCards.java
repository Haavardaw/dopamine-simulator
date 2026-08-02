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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * The skill guide unlocks, read from a bundled data file rather than written as
 * enum constants.
 *
 * <p>Three thousand of them will not fit in an enum: the static initialiser
 * passes the sixty four kilobyte method limit somewhere under two thousand
 * constants, and {@link Cards} already holds six hundred. It is the right shape
 * anyway. The enum exists so that code naming a card stops compiling when the
 * card moves, and no code will ever name Crude wooden chair. These are bulk
 * content, regenerated from a fresh sweep of the in-game guides.
 *
 * <p>Ids still come from the set and the name, exactly as the enum's do, so the
 * two kinds of card are indistinguishable once loaded.
 */
public final class SkillCards
{
	private static final String RESOURCE = "/skill-cards.tsv";

	private SkillCards()
	{
	}

	static List<Card> load()
	{
		List<Card> out = new ArrayList<>();
		try (InputStream in = SkillCards.class.getResourceAsStream(RESOURCE))
		{
			if (in == null)
			{
				throw new IllegalStateException("missing " + RESOURCE);
			}
			try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(in, StandardCharsets.UTF_8)))
			{
				String line;
				int number = 0;
				while ((line = reader.readLine()) != null)
				{
					number++;
					if (line.isEmpty() || line.charAt(0) == '#')
					{
						continue;
					}
					String[] parts = line.split("\t", -1);
					if (parts.length < 5)
					{
						throw new IllegalStateException(
							RESOURCE + " line " + number + " has " + parts.length + " columns");
					}
					out.add(parse(parts, number));
				}
			}
		}
		catch (IOException e)
		{
			throw new UncheckedIOException("could not read " + RESOURCE, e);
		}
		return Collections.unmodifiableList(out);
	}

	private static Card parse(String[] parts, int number)
	{
		CardSet set;
		Rarity rarity;
		int itemId;
		try
		{
			set = CardSet.valueOf(parts[0]);
			rarity = Rarity.valueOf(parts[2]);
			itemId = Integer.parseInt(parts[3]);
		}
		catch (IllegalArgumentException e)
		{
			throw new IllegalStateException(RESOURCE + " line " + number + " is malformed", e);
		}

		String name = parts[4].trim();
		if (name.isEmpty())
		{
			throw new IllegalStateException(RESOURCE + " line " + number + " has no name");
		}
		return new Card(idFor(set, name), name, set, rarity, itemId, -1);
	}

	/**
	 * Matches how {@link Cards} builds an id, with one addition: the plus and
	 * minus that mark a potion's strength are spelled out first.
	 *
	 * <p>Stripping them collapsed the Chambers of Xeric potions onto each other,
	 * so Antipoison (+) and Antipoison (-) both became antipoison and the
	 * catalogue refused to load. No card in {@link Cards} carries either mark, so
	 * spelling them out here cannot move an id that already exists.
	 */
	private static String idFor(CardSet set, String name)
	{
		return set.name().toLowerCase(Locale.ROOT) + "-"
			+ name.toLowerCase(Locale.ROOT)
				.replace("(+)", " plus ")
				.replace("(-)", " minus ")
				.replaceAll("[^a-z0-9]+", "-")
				.replaceAll("(^-|-$)", "");
	}
}
