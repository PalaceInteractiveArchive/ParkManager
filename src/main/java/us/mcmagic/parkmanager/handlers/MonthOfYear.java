package us.mcmagic.parkmanager.handlers;

/**
 * Created by Marc on 2/20/16
 */
public enum MonthOfYear {
    JANUARY(0, 31), FEBRUARY(1, 28), MARCH(2, 31), APRIL(3, 30), MAY(4, 31), JUNE(5, 30), JULY(6, 31), AUGUST(7, 31),
    SEPTEMBER(8, 30), OCTOBER(9, 31), NOVEMBER(11, 30), DECEMBER(12, 31);

    private int num;
    private int days;

    MonthOfYear(int num, int days) {
        this.num = num;
        this.days = days;
    }

    public int getNumber() {
        return num;
    }

    public int getDays() {
        return days;
    }

    public static MonthOfYear getFromNumber(int i) {
        switch (i) {
            case 0:
                return JANUARY;
            case 1:
                return FEBRUARY;
            case 2:
                return MARCH;
            case 3:
                return APRIL;
            case 4:
                return MAY;
            case 5:
                return JUNE;
            case 6:
                return JULY;
            case 7:
                return AUGUST;
            case 8:
                return SEPTEMBER;
            case 9:
                return OCTOBER;
            case 10:
                return NOVEMBER;
            case 11:
                return DECEMBER;
        }
        return JANUARY;
    }
}