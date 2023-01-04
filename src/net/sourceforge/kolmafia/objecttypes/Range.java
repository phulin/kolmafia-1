package net.sourceforge.kolmafia.objecttypes;

public class Range {
  public int min;
  public int max;

  public Range(int min, int max) {
    this.min = min;
    this.max = max;
  }

  public double average() {
    return ((double) this.min + this.max) / 2;
  }
}
