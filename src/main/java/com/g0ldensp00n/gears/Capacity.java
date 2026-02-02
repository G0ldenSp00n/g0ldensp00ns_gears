package com.g0ldensp00n.gears;

public record Capacity(int value) implements StressCapacity {
  public String toString() {
    return "Capacity@" + value;
  }
}
