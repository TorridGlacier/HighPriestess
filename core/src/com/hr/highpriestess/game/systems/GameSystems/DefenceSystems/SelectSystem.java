package com.hr.highpriestess.game.systems.GameSystems.DefenceSystems;

import com.artemis.Aspect;
import com.artemis.BaseEntitySystem;
import com.artemis.BaseSystem;
import com.artemis.ComponentMapper;
import com.artemis.managers.TagManager;
import com.artemis.systems.IteratingSystem;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.hr.highpriestess.game.components.Game.Defence.Selectable;
import com.hr.highpriestess.game.components.Menu.Bounds;
import com.hr.highpriestess.game.systems.MenuSystems.CollisionSystem;

/**
 * Created by Titas on 2016-08-02.
 */
public class SelectSystem extends BaseEntitySystem {

    String TAG = SelectSystem.class.getName();

    boolean entitiesSelected;
    CollisionSystem collisionSystem;
    TagManager tagManager;

    ComponentMapper<Selectable> selectableCm;

    int lowestSelectedLayer = 999;

    IntBag selectedEntities = new IntBag();

    boolean selectionSquareAlive = false;

    public SelectSystem() {
        super(Aspect.all(Selectable.class, Bounds.class));
    }


    @Override
    protected void processSystem() {
        if (tagManager.isRegistered("selectionSquare")) {
            selectionSquareAlive = true;
            int selectSquare = tagManager.getEntity("selectionSquare").getId();
            IntBag actives = subscription.getEntities();
            for (int i = 0; i < actives.size(); i++) {
                int e = actives.get(i);
                if (!selectedEntities.contains(e)) {
                    if (collisionSystem.twoEntityCollision(e, selectSquare)) {
                        System.out.println("Collision detected");
                        if (lowestSelectedLayer < selectableCm.get(e).getSelectionLayer()) {
                            selectedEntities.clear();
                            lowestSelectedLayer = selectableCm.get(e).getSelectionLayer();
                            selectedEntities.add(e);
                        } else {
                            selectedEntities.add(e);
                        }
                    }






                }
            }} else if (selectionSquareAlive) {
                for (int i = 0; i < selectedEntities.size(); i++) {
                    selectableCm.get(selectedEntities.get(i)).setSelected(true);
                    Gdx.app.debug(TAG, selectedEntities.get(i) + " is selected");
                }
                if (selectedEntities.size() == 0) {
                    Gdx.app.debug(TAG, "Nothing is selected");
                }
                selectedEntities.clear();
                lowestSelectedLayer = 999;
                selectionSquareAlive = false;
            }
        }
    }
