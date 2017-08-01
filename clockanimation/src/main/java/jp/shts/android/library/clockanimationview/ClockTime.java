package jp.shts.android.library.clockanimationview;

class ClockTime {
    int hours, minutes;

    ClockTime(int hours, int minutes) {
        this.hours = hours;
        this.minutes = minutes;
    }

    int toMinutes() {
        return hours * 60 + minutes;
    }
}