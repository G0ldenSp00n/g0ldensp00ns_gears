package com.g0ldensp00n.gears.states;

import javax.annotation.Nullable;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;

public class CreativeMotorBlock implements Component<ChunkStore> {
  public CreativeMotorBlock() {
  }

  public CreativeMotorBlock(
      int speed) {
    this.rotationSpeed = speed;
  }

  public static final BuilderCodec<CreativeMotorBlock> CODEC = BuilderCodec
      .builder(CreativeMotorBlock.class, CreativeMotorBlock::new)
      .append(
          new KeyedCodec<>("Rotation Speed", Codec.INTEGER),
          (shaftBlock, rotationSpeed) -> shaftBlock.rotationSpeed = rotationSpeed,
          shaftBlock -> shaftBlock.rotationSpeed == 0 ? null : shaftBlock.rotationSpeed)
      .add()
      .build();
  private int rotationSpeed = 128;

  @Nullable
  @Override
  public Component<ChunkStore> clone() {
    return new CreativeMotorBlock(this.rotationSpeed);
  }
}
