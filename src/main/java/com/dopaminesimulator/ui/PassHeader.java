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
import java.awt.geom.Point2D;
import javax.swing.JComponent;

public class PassHeader extends JComponent
{
	public static final int HEIGHT = 68;

	private static final Color PLATE_TOP = new Color(0x25, 0x25, 0x2B);
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
		g.setPaint(new GradientPaint(0, 0, PLATE_TOP, 0, HEIGHT, PLATE_BOTTOM));
		g.fillRoundRect(0, 0, width, HEIGHT, 8, 8);

		g.setPaint(new RadialGradientPaint(new Point2D.Float(width * 0.16f, 0f),
			Math.max(50f, width * 0.7f), new float[]{0f, 1f},
			new Color[]{withAlpha(accent, 62), withAlpha(accent, 0)},
			MultipleGradientPaint.CycleMethod.NO_CYCLE));
		g.fillRoundRect(0, 0, width, HEIGHT, 8, 8);

		g.setColor(withAlpha(accent, 120));
		g.setStroke(new BasicStroke(1.5f));
		g.drawRoundRect(0, 0, width - 1, HEIGHT - 1, 8, 8);
		g.setColor(accent);
		g.fillRoundRect(0, 9, 3, HEIGHT - 18, 2, 2);

		g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 9));
		g.setColor(withAlpha(accent, 210));
		g.drawString("SEASON " + season, 12, 17);

		g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
		g.setColor(Color.WHITE);
		g.drawString(theme, 12, 35);

		String badge = premium ? "PREMIUM" : "FREE";
		g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 9));
		FontMetrics metrics = g.getFontMetrics();
		int badgeW = metrics.stringWidth(badge) + 12;
		int badgeX = width - badgeW - 10;
		g.setColor(premium ? withAlpha(accent, 190) : new Color(0x33, 0x33, 0x3A));
		g.fillRoundRect(badgeX, 9, badgeW, 14, 7, 7);
		g.setColor(premium ? new Color(0x12, 0x12, 0x14) : new Color(0x8E, 0x8E, 0x98));
		g.drawString(badge, badgeX + 6, 20);

		String tierLabel = "TIER " + tier + " / " + tiers;
		g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 11));
		metrics = g.getFontMetrics();
		g.setColor(accent);
		g.drawString(tierLabel, width - metrics.stringWidth(tierLabel) - 10, 37);

		int barY = HEIGHT - 19;
		int barW = width - 24;
		g.setColor(TRACK);
		g.fillRoundRect(12, barY, barW, 6, 3, 3);
		double progress = need <= 0d ? 1d : Math.min(1d, into / need);
		g.setPaint(new GradientPaint(12, barY, accent.darker(), 12 + barW, barY, accent));
		g.fillRoundRect(12, barY, (int) Math.round(barW * progress), 6, 3, 3);

		g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 9));
		g.setColor(new Color(0x93, 0x93, 0x9C));
		g.drawString(need <= 0d
			? "Season complete"
			: (long) into + " / " + (long) need + " pass xp to tier " + (tier + 1), 12, HEIGHT - 6);

		g.dispose();
	}

	private static Color withAlpha(Color colour, int alpha)
	{
		return new Color(colour.getRed(), colour.getGreen(), colour.getBlue(), alpha);
	}
}
