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

import com.dopaminesimulator.incremental.BigNumbers;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.MultipleGradientPaint;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import javax.swing.JComponent;

public class PointsHeader extends JComponent
{
	private static final int HEIGHT = 62;
	private static final Color GOLD = new Color(0xFF, 0xB3, 0x00);
	private static final Color GOLD_LIGHT = new Color(0xFF, 0xDD, 0x82);
	private static final Color SURGE = new Color(0xFF, 0xE9, 0xA8);
	private static final Color BACKDROP_TOP = new Color(0x27, 0x24, 0x1C);
	private static final Color BACKDROP_BOTTOM = new Color(0x18, 0x18, 0x1A);

	private final double points;
	private final double perHour;
	private final boolean surging;
	public PointsHeader(double points, double perHour, boolean surging)
	{
		this.points = points;
		this.perHour = perHour;
		this.surging = surging;

		setPreferredSize(new Dimension(0, HEIGHT));
		setMaximumSize(new Dimension(Integer.MAX_VALUE, HEIGHT));
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
		int height = getHeight();
		g.setPaint(new GradientPaint(0, 0, BACKDROP_TOP, 0, height, BACKDROP_BOTTOM));
		g.fillRoundRect(0, 0, width - 1, height - 1, 8, 8);

		g.setPaint(new RadialGradientPaint(
			new Point2D.Float(width / 2f, height * 0.42f),
			Math.max(width, height) * 0.7f,
			new float[]{0f, 1f},
			new Color[]{
				new Color(GOLD.getRed(), GOLD.getGreen(), GOLD.getBlue(), surging ? 70 : 34),
				new Color(GOLD.getRed(), GOLD.getGreen(), GOLD.getBlue(), 0)},
			MultipleGradientPaint.CycleMethod.NO_CYCLE));
		g.fillRoundRect(0, 0, width - 1, height - 1, 8, 8);
		g.setColor(surging ? SURGE : new Color(0x3A, 0x34, 0x24));
		g.drawRoundRect(0, 0, width - 1, height - 1, 8, 8);
		String value = BigNumbers.format(points);
		g.setFont(g.getFont().deriveFont(Font.BOLD, fontSizeFor(value)));
		FontMetrics metrics = g.getFontMetrics();
		g.setPaint(new GradientPaint(0, 12, surging ? SURGE : GOLD_LIGHT, 0, 40, GOLD));
		g.drawString(value, (width - metrics.stringWidth(value)) / 2, 34);
		g.setFont(g.getFont().deriveFont(Font.PLAIN, 11f));
		g.setColor(new Color(0x9A, 0x92, 0x80));
		String rate = BigNumbers.format(perHour) + " per hour";
		g.drawString(rate, (width - g.getFontMetrics().stringWidth(rate)) / 2, 50);
		g.dispose();
	}
	private static float fontSizeFor(String value)
	{
		if (value.length() <= 6)
		{
			return 26f;
		}
		return value.length() <= 8 ? 22f : 18f;
	}
}
