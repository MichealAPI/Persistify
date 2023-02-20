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

package it.mikeslab.persistify.api;

import it.mikeslab.persistify.util.MongoDBHandler;

import java.util.UUID;

public class APIManager {

    private final MongoDBHandler mongoDBHandler;

    public APIManager(MongoDBHandler mongoDBHandler) {
        this.mongoDBHandler = mongoDBHandler;
    }

    /**
     * Sets the level of the specified player on the specified server.
     *
     * @param serverName the name of the server the player is on
     * @param playerUUID the UUID of the player whose level to set
     * @param level      the level to set for the player
     */
    public void setPlayerLevel(String serverName, UUID playerUUID, int level) {
        mongoDBHandler.setLevelByUUID(serverName, playerUUID, level);
    }

    /**
     * Gets the level of the specified player on the specified server.
     *
     * @param serverName the name of the server the player is on
     * @param playerUUID the UUID of the player to get the level for
     * @return the player's level, or -1 if the player was not found in the database
     */
    public int getPlayerLevel(String serverName, UUID playerUUID) {
        return mongoDBHandler.getLevelByUUID(serverName, playerUUID);
    }

}
