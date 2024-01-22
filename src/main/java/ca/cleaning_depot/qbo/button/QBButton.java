package ca.cleaning_depot.qbo.button;

import com.evnt.client.common.EVEManagerUtil;
import com.evnt.client.modules.ClientModuleImpl;
import com.fbi.gui.util.UtilGui;
import com.fbi.plugins.FishbowlPluginButton;
import com.fbi.util.logging.FBLogger;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.*;
import java.util.HashMap;

public abstract class QBButton extends FishbowlPluginButton {
	@Serial
	private static final long serialVersionUID = 1039147703509824427L;

	/**
	 * @return The txnId's of every item on the form
	 */
	protected abstract String[] getItemIds(int formId) throws Exception;

	/**
	 * @return The path in QBO (invoice/bill/etc.)
	 */
	protected abstract String getQBPath(int formId, String extId);

	/**
	 * @return The error message that pops up when no form (so/po/etc.) is selected
	 */
	protected abstract String getErrorMessage();

	public QBButton() {
		this.setVisible(false);
		this.setPluginName("QBO Button");
		this.setName("See in QBO");
		this.setText("See in QBO");
		this.setToolTipText("Open a browser tab to the form in QBO");
		this.setIcon(new ImageIcon(this.getClass().getResource("/images/qbo.png")));
		this.addActionListener(this::onClick);
		SwingUtilities.invokeLater(this::init);
	}

	private void init() {
		// this runs after the subclass constructor is finished
		HashMap<String, ClientModuleImpl> modules = (HashMap<String, ClientModuleImpl>) EVEManagerUtil.getEveManager().getModules();
		modules.get(this.getModuleName()).addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent e) {
				QBButton.this.setVisible(QBButton.this.hasAccess());
			}
		});
		this.setVisible(this.hasAccess());
	}

	private String getStackTrace(Exception e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		return sw.toString();
	}

	protected void openQBO(int formId, String extId) throws Exception {
		this.openURLInBrowser("https://app.qbo.intuit.com/app/" + this.getQBPath(formId, extId) + "?txnId=" + extId);
	}

	protected final void openURLInBrowser(String url) throws Exception {
		String[] cmd = {"open", url};
		if (System.getProperty("os.name").startsWith("Windows")) {
			cmd = new String[]{"cmd", "/c", "start " + url.replace("&", "^&")};
		}

		Process p = Runtime.getRuntime().exec(cmd);
		BufferedReader reader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
		StringBuilder out = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			out.append(line);
		}

		if (!out.isEmpty()) {
			throw new Exception(out.toString());
		}
	}

	private void onClick(ActionEvent event) {
		int formId = this.getObjectId();

		if (formId < 1) {
			UtilGui.showErrorMessageDialog(this.getErrorMessage(), "Error");
			return;
		}

		String[] ids;
		try {
			ids = this.getItemIds(formId);
		} catch (Exception e) {
			UtilGui.showErrorMessageDialog("Unable to read item numbers\n" + this.getStackTrace(e), "Error");
			FBLogger.error(e.getMessage(), e);
			return;
		}

		if (ids.length == 0) {
			UtilGui.showMessageDialog("This form has not been posted to QBO yet.");
			return;
		}

		try {
			for (String extId : ids) {
				this.openQBO(formId, extId);
			}
		} catch (Exception ex) {
			UtilGui.showErrorMessageDialog("Unable to open web browser\n" + ex.getMessage(), "Error");
			FBLogger.error(ex.getMessage(), ex);
		}
	}

	protected final boolean hasAccess() {
		return this.hasAccess(this.getPluginName() + "-" + this.getModuleName());
	}

}
