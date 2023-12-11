
package example.biz;

public enum CondType {
  EQ(0),
  NE(1),
  BT(2),
  LT(3),
  BE(4),
  LE(5),
  LIKE(6),
  IS_NULL(7),
  NOT_NULL(8);

  private final int value;

  private CondType(int value) {
    this.value = value;
  }

  /**
   * Get the integer value of this enum value, as defined in the Thrift IDL.
   */
  public int getValue() {
    return value;
  }

  /**
   * Find a the enum type by its integer value, as defined in the Thrift IDL.
   * @return null if the value is not found.
   */
  public static CondType findByValue(int value) { 
    switch (value) {
      case 0:
        return EQ;
      case 1:
        return NE;
      case 2:
        return BT;
      case 3:
        return LT;
      case 4:
        return BE;
      case 5:
        return LE;
      case 6:
        return LIKE;
      case 7:
        return IS_NULL;
      case 8:
        return NOT_NULL;
      default:
        return null;
    }
  }
}
