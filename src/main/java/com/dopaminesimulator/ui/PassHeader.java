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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MultipleGradientPaint;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;
import java.awt.geom.Point2D;
import javax.swing.JComponent;

public class PassHeader extends JComponent
{
	public static final int HEIGHT = 92;

	private static final Color PLATE_TOP = new Color(0x2B, 0x2A, 0x33);
	private static final Color GOLD = new Color(0xF2, 0xC8, 0x5A);
	private static final Color PLATE_BOTTOM = new Color(0x15, 0x15, 0x19);
	private static final Color TRACK = new Color(0x0E, 0x0E, 0x11);

	private final int season;
	private final String theme;
	private final Color accent;
	private final int tier;
	private final int tiers;
	private final double into;
	private final double need;
	private final boolean premium;

	public PassHeader(int season, String theme, Color accent, int tier, int tiers,
		double into, double need, boolean premium)
	{
		this.season = season;
		this.theme = theme;
		this.accent = accent;
		this.tier = tier;
		this.tiers = tiers;
		this.into = into;
		this.need = need;
		this.premium = premium;
		setPreferredSize(new Dimension(0, HEIGHT));
		setMaximumSize(new Dimension(Integer.MAX_VALUE, HEIGHT));
		setMinimumSize(new Dimension(0, HEIGHT));
		setOpaque(false);
	}

	@Override
	protected void paintComponent(Graphics graphics)
	{
		Graphics2D g = (Graphics2D) graphics.create();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
			RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		int width = getWidth();

		g.setPaint(new GradientPaint(0, 0, PLATE_TOP, width, HEIGHT, PLATE_BOTTOM));
		g.fillRoundRect(0, 0, width, HEIGHT, 9, 9);

		g.setPaint(new RadialGradientPaint(new Point2D.Float(width * 0.12f, 0f),
			Math.max(60f, width * 0.85f), new float[]{0f, 1f},
			new Color[]{withAlpha(accent, 96), withAlpha(accent, 0)},
			MultipleGradientPaint.CycleMethod.NO_CYCLE));
		g.fillRoundRect(0, 0, width, HEIGHT, 9, 9);

		drawSheen(g, width);

		g.setColor(withAlpha(premium ? GOLD : accent, 150));
		g.setStroke(new BasicStroke(1.5f));
		g.drawRoundRect(0, 0, width - 1, HEIGHT - 1, 9, 9);
		g.setPaint(new GradientPaint(0, 8, brighten(accent), 0, HEIGHT - 8, accent.darker()));
		g.fillRoundRect(0, 8, 4, HEIGHT - 16, 2, 2);

		g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 9));
		g.setColor(withAlpha(accent, 225));
		g.drawString("SEASON " + season, 13, 20);

		g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 17));
		g.setColor(new Color(0x08, 0x08, 0x0A));
		g.drawString(theme, 14, 43);
		g.setPaint(new GradientPaint(13, 28, Color.WHITE, 13, 44, brighten(accent)));
		g.drawString(theme, 13, 42);

		String badge = premium ? "PREMIUM" : "FREE";
		g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 9));
		FontMetrics metrics = g.getFontMetrics();
		int badgeW = metrics.stringWidth(badge) + 14;
		int badgeX = width - badgeW - 11;
		if (premium)
		{
			g.setPaint(new GradientPaint(badgeX, 10, GOLD, badgeX, 26, GOLD.darker()));
		}
		else
		{
			g.setPaint(new GradientPaint(badgeX, 10, new Color(0x3A, 0x3A, 0x42),
				badgeX, 26, new Color(0x28, 0x28, 0x2E)));
		}
		g.fillRoundRect(badgeX, 11, badgeW, 15, 7, 7);
		g.setColor(premium ? new Color(0x12, 0x0E, 0x04) : new Color(0x9A, 0x9A, 0xA4));
		g.drawString(badge, badgeX + 7, 22);

		String tierLabel = "TIER " + tier + " / " + tiers;
		g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
		metrics = g.getFontMetrics();
		g.setColor(brighten(accent));
		g.drawString(tierLabel, width - metrics.stringWidth(tierLabel) - 11, 44);

		int barY = 60;
		int barH = 8;
		int barW = width - 26;
		g.setColor(TRACK);
		g.fillRoundRect(13, barY, barW, barH, 4, 4);

		double progress = need <= 0d ? 1d : Math.min(1d, into / need);
		int filled = (int) Math.round(barW * progress);
		if (filled > 0)
		{
			g.setPaint(new GradientPaint(13, barY, accent.darker(), 13 + filled, barY,
				brighten(accent)));
			g.fillRoundRect(13, barY, filled, barH, 4, 4);
			g.setColor(withAlpha(Color.WHITE, 60));
			g.fillRoundRect(13, barY + 1, filled, barH / 2 - 1, 3, 3);
		}
		g.setColor(withAlpha(accent, 90));
		g.drawRoundRect(13, barY, barW, barH, 4, 4);

		g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
		g.setColor(new Color(0xA0, 0xA0, 0xAA));
		g.drawString(need <= 0d
			? "Season complete - claim what is left, then roll on"
			: (long) into + " / " + (long) need + " pass xp to tier " + (tier + 1),
			13, HEIGHT - 10);

		g.dispose();
	}

	private void drawSheen(Graphics2D g, int width)
	{
		Shape clip = g.getClip();
		g.setClip(new RoundRectangle2D.Float(0, 0, width, HEIGHT, 9, 9));
		g.setPaint(new GradientPaint(width * 0.35f, 0, withAlpha(Color.WHITE, 0),
			width * 0.62f, HEIGHT, withAlpha(Color.WHITE, 22)));
		g.fillRect(0, 0, width, HEIGHT);
		g.setClip(clip);
	}

	private static Color brighten(Color colour)
	{
		return new Color(Math.min(255, colour.getRed() + 70),
			Math.min(255, colour.getGreen() + 70), Math.min(255, colour.getBlue() + 70));
	}

	private static Color withAlpha(Color colour, int alpha)
	{
		return new Color(colour.getRed(), colour.getGreen(), colour.getBlue(), alpha);
	}
}
