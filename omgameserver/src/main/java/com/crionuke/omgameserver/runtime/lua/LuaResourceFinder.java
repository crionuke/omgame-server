package com.crionuke.omgameserver.runtime.lua;

import org.jboss.logging.Logger;
import org.luaj.vm2.lib.ResourceFinder;

import java.io.*;

/**
 * @author Kirill Byvshev (k@byv.sh)
 * @version 1.0.0
 */
class LuaResourceFinder implements ResourceFinder {
    static final Logger LOG = Logger.getLogger(LuaResourceFinder.class);

    final String rootDirectory;

    LuaResourceFinder() {
        this.rootDirectory = ".";
    }

    LuaResourceFinder(String rootDirectory) {
        this.rootDirectory = rootDirectory;
    }

    @Override
    public InputStream findResource(String fileName) {
        File parent = new File(rootDirectory);
        File file = new File(parent, fileName);
        if (file.exists()) {
            try {
                InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
                LOG.infof("Resource found, fileName=%s", fileName);
                return inputStream;
            } catch (IOException e) {
                LOG.warnf("Failed to open resource, fileName=%s. %s", fileName, e.getMessage());
                return null;
            }
        } else {
            LOG.warnf("Resource not exists, get resource, fileName=%s", fileName);
            return getClass().getResourceAsStream(fileName.startsWith("/") ? fileName : "/" + fileName);
        }
    }
}
