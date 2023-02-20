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

package it.mikeslab.persistify.util;


import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import it.mikeslab.persistify.Persistify;
import it.mikeslab.persistify.object.User;
import net.md_5.bungee.api.plugin.Plugin;
import org.bson.Document;

import java.util.UUID;

/**
 * A helper class for connecting to and interacting with a MongoDB database.
 */
public class MongoDBHandler {

    private MongoCollection<Document> collection;
    private MongoClient mongoClient;
    private MongoDatabase database;

    /**
     * Constructs a new instance of MongoDBHandler that connects to the specified MongoDB server and initializes the database and collection.
     *
     * @param connString the connection string to the MongoDB server in the format "mongodb://[username:password@]host[:port][/?options]"
     * @param databaseName the name of the MongoDB database to use
     * @param collectionName the name of the MongoDB collection to use
     *
     */
    public MongoDBHandler(String connString, String databaseName, String collectionName) {
        ConnectionString connectionString = new ConnectionString(connString);

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .serverApi(ServerApi.builder()
                        .version(ServerApiVersion.V1)
                        .build())
                .build();

        mongoClient = MongoClients.create(settings);
        database = mongoClient.getDatabase(databaseName);
        collection = this.getCollection(collectionName);

        Plugin plugin = Persistify.getInstance();
        if(mongoClient != null) {
            plugin.getLogger().info("Connection to MongoDB established.");
        } else {
            plugin.getLogger().severe("Connection to MongoDB failed. Shutting down...");
            plugin.getProxy().stop();
        }
    }

    /**
     * Adds or overwrites a player's data for the specified BungeeCord server name and player UUID.
     *
     * @param user a User object containing the player's data
     */
    public void registerUser(User user) {
        UUID playerUUID = user.getPlayerUUID();
        String playerName = user.getPlayerName();
        int level = user.getLevel();
        String bungeeCordServerName = user.getServerName();

        Document existingDoc = isUserRegistered(bungeeCordServerName, playerUUID.toString());

        if (existingDoc == null) {
            //If the document doesn't exist, create it
            Document newDoc = new Document("serverName", bungeeCordServerName)
                    .append("uuid", playerUUID.toString())
                    .append("name", playerName)
                    .append("level", level);
            collection.insertOne(newDoc);
        }
    }

    /**
     * Sets the level of the player with the specified UUID on the specified BungeeCord server.
     *
     * @param bungeeCordServerName the name of the BungeeCord server you want to set the level on
     * @param playerUUID the UUID of the player
     * @param newLevel the new level of the player
     */
    public void setLevelByUUID(String bungeeCordServerName, UUID playerUUID, int newLevel) {
        collection.updateOne(new Document("serverName", bungeeCordServerName)
                .append("uuid", playerUUID.toString()), new Document("$set", new Document("level", newLevel)));
    }

    /**
     * Gets the level of a player given their UUID and the name of a BungeeCord server.
     *
     * @param serverName the name of the BungeeCord server where the player joined
     * @param playerUUID the UUID of the player whose level is being retrieved
     * @return the level of the player, or -1 if no data was found for the player
     */
    public int getLevelByUUID(String serverName, UUID playerUUID) {
        Document doc = collection.find(new Document("serverName", serverName).append("uuid", playerUUID.toString())).first();
        if (doc != null) {
            return doc.getInteger("level", -1);
        } else {
            return -1;
        }
    }

    /**
     * Disconnects from the MongoDB database and releases any resources held by the MongoDB driver.
     */
    public void disconnect() {
        if (mongoClient != null) {
            mongoClient.close();
            mongoClient = null;
            database = null;
            collection = null;
        }
    }


    private Document isUserRegistered(String serverName, String playerUUID) {
        return collection.find(new Document("serverName", serverName)
                .append("uuid", playerUUID)).first();
    }


    private MongoCollection<Document> getCollection(String collectionName) {
        if (!doesCollectionExist(collectionName)) {
            database.createCollection(collectionName);
        }
        return database.getCollection(collectionName);
    }

    private boolean doesCollectionExist(String collectionName) {
        for (String collection : database.listCollectionNames()) {
            if (collection.equalsIgnoreCase(collectionName)) {
                return true;
            }
        }
        return false;
    }


}

