/*
 * Copyright 2014 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.terasology.cities.model;

import java.awt.geom.Ellipse2D;

import javax.vecmath.Point2i;

/**
 * TODO Type description
 * @author Martin Steiger
 */
public class RoundTower extends AbstractBuilding implements Tower {

    /**
     * @param center the center of the tower
     * @param radius the radius
     * @param baseHeight the height of the floor level
     * @param wallHeight the building height above the floor level
     */
    public RoundTower(Point2i center, int radius, int baseHeight, int wallHeight) {
        super(
            new Ellipse2D.Double(center.x - radius, center.y - radius, 2 * radius, 2 * radius),
            new ConicRoof(center, radius + 2, baseHeight + wallHeight, 1),
            baseHeight,
            wallHeight);
    }

    @Override
    public Ellipse2D getLayout() {
        return (Ellipse2D) super.getLayout();
    }

}
