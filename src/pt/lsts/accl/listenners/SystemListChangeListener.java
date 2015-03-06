package pt.lsts.accl.listenners;

import pt.lsts.accl.sys.Sys;

import java.util.ArrayList;

public interface SystemListChangeListener {

	public void onSystemListChange(ArrayList<Sys> list);
}
