package ca.cleaning_depot.template;

import com.fbi.gui.util.UtilGui;
import com.fbi.plugins.FishbowlPlugin;
import com.fbi.sdk.constants.MenuGroupNameConst;

public class Template extends FishbowlPlugin {
	public PBJPlugin() {
		this.setMenuGroup(MenuGroupNameConst.INTEGRATIONS);
		this.setModuleName("Template");
		this.setButtonPrintVisible(false);
		this.setButtonEmailVisible(false);
	}

	@Override
	protected void initModule() {
		super.initModule();

		JButton button = new JButton("Template");
		button.addActionListener(ev -> UtilGui.showMessageDialog("Test"));
		this.add(button);
	}
}
