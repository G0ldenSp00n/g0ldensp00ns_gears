package com.g0ldensp00n.gears;

public final record Stress(
    int value) implements StressCapacity {

  @Override
  public String toString() {
    return "Stress@" + value;
  }
}
