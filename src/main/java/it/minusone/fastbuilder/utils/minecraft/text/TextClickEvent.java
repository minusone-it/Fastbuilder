package it.minusone.fastbuilder.utils.minecraft.text;

public class TextClickEvent {

    final char b;
    private final String s;

    public TextClickEvent(String s, char b) {
        this.s = s;
        this.b = b;
    }

    public static TextClickEvent openURL(String s) {
        return new TextClickEvent(s, (char) 4);
    }

    public static TextClickEvent runCommand(String s) {
        return new TextClickEvent(s, (char) 5);
    }

    public static TextClickEvent suggestCommand(String s) {
        return new TextClickEvent(s, (char) 6);
    }

    public String getString() {
        return s;
    }
}
