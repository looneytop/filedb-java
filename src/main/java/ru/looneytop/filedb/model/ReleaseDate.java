package ru.looneytop.filedb.model;

public record ReleaseDate(int year, int month, int day) {
    public ReleaseDate(int year, int month, int day) {
        if (year < 1 || year > 9999) {
            throw new IllegalArgumentException("Год должен быть от 1 до 9999");
        }
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Месяц должен быть от 1 до 12");
        }
        if (!checkDay(year, month, day)) {
            throw new IllegalArgumentException("День указан неверный");
        }

        this.year = year;
        this.month = month;
        this.day = day;
    }

    private static boolean checkDay(int year, int month, int day) {
        if (day < 1) return false;

        if (month == 4 || month == 6 || month == 9 || month == 11) {
            if (day > 30) return false;
        } else if (month == 2) {
            if ((year % 400 == 0) || (year % 4 == 0 && year % 100 != 0)) {
                return day <= 29;
            }

            return day <= 28;
        }

        return day <= 31;
    }

/*
    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }
*/
}

