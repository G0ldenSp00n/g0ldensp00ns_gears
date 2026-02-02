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

public class GearBlock extends RotationalBlock {
  public static final BuilderCodec<GearBlock> CODEC = BuilderCodec
      .builder(GearBlock.class, GearBlock::new, RotationalBlock.CODEC)
      .build();

  public GearBlock() {
  }

  public GearBlock(UUID rotationNetworkID, int rotationSpeed) {
    super(rotationNetworkID, rotationSpeed);
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
