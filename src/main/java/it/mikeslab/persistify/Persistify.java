/*
 *  Copyright (c) 2023, MikesLab
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  1. Redistributions of source code must retain the above copyright notice, this
 *     list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *  3. Neither the name of the copyright holder nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *  4. Redistribution of this software in source or binary forms shall be free
 *     of all charges or fees to the recipient of this software.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 *  AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 *  IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 *  FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 *  DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 *  SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 *  CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 *  OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 *  OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package it.mikeslab.persistify;

import com.google.common.base.Stopwatch;
import it.mikeslab.persistify.api.APIManager;
import it.mikeslab.persistify.listener.UserConnectedEvent;
import it.mikeslab.persistify.util.ConfigHandler;
import it.mikeslab.persistify.util.MongoDBHandler;
import lombok.Getter;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;

import java.util.concurrent.TimeUnit;

public final class Persistify extends Plugin {
    @Getter private static Persistify instance;
    @Getter private APIManager apiManager;
    private MongoDBHandler dbHandler;

    @Override
    public void onEnable() {
        instance = this;

        getLogger().info("===== Persistify =====");
        getLogger().info("Initializing plug-in...");
        Stopwatch stopwatch = Stopwatch.createStarted();

        new ConfigHandler().loadConfig();
        initDbHandler();
        registerListeners();

        stopwatch.stop();
        getLogger().info("Plugin enabled in " + stopwatch.elapsed(TimeUnit.MILLISECONDS) + "ms");
        getLogger().info("===== Persistify =====");
    }

    @Override
    public void onDisable() {
        dbHandler.disconnect();
    }


    private void registerListeners() {
        this.getProxy().getPluginManager().registerListener(this, new UserConnectedEvent(dbHandler));
    }

    private void initDbHandler() {
        Configuration config = ConfigHandler.getConfig();

        String connectionString = config.getString("connection-string");
        String database = config.getString("database");
        String collection = config.getString("collection");

        dbHandler = new MongoDBHandler(connectionString, database, collection);
    }



}
