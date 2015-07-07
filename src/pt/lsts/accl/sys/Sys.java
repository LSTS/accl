package pt.lsts.accl.sys;

import pt.lsts.accl.util.IMCUtils;
import pt.lsts.imc.Announce;
import pt.lsts.imc.IMCMessage;

/**
 * Class that represents the generic IMC System.
 * Specific systems can extend this class for better and more specific fields and methods.
 *
 * Created by jloureiro on 02-07-2015.
 */
public class Sys {
    private int ID;
    private String name;
    private Announce.SYS_TYPE sysType;
    private String ipAddress;
    private IMCMessage lastMsgReceived;

    /**
     *  Build a sys from its Announce Message
     * @param announceMsg the Announce of the system
     */
    public Sys(Announce announceMsg){
        this(announceMsg.getSrc(), announceMsg.getSysName(), announceMsg.getSysType(), IMCUtils.getAnnounceIMCAddressPort(announceMsg)[0]);
        setLastMsgReceived(announceMsg);
    }

    /**
     * Necessary empty construtor to extend class
     */
    public Sys(){

    }

    /**
     * Generic Construtor specifying each field
     * @param ID System ID
     * @param name System Name to be displayed. Also used in {@link pt.lsts.imc.net.IMCProtocol#sendMessage(String, IMCMessage)}
     * @param sysType The type of sys, can be used to cast to subclasses of this class
     * @param ipAddress The IP Address used to communicate with system
     */
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

    /**
     * Used {@link IMCMessage#getAgeInSeconds()}
     * @return seconds since last message received
     */
    public double getLastMsgReceivedAgeInSeconds() {
        return (System.currentTimeMillis()-getLastMsgReceived().getTimestampMillis())/1000;
    }

    public IMCMessage getLastMsgReceived() {
        return lastMsgReceived;
    }

    public void setLastMsgReceived(IMCMessage lastMsgReceived) {
        this.lastMsgReceived = lastMsgReceived;
    }

    /**
     * Compare this system with another. compare using ID
     * @param sys the system to compare with
     * @return true if same system, false otherwise
     */
    public boolean equals(Sys sys){
        if (sys.getID()==this.getID())
            return true;
        return false;
    }




}
