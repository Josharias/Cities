/*
 * Copyright 2013 MovingBlocks
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

package org.terasology.cities;

import java.util.Arrays;
import java.util.List;

import org.terasology.cities.terrain.NoiseHeightMap;
import org.terasology.core.world.generator.AbstractBaseWorldGenerator;
import org.terasology.engine.SimpleUri;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.registry.CoreRegistry;
import org.terasology.rendering.nui.databinding.BindHelper;
import org.terasology.rendering.nui.databinding.Binding;
import org.terasology.rendering.nui.databinding.DefaultBinding;
import org.terasology.world.WorldComponent;
import org.terasology.world.generator.RegisterWorldGenerator;
import org.terasology.world.generator.WorldConfigurator;
import org.terasology.world.generator.params.BooleanParameter;
import org.terasology.world.generator.params.FloatParameter;
import org.terasology.world.generator.params.IntParameter;
import org.terasology.world.generator.params.Parameter;
import org.terasology.world.generator.params.StringParameter;

import com.google.common.base.Optional;

/**
 * @author Martin Steiger
 */
@RegisterWorldGenerator(id = "city", displayName = "City World")
public class CityWorldGenerator extends AbstractBaseWorldGenerator {

    private NoiseHeightMap heightMap;

    /**
     * @param uri the uri
     */
    public CityWorldGenerator(SimpleUri uri) {
        super(uri);
    }

    @Override
    public void initialize() {

        // TODO: this should come from elsewhere
        CityWorldConfig config = new CityWorldConfig();
        
        
        heightMap = new NoiseHeightMap();
        
        register(new HeightMapTerrainGenerator(heightMap, config));
//        register(new BoundaryGenerator(heightMap));
        register(new CityTerrainGenerator(heightMap, config));
        register(new FloraGeneratorFast(heightMap));
    }
    
    @Override
    public void setWorldSeed(String seed) {
        if (seed == null) {
            return;
        }
        
        if (heightMap == null) {
            heightMap = new NoiseHeightMap();
        }

        EntityRef worldEntity = getWorldEntity();
        
        CityConfigComponent configComp = worldEntity.getComponent(CityConfigComponent.class);
        
        if (configComp == null) {
            configComp = new CityConfigComponent();
            configComp.config = "asdsabcvfn7zjh6wser";
            worldEntity.addComponent(configComp);
        }
        
        heightMap.setSeed(seed);
        
        super.setWorldSeed(seed);
    }
    
    private EntityRef getWorldEntity() {
        EntityManager entityManager = CoreRegistry.get(EntityManager.class);

        for (EntityRef entity : entityManager.getEntitiesWith(WorldComponent.class)) {
            return entity;
        }
        return EntityRef.NULL;
    }

    @Override
    public Optional<WorldConfigurator> getConfigurator() {

        WorldConfigurator wc = new WorldConfigurator() {

            @Override
            public List<Parameter> getParams() {
                final CityWorldConfig config = new CityWorldConfig();

                Binding<Integer> minBinding = new Binding<Integer>() {

                    @Override
                    public Integer get() {
                        return config.getMinCitiesPerSector();
                    }

                    @Override
                    public void set(Integer value) {
                        config.setMinCitiesPerSector(value);
                    }
                };

                Parameter p1 = new IntParameter("Min. Cities", minBinding, 0, 5);
                Parameter p2 = new IntParameter("Max. Cities", new DefaultBinding<Integer>(4), 0, 5);
                Parameter p3 = new IntParameter("Min. Size", new DefaultBinding<>(50), 0, 500);
                Parameter p4 = new IntParameter("Max. Size", new DefaultBinding<>(300), 0, 500);
                Parameter p7 = new FloatParameter("Quality", new DefaultBinding<>(0.5f), 0f, 1f, 0.1f);
                Parameter p5 = new BooleanParameter("Symmetric", new DefaultBinding<>(Boolean.TRUE));
                Parameter p6 = new StringParameter("Theme", new DefaultBinding<>("Dwarf"));
                Parameter p8 = new IntParameter("Theme22", BindHelper.bindBeanProperty("minCitiesPerSector", config, int.class), 0, 12);

                CityConfigComponent configComp = new CityConfigComponent();
                configComp.config = "sdf";
                configComp.intConfig = 21;
                
                List<Parameter> list = Arrays.asList(p1, p2, p3, p4, p7, p5, p6, p7, p8);
                
                return list;
            }

        };

        return Optional.of(wc);
    }

}
