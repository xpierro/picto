package com.github.picto.module;

import com.github.picto.filesystem.FilesystemMetainfo;
import com.github.picto.filesystem.IFilesystemMetainfo;
import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;

/**
 * Created by Pierre on 20/09/15.
 */
public class FilesystemModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(IFilesystemMetainfo.class).to(FilesystemMetainfo.class);
    }
}
