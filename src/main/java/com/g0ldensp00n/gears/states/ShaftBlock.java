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

public class ShaftBlock extends RotationalBlock {
  public static final BuilderCodec<ShaftBlock> CODEC = BuilderCodec
      .builder(ShaftBlock.class, ShaftBlock::new, RotationalBlock.CODEC)
      .versioned().codecVersion(VERISON)
      .build();

  public ShaftBlock() {
  }

  public ShaftBlock(
      UUID rotationNetworkId,
      int speed) {
    super(rotationNetworkId, speed);
  }

  public static ComponentType<ChunkStore, ShaftBlock> getComponentType() {
    return GearsPlugin.get().getShaftBlockComponentType();
  }

  // @Override
  // public String toString() {
  // String rotationId = "null";
  // if (this.rotationNetworkID != null) {
  // rotationId = this.rotationNetworkID.toString();
  // }
  // return "ShaftBlock{rotationNetworkID="
  // + rotationId
  // + ", rotationSpeed="
  // + this.rotationSpeed
  // + "}";
  // }

  @Nullable
  @Override
  public Component<ChunkStore> clone() {
    return new ShaftBlock(this.rotationNetworkID, this.rotationSpeed);
  }
}
