package com.hr.highpriestess.game.systems.GameSystems;

import com.artemis.BaseSystem;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapImageLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Queue;
import com.hr.highpriestess.game.components.Game.Tracker.NeighborMapTracker;
import com.hr.highpriestess.game.systems.MenuSystems.AssetSystem;
import com.hr.highpriestess.game.util.MapSetupHolder;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by Titas on 2017-07-12.
 */
public class BackgroundAssetSystem extends BaseSystem {


    ComponentMapper<NeighborMapTracker> neighborCm;

    TagManager tagManager;

    AssetSystem assetSystem;


    String TAG = BackgroundAssetSystem.class.getName();

    public boolean isSetup = false;
    private boolean assetsLoaded = false;



    private void removeUnusedResources() {
        Gdx.app.debug(TAG, "Removing unused resources");
        HashMap<String, Array<String>> map = neighborCm.get(tagManager.getEntity("tracker")).neighborMapAssets;
        HashSet<String> neighbors = neighborCm.get(tagManager.getEntity("tracker")).currentNeighborNames;
        HashMap<String, Integer> assetCount = neighborCm.get(tagManager.getEntity("tracker")).assetUseCounter;

        for (String mapName : map.keySet()) {
            Gdx.app.debug(TAG, "Checking map with name: " + mapName);
            if (!neighbors.contains(mapName)) {
                Gdx.app.debug(TAG, mapName + " is not in neighbors");
                for (String assetName : map.get(mapName)) {
                    Gdx.app.debug(TAG, "decrementing by one asset with name " + assetName);
                    assetCount.put(assetName, assetCount.get(assetName) - 1);
                    Gdx.app.debug(TAG, "assetCount for " + assetName + " is " + assetCount.get(assetName));
                    if (assetCount.get(assetName) == 0) {
                        Gdx.app.debug(TAG, "Removing asset from assetUseCounter with name: " + assetName);
                        assetCount.remove(assetName);
                        Gdx.app.debug(TAG, "Unloading resource with name: " + assetName);
                        assetSystem.assetManager.unload(assetName);
                    }
                }
                map.put(mapName, new Array<String>());
            } else {
                Gdx.app.debug(TAG, mapName + " is in neighbors");
            }
        }
    }


    private void getNeighborAssets() {
        Gdx.app.debug(TAG, "getting ready to rerieve assets of all neighboring maps");
        Entity tracker = tagManager.getEntity("tracker");
        Gdx.app.debug(TAG, "neighboring map count is " + neighborCm.get(tracker).currentNeighborNames.size());
        Array<TiledMapTileLayer> layers = new Array<TiledMapTileLayer>();
        for (String mapName : neighborCm.get(tracker).currentNeighborNames) {
            if (neighborCm.get(tracker).lastMapNeighborNames.contains(mapName) ||
                    neighborCm.get(tracker).activeMapName.equals(mapName))
                        continue;
            layers.clear();
            TiledMap map = assetSystem.assetManager.get(mapName);
            Gdx.app.debug(TAG, "going through layers of " +mapName);
            for (MapLayer rawLayer : map.getLayers()) {
                if (rawLayer.getClass() == TiledMapImageLayer.class) continue;
                Gdx.app.debug(TAG, rawLayer.getClass().toString());
                layers.add((TiledMapTileLayer) rawLayer);
            }
            Gdx.app.debug(TAG, "done retrieving layers of " + mapName);

            Gdx.app.debug(TAG, "retrieving height and width of " + mapName);
            float width = layers.get(0).getWidth();
            float height = layers.get(0).getHeight();

            Gdx.app.debug(TAG, "getting assets from map " + mapName);
            MapSetupHolder.getAssetsFromMap(layers, width, height, tracker, mapName);
            Gdx.app.debug(TAG, "getting assets from map finished " + mapName);
        }
    }



    private void addAssetsToQueue() {
        Gdx.app.debug(TAG, "Adding assets to Background system Queue");
        HashMap<String, Array<String>> map = neighborCm.get(tagManager.getEntity("tracker")).neighborMapAssets;
        for (String key : map.keySet()) {
            for (int i = 0; i < map.get(key).size; i++) {
                if (!assetSystem.assetManager.isLoaded(map.get(key).get(i))) {
                    assetSystem.assetManager.load(map.get(key).get(i), Texture.class); // For now we only load textures
                }
            }
        }

    }

    private void debugPrint() {
        if (Gdx.app.getLogLevel() != Gdx.app.LOG_DEBUG)
            return;

        NeighborMapTracker neigh = neighborCm.get(tagManager.getEntity("tracker"));
        for (String key : neigh.assetUseCounter.keySet()) {
            Gdx.app.debug("Use of resources", key + " : " +neigh.assetUseCounter.get(key));
        }
        Gdx.app.debug(TAG, "LastMapNeighbors");
        for (String mapName : neigh.lastMapNeighborNames) {
            Gdx.app.debug("", mapName);
        }

        Gdx.app.debug(TAG, "currentNeighbors");
        for (String mapName : neigh.currentNeighborNames) {
            Gdx.app.debug("", mapName);
        }

        Gdx.app.debug(TAG, "assetLists");
        String mapResources;
        for (String mapName : neigh.neighborMapAssets.keySet()) {
            mapResources = "";
            for (String resourceName : neigh.neighborMapAssets.get(mapName)) {
                mapResources += resourceName + ", ";
            }
            Gdx.app.debug(mapName, mapResources);
        }

    }

    @Override
    protected void processSystem() {

        if (!isSetup) {
            getNeighborAssets();
            removeUnusedResources();
            addAssetsToQueue();
            debugPrint();
            isSetup = true;
            Gdx.app.debug(TAG, "Setup Complete");
        }


        if (assetSystem.assetManager.getProgress() != 1) {
            Gdx.app.debug(TAG, "assetManager progress: " + assetSystem.assetManager.getProgress());
        }

        assetSystem.assetManager.update(10); //update the assetSystem for 10 milliseconds.


    }
}