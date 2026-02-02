package com.g0ldensp00n.gears.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.g0ldensp00n.gears.GearsPlugin;
import com.g0ldensp00n.gears.states.RotationalBlock;
import com.g0ldensp00n.gears.states.ShaftBlock;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.entity.Entity;
import com.hypixel.hytale.server.core.modules.block.BlockModule;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public class RotationNetworkEntity extends Entity {
  public static final BuilderCodec<RotationNetworkEntity> CODEC = BuilderCodec
      .builder(RotationNetworkEntity.class, RotationNetworkEntity::new, Entity.CODEC)
      .append(
          new KeyedCodec<>("Rotation Network ID", Codec.UUID_BINARY),
          (rotationNetworkEntity, rotationNetworkId) -> rotationNetworkEntity.rotationNetworkId = rotationNetworkId,
          rotationNetworkEntity -> rotationNetworkEntity.rotationNetworkId)
      .add()
      .build();

  private UUID rotationNetworkId;

  private Map<RotationalBlock, Float> sources;
  private Map<RotationalBlock, Float> members;

  public RotationNetworkEntity() {
    sources = new HashMap<>();
    members = new HashMap<>();
  }

  public void initialise(@Nonnull UUID id) {
    this.rotationNetworkId = id;
  }

  @Nullable
  public static ShaftBlock getShaft(WorldChunk chunk, Vector3i shaftOrigin) {
    Ref<ChunkStore> connection = BlockModule.getBlockEntity(chunk.getWorld(), shaftOrigin.x,
        shaftOrigin.y, shaftOrigin.z);
    if (connection != null) {
      ShaftBlock connectedShaft = connection.getStore().getComponent(connection,
          ShaftBlock.getComponentType());
      return connectedShaft;
    }
    return null;
  }

  public static boolean canShaftAttach(WorldChunk chunk, Vector3i connectionPoint, int shaftRotationIndex) {
    ShaftBlock shaft = getShaft(chunk, connectionPoint);
    if (shaft != null) {
      int connectionRotationIndex = chunk.getRotationIndex(connectionPoint.x, connectionPoint.y,
          connectionPoint.z);
      if (connectionRotationIndex == shaftRotationIndex) {
        GearsPlugin.get().getLogger().at(Level.INFO).log("Found Connection");
        return true;
      }
    }
    return false;
  }

  @Nullable
  public static RotationNetworkEntity getShaftRotationNetwork(WorldChunk chunk, Vector3i shaftOrigin) {
    ShaftBlock shaft = getShaft(chunk, shaftOrigin);
    if (shaft != null && shaft.getRotationNetworkID() != null) {
      Ref<EntityStore> entity = chunk.getWorld().getEntityRef(shaft.getRotationNetworkID());
      RotationNetworkEntity rotationNetworkEntity = entity.getStore().getComponent(entity,
          RotationNetworkEntity.getComponentType());
      return rotationNetworkEntity;
    }
    return null;
  }

  public void addShaft(WorldChunk chunk, Vector3i shaftOrigin) {
    GearsPlugin.get().getLogger().atInfo().log("ADD TO NETWORK");
    ShaftBlock shaftBlock = getShaft(chunk, shaftOrigin);
    shaftBlock.setRotationNetworkID(this.getUuid());
  }

  @Override
  public RotationNetworkEntity clone() {
    RotationNetworkEntity copy = new RotationNetworkEntity();
    copy.initialise(this.rotationNetworkId);
    return copy;
  }

  @Nullable
  public static ComponentType<EntityStore, RotationNetworkEntity> getComponentType() {
    // return EntityModule.get().getComponentType(RotationNetworkEntity.class);
    return GearsPlugin.get().getRotationNetworkEntityComponentType();
  }
}
