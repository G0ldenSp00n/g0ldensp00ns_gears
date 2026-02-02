package com.g0ldensp00n.gears.states;

import java.util.UUID;

import javax.annotation.Nullable;

import com.g0ldensp00n.gears.Stress;
import com.g0ldensp00n.gears.StressCapacity;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;

public abstract class RotationalBlock implements Component<ChunkStore> {
  public static int VERISON = 1;

  public static final BuilderCodec<RotationalBlock> CODEC = BuilderCodec
      .abstractBuilder(RotationalBlock.class)
      .versioned().codecVersion(VERISON)
      .append(new KeyedCodec<>("RotationNetworkID", Codec.UUID_BINARY),
          (state, rotationNetworkID) -> state.rotationNetworkID = rotationNetworkID,
          state -> state.rotationNetworkID)
      .add()
      .append(
          new KeyedCodec<>("RotationSpeed", Codec.INTEGER),
          (shaftBlock, rotationSpeed) -> shaftBlock.rotationSpeed = rotationSpeed,
          shaftBlock -> shaftBlock.rotationSpeed)
      .add()
      .append(
          new KeyedCodec<>("RotationStressCapacity", StressCapacity.STRESS_CAPACITY),
          (shaftBlock, rotationStressCapacity) -> {
            shaftBlock.rotationStressCapacity = rotationStressCapacity;
          },
          shaftBlock -> shaftBlock.rotationStressCapacity)
      .add()
      .append(new KeyedCodec<>("IsOverstressed", Codec.BOOLEAN),
          (state, isOverstressed) -> state.isOverstressed = isOverstressed,
          state -> state.isOverstressed)
      .add()
      .build();

  @Nullable
  protected UUID rotationNetworkID;
  protected int rotationSpeed = 0;
  protected StressCapacity rotationStressCapacity = new Stress(0);
  protected boolean isOverstressed = false;

  public RotationalBlock() {
  }

  public RotationalBlock(
      UUID rotationNetworkId,
      int speed, StressCapacity stressCapacity) {
    this.rotationNetworkID = rotationNetworkId;
    this.rotationSpeed = speed;
    this.rotationStressCapacity = stressCapacity;
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

  @Override
  public String toString() {
    String rotationId = "null";
    if (this.rotationNetworkID != null) {
      rotationId = this.rotationNetworkID.toString();
    }
    String rotationStressCapacity = "null";
    if (this.rotationStressCapacity != null) {
      rotationStressCapacity = this.rotationStressCapacity.toString();
    }
    String className = this.getClass().getCanonicalName();
    return className + "{rotationNetworkID="
        + rotationId
        + ", rotationSpeed="
        + this.rotationSpeed
        + ", rotationStressCapacity="
        + rotationStressCapacity
        + "}";
  }

  public abstract Component<ChunkStore> clone();

  // TODO: Figure this Out?
  // @Nullable
  // @Override
  // public Component<ChunkStore> clone() {
  // DirectDecodeCodec<RotationalBlock> codec =
  // GearsPlugin.get().getCodec((Class<RotationalBlock>) this.getClass());
  // Function<World, Entity> constructor =
  // EntityModule.get().getConstructor((Class<RotationalBlock>) this.getClass());
  // BsonDocument document = codec.encode(this,
  // ExtraInfo.THREAD_LOCAL.get()).asDocument();
  // document.put("EntityType",
  // new BsonString(EntityModule.get().getIdentifier((Class<? extends Entity>)
  // this.getClass())));
  // Entity t = constructor.apply(null);
  // codec.decode(document, t, ExtraInfo.THREAD_LOCAL.get());
  // return t;
  // }

}
