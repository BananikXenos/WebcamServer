package xyz.synse.datacenter.utils.utilities;

public class Orientation {
    private float azimuth, pitch, roll;

    public Orientation(float azimuth, float pitch, float roll) {
        this.azimuth = azimuth;
        this.pitch = pitch;
        this.roll = roll;
    }

    public Orientation() {
        this.azimuth = 0;
        this.pitch = 0;
        this.roll = 0;
    }

    public float getAzimuth() {
        return azimuth;
    }

    public void setAzimuth(float azimuth) {
        this.azimuth = azimuth;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public float getRoll() {
        return roll;
    }

    public void setRoll(float roll) {
        this.roll = roll;
    }

    public void set(float azimuth, float pitch, float roll) {
        this.azimuth = azimuth;
        this.pitch = pitch;
        this.roll = roll;
    }

    @Override
    public String toString() {
        return "azimuth=" + azimuth +
                ", pitch=" + pitch +
                ", roll=" + roll;
    }

    public void set(Orientation orientation) {
        this.azimuth = orientation.azimuth;
        this.pitch = orientation.pitch;
        this.roll = orientation.roll;
    }
}
