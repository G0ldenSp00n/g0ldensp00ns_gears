package com.g0ldensp00n.gears.states;

import java.util.UUID;

import javax.annotation.Nullable;

import com.g0ldensp00n.gears.GearsPlugin;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;

public class ShaftBlock implements Component<ChunkStore> {
  public static int VERISON = 1;

  public static final BuilderCodec<ShaftBlock> CODEC = BuilderCodec.builder(ShaftBlock.class, ShaftBlock::new)
      .versioned().codecVersion(VERISON)
      .append(
          new KeyedCodec<>("Rotation Speed", Codec.INTEGER),
          (shaftBlock, rotationSpeed) -> shaftBlock.rotationSpeed = rotationSpeed,
          shaftBlock -> shaftBlock.rotationSpeed == 0 ? null : shaftBlock.rotationSpeed)
      .add()
      .append(new KeyedCodec<>("RotationNetworkID", Codec.UUID_BINARY),
          (state, rotationNetworkID) -> state.rotationNetworkID = rotationNetworkID,
          state -> state.rotationNetworkID)
      .add()
      .build();

  protected int rotationSpeed;
  @Nullable
  protected UUID rotationNetworkID;

  public ShaftBlock() {
  }

  public ShaftBlock(
      UUID rotationNetworkId,
      int speed) {
    this.rotationNetworkID = rotationNetworkId;
    this.rotationSpeed = speed;
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

  public static ComponentType<ChunkStore, ShaftBlock> getComponentType() {
    return GearsPlugin.get().getShaftBlockComponentType();
  }

  @Override
  public String toString() {
    String rotationId = "null";
    if (this.rotationNetworkID != null) {
      rotationId = this.rotationNetworkID.toString();
    }
    return "ShaftBlock{rotationNetworkID="
        + rotationId
        + ", rotationSpeed="
        + this.rotationSpeed
        + "}";
  }

  @Nullable
  @Override
  public Component<ChunkStore> clone() {
    return new ShaftBlock(this.rotationNetworkID, this.rotationSpeed);
  }
}
