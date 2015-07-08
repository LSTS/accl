package pt.lsts.accl.util.pos;


import pt.lsts.accl.util.pos.LatLng;

import pt.lsts.imc.*;



public class Position {

    private LatLng latLng;
    private double orientation;// psi, angle used for maps representation
    private double height;
    private double z;
    private double depth;
    private double altitude;
    private EulerAngles eulerAngles;//euler angles phi theta psi

    public static Position calcPositionFromEstimatedState(EstimatedState estimatedStateMsg){
        double lat= Math.toDegrees(estimatedStateMsg.getLat());
        double lon= Math.toDegrees(estimatedStateMsg.getLon());
        double xN = estimatedStateMsg.getX();//offset North
        double yE = estimatedStateMsg.getY();//offset East
        LatLng latLngAbsolute = applyOffset(lat, lon, xN, yE);

        double orientation = Math.toDegrees(estimatedStateMsg.getPsi());
        double height = estimatedStateMsg.getHeight();
        double z = estimatedStateMsg.getZ();
        double depth = estimatedStateMsg.getDepth();
        double altitude = estimatedStateMsg.getAlt();

        EulerAngles eulerAngles = new EulerAngles(estimatedStateMsg);

        return new Position(latLngAbsolute,orientation,height,z,depth,altitude,eulerAngles);
    }

    public static LatLng applyOffset(double lat, double lon, double xN, double yE){
        //Earth’s radius, sphere
        double R=6378137;

        //Coordinate offsets in radians
        double dLat = xN/R;
        double dLon = yE/(R*Math.cos(Math.toRadians(lat)));

        //OffsetPosition, decimal degrees
        double newLat = lat + Math.toDegrees(dLat);
        double newLon = lon + Math.toDegrees(dLon);

        LatLng result = new LatLng(newLat,newLon);
        return result;
    }

    public static LatLng applyOffset(LatLng latLng, double xN, double yE){
        double lat = latLng.getLat();
        double lon = latLng.getLon();
        return applyOffset(lat, lon, xN, yE);
    }

    public Position(double lat, double lon, double orientation, double height, double z, double depth, double altitude, double phi, double theta, double psi) {
        this(new LatLng(lat, lon), orientation, height, z, depth, altitude, new EulerAngles(phi, theta, psi));
    }

    public Position(double lat, double lon, double orientation, double height, double z, double depth, double altitude, EulerAngles eulerAngles){
        this(new LatLng(lat,lon), orientation, height, z, depth, altitude, eulerAngles);
    }

    public Position(LatLng latLng, double orientation, double height, double z, double depth, double altitude, double phi, double theta, double psi){
        this(latLng, orientation, height, z, depth, altitude, new EulerAngles(phi,theta,psi));
    }

    public Position(LatLng latLng, double orientation, double height, double z, double depth, double altitude, EulerAngles eulerAngles){
        setLatLng(latLng);
        setOrientation(orientation);
        setHeight(height);
        setZ(z);
        setDepth(depth);
        setAltitude(altitude);
        setEulerAngles(eulerAngles);
    }


    public double getOrientation() {
        return orientation;
    }

    public void setOrientation(double orientation) {
        this.orientation = orientation;
    }

    public double getLatitude(){
        return getLatLng().getLat();
    }

    public double getLongitude(){
        return getLatLng().getLon();
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public double getDepth() {
        return depth;
    }

    public void setDepth(double depth) {
        this.depth = depth;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public EulerAngles getEulerAngles() {
        return eulerAngles;
    }

    public void setEulerAngles(EulerAngles eulerAngles) {
        this.eulerAngles = eulerAngles;
    }

    public void setEulerAngles(pt.lsts.imc.EulerAngles eulerAnglesMsg){
        setEulerAngles(new EulerAngles(eulerAnglesMsg));
    }

    public void setEulerAngles(EstimatedState estimatedStateMsg){
        setEulerAngles(new EulerAngles(estimatedStateMsg));
    }

}
