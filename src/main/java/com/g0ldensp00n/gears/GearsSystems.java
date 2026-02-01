package com.g0ldensp00n.gears;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

import javax.annotation.Nullable;

import com.g0ldensp00n.gears.entities.RotationNetworkEntity;
import com.g0ldensp00n.gears.states.GearBlock;
import com.g0ldensp00n.gears.states.ShaftBlock;
import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.Archetype;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.RemoveReason;
import com.hypixel.hytale.component.ResourceType;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.spatial.SpatialResource;
import com.hypixel.hytale.component.system.RefSystem;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.asset.type.model.config.Model;
import com.hypixel.hytale.server.core.asset.type.model.config.ModelAsset;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.modules.block.BlockModule;
import com.hypixel.hytale.server.core.modules.block.BlockModule.BlockStateInfo;
import com.hypixel.hytale.server.core.modules.entity.EntityModule;
import com.hypixel.hytale.server.core.modules.entity.component.ActiveAnimationComponent;
import com.hypixel.hytale.server.core.modules.entity.component.ModelComponent;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.EntityChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.section.BlockSection;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.spawning.world.WorldEnvironmentSpawnData;
import com.hypixel.hytale.server.spawning.world.WorldNPCSpawnStat;
import com.nimbusds.jose.util.Pair;

public class GearsSystems {
  public GearsSystems() {
  }

  // public static class OnGearBlockEntityAdded extends HolderSystem<EntityStore>
  // {
  // private static final Query<EntityStore> QUERY = Archetype.of(ModelComponent);
  //
  // @Nullable
  // @Override
  // public Query<EntityStore> getQuery() {
  // return QUERY;
  // }
  //
  // @Override
  // public void onEntityAdd(Holder<EntityStore> arg0, AddReason arg1,
  // Store<EntityStore> arg2) {
  // Model model = arg0.getComponent(ModelComponent.getComponentType());
  // }
  //
  // @Override
  // public void onEntityRemoved(Holder<EntityStore> arg0, RemoveReason arg1,
  // Store<EntityStore> arg2) {
  // }
  //
  // }
  //
  public static class OnGearBlockAdded extends RefSystem<ChunkStore> {
    private static final Query<ChunkStore> QUERY = Query.and(BlockModule.BlockStateInfo.getComponentType(),
        Query.or(GearBlock.getComponentType(), ShaftBlock.getComponentType()));

    @Override
    public void onEntityAdded(Ref<ChunkStore> entityRef, AddReason arg1, Store<ChunkStore> chunkStore,
        CommandBuffer<ChunkStore> commandBuffer) {
      if (arg1 == AddReason.LOAD) {
        return;
      }
      GearsPlugin.get().getLogger().at(Level.INFO).log("Rotation Block Placed");

      BlockStateInfo info = chunkStore.getComponent(entityRef, BlockStateInfo.getComponentType());
      int index = info.getIndex();
      Ref<ChunkStore> chunkRef = info.getChunkRef();
      WorldChunk chunk = chunkStore.getComponent(chunkRef, WorldChunk.getComponentType());
      if (chunk == null) {

        GearsPlugin.get().getLogger().at(Level.WARNING).log("CHUNK NULl?!?");
        return;
      }
      int x = (chunk.getX() << 5) | ChunkUtil.xFromBlockInColumn(index);
      int y = ChunkUtil.yFromBlockInColumn(index);
      int z = (chunk.getZ() << 5) | ChunkUtil.zFromBlockInColumn(index);
      Vector3d blockCenter = new Vector3d(x + 0.5, y + 0.5, z + 0.5);
      ShaftBlock shaftComponent = commandBuffer.getComponent(entityRef, ShaftBlock.getComponentType());
      if (shaftComponent != null) {
        World world = chunk.getWorld(); // Or chunkStore.getExternalData().getWorld()
        // TODO: Get the Block Roations and Only Search that Way
        Store<EntityStore> store = chunk.getWorld().getEntityStore().getStore();

        UUID firstNetworkID = null;

        int shaftRotationIndex = chunk.getRotationIndex(x, y, z);
        GearsPlugin.get().getLogger().at(Level.INFO).log("Rotation Index - %d", shaftRotationIndex);
        Vector3i connectionPointFront = new Vector3i(x, y, z);
        Vector3i connectionPointBack = new Vector3i(x, y, z);

        if (shaftRotationIndex == 0) {
          connectionPointFront.add(0, 1, 0);
          connectionPointBack.subtract(0, 1, 0);
        } else if (shaftRotationIndex == 5) {
          connectionPointFront.add(1, 0, 0);
          connectionPointBack.subtract(1, 0, 0);
        } else if (shaftRotationIndex == 4) {
          connectionPointFront.add(0, 0, 1);
          connectionPointBack.subtract(0, 0, 1);
        }
        // Ref<ChunkStore> frontConnection = BlockModule.getBlockEntity(world,
        // connectionPointFront.x,

        if (RotationNetworkEntity.canShaftAttach(chunk, connectionPointFront, shaftRotationIndex) &&
            RotationNetworkEntity.canShaftAttach(chunk, connectionPointBack, shaftRotationIndex)
            && RotationNetworkEntity.getShaftRotationNetwork(chunk, connectionPointFront)
                .getUuid() != RotationNetworkEntity.getShaftRotationNetwork(chunk, connectionPointBack).getUuid()) {
          GearsPlugin.get().getLogger().atInfo().log("MERGE NETWORKS");
        } else if (RotationNetworkEntity.canShaftAttach(chunk, connectionPointFront, shaftRotationIndex)) {
          RotationNetworkEntity rotationNetworkEntity = RotationNetworkEntity.getShaftRotationNetwork(chunk,
              connectionPointBack);
          assert rotationNetworkEntity != null;
          rotationNetworkEntity.addShaft(chunk, new Vector3i(x, y, z));
        } else if (RotationNetworkEntity.canShaftAttach(chunk, connectionPointBack, shaftRotationIndex)) {
          RotationNetworkEntity rotationNetworkEntity = RotationNetworkEntity.getShaftRotationNetwork(chunk,
              connectionPointBack);
          assert rotationNetworkEntity != null;
          rotationNetworkEntity.addShaft(chunk, new Vector3i(x, y, z));
        } else {
          world.execute(() -> {
            Pair<Ref<EntityStore>, RotationNetworkEntity> rotationNetwork = GearsPlugin.get().spawnRotationNetwork(
                store,
                Integer.MIN_VALUE, new Vector3d(x, y, z));
            shaftComponent.setRotationNetworkID(rotationNetwork.getRight().getUuid());
          });
        }

        // if (frontConnection != null) {
        // ShaftBlock frontConnectedShaft =
        // frontConnection.getStore().getComponent(frontConnection,
        // ShaftBlock.getComponentType());
        // if (frontConnectedShaft != null) {
        // int frontConnectionRotationIndex =
        // chunk.getRotationIndex(connectionPointFront.x, connectionPointFront.y,
        // connectionPointFront.z);
        // if (frontConnectionRotationIndex == rotationIndex) {
        // GearsPlugin.get().getLogger().at(Level.INFO).log("Found Connection");
        // }
        // }
        // }

        // for (int surround_x = 0; surround_x < 3; surround_x++) {
        // for (int surround_y = 0; surround_y < 3; surround_y++) {
        // for (int surround_z = 0; surround_z < 3; surround_z++) {
        // if (surround_x == x && surround_y == y && surround_z == z) {
        // continue;
        // } else {
        // Ref<ChunkStore> surrounding_block = BlockModule.getBlockEntity(world, x +
        // surround_x,
        // y + surround_y, z + surround_z);
        // // TODO: Handle Chunk Edge??
        // if (surrounding_block != null) {
        // ShaftBlock surrounding_shaft =
        // surrounding_block.getStore().getComponent(surrounding_block,
        // ShaftBlock.getComponentType());
        // if (surrounding_shaft != null) {
        // if (firstNetworkID == null) {
        // firstNetworkID = surrounding_shaft.getRotationNetworkID();
        // shaftComponent.setRotationNetworkID(firstNetworkID);
        // if (firstNetworkID != null) {
        // Ref<EntityStore> entity = world.getEntityRef(firstNetworkID);
        // if (entity != null) {
        // RotationNetworkEntity rotationNetworkEntity =
        // entity.getStore().getComponent(entity,
        // RotationNetworkEntity.getComponentType());
        // GearsPlugin.get().getLogger().at(Level.INFO)
        // .log("Found ENTITY " + rotationNetworkEntity.getUuid());
        // }
        // }
        // }
        // surrounding_shaft.setRotationNetworkID(firstNetworkID);
        // GearsPlugin.get().getLogger().at(Level.INFO).log("Found Attached Shaft");
        // }
        // }
        // }
        // }
        // }
        // }
        if (firstNetworkID == null) {
          // world.execute(() -> {
          // Pair<Ref<EntityStore>, RotationNetworkEntity> rotationNetwork =
          // GearsPlugin.get().spawnRotationNetwork(
          // store,
          // Integer.MIN_VALUE, new Vector3d(x, y, z));
          // shaftComponent.setRotationNetworkID(rotationNetwork.getRight().getUuid());
          // });
        }
        ResourceType<EntityStore, SpatialResource<Ref<EntityStore>, EntityStore>> resourceType = EntityModule.get()
            .getEntitySpatialResourceType();
        SpatialResource<Ref<EntityStore>, EntityStore> spatial = world.getEntityStore().getStore()
            .getResource(resourceType);
        // 5. Query the spatial index for the entity at this location
        List<Ref<EntityStore>> results = new ArrayList<>();
        spatial.getSpatialStructure().collect(blockCenter, 0.1, results);
        // 6. Iterate to find the entity with the animation component
        if (results.size() == 0) {
          GearsPlugin.get().getLogger().at(Level.WARNING).log("NO MATCH");
        }
        for (Ref<EntityStore> ref : results) {
          if (ref.isValid()) {
            ActiveAnimationComponent animComp = ref.getStore().getComponent(ref,
                ActiveAnimationComponent.getComponentType());
            if (animComp != null) {
              for (String playing_animations : animComp.getActiveAnimations()) {
                GearsPlugin.get().getLogger().at(Level.INFO).log(playing_animations);
              }
              // animComp.setPlayingAnimation(slot, animation);
              // SUCCESS: You have the component!
              // Example: animComp.setPlayingAnimation(AnimationSlot.MAIN, "animation_name");
              break;
            }
          } else {
            GearsPlugin.get().getLogger().at(Level.WARNING).log("REF INVALID?");
          }
        }
        GearsPlugin.get().getLogger().at(Level.WARNING)
            .log("Shaft with Network ID " + shaftComponent.getRotationNetworkID());
      } else {

        GearsPlugin.get().getLogger().at(Level.WARNING).log("SHAFT COMP NULL?");
      }

      // TODO Auto-generated method stub
      // throw new UnsupportedOperationException("Unimplemented method
      // 'onEntityAdded'");
    }

    @Override
    public void onEntityRemove(Ref<ChunkStore> arg0, RemoveReason arg1, Store<ChunkStore> arg2,
        CommandBuffer<ChunkStore> arg3) {
      // TODO Auto-generated method stub
      // throw new UnsupportedOperationException("Unimplemented method
      // 'onEntityRemove'");
    }

    @Override
    public Query<ChunkStore> getQuery() {
      return QUERY;
    }
  }
}
