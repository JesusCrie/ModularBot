package com.jesus_crie.modularbot.utils;

import net.dv8tion.jda.core.utils.SessionControllerAdapter;
import org.slf4j.LoggerFactory;

public class ModularSessionController extends SessionControllerAdapter {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger("Session Controller");

    @Override
    public void appendSession(SessionConnectNode node) {
        LOG.info("Queuing new node !");
        super.appendSession(node);
    }
}
