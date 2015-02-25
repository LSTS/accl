package pt.lsts.accl.examples;

import pt.lsts.accl.bus.AcclBus;
import pt.lsts.accl.event.EventMainSystemSelected;
import pt.lsts.imc.PlanControl;
import pt.lsts.imc.PlanControl.OP;
import pt.lsts.imc.Teleoperation;

import com.squareup.otto.Subscribe;

public class DesktopExample2 {

	@Subscribe
	public void on(EventMainSystemSelected mainSystem) {
		System.out.println("Main system selected: " + mainSystem);

		PlanControl pc = new PlanControl().setOp(OP.START)
				.setType(PlanControl.TYPE.REQUEST).setPlanId("dummy")
				.setArg(new Teleoperation()).setRequestId(2334);
		AcclBus.send(pc, mainSystem.getSystem());
	}
}
