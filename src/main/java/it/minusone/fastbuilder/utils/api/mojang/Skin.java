package it.minusone.fastbuilder.utils.api.mojang;

public class Skin {
    private String value;
    private String signature;

    public Skin(String value, String signature) {
        this.value = value;
        this.signature = signature;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public ProfileTexture getTexture() {
        return new ProfileTexture(value);
    }
}
