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
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.MultipleGradientPaint;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.DoubleSupplier;
import javax.swing.JComponent;
import javax.swing.Timer;

public class ClickButton extends JComponent
{
	private static final int HEIGHT = 100;
	private static final int FRAME_MS = 33;
	private static final long PARTICLE_LIFETIME_MS = 900L;
	private static final long PRESS_LIFETIME_MS = 160L;
	private static final int MAX_PARTICLES = 18;

	private final DoubleSupplier pointsPerClick;
	private final Runnable onClick;
	private final Random random = new Random();
	private final List<Particle> particles = new ArrayList<>();
	private final Timer animator;
	private BufferedImage icon;
	private String status;
	private boolean surging;
	private boolean hovered;
	private long pressedAt;
	private static final class Particle
	{
		private final String text;
		private final long start;
		private final int driftX;
		private Particle(String text, int driftX)
		{
			this.text = text;
			this.driftX = driftX;
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

	/**
	 * Shown instead of the payout when there is a reason the button is not
	 * paying. A button that silently stops giving anything reads as broken.
	 */
	public void setStatus(String status)
	{
		this.status = status;
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
				random.nextInt(70) - 35));
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
		Skin.smooth(g);

		int width = getWidth();
		int height = getHeight();
		int centreX = width / 2;
		int centreY = (height - 16) / 2;
		int plate = (int) (Math.min(width, height - 16) * 0.78d * currentScale());

		drawGlow(g, centreX, centreY, plate);
		if (surging)
		{
			drawGoldenCookie(g, centreX, centreY, plate);
		}
		else
		{
			drawPlate(g, centreX, centreY, plate);
			drawIcon(g, centreX, centreY, plate);
		}
		drawLabel(g, width, height);
		drawParticles(g, centreX, centreY);

		g.dispose();
	}

	private double currentScale()
	{
		double scale = hovered ? 1.04d : 1.0d;
		long sincePress = System.currentTimeMillis() - pressedAt;
		if (sincePress < PRESS_LIFETIME_MS)
		{
			scale *= 0.91d + 0.09d * (sincePress / (double) PRESS_LIFETIME_MS);
		}
		if (surging)
		{
			scale *= 1.03d + 0.03d * Math.sin(System.currentTimeMillis() / 120d);
		}
		return scale;
	}

	private void drawGlow(Graphics2D g, int centreX, int centreY, int plate)
	{
		float radius = plate * (surging ? 1.35f : 1.0f);
		Color tint = surging ? Skin.YELLOW : Skin.GOLD;
		g.setPaint(new RadialGradientPaint(
			new Point2D.Float(centreX, centreY), radius,
			new float[]{0f, 1f},
			new Color[]{Skin.withAlpha(tint, surging ? 120 : hovered ? 70 : 45),
				Skin.withAlpha(tint, 0)},
			MultipleGradientPaint.CycleMethod.NO_CYCLE));
		g.fillOval((int) (centreX - radius), (int) (centreY - radius),
			(int) (radius * 2), (int) (radius * 2));
	}

	private void drawPlate(Graphics2D g, int centreX, int centreY, int plate)
	{
		int x = centreX - plate / 2;
		int y = centreY - plate / 2;

		g.setPaint(new GradientPaint(x, y, Skin.mix(Skin.GOLD_DEEP, Skin.CARD, 0.45f),
			x, y + plate, new Color(0x14, 0x12, 0x10)));
		g.fillOval(x, y, plate, plate);

		g.setStroke(new BasicStroke(2f));
		g.setPaint(new GradientPaint(x, y, surging ? Skin.YELLOW : Skin.GOLD,
			x, y + plate, Skin.GOLD_DEEP));
		g.drawOval(x, y, plate - 1, plate - 1);

		if (!surging)
		{
			return;
		}
		// a ring breathing outward, so a surge is unmissable without a second colour
		double pulse = 0.5d + 0.5d * Math.sin(System.currentTimeMillis() / 150d);
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
			(float) (0.25d + 0.5d * pulse)));
		g.setStroke(new BasicStroke(1.5f));
		g.setColor(Skin.YELLOW);
		int spread = (int) (5 + 7 * pulse);
		g.drawOval(x - spread, y - spread, plate + spread * 2, plate + spread * 2);
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
	}

	/**
	 * What a surge looks like: a golden biscuit rather than the ordinary coin.
	 *
	 * <p>Drawn rather than bundled. It is the shape of the game's own cookie, an
	 * eight sided slab with chunks in it, which is simple enough to lay out in a
	 * dozen lines and costs nothing to redistribute. Drawing it also means it
	 * scales with the button and takes the surge's colours rather than sitting in
	 * the panel as a fixed lump of pixels.
	 */
	private void drawGoldenCookie(Graphics2D g, int centreX, int centreY, int plate)
	{
		double radius = plate / 2d;
		Path2D.Double biscuit = new Path2D.Double();
		for (int i = 0; i < 8; i++)
		{
			// starting off axis puts flat edges top and bottom, as the model has
			double angle = Math.PI * 2 * i / 8 + Math.PI / 8;
			double px = centreX + Math.cos(angle) * radius;
			double py = centreY + Math.sin(angle) * radius * 0.86d;
			if (i == 0)
			{
				biscuit.moveTo(px, py);
			}
			else
			{
				biscuit.lineTo(px, py);
			}
		}
		biscuit.closePath();

		g.setPaint(new GradientPaint(
			centreX - (float) radius, centreY - (float) radius, new Color(0xFF, 0xE4, 0x3A),
			centreX + (float) radius, centreY + (float) radius, new Color(0xB9, 0x6A, 0x08)));
		g.fill(biscuit);
		g.setStroke(new BasicStroke(1.5f));
		g.setColor(new Color(0xFF, 0xF4, 0x9B, 200));
		g.draw(biscuit);

		// chunks, at fixed offsets and angles so the biscuit does not shimmer
		// between frames the way anything random would
		double[][] chunks = {
			{-0.34, -0.46, 0.15, 2.6}, {0.04, -0.54, 0.11, 0.4},
			{0.46, -0.32, 0.10, 3.0}, {-0.52, 0.04, 0.12, 1.3},
			{-0.08, -0.10, 0.17, 2.2}, {0.30, 0.02, 0.12, 0.9},
			{-0.30, 0.42, 0.11, 2.9}, {0.16, 0.44, 0.13, 1.8},
			{0.50, 0.30, 0.09, 2.0}, {-0.10, 0.20, 0.10, 0.2},
		};
		Shape clip = g.getClip();
		g.clip(biscuit);
		g.setColor(new Color(0x33, 0x1D, 0x03, 235));
		for (double[] chunk : chunks)
		{
			double cx = centreX + chunk[0] * radius;
			double cy = centreY + chunk[1] * radius * 0.86d;
			double size = chunk[2] * radius;
			Path2D.Double chip = new Path2D.Double();
			for (int i = 0; i < 3; i++)
			{
				double angle = chunk[3] + Math.PI * 2 * i / 3;
				double px = cx + Math.cos(angle) * size;
				double py = cy + Math.sin(angle) * size;
				if (i == 0)
				{
					chip.moveTo(px, py);
				}
				else
				{
					chip.lineTo(px, py);
				}
			}
			chip.closePath();
			g.fill(chip);
		}
		g.setClip(clip);
	}

	private void drawIcon(Graphics2D g, int centreX, int centreY, int plate)
	{
		if (icon == null)
		{
			return;
		}
		int size = (int) (plate * 0.58d);
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
		if (status != null && !status.isEmpty())
		{
			g.setFont(Skin.body());
			Skin.centred(g, status, 0, width, height - 4, Skin.MUTED);
			return;
		}
		g.setFont(Skin.heading());
		Skin.centred(g, surging
				? "SURGE  +" + BigNumbers.format(pointsPerClick.getAsDouble())
				: "+" + BigNumbers.format(pointsPerClick.getAsDouble()),
			0, width, height - 4, surging ? Skin.YELLOW : Skin.GOLD);
	}

	private void drawParticles(Graphics2D g, int centreX, int centreY)
	{
		Composite before = g.getComposite();
		g.setFont(Skin.body());
		FontMetrics metrics = g.getFontMetrics();
		for (Particle particle : particles)
		{
			float progress = particle.progress();
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
				Math.max(0f, 1f - progress)));
			int x = centreX - metrics.stringWidth(particle.text) / 2
				+ (int) (particle.driftX * progress);
			Skin.text(g, particle.text, x, centreY - (int) (progress * 46),
				surging ? Skin.YELLOW : Skin.GOLD);
		}
		g.setComposite(before);
	}

	public void dispose()
	{
		animator.stop();
	}
}
