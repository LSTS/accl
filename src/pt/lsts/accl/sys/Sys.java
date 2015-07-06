package pt.lsts.accl.sys;

import pt.lsts.accl.util.IMCUtils;
import pt.lsts.imc.Announce;
import pt.lsts.imc.IMCMessage;

/**
 * Created by jloureiro on 02-07-2015.
 */
public class Sys {
    private int ID;
    private String name;
    private Announce.SYS_TYPE sysType;
    private String ipAddress;
    private IMCMessage lastMsgReceived;

    public Sys(Announce announceMsg){
        this(announceMsg.getOwner(), announceMsg.getSysName(), announceMsg.getSysType(), IMCUtils.getAnnounceIMCAddressPort(announceMsg)[0]);
        setLastMsgReceived(announceMsg);
    }

    public Sys(){

    }

    public Sys(int ID, String name, Announce.SYS_TYPE sysType, String ipAddress){
        setID(ID);
        setName(name);
        setSysType(sysType);
        setIpAddress(ipAddress);
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Announce.SYS_TYPE getSysType() {
        return sysType;
    }

    public void setSysType(Announce.SYS_TYPE sysType) {
        this.sysType = sysType;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public long getLastMsgReceivedTime() {
        return getLastMsgReceived().getTimestampMillis();
    }

    public double getLastMsgReceivedAgeInSeconds() {
        return getLastMsgReceived().getAgeInSeconds();
    }

    public IMCMessage getLastMsgReceived() {
        return lastMsgReceived;
    }

    public void setLastMsgReceived(IMCMessage lastMsgReceived) {
        this.lastMsgReceived = lastMsgReceived;
    }

    public boolean isEqualTo(Sys sys){
        if (sys.getName().equals(this.getName())
            && sys.getIpAddress().equals(this.getIpAddress())
            && sys.getID()==this.getID())
            return true;
        return false;
    }




}
