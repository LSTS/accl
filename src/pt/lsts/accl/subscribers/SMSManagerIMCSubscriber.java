package pt.lsts.accl.subscribers;

import pt.lsts.accl.managers.SMSManager;
import pt.lsts.imc.IMCMessage;
import pt.lsts.imc.Sms;
import android.util.Log;

public class SMSManagerIMCSubscriber implements IMCSubscriber{

	public static final String[] SUBSCRIBED_MSGS = { "Sms" };
	public static final String TAG = "AccuSmsHandler";
	private SMSManager sMSManager;
    private Thread thread;
	
	public SMSManagerIMCSubscriber(SMSManager sMSManager){
		this.sMSManager = sMSManager;
	}
	
	@Override
	public void onReceive(final IMCMessage msg) {

        if (thread!=null)//if there is a previous message, finish going through that one
            while (thread.isAlive());

        thread = new Thread() {
            @Override
            public void run() {

                if (msg.getMgid() == Sms.ID_STATIC
                        && msg.getDst() == sMSManager.getmManager().getLocalId()) {
                    Log.i("SmsManager", "Sending an SMS to " + msg.getString("number"));
                    sMSManager.sendSms(msg.getString("number"), msg.getString("contents"),
                            msg.getInteger("timeout"));
                } else {
                    Log.w("SmsManager", "Ignoring Sms request");
                    System.out.println(sMSManager.getmManager().getLocalId());
                    System.out.println(msg.toString());
                }

            }
        };
        thread.start();

    }
}
