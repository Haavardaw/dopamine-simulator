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
package com.dopaminesimulator;

import com.dopaminesimulator.cards.Card;
import com.dopaminesimulator.cards.CardSet;
import com.dopaminesimulator.cards.Rarity;
import com.dopaminesimulator.cards.Region;
import com.dopaminesimulator.core.Balance;
import com.dopaminesimulator.core.DopamineEngine;
import com.dopaminesimulator.core.DopamineEvent;
import com.dopaminesimulator.core.DopamineState;
import com.dopaminesimulator.feats.Feats;
import com.dopaminesimulator.incremental.BigNumbers;
import com.dopaminesimulator.incremental.Prestige;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Function;
import com.dopaminesimulator.core.IncomeTracker;
import com.dopaminesimulator.core.PointListener;
import com.dopaminesimulator.core.Reward;
import com.dopaminesimulator.core.RewardType;
import com.dopaminesimulator.core.RewardQueue;
import com.dopaminesimulator.packs.PackTier;
import com.dopaminesimulator.points.ClickState;
import com.dopaminesimulator.points.PointSource;
import com.dopaminesimulator.systems.CollectionService;
import com.dopaminesimulator.systems.BannerService;
import com.dopaminesimulator.systems.PackService;
import com.dopaminesimulator.systems.PassService;
import com.dopaminesimulator.systems.PassSystem;
import com.dopaminesimulator.systems.AchievementSystem;
import com.dopaminesimulator.systems.FeatSystem;
import com.dopaminesimulator.systems.PointSystem;
import com.dopaminesimulator.ui.CardArtService;
import com.dopaminesimulator.ui.GameIcons;
import com.google.inject.Provides;
import java.awt.image.BufferedImage;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Named;
import javax.swing.SwingUtilities;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.events.CommandExecuted;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Hitsplat;
import net.runelite.api.HitsplatID;
import net.runelite.api.Player;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.events.HitsplatApplied;
import net.runelite.api.events.StatChanged;
import com.dopaminesimulator.dev.WidgetDump;
import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Deque;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.ItemStack;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.loottracker.LootReceived;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import net.runelite.client.util.ImageUtil;
import net.runelite.http.api.loottracker.LootRecordType;

@Slf4j
@PluginDescriptor(
	name = "Dopamine Simulator",
	description = "A progression system layered over the progression system layered over the game",
	tags = {"cards", "collection", "progression", "packs", "meta", "idle"}
)
public class DopamineSimulatorPlugin extends Plugin
{
	private static final int SAVE_INTERVAL_TICKS = 100;

	private static final int CROSS_REGION_SENTINEL = 100;

	@Inject
	private Client client;

	@Inject
	@Named("developerMode")
	private boolean developerMode;

	/** Set by ::dumpui sweep. Records every interface as it opens, once each. */
	private boolean sweeping;
	private final Set<Integer> sweptGroups = new HashSet<>();
	private final Deque<Integer> pendingGroups = new ArrayDeque<>();

	@Inject
	private ClientThread clientThread;

	@Inject
	private ClientToolbar clientToolbar;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private InfoBoxManager infoBoxManager;

	@Inject
	private ItemManager itemManager;

	@Inject
	private SaveManager saveManager;

	private static final long RESET_CONFIRM_MS = 30_000L;

	private long resetRequestedAt;

	private String pendingResetCommand;

	private final AchievementSystem achievementSystem = new AchievementSystem();

	@Inject
	private DopamineSimulatorConfig config;

	@Inject
	private RevealSoundService revealSounds;

	@Inject
	@Getter
	private CardArtService cardArtService;

	@Inject
	@Getter
	private GameIcons gameIcons;

	@Getter
	private DopamineEngine engine;

	@Getter
	private RewardQueue rewards;

	@Getter
	private CollectionService collection;

	@Getter
	private PackService packService;

	@Getter
	private PassService passService;

	@Getter
	private BannerService bannerService;

	@Getter
	private IncomeTracker incomeTracker;

	@Getter
	private ClickState clickState;

	private DopamineSimulatorPanel panel;
	private NavigationButton navButton;
	private DopamineOverlay overlay;
	private PackRevealOverlay revealOverlay;
	private FloatingTextOverlay floatingTextOverlay;
	private SurgeInfoBox surgeInfoBox;

	private final Random random = new Random();

	private final Map<Skill, Integer> lastXp = new EnumMap<>(Skill.class);
	private final Map<Skill, Integer> lastLevel = new EnumMap<>(Skill.class);
	private final Set<PointSource> announcedSources = EnumSet.noneOf(PointSource.class);

	private WorldPoint lastLocation;
	private int lastHitpoints = -1;

	private long loadedAccountHash = Long.MIN_VALUE;
	private int ticksSinceSave;

	@Provides
	DopamineSimulatorConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(DopamineSimulatorConfig.class);
	}
	@Override
	protected void startUp()
	{
		rewards = new RewardQueue();
		incomeTracker = new IncomeTracker();
		clickState = new ClickState();
		collection = new CollectionService();
		packService = new PackService(random, collection);
		passService = new PassService(random, packService);
		bannerService = new BannerService(random, packService, collection);

		floatingTextOverlay = new FloatingTextOverlay(client, config, gameIcons);
		PointListener listeners = (source, detail, amount, tick) ->
		{
			incomeTracker.onPointsGained(source, detail, amount, tick);
			floatingTextOverlay.onPointsGained(source, detail, amount, tick);
		};
		engine = new DopamineEngine(new DopamineState(), rewards)
			.register(new PointSystem(listeners))
			.register(new FeatSystem())
			.register(achievementSystem)
			.register(new PassSystem());
		panel = new DopamineSimulatorPanel(this, config);
		navButton = NavigationButton.builder()
			.tooltip("Dopamine Simulator")
			.icon(buildIcon())
			.priority(7)
			.panel(panel)
			.build();
		clientToolbar.addNavigation(navButton);
		overlay = new DopamineOverlay(this, config);
		overlayManager.add(overlay);

		revealOverlay = new PackRevealOverlay(client, config, revealSounds, cardArtService,
			() -> engine == null ? new DopamineState() : engine.getState());
		overlayManager.add(revealOverlay);
		overlayManager.add(floatingTextOverlay);
		surgeInfoBox = new SurgeInfoBox(buildIcon(), this, config, clickState);
		infoBoxManager.addInfoBox(surgeInfoBox);

		clientThread.invokeLater(() ->
		{
			gameIcons.warm(this::refreshPanel);
			applyPluginIcon();
		});

		rewards.addListener(queue -> refreshPanel());
		clientThread.invokeLater(this::loadStateForCurrentAccount);
	}
	@Override
	protected void shutDown()
	{
		persist();
		overlayManager.remove(overlay);
		overlayManager.remove(revealOverlay);
		overlayManager.remove(floatingTextOverlay);
		infoBoxManager.removeInfoBox(surgeInfoBox);
		clientToolbar.removeNavigation(navButton);
		rewards.clearListeners();
		resetTracking();
		loadedAccountHash = Long.MIN_VALUE;
		panel = null;
		overlay = null;
		revealOverlay = null;
		floatingTextOverlay = null;
		surgeInfoBox = null;
		incomeTracker = null;
		clickState = null;
		engine = null;
		rewards = null;
	}
	private void resetTracking()
	{
		lastXp.clear();
		lastLevel.clear();
		announcedSources.clear();
		lastLocation = null;
		lastHitpoints = -1;
	}
	@Subscribe
	public void onWidgetLoaded(WidgetLoaded event)
	{
		if (sweeping && sweptGroups.add(event.getGroupId()))
		{
			pendingGroups.add(event.getGroupId());
		}
	}

	/**
	 * Interfaces are not fully built when they announce themselves, so a group is
	 * written on the tick after it loads rather than the moment it arrives.
	 */
	private void drainSweep()
	{
		while (!pendingGroups.isEmpty())
		{
			int group = pendingGroups.poll();
			try
			{
				WidgetDump.append(client, group, WidgetDump.sweepFile());
			}
			catch (IOException e)
			{
				log.warn("could not append widget group {}", group, e);
			}
		}
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		if (sweeping)
		{
			drainSweep();
		}
		if (engine == null)
		{
			return;
		}
		engine.accept(DopamineEvent.tick());
		rollSeasons();
		trackMovement();
		trackHealth();
		rollForSurge();
		checkSourceUnlocks();
		floatingTextOverlay.flushPending();
		if (++ticksSinceSave >= SAVE_INTERVAL_TICKS)
		{
			ticksSinceSave = 0;
			persist();
		}
	}
	private void trackMovement()
	{
		Player local = client.getLocalPlayer();
		if (local == null)
		{
			return;
		}
		WorldPoint current = local.getWorldLocation();
		if (current == null)
		{
			return;
		}
		if (lastLocation != null && lastLocation.getPlane() == current.getPlane())
		{
			int tiles = lastLocation.distanceTo(current);
			if (tiles > 0 && tiles < CROSS_REGION_SENTINEL)
			{
				int stepped = Math.min(tiles, (int) Balance.MAX_TILES_PER_TICK);
				for (int i = 0; i < stepped; i++)
				{
					engine.accept(DopamineEvent.distance(1));
				}
			}
		}
		lastLocation = current;
	}
	private void trackHealth()
	{
		int current = client.getBoostedSkillLevel(Skill.HITPOINTS);
		if (lastHitpoints >= 0 && current > lastHitpoints)
		{
			engine.accept(DopamineEvent.healthRestored(current - lastHitpoints));
		}

		lastHitpoints = current;
	}

	private void rollForSurge()
	{
		DopamineState state = engine.getState();
		long now = System.currentTimeMillis();

		if (state.getLifetimePoints() < ClickState.SURGE_UNLOCK_AT || clickState.isSurging(now))
		{
			return;
		}

		if (random.nextDouble() < ClickState.surgeChancePerTick(
			state.getSourceUpgradeLevel(PointSource.CLICK)))
		{
			clickState.startSurge(now);
			refreshPanel();
		}
	}

	private void checkSourceUnlocks()
	{
		DopamineState state = engine.getState();
		for (PointSource source : PointSource.values())
		{
			if (state.isSourceUnlocked(source) && announcedSources.add(source))
			{
				if (state.getLifetimePoints() - source.getUnlockAtLifetimePoints() < 5_000d)
				{
					rewards.push(Reward.sourceUnlocked(source));
				}
			}
		}
	}

	@Subscribe
	public void onStatChanged(StatChanged event)
	{
		if (engine == null)
		{
			return;
		}
		Skill skill = event.getSkill();
		Integer previousLevel = lastLevel.put(skill, event.getLevel());
		if (previousLevel != null && event.getLevel() > previousLevel)
		{
			engine.accept(DopamineEvent.levelUp(skill.name(), event.getLevel()));
		}
		Integer previousXp = lastXp.put(skill, event.getXp());
		if (previousXp == null || event.getXp() <= previousXp)
		{
			return;
		}
		engine.accept(DopamineEvent.xp(skill.name(), event.getXp() - previousXp));
		refreshPanel();
	}
	@Subscribe
	public void onLootReceived(LootReceived event)
	{
		if (engine == null)
		{
			return;
		}

		if (event.getType() == LootRecordType.NPC || event.getType() == LootRecordType.PLAYER)
		{
			engine.accept(DopamineEvent.kill(event.getName(), event.getCombatLevel()));
		}
		long value = 0;
		if (event.getItems() != null)
		{
			for (ItemStack item : event.getItems())
			{
				value += (long) itemManager.getItemPrice(item.getId()) * item.getQuantity();
			}
		}
		if (value > 0)
		{
			engine.accept(DopamineEvent.loot(event.getName(), value));
		}
		refreshPanel();
	}
	@Subscribe
	public void onHitsplatApplied(HitsplatApplied event)
	{
		if (!isPlayable() || event.getActor() != client.getLocalPlayer())
		{
			return;
		}

		Hitsplat hitsplat = event.getHitsplat();
		int amount = hitsplat.getAmount();
		if (amount <= 0 || !isDamage(hitsplat.getHitsplatType()))
		{
			return;
		}

		engine.accept(DopamineEvent.damageTaken(amount));
	}

	private static boolean isDamage(int hitsplatType)
	{
		switch (hitsplatType)
		{
			case HitsplatID.DAMAGE_ME:
			case HitsplatID.DAMAGE_ME_CYAN:
			case HitsplatID.DAMAGE_ME_ORANGE:
			case HitsplatID.DAMAGE_ME_YELLOW:
			case HitsplatID.DAMAGE_ME_WHITE:
			case HitsplatID.POISON:
			case HitsplatID.VENOM:
				return true;
			default:
				return false;
		}
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		if (engine == null)
		{
			return;
		}
		switch (event.getGameState())
		{
			case LOGGING_IN:
			case HOPPING:
				resetTracking();
				break;
			case LOGGED_IN:
				loadStateForCurrentAccount();
				break;
			case LOGIN_SCREEN:
				persist();
				resetTracking();
				break;
			default:
				break;
		}

		refreshPanel();
	}
	public boolean isPlayable()
	{
		return engine != null && client.getGameState() == GameState.LOGGED_IN;
	}

	/**
	 * What one click is worth: a few seconds of everything else you earn, so it
	 * stays worth pressing at every stage instead of paying a flat sum that the
	 * rest of the economy leaves behind.
	 */
	/**
	 * What one click is worth. Grows with income but slower than it, so clicking
	 * is a strong early source and gently becomes the weaker option next to
	 * playing, without ever paying nothing.
	 */
	public double clickPayout()
	{
		if (!isPlayable())
		{
			return 1d;
		}
		long tick = engine.getState().getTick();
		double others = Math.max(0d,
			incomeTracker.totalPerHour(tick) - incomeTracker.perHour(PointSource.CLICK, tick));
		double surge = clickState == null
			? 1d : clickState.multiplier(System.currentTimeMillis());
		return Math.max(1d, PointSource.CLICK_COEFFICIENT
			* Math.pow(others, PointSource.CLICK_EXPONENT)) * surge;
	}

	public void click()
	{
		clientThread.invoke(() ->
		{
			if (!isPlayable())
			{
				return;
			}
			engine.accept(DopamineEvent.click(clickPayout()));
			refreshPanel();
		});
	}
	public void buyPacks(PackTier tier, CardSet targetSet, int count)
	{
		clientThread.invoke(() ->
		{
			if (!isPlayable() || count < 1)
			{
				return;
			}
			if (count == 1)
			{
				for (Card card : packService.buy(engine.getState(), tier, targetSet, rewards))
				{
					announce(card);
				}
				persist();
				refreshPanel();
				return;
			}
			RewardQueue batch = new RewardQueue();
			List<Card> pulled = packService.buyMany(engine.getState(), tier, targetSet, count, batch);
			if (pulled.isEmpty())
			{
				refreshPanel();
				return;
			}
			PackRevealOverlay reveal = revealOverlay;
			if (reveal != null)
			{
				reveal.makeWayForBatch();
			}

			revealHighlights(batch.claimAll());
			summarise(tier, pulled);
			persist();
			refreshPanel();
		});
	}

	private static final int BULK_HIGHLIGHTS = 10;
	private void revealHighlights(List<Reward> batch)
	{
		batch.sort(Comparator.comparingInt(DopamineSimulatorPlugin::significance).reversed());
		List<Reward> highlights = batch.size() > BULK_HIGHLIGHTS
			? batch.subList(0, BULK_HIGHLIGHTS)
			: batch;

		PackRevealOverlay reveal = revealOverlay;
		if (reveal != null)
		{
			reveal.pushBatch(highlights);
		}
		for (Reward reward : highlights)
		{
			if (reward.getCard() != null)
			{
				announce(reward.getCard());
			}
		}
	}

	static int significance(Reward reward)
	{
		int rarity = reward.getRarity() == null ? 0 : reward.getRarity().ordinal();
		switch (reward.getType())
		{
			case BANNER_WIN:
				return 2000;
			case SET_COMPLETE:
				return 1000;
			case NEW_CARD:
				return 500 + rarity;
			case FUSION:
				return 400 + rarity;
			case STAR_UP:
				return 200 + rarity;
			default:
				return rarity;
		}
	}
	private void summarise(PackTier tier, List<Card> pulled)
	{
		if (!config.chatMessageOnNewCard())
		{
			return;
		}
		int packs = Math.max(1, pulled.size() / Math.max(1, tier.getCardCount()));
		long best = pulled.stream()
			.filter(c -> c.getRarity().ordinal() >= Rarity.EPIC.ordinal())
			.count();
		client.addChatMessage(ChatMessageType.GAMEMESSAGE, "",
			"Opened <col=ffb300>" + packs + "</col> " + tier.getDisplayName() + " packs: "
				+ pulled.size() + " cards, <col=ffb300>" + best + "</col> epic or better.", null);
	}
	public void buySourceUpgrade(PointSource source, int levels)
	{
		clientThread.invoke(() ->
		{
			if (!isPlayable())
			{
				return;
			}
			DopamineState state = engine.getState();
			int current = state.getSourceUpgradeLevel(source);
			int affordable = affordableCount(levels,
				count -> source.upgradeCostForMany(current, count), state.getPoints());
			if (affordable > 0
				&& state.spendPoints(source.upgradeCostForMany(current, affordable)))
			{
				state.addSourceUpgrades(source, affordable);
				persist();
			}
			refreshPanel();
		});
	}

	private static int affordableCount(int requested, java.util.function.IntToDoubleFunction cost,
									   double budget)
	{
		int count = requested;
		while (count > 0 && cost.applyAsDouble(count) > budget)
		{
			count--;
		}
		return count;
	}

	public void claimPassTier(int tier, boolean premium, CardSet targetSet)
	{
		clientThread.invoke(() ->
		{
			if (!isPlayable())
			{
				return;
			}
			if (passService.claim(engine.getState(), tier, premium, targetSet, rewards))
			{
				persist();
				refreshPanel();
			}
		});
	}

	public void claimAllPassTiers(CardSet targetSet)
	{
		clientThread.invoke(() ->
		{
			if (!isPlayable())
			{
				return;
			}
			int claimed = passService.claimAll(engine.getState(), targetSet, rewards);
			if (claimed > 0)
			{
				persist();
				refreshPanel();
			}
		});
	}

	public void pullBanner(Rarity rarity, CardSet targetSet, int count)
	{
		clientThread.invoke(() ->
		{
			if (!isPlayable())
			{
				return;
			}
			if (count == 1)
			{
				bannerService.pull(engine.getState(), rarity, targetSet, rewards);
				persist();
				refreshPanel();
				return;
			}

			// a ten pull is forty odd cards; show the best of them, not all of them
			RewardQueue batch = new RewardQueue();
			int pulled = 0;
			for (int i = 0; i < count; i++)
			{
				if (!bannerService.canPull(engine.getState(), rarity))
				{
					break;
				}
				bannerService.pull(engine.getState(), rarity, targetSet, batch);
				pulled++;
			}
			if (pulled == 0)
			{
				refreshPanel();
				return;
			}

			PackRevealOverlay reveal = revealOverlay;
			if (reveal != null)
			{
				reveal.makeWayForBatch();
			}
			revealHighlights(batch.claimAll());
			persist();
			refreshPanel();
		});
	}

	private void rollSeasons()
	{
		DopamineState state = engine.getState();
		long now = System.currentTimeMillis();
		boolean rolled = passService.rollIfExpired(state, now);
		rolled |= bannerService.rollIfExpired(state, now);
		if (!rolled)
		{
			return;
		}

		persist();
		refreshPanel();
		client.addChatMessage(ChatMessageType.GAMEMESSAGE, "",
			"Dopamine Simulator: a new season has begun - <col=ffb300>"
				+ Region.forSeason(state.getPassSeason()).getSeasonName() + "</col>.", null);
	}

	public void prestige()
	{
		clientThread.invoke(() ->
		{
			if (!isPlayable())
			{
				return;
			}
			DopamineState state = engine.getState();
			int stars = state.getTotalStars();
			if (!Prestige.canPrestige(stars))
			{
				return;
			}

			int gained = Prestige.insightFor(stars);
			state.prestige(gained);
			incomeTracker.reset();
			announcedSources.clear();
			if (revealOverlay != null)
			{
				revealOverlay.clear();
			}
			persist();
			refreshPanel();
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "",
				"Dopamine Simulator: prestiged for <col=ffb300>" + gained
					+ "</col> insight. Every upgrade level is now worth "
					+ Math.round((Prestige.gainMultiplier(state.getInsight()) - 1d) * 100d)
					+ "% more.", null);
		});
	}

	public void useWildcard(Card card)
	{
		clientThread.invoke(() ->
		{
			if (!isPlayable() || card == null)
			{
				return;
			}
			DopamineState state = engine.getState();
			if (!state.spendWildcard())
			{
				return;
			}
			collection.grant(state, card, rewards, false,
				Math.max(1, card.getRarity().copiesForMaxStars() / 10));
			persist();
			refreshPanel();
		});
	}

	public void forgeWithShards(Card card)
	{
		clientThread.invoke(() ->
		{
			if (!isPlayable() || card == null)
			{
				return;
			}
			DopamineState state = engine.getState();
			if (!state.spendShards(card.getRarity(), Balance.SHARDS_PER_FORGE))
			{
				return;
			}
			collection.grant(state, card, rewards, false,
				Math.max(1, card.getRarity().copiesForMaxStars() / 20));
			persist();
			refreshPanel();
		});
	}

	public void selectCardBack(String id)
	{
		clientThread.invoke(() ->
		{
			if (!isPlayable())
			{
				return;
			}
			DopamineState state = engine.getState();
			if (state.hasBack(id))
			{
				state.setSelectedBack(id);
				persist();
				refreshPanel();
			}
		});
	}

	public void buyPassPremium()
	{
		clientThread.invoke(() ->
		{
			if (isPlayable() && passService.buyPremium(engine.getState()))
			{
				persist();
				refreshPanel();
			}
		});
	}

	public void startNextPassSeason()
	{
		clientThread.invoke(() ->
		{
			if (isPlayable() && passService.startNextSeason(engine.getState()))
			{
				persist();
				refreshPanel();
			}
		});
	}

	public void flash(Reward reward)
	{
		PackRevealOverlay reveal = revealOverlay;
		if (reveal != null)
		{
			reveal.push(reward);
		}
		if (reward.getType() == RewardType.FEAT || reward.getType() == RewardType.ACHIEVEMENT)
		{
			// flash() runs on the Swing timer, and addChatMessage asserts the client thread.
			String line = "<col=ffb300>" + reward.getTitle() + "</col> - " + reward.getDetail();
			clientThread.invokeLater(() ->
				client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", line, null));
		}
		FloatingTextOverlay floating = floatingTextOverlay;
		if (floating != null)
		{
			floating.reward(reward);
		}
	}
	private void announce(Card card)
	{
		if (!config.chatMessageOnNewCard()
			|| card.getRarity().ordinal() < config.minimumChatRarity().ordinal())
		{
			return;
		}
		client.addChatMessage(ChatMessageType.GAMEMESSAGE, "",
			"<col=" + hex(card.getRarity()) + ">" + card.getRarity().getDisplayName() + "</col>: "
				+ card.getName(), null);
	}
	private static String hex(Rarity rarity)
	{
		return String.format("%06x", rarity.getColour().getRGB() & 0xFFFFFF);
	}
	private void loadStateForCurrentAccount()
	{
		long accountHash = client.getAccountHash();
		if (accountHash == loadedAccountHash)
		{
			return;
		}
		if (loadedAccountHash != Long.MIN_VALUE)
		{
			saveManager.save(loadedAccountHash, engine.getState());
		}
		engine.setState(saveManager.load(accountHash));
		loadedAccountHash = accountHash;
		incomeTracker.reset();
		clickState.clear();
		announcedSources.clear();
		achievementSystem.newSession();
		if (revealOverlay != null)
		{
			revealOverlay.clear();
		}
		if (floatingTextOverlay != null)
		{
			floatingTextOverlay.clear();
		}

		refreshPanel();
	}
	@Subscribe
	public void onCommandExecuted(CommandExecuted event)
	{
		if ("resetfeats".equalsIgnoreCase(event.getCommand()))
		{
			runReset("resetfeats", "feat ranks",
				state -> Feats.tiersEarned(state) + " feat ranks",
				DopamineState::resetFeats);
		}
		else if ("resetdopamine".equalsIgnoreCase(event.getCommand()))
		{
			runReset("resetdopamine", "everything",
				state -> "every card, upgrade, feat and pass season",
				this::wipe);
		}
		else if ("givedopamine".equalsIgnoreCase(event.getCommand()) && developerMode)
		{
			grantPoints(event.getArguments());
		}
		else if ("dumpui".equalsIgnoreCase(event.getCommand()) && developerMode)
		{
			String[] arguments = event.getArguments();
			if (arguments != null && arguments.length > 0
				&& "sweep".equalsIgnoreCase(arguments[0]))
			{
				toggleSweep();
			}
			else
			{
				dumpOpenInterfaces(arguments);
			}
		}
	}

	/**
	 * Records every interface from now until told to stop, one entry each, all
	 * appended to a single file. For walking a couple of hundred windows without
	 * running a command at every one of them.
	 */
	private void toggleSweep()
	{
		sweeping = !sweeping;
		if (sweeping)
		{
			sweptGroups.clear();
			pendingGroups.clear();
		}
		client.addChatMessage(ChatMessageType.GAMEMESSAGE, "",
			sweeping
				? "Dopamine Simulator: recording interfaces to "
					+ WidgetDump.sweepFile().getAbsolutePath()
					+ ". Open the windows you want, then ::dumpui sweep again to stop."
				: "Dopamine Simulator: stopped recording, " + sweptGroups.size()
					+ " interfaces captured.", null);
	}

	/**
	 * Writes whatever interfaces are open to a file, with the sprite, model and
	 * item id behind each widget. Beats searching the api jar and concluding a
	 * monster has no art because one particular list did not mention it.
	 */
	private void dumpOpenInterfaces(String[] arguments)
	{
		String label = arguments != null && arguments.length > 0 ? arguments[0] : "dump";
		try
		{
			File written = WidgetDump.dump(client, label);
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "",
				written == null
					? "Dopamine Simulator: no interface is open to dump."
					: "Dopamine Simulator: wrote " + written.getAbsolutePath(), null);
		}
		catch (IOException e)
		{
			log.warn("could not dump widgets", e);
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "",
				"Dopamine Simulator: could not write the dump, see the client log.", null);
		}
	}

	private void grantPoints(String[] arguments)
	{
		if (!isPlayable())
		{
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "",
				"Dopamine Simulator: log in first.", null);
			return;
		}

		double amount = arguments == null || arguments.length == 0
			? 1_000_000d
			: parseAmount(arguments[0]);
		if (amount <= 0d)
		{
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "",
				"Dopamine Simulator: usage ::givedopamine 250k", null);
			return;
		}

		DopamineState state = engine.getState();
		state.addPoints(amount);
		persist();
		refreshPanel();
		client.addChatMessage(ChatMessageType.GAMEMESSAGE, "",
			"Dopamine Simulator: added <col=ffb300>" + BigNumbers.format(amount)
				+ "</col> points.", null);
	}

	private static double parseAmount(String raw)
	{
		String text = raw.trim().toLowerCase(Locale.ROOT).replace(",", "");
		double scale = 1d;
		if (text.endsWith("k"))
		{
			scale = 1_000d;
		}
		else if (text.endsWith("m"))
		{
			scale = 1_000_000d;
		}
		else if (text.endsWith("b"))
		{
			scale = 1_000_000_000d;
		}

		try
		{
			return Double.parseDouble(scale > 1d
				? text.substring(0, text.length() - 1)
				: text) * scale;
		}
		catch (NumberFormatException e)
		{
			return 0d;
		}
	}

	private void runReset(String command, String what,
		Function<DopamineState, String> summary, Consumer<DopamineState> reset)
	{
		if (engine == null || !isPlayable())
		{
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "",
				"Dopamine Simulator: log in first.", null);
			return;
		}

		DopamineState state = engine.getState();
		long now = System.currentTimeMillis();

		if (!command.equals(pendingResetCommand) || now - resetRequestedAt > RESET_CONFIRM_MS)
		{
			pendingResetCommand = command;
			resetRequestedAt = now;
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "",
				"Dopamine Simulator: this wipes <col=ffb300>" + summary.apply(state)
					+ "</col> and cannot be undone."
					+ " Run <col=ffb300>::" + command + "</col> again to confirm.", null);
			return;
		}

		pendingResetCommand = null;
		resetRequestedAt = 0L;
		reset.accept(state);
		persist();
		refreshPanel();
		client.addChatMessage(ChatMessageType.GAMEMESSAGE, "",
			"Dopamine Simulator: reset " + what + ".", null);
	}

	private void wipe(DopamineState state)
	{
		engine.setState(new DopamineState());
		incomeTracker.reset();
		clickState.clear();
		announcedSources.clear();
		achievementSystem.newSession();
		if (revealOverlay != null)
		{
			revealOverlay.clear();
		}
		if (floatingTextOverlay != null)
		{
			floatingTextOverlay.clear();
		}
	}

	private void persist()
	{
		if (engine != null && loadedAccountHash != Long.MIN_VALUE)
		{
			saveManager.save(loadedAccountHash, engine.getState());
		}
	}
	private void refreshPanel()
	{
		DopamineSimulatorPanel current = panel;
		if (current != null)
		{
			current.refresh();
		}
	}

	private void applyPluginIcon()
	{
		BufferedImage cookie = itemManager.getImage(ItemID.CHOCCHIP_CRUNCHIES);
		if (cookie == null)
		{
			return;
		}

		BufferedImage large = ImageUtil.resizeImage(cookie, 32, 32);
		SurgeInfoBox box = surgeInfoBox;
		if (box != null)
		{
			box.setImage(large);
		}

		BufferedImage small = ImageUtil.resizeImage(cookie, 16, 16);
		SwingUtilities.invokeLater(() ->
		{
			if (panel == null || navButton == null)
			{
				return;
			}

			clientToolbar.removeNavigation(navButton);
			navButton = NavigationButton.builder()
				.tooltip("Dopamine Simulator")
				.icon(small)
				.priority(7)
				.panel(panel)
				.build();
			clientToolbar.addNavigation(navButton);
		});
	}

	private static BufferedImage buildIcon()
	{
		BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		java.awt.Graphics2D g = image.createGraphics();
		g.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
			java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(new java.awt.Color(0x2E, 0x2E, 0x2E));
		g.fillRoundRect(2, 1, 9, 13, 3, 3);
		g.setColor(Rarity.LEGENDARY.getColour());
		g.drawRoundRect(2, 1, 9, 13, 3, 3);
		g.setColor(new java.awt.Color(0x42, 0xA5, 0xF5));
		g.fillRoundRect(6, 3, 8, 12, 3, 3);
		g.setColor(new java.awt.Color(0x1E, 0x1E, 0x1E));
		g.drawRoundRect(6, 3, 8, 12, 3, 3);
		g.dispose();
		return image;
	}
}
