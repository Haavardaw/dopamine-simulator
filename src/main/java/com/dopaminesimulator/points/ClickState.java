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
	public static final long SURGE_DURATION_MS = 9_000L;
	public static final double SURGE_CHANCE_PER_TICK = 0.00125d;
	public static final double SURGE_MULTIPLIER = 25d;
	public static final double SURGE_UNLOCK_AT = 25_000d;

	/**
	 * The most clicking can add in an hour, as a share of what everything else
	 * earns. The cap is what lets a single click be worth having: without it,
	 * anything generous enough to feel good was also generous enough to beat
	 * playing the game, and anything safe enough not to was not worth pressing.
	 */
	public static final double HOURLY_SHARE_CAP = 0.35d;

	private static final long WINDOW_MS = 3_600_000L;

	/**
	 * Clicks landing closer together than this pay nothing extra.
	 *
	 * <p>Not really about the total, which the allowance already bounds, but
	 * about there being no reason to reach for an autoclicker: past a couple of
	 * presses a second the extra ones do nothing at all.
	 */
	private static final long MIN_GAP_MS = 250L;

	private long surgeEndsAt;
	private long lastSurgeStartedAt;
	private long windowStartedAt;
	private long lastPaidAt;
	private double earnedInWindow;

	/**
	 * Scales a click down as the hour's allowance runs out. Not persisted: it
	 * resets when you log in, which is a small kindness rather than a loophole,
	 * since the allowance refills every hour anyway.
	 */
	public double allowanceFor(double rawPayout, double otherIncomePerHour, long nowMs)
	{
		if (windowStartedAt == 0L || nowMs - windowStartedAt >= WINDOW_MS)
		{
			windowStartedAt = nowMs;
			earnedInWindow = 0d;
		}
		if (nowMs - lastPaidAt < MIN_GAP_MS)
		{
			return 0d;
		}
		double cap = otherIncomePerHour * HOURLY_SHARE_CAP;
		if (cap <= 0d)
		{
			lastPaidAt = nowMs;
			return rawPayout;
		}
		// a hard stop, not a taper. Paying even a small fraction past the cap
		// left it unbounded: eight percent of four clicks a second still came to
		// more than everything else earned.
		double remaining = Math.max(0d, cap - earnedInWindow);
		double paid = Math.min(rawPayout, remaining);
		earnedInWindow += paid;
		lastPaidAt = nowMs;
		return paid;
	}

	/** What is left of this hour's clicking allowance, as a share of it. */
	public double allowanceLeft(double otherIncomePerHour, long nowMs)
	{
		if (windowStartedAt == 0L || nowMs - windowStartedAt >= WINDOW_MS
			|| otherIncomePerHour <= 0d)
		{
			return 1d;
		}
		double cap = otherIncomePerHour * HOURLY_SHARE_CAP;
		return cap <= 0d ? 1d : Math.max(0d, 1d - earnedInWindow / cap);
	}

	public long allowanceResetsInMs(long nowMs)
	{
		if (windowStartedAt == 0L)
		{
			return 0L;
		}
		return Math.max(0L, WINDOW_MS - (nowMs - windowStartedAt));
	}

	public boolean isSurging(long nowMs)
	{
		return nowMs < surgeEndsAt;
	}
	public void startSurge(long nowMs)
	{
		surgeEndsAt = nowMs + SURGE_DURATION_MS;
		lastSurgeStartedAt = nowMs;
	}
	public double multiplier(long nowMs)
	{
		return isSurging(nowMs) ? SURGE_MULTIPLIER : 1d;
	}
	public double secondsRemaining(long nowMs)
	{
		return Math.max(0d, (surgeEndsAt - nowMs) / 1000d);
	}
	public void clear()
	{
		surgeEndsAt = 0L;
		lastSurgeStartedAt = 0L;
		windowStartedAt = 0L;
		lastPaidAt = 0L;
		earnedInWindow = 0d;
	}
}
