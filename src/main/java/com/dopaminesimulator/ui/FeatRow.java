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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JComponent;

public class FeatRow extends JComponent
{
	public static final int HEIGHT = 46;

	private static final int MEDAL = 30;
	private static final int PAD = 8;
	private static final Color BODY = new Color(0x25, 0x25, 0x2A);
	private static final Color TRACK = new Color(0x16, 0x16, 0x19);
	private static final Color NAME = new Color(0xE4, 0xE4, 0xEA);
	private static final Color MUTED = new Color(0x8C, 0x8C, 0x96);

	private final String name;
	private final String progressText;
	private final int tier;
	private final int maxTier;
	private final double fraction;
	private final boolean mastered;

	public FeatRow(Feat feat, int tier, String progressText, double fraction)
	{
		this.name = feat.getDisplayName();
		this.progressText = progressText;
		this.tier = tier;
		this.maxTier = feat.maxTier();
		this.fraction = Math.max(0d, Math.min(1d, fraction));
		this.mastered = tier >= feat.maxTier();
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
		Color rank = Feat.tierColour(tier);

		g.setColor(BODY);
		g.fillRoundRect(0, 0, width, HEIGHT, 6, 6);

		drawMedal(g, rank);

		int textX = PAD + MEDAL + 10;
		int textWidth = Math.max(20, width - textX - PAD);
		int nameWidth = Math.max(20, textWidth - 42);

		g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
		g.setColor(tier > 0 ? rank : NAME);
		g.drawString(elide(g, name, nameWidth), textX, 18);

		g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
		g.setColor(MUTED);
		g.drawString(elide(g, progressText, textWidth), textX, 31);

		drawRankCount(g, width, rank);

		if (!mastered)
		{
			int barY = HEIGHT - 8;
			g.setColor(TRACK);
			g.fillRoundRect(textX, barY, textWidth, 3, 3, 3);
			g.setColor(rank);
			g.fillRoundRect(textX, barY, (int) Math.round(textWidth * fraction), 3, 3, 3);
		}

		g.dispose();
	}

	private void drawMedal(Graphics2D g, Color rank)
	{
		int y = (HEIGHT - MEDAL) / 2;

		g.setColor(tier > 0 ? rank.darker().darker() : TRACK);
		g.fillOval(PAD, y, MEDAL, MEDAL);
		g.setColor(rank);
		g.setStroke(new BasicStroke(tier > 0 ? 2f : 1f));
		g.drawOval(PAD, y, MEDAL - 1, MEDAL - 1);

		String label = tier > 0 ? String.valueOf(tier) : "–";
		g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, tier > 0 ? 13 : 11));
		FontMetrics metrics = g.getFontMetrics();
		g.setColor(tier > 0 ? rank : MUTED);
		g.drawString(label,
			PAD + (MEDAL - metrics.stringWidth(label)) / 2,
			y + (MEDAL + metrics.getAscent()) / 2 - 2);
	}

	private void drawRankCount(Graphics2D g, int width, Color rank)
	{
		String text = tier + " / " + maxTier;
		g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 10));
		FontMetrics metrics = g.getFontMetrics();
		g.setColor(tier > 0 ? rank : MUTED);
		g.drawString(text, width - PAD - metrics.stringWidth(text), 17);
	}

	private String elide(Graphics2D g, String text, int maxWidth)
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
}
