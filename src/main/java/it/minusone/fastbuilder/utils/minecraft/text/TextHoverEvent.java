package it.minusone.fastbuilder.utils.minecraft.text;

public class TextHoverEvent {

    final char b;
    private final String s;
    public TextHoverEvent(String s, char b) {
        this.s = s;
        this.b = b;
    }

    public static TextHoverEvent showText(String s) {
        return new TextHoverEvent(s, (char) 7);
    }

    public String getString() {
        return s;
    }
}
