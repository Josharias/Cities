/*
 * Copyright 2013 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.terasology.cities.contour;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import org.terasology.cities.terrain.HeightMap;
import org.terasology.cities.terrain.HeightMapAdapter;

/**
 * Heavily inspired by sample code from the book 
 * <p>
 * "Digital Image Processing - An Algorithmic Introduction using Java" 
 * by Wilhelm Burger and Mark J. Burge
 * </p>
 * <pre>
 * https://github.com/biometrics/imagingbook
 * </pre>
 * @author Martin Steiger
 */
public class ContourTracer {
    private static final byte FOREGROUND = 1;
    private static final byte BACKGROUND = 0;

    private List<Contour> contours;

    private final HeightMap dataMap;
    private final int width;
    private final int height;
    private final int offY;
    private final int offX;

    /**
     * @param orgHm the original height map to use
     * @param rc the scanning area
     * @param threshold the sea level threshold
     */
    public ContourTracer(final HeightMap orgHm, final Rectangle rc, final int threshold) {

        this.width = rc.width;
        this.height = rc.height;
        this.offX = rc.x;
        this.offY = rc.y;
        
        this.dataMap = new HeightMapAdapter() {

            @Override
            public int apply(int x, int z) {

                if (orgHm.apply(x, z) > threshold) {
                    return BACKGROUND;
                } else {
                    return FOREGROUND;
                }
            }
        };
    }

    /**
     * @return a list of contours
     */
    public List<Contour> getContours() {
        if (contours == null) {
            contours = new ArrayList<>();
            findAllContours();
        }
        
        return contours;
    }

    // Trace one contour starting at (xS,yS)
    // in direction dS with label label
    // trace one contour starting at (xS,yS) in direction dS
    private Contour traceContour(int xS, int yS, int dS) {
        Contour cont = new Contour();

        int xT; // T = successor of starting point (xS,yS)
        int yT;
        
        int xP; // P = previous contour point
        int yP;
        
        int xC; // C = current contour point
        int yC;
        
        Point pt = new Point(xS, yS);
        int dNext = findNextPoint(pt, dS);
        cont.addPoint(pt);
        
        xP = xS;
        yP = yS;
        xT = pt.x;
        yT = pt.y;
        xC = pt.x;
        yC = pt.y;

        boolean done = (xS == xT && yS == yT); // true if isolated pixel

        while (!done) {
            pt = new Point(xC, yC);
            int dSearch = (dNext + 6) % 8;
            dNext = findNextPoint(pt, dSearch);
            xP = xC;
            yP = yC;
            xC = pt.x;
            yC = pt.y;
            // are we back at the starting position?
            done = (xP == xS && yP == yS && xC == xT && yC == yT);
            if (!done) {
                cont.addPoint(pt);
                if (cont.getPoints().size() > 200) {
                    done = true;
                    System.out.println("ABORTING");
                }
            }
        }
        return cont;
    }

    /** 
     * @param pt the start point (<b>modified during op</b>)
     * @param startDir the start search direction
     * @return the final tracing direction
     */
    private int findNextPoint(Point pt, int startDir) {

        final int[][] delta = {
            {+1, 0}, {+1, +1}, {0, +1}, {-1, +1}, 
            {-1, 0}, {-1, -1}, {0, -1}, {+1, -1}};

        int dir = startDir;
        
        for (int i = 0; i < 7; i++) {
            int x = pt.x + delta[dir][0];
            int y = pt.y + delta[dir][1];
            if (dataMap.apply(x, y) == BACKGROUND) {
                dir = (dir + 1) % 8;
            } else { // found non-background pixel
                pt.x = x;
                pt.y = y;
                break;
            }
        }
        return dir;
    }
    
    /** 
     * @param px the point x of interest
     * @param py the point y of interest
     * @return true if inside
     */
    private boolean isInside(int px, int py) {
        return dataMap.apply(px - 1, py) != BACKGROUND;
    }

    private void findAllContours() {
        
        // scan top to bottom, left to right
        for (int y = offY; y < offY + height; y++) {
            for (int x = offX; x < offX + width; x++) {

                Point pt = new Point(x, y);

                if (dataMap.apply(x, y) == FOREGROUND) {
                    if (!alreadyFound(pt) && !isInside(x, y)) {
                        System.out.println("CONTOUR " + x + " / " + y);
                        Contour oc = traceContour(x, y, 0);
                        contours.add(oc);
                    }

                } else { // BACKGROUND pixel
                    if (x > offX && dataMap.apply(x - 1, y) == FOREGROUND && !alreadyFound(new Point(x - 1, y))) {
                        Contour ic = traceContour(x - 1, y, 0);
                        contours.add(ic);
                    }
                }
            }
        }
    }

    private boolean alreadyFound(Point pt) {
        for (Contour c : contours) {
            if (c.getPoints().contains(pt)) {
                return true;
            }
        }
        
        return false;
    }
}
