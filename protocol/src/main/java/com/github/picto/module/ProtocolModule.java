package com.github.picto.module;

import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;

/**
 * Created by Pierre on 08/09/15.
 */
public class ProtocolModule extends AbstractModule {
    @Override
    protected void configure() {
        EventBus eventBus = new EventBus();
        bind(EventBus.class).toInstance(eventBus);
    }
}
