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
package com.dopaminesimulator.points;

public class ClickState
{
	public static final double SURGE_UNLOCK_AT = 25_000d;

	/** Roughly seven and a half surges an hour before any upgrades. */
	public static final double BASE_SURGE_CHANCE_PER_TICK = 0.00125d;

	/**
	 * The most the click upgrade can ever multiply the catch rate by.
	 *
	 * <p>It used to add a flat share of the base rate per level, which was fine
	 * when a surge was only a click multiplier. Seven dishes later, four of them
	 * paying a share of total income, and the same flat rate took food from a
	 * third of an hour's earnings at level nought to nearly twice an hour's at
	 * level a hundred and twenty, which the cost curve does not stop you reaching
	 * in a long game.
	 *
	 * <p>So it approaches a ceiling instead of climbing forever. Food is worth
	 * about thirty percent on top of an hour untouched and around half at the
	 * limit: active play stays well rewarded without making the rest pointless.
	 */
	public static final double SURGE_RATE_CAP = 1.75d;

	/**
	 * The last click level worth selling, and the one that reaches the ceiling.
	 *
	 * <p>The ladder is short and it finishes. Every other source grows eleven
	 * percent a level forever, so its ladder can be geometric and still worth
	 * climbing; this one buys a share of a bounded thing, and pricing a bounded
	 * benefit on a curve that doubles every four levels would mean the last few
	 * cost millions for a percent each. Twenty levels, evenly spaced, bought out
	 * for well under a hundred thousand points, and then it says MAX.
	 */
	public static final int MAX_LEVEL = 20;

	/**
	 * The two dishes that pay out at once would otherwise appear for a single
	 * frame, so the button keeps showing whatever landed for this long whether or
	 * not it is still doing anything.
	 */
	public static final long SHOW_FOR_MS = 4_000L;

	private long surgeEndsAt;
	private long showUntil;
	private long lastSurgeStartedAt;
	private GnomeFood active;

	/**
	 * Clicking is not upgraded by paying it more per press. The upgrade buys
	 * surges instead, so a level makes the good moments happen more often rather
	 * than making the ordinary press larger.
	 */
	public static double surgeChancePerTick(int clickLevel)
	{
		return BASE_SURGE_CHANCE_PER_TICK * rateMultiplier(clickLevel);
	}

	/** How much more often dishes come than they do untouched. */
	public static double rateMultiplier(int clickLevel)
	{
		int level = Math.max(0, Math.min(MAX_LEVEL, clickLevel));
		return 1d + (SURGE_RATE_CAP - 1d) * level / MAX_LEVEL;
	}

	public static double surgesPerHour(int clickLevel)
	{
		return surgeChancePerTick(clickLevel) * 6_000d;
	}

	/** Whether a dish that lasts is still running. */
	public boolean isSurging(long nowMs)
	{
		return nowMs < surgeEndsAt;
	}

	/** What landed, whether or not it is still running. Null before the first. */
	public GnomeFood getActive(long nowMs)
	{
		return isSurging(nowMs) ? active : null;
	}

	/** What landed most recently, for showing it after an instant one. */
	public GnomeFood getLast()
	{
		return active;
	}

	public void start(GnomeFood food, long nowMs)
	{
		active = food;
		lastSurgeStartedAt = nowMs;
		surgeEndsAt = food.lasts() ? nowMs + food.getDurationMs() : 0L;
		showUntil = Math.max(surgeEndsAt, nowMs + SHOW_FOR_MS);
	}

	/** What the button should be showing, if anything. */
	public GnomeFood getShown(long nowMs)
	{
		return nowMs < showUntil ? active : null;
	}

	public double multiplier(long nowMs)
	{
		GnomeFood food = getActive(nowMs);
		return food == null ? 1d : food.clickMultiplier();
	}

	public double incomeMultiplier(long nowMs)
	{
		GnomeFood food = getActive(nowMs);
		return food == null ? 1d : food.incomeMultiplier();
	}

	public double secondsRemaining(long nowMs)
	{
		return Math.max(0d, (surgeEndsAt - nowMs) / 1000d);
	}

	public void clear()
	{
		surgeEndsAt = 0L;
		showUntil = 0L;
		lastSurgeStartedAt = 0L;
		active = null;
	}
}
