package com.g0ldensp00n.gears.states;

import java.util.UUID;

import javax.annotation.Nullable;

import com.g0ldensp00n.gears.GearsPlugin;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.modules.entity.component.ModelComponent;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;

public class GearBlock implements Component<ChunkStore> {
  public static int VERISON = 1;

  public static final BuilderCodec<GearBlock> CODEC = BuilderCodec.builder(GearBlock.class, GearBlock::new)
      .versioned()
      .codecVersion(VERISON)
      .append(new KeyedCodec<>("RotationSpeed", Codec.INTEGER),
          (state, rotationSpeed) -> state.rotationSpeed = rotationSpeed,
          state -> state.rotationSpeed)
      .add()
      .append(new KeyedCodec<>("RotationNetworkID", Codec.UUID_BINARY),
          (state, rotationNetworkID) -> state.rotationNetworkID = rotationNetworkID,
          state -> state.rotationNetworkID)
      .add()
      .build();

  protected int rotationSpeed;
  protected UUID rotationNetworkID;

  public GearBlock() {
  }

  public GearBlock(UUID rotationNetworkID, int rotationSpeed) {
    this.rotationSpeed = rotationSpeed;
    this.rotationNetworkID = rotationNetworkID;
  }

  public int getRotationSpeed() {
    return this.rotationSpeed;
  }

  public void setRotationSpeed(int rotationSpeed) {
    this.rotationSpeed = rotationSpeed;
  }

  public UUID getRotationNetworkID() {
    return this.rotationNetworkID;
  }

  public void setRotationNetworkID(UUID rotationNetworkID) {
    this.rotationNetworkID = rotationNetworkID;
  }

  public static ComponentType<ChunkStore, GearBlock> getComponentType() {
    return GearsPlugin.get().getGearBlockComponentType();
  }

  @Nullable
  @Override
  public Component<ChunkStore> clone() {
    return new GearBlock(rotationNetworkID, rotationSpeed);
  }

}
