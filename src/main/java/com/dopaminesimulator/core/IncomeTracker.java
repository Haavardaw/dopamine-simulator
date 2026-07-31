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

import com.dopaminesimulator.points.PointSource;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.EnumMap;
import java.util.Map;
import lombok.Value;

public class IncomeTracker implements PointListener
{
	private static final long WINDOW_TICKS = 3000L;
	@Value
	public static class Entry
	{
		PointSource source;
		double amount;
		long tick;
	}
	private final Deque<Entry> entries = new ArrayDeque<>();
	private long latestTick;
	@Override
	public void onPointsGained(PointSource source, String detail, double amount, long tick)
	{
		latestTick = Math.max(latestTick, tick);
		entries.addLast(new Entry(source, amount, tick));
		prune();
	}
	private void prune()
	{
		while (!entries.isEmpty() && latestTick - entries.peekFirst().getTick() > WINDOW_TICKS)
		{
			entries.removeFirst();
		}
	}
	public void reset()
	{
		entries.clear();
		latestTick = 0;
	}

	public boolean isEmpty()
	{
		return entries.isEmpty();
	}

	public double perHour(PointSource source, long currentTick)
	{
		if (entries.isEmpty())
		{
			return 0d;
		}
		long oldest = entries.peekFirst().getTick();
		long elapsed = Math.max(1L, currentTick - oldest);
		double total = 0d;
		for (Entry entry : entries)
		{
			if (source == null || entry.getSource() == source)
			{
				total += entry.getAmount();
			}
		}
		return total / elapsed * Balance.TICKS_PER_HOUR;
	}
	public double totalPerHour(long currentTick)
	{
		return perHour(null, currentTick);
	}
	public Map<PointSource, Double> breakdown(long currentTick)
	{
		Map<PointSource, Double> rates = new EnumMap<>(PointSource.class);
		for (PointSource source : PointSource.values())
		{
			rates.put(source, perHour(source, currentTick));
		}
		return rates;
	}
}
