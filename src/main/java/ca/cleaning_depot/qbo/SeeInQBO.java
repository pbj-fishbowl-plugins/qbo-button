package ca.cleaning_depot.qbo;

import com.evnt.common.Module;
import com.fbi.plugins.FishbowlPlugin;
import com.fbi.util.FishbowlBrowser;

import java.awt.*;
import java.util.List;

public class SeeInQBO extends FishbowlPlugin {
	private boolean isClosed = true;
	private FishbowlBrowser browser;
	private static final String[] ACCESS_RIGHTS = new String[]{
			Module.SO.getName(), Module.PO.getName(), Module.CUSTOMER.getName()
	};

	public SeeInQBO() {
		this.setModuleName("QBO Button");
		this.setLayout(new BorderLayout());

		List<String> a = this.getAccessRights();
		for (String accessRight : ACCESS_RIGHTS) {
			if (a.contains(accessRight) || a.contains(this.getModuleName() + "-" + accessRight)) {
				continue;
			}
			this.addAccessRight(accessRight);
		}
	}

	@Override
	public boolean activateModule() {
		// Runs every time the tab is focused
		if (this.isClosed) {
			this.isClosed = false;
			this.browser = new FishbowlBrowser();
			this.browser.loadUrl("https://github.com/pbj-fishbowl-plugins/qbo-button/releases");
			this.add(this.browser.getBrowserView(), BorderLayout.CENTER);
		}
		return super.activateModule();
	}

	@Override
	public boolean closeModule() {
		// Runs every time the tab is closed
		this.isClosed = true;
		this.removeAll();
		this.browser.close();
		this.browser = null;
		return super.closeModule();
	}
}
