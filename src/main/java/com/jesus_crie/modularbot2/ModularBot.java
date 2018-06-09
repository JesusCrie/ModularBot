package com.jesus_crie.modularbot2;

import com.jesus_crie.modularbot2.managers.ModularEventManager;
import com.jesus_crie.modularbot2.module.Lifecycle;
import com.jesus_crie.modularbot2.module.ModuleManager;
import com.jesus_crie.modularbot2.utils.IStateProvider;
import com.jesus_crie.modularbot2.utils.ModularSessionController;
import com.jesus_crie.modularbot2.utils.ModularThreadFactory;
import net.dv8tion.jda.bot.sharding.DefaultShardManager;
import net.dv8tion.jda.bot.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.bot.sharding.ShardManager;
import net.dv8tion.jda.core.audio.factory.IAudioSendFactory;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.impl.ModularLog;
import org.slf4j.impl.ModularLogger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.security.auth.login.LoginException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.function.IntFunction;

public class ModularBot extends DefaultShardManager {

    private static Logger logger = LoggerFactory.getLogger("ModularBot");

    protected final ModuleManager moduleManager;

    /**
     * @param token                     The token
     * @param shardsTotal               The total amount of shards or {@code -1} to retrieve the recommended amount from discord.
     * @param stateProvider             Provide the online status, game and idle of each shard.
     * @param enableVoice               Whether or not Voice should be enabled
     * @param enableBulkDeleteSplitting Whether or not {@link DefaultShardManagerBuilder#setBulkDeleteSplittingEnabled(boolean)
     *                                  bulk delete splitting} should be enabled
     * @param useShutdownNow            Whether the ShardManager should use JDA#shutdown() or not
     * @param maxReconnectDelay         The max reconnect delay (default 900)
     * @param corePoolSize              The core pool size for JDA's internal executor (default 2)
     * @param audioSendFactory          The {@link IAudioSendFactory IAudioSendFactory}
     */
    ModularBot(final String token, final int shardsTotal, final @Nullable IStateProvider stateProvider,
               final boolean enableVoice, final boolean enableBulkDeleteSplitting, final boolean useShutdownNow,
               final int maxReconnectDelay, final int corePoolSize, final @Nullable IAudioSendFactory audioSendFactory,
               final @Nonnull ModuleManager moduleManager, final @Nonnull List<IntFunction<Object>> listenerProviders) {
        super(shardsTotal, null,
                new ModularSessionController(),
                new ArrayList<>(), listenerProviders,
                token, new ModularEventManager(),
                audioSendFactory,
                stateProvider != null ? stateProvider.getGameProvider() : null, stateProvider != null ? stateProvider.getOnlineStatusProvider() : null,
                null, null,
                new ModularThreadFactory("Global", true),
                maxReconnectDelay, corePoolSize,
                enableVoice,
                true, enableBulkDeleteSplitting, true,
                stateProvider != null ? stateProvider.getIdleProvider() : null,
                true, useShutdownNow,
                false, null);

        listenerProviders.add(shard -> new ListenerAdapter() {
            @Override
            public void onReady(ReadyEvent event) {
                ModularBot.this.onReady();
            }
        });

        this.moduleManager = moduleManager;
        moduleManager.initialize();

        logger.info("Modular initialized !");
    }

    /**
     * Start the bot by connecting it to discord.
     *
     * @throws LoginException If the credentials are wrong.
     */
    @Override
    public void login() throws LoginException {
        logger.info("Starting shards...");
        moduleManager.dispatch(Lifecycle::onPrepareShards);
        super.login();
        logger.info(shards.size() + " shards successfully spawned !");
        moduleManager.dispatch(Lifecycle::onShardsLoaded);
    }

    private void onReady() {
        moduleManager.finalizeInitialization(this);
    }

    @Override
    protected ScheduledExecutorService createExecutor(ThreadFactory threadFactory) {
        return Executors.newScheduledThreadPool(corePoolSize, r -> {
            Thread t = threadFactory.newThread(r);
            t.setPriority(Thread.NORM_PRIORITY + 1);
            return t;
        });
    }

    @Override
    public void addEventListener(Object... listeners) {
        if (shards != null)
            super.addEventListener(listeners);
    }

    @Override
    public void addEventListeners(IntFunction<Object> eventListenerProvider) {
        if (shards != null)
            super.addEventListeners(eventListenerProvider);
    }

    @Override
    public void removeEventListener(Object... listeners) {
        if (shards != null)
            super.removeEventListener(listeners);
    }

    @Override
    public void shutdown() {
        logger.info("Shutting down...");
        moduleManager.dispatch(Lifecycle::onShutdownShards);
        super.shutdown();
        moduleManager.unload();

        logger.info("Bot powered off successfully !");
    }
}
