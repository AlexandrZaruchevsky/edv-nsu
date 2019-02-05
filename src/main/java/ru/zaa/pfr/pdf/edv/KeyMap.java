package ru.zaa.pfr.pdf.edv;

public class KeyMap implements Comparable<KeyMap>{

    String npd;
    String snils;

    public KeyMap(String npd, String snils) {
        this.npd = npd;
        this.snils = snils;
    }

    public String getNpd() {
        return npd;
    }

    public void setNpd(String npd) {
        this.npd = npd;
    }

    public String getSnils() {
        return snils;
    }

    public void setSnils(String snils) {
        this.snils = snils;
    }

    @Override
    public boolean equals(Object obj) {
        return this.snils.equals(((KeyMap) obj).snils);
    }

    @Override
    public int hashCode() {
        return snils.hashCode();
    }

    public int compareTo(KeyMap o) {
        return this.npd.compareTo(o.npd);
    }
}
