package pt.lsts.accl.sys;

public class Sys {

	private int mId;
	private String mName;
	private String mType; //CCU, STATICSENSOR, HUMANSENSOR, MOBILESENSOR, WSN, UUV, USV, UAV, UGV
	private String mAddress;
	private int mPort;
	public long lastMessageReceived;
    
    // This 2 Booleans are used to compute the color of
    // each sys in system list and serve as the actual state
    boolean mConnected;
    boolean mError;
    
    public Sys(String address, int port, String name, int id, String type,
			boolean connected, boolean error) {
		super();
		this.mAddress = address;
		this.mPort = port;
		this.mName = name;
		this.mId = id;
		this.mConnected = connected;
		this.mError = error;
		this.mType = type;
		lastMessageReceived = System.currentTimeMillis();
	}
    
    //------Id
    public int getId() {
		return mId;
	}

	public void setId(int id) {
		this.mId = id;
	}
	
	//------Name
	public String getName() {
		return mName;
	}

	public void setName(String name) {
		this.mName = name;
	}

	//------Type
	public String getType() {
		return mType;
	}
	
	public void setType(String type) {
		this.mType = type;
	}
	
	//------Address
	public String getAddress() {
		return mAddress;
	}

	public void setAddress(String address) {
		this.mAddress = address;
	}
	
	//------Port
	public int getPort() {
		return mPort;
	}

	public void setPort(int port) {
		this.mPort = port;
	}
	
	//------Connected
	public boolean isConnected() {
		return mConnected;
	}

	public void setConnected(boolean mConnected) {
		this.mConnected = mConnected;
	}
	
	//------Error
	public boolean isError() {
		return mError;
	}

	public void setError(boolean error) {
		this.mError = error;
	}

}
