package simulation.population;

public enum AgeGroup {
    _0_TO_4(0, 4),
    _5_TO_9(5, 9),
    _10_TO_14(10, 14),
    _15_TO_19(15, 19),
    _20_TO_24(20, 24),
    _25_TO_29(25, 29),
    _30_TO_34(30, 34),
    _35_TO_39(35, 39),
    _40_TO_44(40, 44),
    _45_TO_49(45, 49),
    _50_TO_54(50, 54),
    _55_TO_59(55, 59),
    _60_TO_64(60, 64),
    _65_TO_69(65, 69),
    _70_TO_74(70, 74),
    _75_TO_79(75, 79),
    _80_TO_84(80, 84),
    _85_TO_89(85, 89),
    _90_TO_94(90, 94),
    _95_TO_99(95, 99),
    _100_PLUS(100, 105);

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
}
