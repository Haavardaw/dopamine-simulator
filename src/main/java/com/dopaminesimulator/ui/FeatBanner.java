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
package com.dopaminesimulator.ui;

import com.dopaminesimulator.feats.Feat;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;

public final class FeatBanner
{
	public static final int WIDTH = 300;
	public static final int HEIGHT = 64;

	private static final Color PLATE_TOP = new Color(0x2A, 0x2A, 0x30);
	private static final Color PLATE_BOTTOM = new Color(0x16, 0x16, 0x1A);
	private static final Color CAPTION = new Color(0x9A, 0x9A, 0xA4);

	private FeatBanner()
	{
	}

	public static void draw(Graphics2D g, String title, int tier, double shine)
	{
		Color rank = Feat.tierColour(tier);

		g.setPaint(new GradientPaint(0, 0, PLATE_TOP, 0, HEIGHT, PLATE_BOTTOM));
		g.fillRoundRect(0, 0, WIDTH, HEIGHT, 8, 8);

		g.setColor(withAlpha(rank, 110));
		g.setStroke(new BasicStroke(1.5f));
		g.drawRoundRect(0, 0, WIDTH - 1, HEIGHT - 1, 8, 8);

		g.setColor(rank);
		g.fillRoundRect(0, 8, 4, HEIGHT - 16, 3, 3);

		drawSweep(g, rank, shine);
		drawMedal(g, rank, tier);

		int textX = 78;
		g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 9));
		g.setColor(CAPTION);
		g.drawString("FEAT EARNED", textX, 20);

		g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
		g.setColor(Color.WHITE);
		g.drawString(elide(g, title, WIDTH - textX - 12), textX, 39);

		g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
		g.setColor(rank);
		g.drawString("Rank " + tier + " of " + Feat.RANKS
			+ "   +" + Math.round(Feat.BONUS_PER_TIER * 100d) + "% to everything", textX, 54);
	}

	private static void drawMedal(Graphics2D g, Color rank, int tier)
	{
		int radius = 21;
		int centreX = 42;
		int centreY = HEIGHT / 2;

		g.setColor(withAlpha(rank, 45));
		g.fillOval(centreX - radius - 4, centreY - radius - 4, (radius + 4) * 2, (radius + 4) * 2);
		g.setColor(rank.darker().darker());
		g.fillOval(centreX - radius, centreY - radius, radius * 2, radius * 2);
		g.setColor(rank);
		g.setStroke(new BasicStroke(2.5f));
		g.drawOval(centreX - radius, centreY - radius, radius * 2, radius * 2);
		g.setStroke(new BasicStroke(1f));
		g.drawOval(centreX - radius + 5, centreY - radius + 5,
			(radius - 5) * 2, (radius - 5) * 2);

		String label = String.valueOf(Math.max(1, tier));
		g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 19));
		FontMetrics metrics = g.getFontMetrics();
		g.setColor(rank);
		g.drawString(label, centreX - metrics.stringWidth(label) / 2,
			centreY + metrics.getAscent() / 2 - 2);
	}

	private static void drawSweep(Graphics2D g, Color rank, double shine)
	{
		int x = (int) Math.round(-WIDTH * 0.4d + shine * WIDTH * 1.6d);
		g.setPaint(new GradientPaint(x, 0, withAlpha(rank, 0),
			x + 60, 0, withAlpha(rank, 34)));
		g.fillRoundRect(Math.max(0, x), 1, Math.min(120, WIDTH - Math.max(0, x)),
			HEIGHT - 2, 8, 8);
	}

	private static String elide(Graphics2D g, String text, int maxWidth)
	{
		FontMetrics metrics = g.getFontMetrics();
		if (metrics.stringWidth(text) <= maxWidth)
		{
			return text;
		}

		String trimmed = text;
		while (trimmed.length() > 1 && metrics.stringWidth(trimmed + "…") > maxWidth)
		{
			trimmed = trimmed.substring(0, trimmed.length() - 1);
		}
		return trimmed + "…";
	}

	private static Color withAlpha(Color colour, int alpha)
	{
		return new Color(colour.getRed(), colour.getGreen(), colour.getBlue(), alpha);
	}
}
