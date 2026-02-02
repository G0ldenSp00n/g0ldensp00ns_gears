package com.g0ldensp00n.gears.states;

import java.util.UUID;
import java.util.function.Function;

import javax.annotation.Nullable;

import com.g0ldensp00n.gears.GearsPlugin;
import com.g0ldensp00n.gears.StressCapacity;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.DirectDecodeCodec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;

public abstract class RotationalBlock implements Component<ChunkStore> {
  public static int VERISON = 1;

  public static final BuilderCodec<RotationalBlock> CODEC = BuilderCodec
      .abstractBuilder(RotationalBlock.class)
      .versioned().codecVersion(VERISON)
      .append(
          new KeyedCodec<>("Rotation Speed", Codec.INTEGER),
          (shaftBlock, rotationSpeed) -> shaftBlock.rotationSpeed = rotationSpeed,
          shaftBlock -> shaftBlock.rotationSpeed == 0 ? null : shaftBlock.rotationSpeed)
      .add()
      .append(
          new KeyedCodec<>("Rotation Stress Capacity", StressCapacity.STRESS_CAPACITY_STRING),
          (shaftBlock, rotationStressCapacity) -> {
            shaftBlock.rotationStressCapacity = rotationStressCapacity;
          },
          shaftBlock -> shaftBlock.rotationStressCapacity)
      .add()

      .append(new KeyedCodec<>("RotationNetworkID", Codec.UUID_BINARY),
          (state, rotationNetworkID) -> state.rotationNetworkID = rotationNetworkID,
          state -> state.rotationNetworkID)
      .add()
      .build();

  @Nullable
  protected UUID rotationNetworkID;
  protected int rotationSpeed;
  protected StressCapacity rotationStressCapacity;

  public RotationalBlock() {
  }

  public RotationalBlock(
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

  @Override
  public String toString() {
    String rotationId = "null";
    if (this.rotationNetworkID != null) {
      rotationId = this.rotationNetworkID.toString();
    }
    String className = this.getClass().getCanonicalName();
    return className + "{rotationNetworkID="
        + rotationId
        + ", rotationSpeed="
        + this.rotationSpeed
        + ", rotationStressCapacity="
        + this.rotationStressCapacity.toString()
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
