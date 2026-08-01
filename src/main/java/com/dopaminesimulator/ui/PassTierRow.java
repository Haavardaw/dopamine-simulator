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

import com.dopaminesimulator.pass.PassReward;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.util.function.Consumer;
import javax.swing.JComponent;

/**
 * A rung on the season track: a node on a spine, the free reward, and the
 * premium one beneath it.
 */
public class PassTierRow extends JComponent
{
	public static final int HEIGHT = 40;

	private static final int SPINE_X = 17;
	private static final int NODE = 20;
	private static final int MILESTONE_NODE = 26;
	private static final Color SPINE = new Color(0x2C, 0x2C, 0x33);
	private static final Color BODY = new Color(0x22, 0x22, 0x27);
	private static final Color LOCKED_TEXT = new Color(0x62, 0x62, 0x6A);
	private static final Color CLAIMED_TEXT = new Color(0x4C, 0x4C, 0x54);

	private final int tier;
	private final boolean milestone;
	private final boolean reached;
	private final boolean first;
	private final boolean last;
	private final Color accent;
	private final PassReward free;
	private final PassReward premium;
	private final boolean freeClaimed;
	private final boolean premiumClaimed;
	private final boolean premiumOwned;
	private final BufferedImage freeIcon;
	private final BufferedImage premiumIcon;

	public PassTierRow(int tier, boolean milestone, boolean reached, boolean first, boolean last,
		Color accent, PassReward free, PassReward premium, boolean freeClaimed,
		boolean premiumClaimed, boolean premiumOwned, BufferedImage freeIcon,
		BufferedImage premiumIcon, Consumer<Boolean> onClaim)
	{
		this.tier = tier;
		this.milestone = milestone;
		this.reached = reached;
		this.first = first;
		this.last = last;
		this.accent = accent;
		this.free = free;
		this.premium = premium;
		this.freeClaimed = freeClaimed;
		this.premiumClaimed = premiumClaimed;
		this.premiumOwned = premiumOwned;
		this.freeIcon = freeIcon;
		this.premiumIcon = premiumIcon;

		setPreferredSize(new Dimension(0, HEIGHT));
		setMaximumSize(new Dimension(Integer.MAX_VALUE, HEIGHT));
		setMinimumSize(new Dimension(0, HEIGHT));
		setOpaque(false);

		if (onClaim != null)
		{
			addMouseListener(new MouseAdapter()
			{
				@Override
				public void mousePressed(MouseEvent e)
				{
					boolean lower = e.getY() > HEIGHT / 2;
					if (!reached)
					{
						return;
					}
					if (lower && premiumOwned && !premiumClaimed)
					{
						onClaim.accept(true);
					}
					else if (!lower && !freeClaimed)
					{
						onClaim.accept(false);
					}
				}
			});
		}
	}

	@Override
	protected void paintComponent(Graphics graphics)
	{
		Graphics2D g = (Graphics2D) graphics.create();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
			RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		int width = getWidth();
		drawSpine(g);

		int textX = SPINE_X + 20;
		int plateX = textX - 6;
		int plateW = Math.max(20, width - textX + 2);
		boolean ready = reached
			&& ((!freeClaimed) || (premiumOwned && !premiumClaimed));

		if (ready)
		{
			for (int i = 3; i >= 1; i--)
			{
				g.setColor(withAlpha(accent, 12 * i));
				g.fillRoundRect(plateX - i, 3 - i, plateW + i * 2, HEIGHT - 6 + i * 2, 6, 6);
			}
		}

		g.setPaint(milestone
			? new GradientPaint(plateX, 3, new Color(0x2F, 0x2C, 0x24),
				plateX, HEIGHT - 3, new Color(0x1D, 0x1B, 0x18))
			: new GradientPaint(plateX, 3, new Color(0x27, 0x27, 0x2D),
				plateX, HEIGHT - 3, new Color(0x1D, 0x1D, 0x22)));
		g.fillRoundRect(plateX, 3, plateW, HEIGHT - 6, 5, 5);

		g.setStroke(new BasicStroke(1f));
		g.setColor(withAlpha(Color.WHITE, 20));
		g.drawLine(plateX + 2, 4, plateX + plateW - 3, 4);
		g.setColor(withAlpha(Color.BLACK, 110));
		g.drawLine(plateX + 2, HEIGHT - 4, plateX + plateW - 3, HEIGHT - 4);

		if (milestone)
		{
			g.setColor(withAlpha(reached ? accent : SPINE, 190));
			g.drawRoundRect(plateX, 3, plateW - 1, HEIGHT - 7, 5, 5);
		}
		else if (ready)
		{
			g.setColor(withAlpha(accent, 120));
			g.drawRoundRect(plateX, 3, plateW - 1, HEIGHT - 7, 5, 5);
		}

		drawNode(g);
		drawReward(g, free, freeIcon, textX, 14, freeClaimed, false);
		drawReward(g, premium, premiumIcon, textX, 30, premiumClaimed, true);

		g.dispose();
	}

	private void drawSpine(Graphics2D g)
	{
		g.setColor(reached ? accent.darker() : SPINE);
		g.setStroke(new BasicStroke(3f));
		int top = first ? HEIGHT / 2 : 0;
		int bottom = last ? HEIGHT / 2 : HEIGHT;
		g.drawLine(SPINE_X, top, SPINE_X, bottom);
	}

	private void drawNode(Graphics2D g)
	{
		int size = milestone ? MILESTONE_NODE : NODE;
		int x = SPINE_X - size / 2;
		int y = (HEIGHT - size) / 2;

		if (reached)
		{
			g.setPaint(new GradientPaint(x, y, accent, x, y + size, accent.darker()));
		}
		else
		{
			g.setPaint(new GradientPaint(x, y, new Color(0x2A, 0x2A, 0x30),
				x, y + size, new Color(0x1C, 0x1C, 0x21)));
		}

		if (milestone)
		{
			g.fill(diamond(SPINE_X, HEIGHT / 2, size / 2));
			g.setColor(reached ? brighten(accent) : SPINE);
			g.setStroke(new BasicStroke(1.5f));
			g.draw(diamond(SPINE_X, HEIGHT / 2, size / 2));
		}
		else
		{
			g.fillOval(x, y, size, size);
			g.setColor(reached ? brighten(accent) : SPINE);
			g.setStroke(new BasicStroke(1.5f));
			g.drawOval(x, y, size, size);
		}

		String label = String.valueOf(tier);
		g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, milestone ? 11 : 10));
		FontMetrics metrics = g.getFontMetrics();
		g.setColor(reached ? new Color(0x10, 0x10, 0x12) : LOCKED_TEXT);
		g.drawString(label, SPINE_X - metrics.stringWidth(label) / 2,
			HEIGHT / 2 + metrics.getAscent() / 2 - 1);
	}

	private void drawReward(Graphics2D g, PassReward reward, BufferedImage icon, int x, int baseline,
		boolean claimed, boolean isPremium)
	{
		int iconSize = 13;
		int iconY = baseline - iconSize + 2;

		Color tint = reward.colour();
		boolean available = reached && !claimed && (!isPremium || premiumOwned);
		boolean dim = claimed || !reached || (isPremium && !premiumOwned);

		g.setColor(dim ? new Color(0x26, 0x26, 0x2C) : withAlpha(tint, 70));
		g.fillRoundRect(x, iconY, iconSize, iconSize, 3, 3);
		if (icon != null)
		{
			java.awt.Composite before = g.getComposite();
			if (dim)
			{
				g.setComposite(java.awt.AlphaComposite.getInstance(
					java.awt.AlphaComposite.SRC_OVER, 0.35f));
			}
			g.drawImage(icon, x, iconY, iconSize, iconSize, null);
			g.setComposite(before);
		}
		else
		{
			g.setColor(dim ? CLAIMED_TEXT : tint);
			g.fillOval(x + 4, iconY + 4, 5, 5);
		}

		g.setFont(new Font(Font.SANS_SERIF, isPremium ? Font.PLAIN : Font.BOLD, 10));
		FontMetrics metrics = g.getFontMetrics();
		String text = reward.describe();
		if (isPremium && !premiumOwned)
		{
			text = "Premium: " + text;
		}

		g.setColor(claimed ? CLAIMED_TEXT : available ? tint : LOCKED_TEXT);
		int textX = x + iconSize + 5;
		g.drawString(elide(metrics, text, getWidth() - textX - 22), textX, baseline);

		if (claimed)
		{
			drawTick(g, getWidth() - 14, baseline - 5);
		}
		else if (available)
		{
			g.setColor(tint);
			g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 9));
			g.drawString("CLAIM", getWidth() - 36, baseline);
		}
	}

	private void drawTick(Graphics2D g, int x, int y)
	{
		g.setColor(CLAIMED_TEXT);
		g.setStroke(new BasicStroke(1.6f));
		g.drawLine(x, y, x + 3, y + 3);
		g.drawLine(x + 3, y + 3, x + 8, y - 3);
	}

	private static Path2D.Double diamond(int cx, int cy, int radius)
	{
		Path2D.Double path = new Path2D.Double();
		path.moveTo(cx, cy - radius);
		path.lineTo(cx + radius, cy);
		path.lineTo(cx, cy + radius);
		path.lineTo(cx - radius, cy);
		path.closePath();
		return path;
	}

	private static String elide(FontMetrics metrics, String text, int maxWidth)
	{
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

	private static Color brighten(Color colour)
	{
		return new Color(Math.min(255, colour.getRed() + 60),
			Math.min(255, colour.getGreen() + 60), Math.min(255, colour.getBlue() + 60));
	}

	private static Color withAlpha(Color colour, int alpha)
	{
		return new Color(colour.getRed(), colour.getGreen(), colour.getBlue(), alpha);
	}
}
