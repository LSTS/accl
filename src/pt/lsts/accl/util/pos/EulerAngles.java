package pt.lsts.accl.util.pos;

import pt.lsts.imc.EstimatedState;


public class EulerAngles {

    private double phi;
    private double theta;
    private double psi;

    public EulerAngles(pt.lsts.imc.EulerAngles eulerAnglesMsg){
        this(Math.toDegrees(eulerAnglesMsg.getPhi()), Math.toDegrees(eulerAnglesMsg.getTheta()), Math.toDegrees(eulerAnglesMsg.getPsi()));
    }

    public EulerAngles(EstimatedState estimatedStateMsg){
        this(Math.toDegrees(estimatedStateMsg.getPhi()), Math.toDegrees(estimatedStateMsg.getTheta()), Math.toDegrees(estimatedStateMsg.getPsi()));
    }

    public EulerAngles(double phi, double theta, double psi){
        setPhi(phi);
        setTheta(theta);
        setPsi(psi);
    }

    public double getPhi() {
        return phi;
    }

    public void setPhi(double phi) {
        this.phi = phi;
    }

    public double getTheta() {
        return theta;
    }

    public void setTheta(double theta) {
        this.theta = theta;
    }

    public double getPsi() {
        return psi;
    }

    public void setPsi(double psi) {
        this.psi = psi;
    }

}
