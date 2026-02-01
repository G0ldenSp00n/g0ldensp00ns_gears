package com.g0ldensp00n.gears;

import java.util.logging.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.g0ldensp00n.gears.entities.RotationNetworkEntity;
import com.g0ldensp00n.gears.states.GearBlock;
import com.g0ldensp00n.gears.states.ShaftBlock;
import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.ComponentRegistryProxy;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.Quatf;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.protocol.Vector3i;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.asset.builder.BuilderManager;
import com.nimbusds.jose.util.Pair;

public class GearsPlugin extends JavaPlugin {
  protected final BuilderManager builderManager = new BuilderManager();

  protected static GearsPlugin instance;
  private ComponentType<ChunkStore, GearBlock> gearBlockComponentType;
  private ComponentType<ChunkStore, ShaftBlock> shaftBlockComponentType;
  private ComponentType<EntityStore, RotationNetworkEntity> rotationNetworkComponentType;

  private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

  public GearsPlugin(@Nonnull JavaPluginInit init) {
    super(init);
  }

  public static GearsPlugin get() {
    return instance;
  }

  @Override
  protected void setup() {
    instance = this;

    this.gearBlockComponentType = this.getChunkStoreRegistry().registerComponent(GearBlock.class,
        "Gear", GearBlock.CODEC);
    this.shaftBlockComponentType = this.getChunkStoreRegistry().registerComponent(ShaftBlock.class,
        "Shaft", ShaftBlock.CODEC);

    this.getChunkStoreRegistry().registerSystem(new GearsSystems.OnGearBlockAdded());

    ComponentRegistryProxy<EntityStore> entityStoreRegistry = this.getEntityStoreRegistry();
    this.rotationNetworkComponentType = entityStoreRegistry.registerComponent(RotationNetworkEntity.class,
        "RotationNetwork",
        RotationNetworkEntity.CODEC);
  }

  public ComponentType<ChunkStore, GearBlock> getGearBlockComponentType() {
    return this.gearBlockComponentType;
  }

  public ComponentType<ChunkStore, ShaftBlock> getShaftBlockComponentType() {
    return this.shaftBlockComponentType;
  }

  public ComponentType<EntityStore, RotationNetworkEntity> getRotationNetworkEntityComponentType() {
    return this.rotationNetworkComponentType;
  }

  @Override
  protected void start() {
    LOGGER.atInfo().log("G0ldenSp00n's Gears Started!");
  }

  @Nullable
  public String getName(int builderIndex) {
    return this.builderManager.lookupName(builderIndex);
  }

  @Nullable
  public Pair<Ref<EntityStore>, RotationNetworkEntity> spawnRotationNetwork(@Nonnull Store<EntityStore> store,
      int roleIndex, Vector3d position) {
    RotationNetworkEntity rotationNetworkComponent = new RotationNetworkEntity();
    Holder<EntityStore> holder = EntityStore.REGISTRY.newHolder();
    holder.addComponent(RotationNetworkEntity.getComponentType(), rotationNetworkComponent);
    holder.addComponent(TransformComponent.getComponentType(),
        new TransformComponent(position, new Vector3f(0, 0, 0)));
    holder.ensureComponent(UUIDComponent.getComponentType());
    Ref<EntityStore> ref = store.addEntity(holder, AddReason.SPAWN);
    if (ref == null) {
      GearsPlugin.get().getLogger().at(Level.WARNING).log("Unable to handle non-spawned entity: %s!");
      return null;
    } else {
      return Pair.of(ref, rotationNetworkComponent);
    }
  }
}
