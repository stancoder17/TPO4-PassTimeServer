package zad1;

public class PolishLanguage {
    public static String translateDayOfWeekTo(String dayOfWeek) {
        switch (dayOfWeek.toLowerCase()) {
            case "monday": return "poniedziałek";
            case "tuesday": return "wtorek";
            case "wednesday": return "środa";
            case "thursday": return "czwartek";
            case "friday": return "piątek";
            case "saturday": return "sobota";
            case "sunday": return "niedziela";
            default: throw new IllegalArgumentException("Invalid day of week: " + dayOfWeek);
        }
    }

    public static String numberToMonth(int month) {
        switch (month) {
            case 1: return "styczeń";
            case 2: return "luty";
            case 3: return "marzec";
            case 4: return "kwiecień";
            case 5: return "maj";
            case 6: return "czerwiec";
            case 7: return "lipiec";
            case 8: return "sierpień";
            case 9: return "wrzesień";
            case 10: return "październik";
            case 11: return "listopad";
            case 12: return "grudzień";
            default: throw new IllegalArgumentException("Invalid month number: " + month);
        }
    }


    public static String getMonthDeclension(String month) {
        switch (month.toLowerCase()) {
            case "styczeń": return "stycznia";
            case "luty": return "lutego";
            case "marzec": return "marca";
            case "kwiecień": return "kwietnia";
            case "maj": return "maja";
            case "czerwiec": return "czerwca";
            case "lipiec": return "lipca";
            case "sierpień": return "sierpnia";
            case "wrzesień": return "września";
            case "październik": return "października";
            case "listopad": return "listopada";
            case "grudzień": return "grudnia";
            default: throw new IllegalArgumentException("Invalid month: " + month);
        }
    }

    public static String getYearDeclension(int value) {
        int lastDigit = value % 10;
        if (lastDigit == 1 && (value < 11 || value > 20)) return "rok";
        if (lastDigit >= 2 && lastDigit <= 4 && (value < 10 || value >= 20)) return "lata";
        return "lat";
    }

    public static String getMonthDeclension(int value) {
        int lastDigit = value % 10;
        if (lastDigit == 1 && value != 11) return "miesiąc";
        if (lastDigit >= 2 && lastDigit <= 4 && (value < 10 || value >= 20)) return "miesiące";
        return "miesięcy";
    }

    public static String getDayDeclension(int value) {
        if (value == 1) return "dzień";
        return "dni";
    }

}
