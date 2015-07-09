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

    /**
     * Get the Source of this system ID
     * @return The Source ID
     */
    public int getID() {
        return ID;
    }

    /**
     * Set the Source of this system ID
     * @param ID The Source ID
     */
    public void setID(int ID) {
        this.ID = ID;
    }

    /**
     * Get the display name of this System
     * @return The Name of this system
     */
    public String getName() {
        return name;
    }

    /**
     * Set the display name of this System
     * @param name The new Name for this system
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Set System Type:
     * CCU(0),
     * HUMANSENSOR(1),
     * UUV(2),
     * USV(3),
     * UAV(4),
     * UGV(5),
     * STATICSENSOR(6),
     * MOBILESENSOR(7),
     * WSN(8);
     * @return The Announce.SYS_TYPE of this System
     * @see pt.lsts.imc.Announce.SYS_TYPE
     */
    public Announce.SYS_TYPE getSysType() {
        return sysType;
    }

    /**
     * Set System Type:
     * CCU(0),
     * HUMANSENSOR(1),
     * UUV(2),
     * USV(3),
     * UAV(4),
     * UGV(5),
     * STATICSENSOR(6),
     * MOBILESENSOR(7),
     * WSN(8);
     * @param sysType The Announce.SYS_TYPE of this System
     * @see pt.lsts.imc.Announce.SYS_TYPE
     */
    public void setSysType(Announce.SYS_TYPE sysType) {
        this.sysType = sysType;
    }

    /**
     * Get the Addressable IP Address of this System
     * Usually an IPv4 address
     * @return the IP Address
     */
    public String getIpAddress() {
        return ipAddress;
    }

    /**
     * Set the IP address for this System
     * @param ipAddress The new IP address for this system
     */
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    /**
     * TimestampMillis of the last received IMCMessage from this system.
     * @return timestamp millis
     */
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

    /**
     * @return The last IMCMessage with generic type sent by this system
    **/
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

    /**
     * Check if the system is a vehicle.
     * @return true if SYS_TYPE = UUV, USV, UAV or UGV, false otherwise
    **/
    public boolean isVehicle(){
        if (getSysType()==
                Announce.SYS_TYPE.UUV
            || getSysType()==
                Announce.SYS_TYPE.USV
            || getSysType()==
                Announce.SYS_TYPE.UAV
            || getSysType()==
                Announce.SYS_TYPE.UGV)
                return true;
            else
                return false;
    }

    /**
     * Check if the system is a Unmanned Aerial Vehicle (UAV).
     * @return true if SYS_TYPE = UAV, false otherwise
     **/
    public boolean isUAV(){
        if (getSysType()== Announce.SYS_TYPE.UAV){
            return true;
        }
        return false;
    }

    /**
     * Check if the system is a Unmanned Underwater Vehicle (UUV), also refered to as Autonomous Underwater Vehicle (AUV) or Light AUV (LAUV).
     * @return true if SYS_TYPE = UUV, false otherwise
     **/
    public boolean isUUV(){
        if (getSysType()== Announce.SYS_TYPE.UUV){
            return true;
        }
        return false;
    }

    /**
     * Check if the system is a Command and Control Unit (CCU)
     * Examples of CCUs: Neptus, ACCU, ASA, other systems that use ACCL.
     * @return true if SYS_TYPE = CCU, false otherwise
     **/
    public boolean isCCU(){
        if (getSysType()== Announce.SYS_TYPE.CCU){
            return true;
        }
        return false;
    }

}
