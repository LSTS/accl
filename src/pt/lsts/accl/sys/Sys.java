package pt.lsts.accl.sys;

import pt.lsts.imc.Announce;
import pt.lsts.imc.IMCMessage;
import pt.lsts.imc.lsf.ImcData;
import pt.lsts.imc.net.IMCConnection;

public class Sys {

	private int mId;
	private String mName;
	private String mType; // CCU, STATICSENSOR, HUMANSENSOR, MOBILESENSOR, WSN,
							// UUV, USV, UAV, UGV

	public enum SYS_TYPE {
		CCU(0), HUMANSENSOR(1), UUV(2), USV(3), UAV(4), UGV(5), STATICSENSOR(6), MOBILESENSOR(
				7), WSN(8);

		protected long value;

		public long value() {
			return value;
		}

		SYS_TYPE(long value) {
			this.value = value;
		}
	}

	private String mAddress;
	private int mPort;
	public long lastMessageReceived;

	// This 2 Booleans are used to compute the color of
	// each sys in system list and serve as the actual state
	boolean mConnected;
	boolean mError;

	Announce announce = new Announce();
	IMCConnection sys = new IMCConnection(mName);
	protected IMCMessage message;

	public Sys(String address, int port, String name, int id, String type,
			boolean connected, boolean error) {
		super();

		this.mId = id;
		this.mName = name;
		this.mType = type;
		this.mAddress = address;
		this.mPort = port;
		lastMessageReceived = System.currentTimeMillis();
		this.mConnected = connected;
		this.mError = error;
	}

	// ------Id
	public int getId() {
		mId = announce.getSrc();
		return mId;
	}

	public void setId(int id) {
		this.mId = id;
	}

	// ------Name
	public String getName() {
		mName = announce.getSysName().toString();
		return mName;
	}

	public void setName(String name) {
		this.mName = name;
	}

	// ------Type
	public String getType() {
		mType = announce.getSysType().toString();
		return mType;
	}

	public void setType(String type) {
		this.mType = type;
	}

	// ------Address
	public String getAddress() {
		return mAddress;
	}

	public void setAddress(String address) {
		this.mAddress = address;
	}

	// ------Port
	public int getPort() {
		return mPort;
	}

	public void setPort(int port) {
		this.mPort = port;
	}

	// ------Connected
	public boolean isConnected() {
		if (sys.isConnected()) {
			mConnected = true;
		} else {
			mConnected = false;
		}
		return mConnected;
	}

	public void setConnected(boolean mConnected) {
		this.mConnected = mConnected;
	}

	// ------Error
	public boolean isError() {
		return mError;
	}

	public void setError(boolean error) {
		this.mError = error;
	}

	// ------send IMC Msg To
	public void sendIMCMsgTo(IMCMessage msg) {
		sys.send(msg);
	}

	// ------get Last IMC Message
	public String getLastIMCMessage() {
		String mLast = sys.recv("EstimatedState", 5000).toString();
		return mLast;
	}

	// ------send HeartBeat
	public void sendHeartBeat(IMCMessage msg) {
		sys.send(msg.setValue("Heartbeat", 150));
	}

	// ------resolve Entity
	public String resolveEntity() {
		String resolve = message.getEntityName();
		return resolve;
	}

	// ------is Vehicle
	public boolean isVehicle() {
		Boolean isV = false;

		if (this.getType() == SYS_TYPE.UUV.toString()) {
			isV = true;
		} else if (this.getType() == SYS_TYPE.USV.toString()) {
			isV = true;
		} else if (this.getType() == SYS_TYPE.UAV.toString()) {
			isV = true;
		} else if (this.getType() == SYS_TYPE.UGV.toString()) {
			isV = true;
		} else {
			isV = false;
		}

		return isV;
	}
	
	// ------get Last Error
	public String getLastError()
	{
		ImcData data = new ImcData();
		String LastError= data.getLast(announce.getSysName()).toString();
		return LastError;
	}

}
