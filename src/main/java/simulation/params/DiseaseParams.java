package simulation.params;

public class DiseaseParams {
    private int initialInfected = 100;
    private float transmissionRate = 0.2f;
    private float recoveryRate = 0.005f;
    private float mortalityRate = 0.001f;

    public void setInitialInfected(int initialInfected) {
        this.initialInfected = initialInfected;
    }

    public void setTransmissionRate(float transmissionRate) {
        this.transmissionRate = transmissionRate;
    }

    public void setRecoveryRate(float recoveryRate) {
        this.recoveryRate = recoveryRate;
    }

    public void setMortalityRate(float mortalityRate) {
        this.mortalityRate = mortalityRate;
    }

    public int getInitialInfected() {
        return initialInfected;
    }

    public float getTransmissionRate() {
        return transmissionRate;
    }

    public float getRecoveryRate() {
        return recoveryRate;
    }

    public float getMortalityRate() {
        return mortalityRate;
    }

    public DiseaseParams() {
    };

    public DiseaseParams(DiseaseParams params) {
        initialInfected = params.initialInfected;
        transmissionRate = params.transmissionRate;
        recoveryRate = params.recoveryRate;
        mortalityRate = params.mortalityRate;
    }
}
