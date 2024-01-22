package ca.cleaning_depot.qbo.button;

import com.evnt.common.Module;
import com.fbi.fbo.impl.dataexport.QueryRow;

import java.io.Serializable;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QBCustomerButton extends QBButton {

	public QBCustomerButton() {
		this.setModuleName(Module.CUSTOMER.getName());
	}

	@Override
	protected String getErrorMessage() {
		return "Select a customer";
	}

	@Override
	protected String[] getItemIds(int customerId) {
		Map<String, Serializable> map = new HashMap<>();
		map.put("id", customerId);

		List<QueryRow> list = this.runQuery("SELECT customer.name FROM customer WHERE customer.id = :id;", map);

		return new String[]{list.get(0).getString("name")};
	}

	@Override
	protected String getQBPath(int customerId, String extId) {
		return null;
	}

	@Override
	protected void openQBO(int customerId, String customerName) throws Exception {
		this.openURLInBrowser(
				"https://app.qbo.intuit.com/app/search?searchCat=transaction&searchTransactionType=x999&searchValue=&searchAdvFieldArray=name&searchAdvFieldLabelArray=Display+Name&searchAdvValueArray=156&searchAdvOpArray=%3D&searchAdvValueLabelArray="
						+ URLEncoder.encode(customerName, Charset.defaultCharset()));
	}
}
