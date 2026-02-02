package com.g0ldensp00n.gears;

import javax.annotation.Nullable;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.function.FunctionCodec;

public sealed interface StressCapacity permits Stress, Capacity {
  public static final FunctionCodec<String, StressCapacity> STRESS_CAPACITY = new FunctionCodec<>(Codec.STRING,
      StressCapacity::fromString, StressCapacity::toString);

  public String toString();

  public static StressCapacity fromString(String string) {
    String[] split = string.split("@");
    if (split[0] == "Stress") {
      return new Stress(Integer.parseInt(split[1]));
    } else if (split[1] == "Capacity") {
      return new Capacity(Integer.parseInt(split[1]));
    }
    assert (false);
    return new Stress(0);
  }

}
