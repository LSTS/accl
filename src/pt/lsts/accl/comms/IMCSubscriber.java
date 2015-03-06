package pt.lsts.accl.comms;

import pt.lsts.imc.IMCMessage;

/**
 * Interface that a class should implement to receive IMCMessages
 * 
 * @author jqcorreia
 *
 */
public interface IMCSubscriber {
	public void onReceive(IMCMessage msg);
}