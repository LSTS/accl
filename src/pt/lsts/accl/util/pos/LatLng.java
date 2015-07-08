package pt.lsts.accl.util.pos;


public class LatLng{

	private double lat;
	private double lon;


	public LatLng(double lat, double lon){
		this.lat=lat;
		this.lon=lon;
	}

	public LatLng(LatLng latLng){
		this.lat = latLng.getLat();
		this.lon = latLng.getLon();
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLon() {
		return lon;
	}

	public void setLon(double lon) {
		this.lon = lon;
	}
}