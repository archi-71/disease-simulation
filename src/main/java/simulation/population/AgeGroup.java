package simulation.population;

public enum AgeGroup {
    _0_TO_9(0, 9),
    _10_TO_19(10, 19),
    _20_TO_29(20, 29),
    _30_TO_39(30, 39),
    _40_TO_49(40, 49),
    _50_TO_59(50, 59),
    _60_TO_69(60, 69),
    _70_TO_79(70, 79),
    _80_TO_89(80, 89),
    _90_PLUS(90, 99);

    private int minAge;
    private int maxAge;

    AgeGroup(int minAge, int maxAge) {
        this.minAge = minAge;
        this.maxAge = maxAge;
    }

    public int getMinAge() {
        return minAge;
    }

    public int getMaxAge() {
        return maxAge;
    }

    public static AgeGroup getAgeGroup(int age) {
        return AgeGroup.values()[age / 10];
    }
}
