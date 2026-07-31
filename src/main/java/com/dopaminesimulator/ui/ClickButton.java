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
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MultipleGradientPaint;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.function.DoubleSupplier;
import javax.swing.JComponent;
import javax.swing.Timer;

public class ClickButton extends JComponent
{
	private static final int HEIGHT = 104;
	private static final int FRAME_MS = 33;
	private static final long PARTICLE_LIFETIME_MS = 900L;
	private static final long PRESS_LIFETIME_MS = 160L;
	private static final int MAX_PARTICLES = 18;
	private static final Color PLATE_TOP = new Color(0x4A, 0x3C, 0x14);
	private static final Color PLATE_BOTTOM = new Color(0x24, 0x1C, 0x0A);
	private static final Color GOLD = new Color(0xFF, 0xB3, 0x00);
	private static final Color GOLD_LIGHT = new Color(0xFF, 0xE2, 0x8A);
	private static final Color SURGE = new Color(0xFF, 0xEC, 0xB0);

	private final DoubleSupplier pointsPerClick;
	private final Runnable onClick;
	private final Random random = new Random();
	private final List<Particle> particles = new ArrayList<>();
	private final Timer animator;
	private BufferedImage icon;
	private boolean surging;
	private boolean hovered;
	private long pressedAt;
	private static final class Particle
	{
		private final String text;
		private final long start;
		private final int driftX;
		private final float size;
		private Particle(String text, int driftX, float size)
		{
			this.text = text;
			this.driftX = driftX;
			this.size = size;
			this.start = System.currentTimeMillis();
		}
		private float progress()
		{
			return Math.min(1f, (System.currentTimeMillis() - start) / (float) PARTICLE_LIFETIME_MS);
		}
		private boolean expired()
		{
			return progress() >= 1f;
		}
	}
	public ClickButton(DoubleSupplier pointsPerClick, Runnable onClick)
	{
		this.pointsPerClick = pointsPerClick;
		this.onClick = onClick;
		setPreferredSize(new Dimension(0, HEIGHT));
		setMaximumSize(new Dimension(Integer.MAX_VALUE, HEIGHT));
		setMinimumSize(new Dimension(0, HEIGHT));
		setOpaque(false);
		setToolTipText("Click for points. Occasionally this surges.");
		animator = new Timer(FRAME_MS, null);
		animator.addActionListener(e ->
		{
			particles.removeIf(Particle::expired);
			repaint();
			if (particles.isEmpty() && !hovered && !surging
				&& System.currentTimeMillis() - pressedAt > PRESS_LIFETIME_MS)
			{
				animator.stop();
			}
		});
		addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseEntered(MouseEvent e)
			{
				hovered = true;
				wake();
			}
			@Override
			public void mouseExited(MouseEvent e)
			{
				hovered = false;
				wake();
			}
			@Override
			public void mousePressed(MouseEvent e)
			{
				pressedAt = System.currentTimeMillis();
				spawnParticle();
				wake();
				onClick.run();
			}
		});
	}
	public void setIcon(BufferedImage icon)
	{
		this.icon = icon;
	}
	public void setSurging(boolean surging)
	{
		this.surging = surging;
		if (surging)
		{
			wake();
		}
	}
	private void wake()
	{
		if (!animator.isRunning())
		{
			animator.start();
		}
		repaint();
	}
	private void spawnParticle()
	{
		int count = surging ? 3 : 1;
		for (int i = 0; i < count; i++)
		{
			particles.add(new Particle("+" + BigNumbers.format(pointsPerClick.getAsDouble()),
				random.nextInt(70) - 35, surging ? 15f : 12f));
		}
		while (particles.size() > MAX_PARTICLES)
		{
			particles.remove(0);
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
		int height = getHeight();
		int centreX = width / 2;
		int centreY = height / 2;
		double scale = currentScale();
		int plate = (int) (Math.min(width, height) * 0.68d * scale);
		drawGlow(g, centreX, centreY, plate);
		drawPlate(g, centreX, centreY, plate);
		drawIcon(g, centreX, centreY, plate);
		drawLabel(g, width, height);
		drawParticles(g, centreX, centreY);
		g.dispose();
	}

	private double currentScale()
	{
		double scale = hovered ? 1.05d : 1.0d;
		long sincePress = System.currentTimeMillis() - pressedAt;
		if (sincePress < PRESS_LIFETIME_MS)
		{
			double t = sincePress / (double) PRESS_LIFETIME_MS;
			scale *= 0.9d + 0.1d * t;
		}
		if (surging)
		{
			scale *= 1.04d + 0.04d * Math.sin(System.currentTimeMillis() / 120d);
		}
		return scale;
	}
	private void drawGlow(Graphics2D g, int centreX, int centreY, int plate)
	{
		float radius = plate * (surging ? 1.5f : 1.05f);
		int alpha = surging ? 130 : hovered ? 70 : 45;
		Color tint = surging ? SURGE : GOLD;
		g.setPaint(new RadialGradientPaint(
			new Point2D.Float(centreX, centreY),
			radius,
			new float[]{0f, 1f},
			new Color[]{
				new Color(tint.getRed(), tint.getGreen(), tint.getBlue(), alpha),
				new Color(tint.getRed(), tint.getGreen(), tint.getBlue(), 0)},
			MultipleGradientPaint.CycleMethod.NO_CYCLE));
		g.fillOval((int) (centreX - radius), (int) (centreY - radius),
			(int) (radius * 2), (int) (radius * 2));
	}
	private void drawPlate(Graphics2D g, int centreX, int centreY, int plate)
	{
		int x = centreX - plate / 2;
		int y = centreY - plate / 2;
		g.setPaint(new java.awt.GradientPaint(x, y, PLATE_TOP, x, y + plate, PLATE_BOTTOM));
		g.fillOval(x, y, plate, plate);
		g.setStroke(new BasicStroke(surging ? 3f : 2f));
		g.setColor(surging ? SURGE : hovered ? GOLD_LIGHT : GOLD);
		g.drawOval(x, y, plate, plate);
		if (surging)
		{
			double pulse = 0.5d + 0.5d * Math.sin(System.currentTimeMillis() / 150d);
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) (0.3d + 0.5d * pulse)));
			g.setStroke(new BasicStroke(1.5f));
			int spread = (int) (6 + 6 * pulse);
			g.drawOval(x - spread, y - spread, plate + spread * 2, plate + spread * 2);
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
		}
	}
	private void drawIcon(Graphics2D g, int centreX, int centreY, int plate)
	{
		if (icon == null)
		{
			return;
		}
		int size = (int) (plate * 0.62d);
		Object previous = g.getRenderingHint(RenderingHints.KEY_INTERPOLATION);
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
			RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		g.drawImage(icon, centreX - size / 2, centreY - size / 2, size, size, null);
		if (previous != null)
		{
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, previous);
		}
	}
	private void drawLabel(Graphics2D g, int width, int height)
	{
		String text = surging
			? "SURGE  +" + BigNumbers.format(pointsPerClick.getAsDouble())
			: "+" + BigNumbers.format(pointsPerClick.getAsDouble());
		g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, surging ? 13 : 12));
		FontMetrics metrics = g.getFontMetrics();
		int x = (width - metrics.stringWidth(text)) / 2;
		int y = height - 5;
		g.setColor(new Color(0, 0, 0, 170));
		g.drawString(text, x + 1, y + 1);
		g.setColor(surging ? SURGE : GOLD_LIGHT);
		g.drawString(text, x, y);
	}

	private void drawParticles(Graphics2D g, int centreX, int centreY)
	{
		Composite before = g.getComposite();
		for (Iterator<Particle> it = particles.iterator(); it.hasNext(); )
		{
			Particle particle = it.next();
			float progress = particle.progress();
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
				Math.max(0f, 1f - progress)));
			g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, (int) particle.size));
			FontMetrics metrics = g.getFontMetrics();
			int x = centreX - metrics.stringWidth(particle.text) / 2
				+ (int) (particle.driftX * progress);
			int y = centreY - (int) (progress * 46);
			g.setColor(Color.BLACK);
			g.drawString(particle.text, x + 1, y + 1);
			g.setColor(surging ? SURGE : GOLD_LIGHT);
			g.drawString(particle.text, x, y);
		}
		g.setComposite(before);
	}

	public void dispose()
	{
		animator.stop();
	}
}
