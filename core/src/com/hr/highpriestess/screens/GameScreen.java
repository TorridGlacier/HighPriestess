package com.hr.highpriestess.screens;

import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.artemis.WorldConfigurationBuilder;
import com.artemis.managers.GroupManager;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.hr.highpriestess.G;
import com.hr.highpriestess.game.systems.GameSystems.*;
import com.hr.highpriestess.game.systems.GameSystems.Render.EntityRenderSystem;
import com.hr.highpriestess.game.systems.GameSystems.Render.TilemapRender;
import com.hr.highpriestess.game.systems.MenuSystems.*;

/**
 * @author Titas
 * @version
 */

// screen for the wandering sequence of the game

public class GameScreen extends AbstractScreen {

    Game game;
    World world;

    public GameScreen(Game game) {

        this.game = game;
        WorldConfiguration config = new WorldConfigurationBuilder()
                .with(
                        // make the menu
                        new GroupManager(),
                        new TagManager(),

                        G.assetSystem,

                        new GameMapSystem("Monastery"),
                        new ChangeMapSystem(),


                        new GameEntitySpawnerSystem(),
                        new GameEntityClearerSystem(),

                        new CollisionSystem(),
                        new CameraSystem(),

                        new KinematicsSystem(),

                        new ControllerSystem(),


                        new TilemapRender(),
                        new EntityRenderSystem()
                ).build();
        G.gameWorld = new World(config);

    }




    @Override
    public void dispose() {
        G.gameWorld.dispose();
    }





    public void render(float deltaTime) {
        // clear the game
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        // process the menuWorld
        G.gameWorld.process();

    }
}
