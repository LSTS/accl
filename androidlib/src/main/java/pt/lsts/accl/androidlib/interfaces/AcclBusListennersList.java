package pt.lsts.accl.androidlib.interfaces;

import java.util.HashSet;

/**
 * Created by jloureiro on 08-09-2015.
 */
public interface AcclBusListennersList {

    public void register(Object pojo);
    public void unregister(Object pojo);
    public void unregisterAll();
}
