package com.jesus_crie.modularbot.core.nashorn;

import com.jesus_crie.modularbot.core.ModularBot;
import com.jesus_crie.modularbot.core.ModularBotBuilder;
import com.jesus_crie.modularbot.core.module.Module;
import com.jesus_crie.modularbot.core.module.ModuleManager;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

/**
 * Wrap a JavaScript module to delegate the calls to the underlying JS code.
 */
@Deprecated
public class JavaScriptModule extends Module {

    private final ScriptObjectMirror jsModule;

    JavaScriptModule(@Nonnull final ScriptObjectMirror module) {
        super((ModuleInfo) module.getMember("info"));
        this.jsModule = module;
        jsModule.setMember("getState", (Supplier<State>) this::getState);
        jsModule.setMember("getBot", (Supplier<ModularBot>) this::getBot);
    }

    @Nonnull
    public ScriptObjectMirror getUnderlyingModule() {
        return jsModule;
    }

    @Override
    public void onLoad(@Nonnull ModuleManager moduleManager, @Nonnull ModularBotBuilder builder) {
        if (jsModule.hasMember("onLoad")) {
            jsModule.callMember("onLoad", moduleManager, builder);
        }
    }

    @Override
    public void onInitialization(@Nonnull final ModuleManager moduleManager) {
        if (jsModule.hasMember("onInitialization")) {
            jsModule.callMember("onInitialization");
        }
    }

    @Override
    public void onPostInitialization() {
        if (jsModule.hasMember("onPostInitialization")) {
            jsModule.callMember("onPostInitialization");
        }
    }

    @Override
    public void onPrepareShards() {
        if (jsModule.hasMember("onPrepareShards")) {
            jsModule.callMember("onPrepareShards");
        }
    }

    @Override
    public void onShardsCreated() {
        if (jsModule.hasMember("onShardsCreated")) {
            jsModule.callMember("onShardsCreated");
        }
    }

    @Override
    public void onShardsReady(@Nonnull final ModularBot bot) {
        super.onShardsReady(bot);
        if (jsModule.hasMember("onShardsCreated")) {
            jsModule.callMember("onShardsCreated", bot);
        }
    }

    @Override
    public void onShutdownShards() {
        if (jsModule.hasMember("onShutdownShards")) {
            jsModule.callMember("onShutdownShards");
        }
    }

    @Override
    public void onUnload() {
        if (jsModule.hasMember("onUnload")) {
            jsModule.callMember("onUnload");
        }
    }
}
