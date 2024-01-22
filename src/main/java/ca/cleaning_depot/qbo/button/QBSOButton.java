package ca.cleaning_depot.qbo.button;

import com.evnt.common.Module;
import com.evnt.util.Util;
import com.fbi.fbo.impl.dataexport.QueryRow;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class QBSOButton extends QBButton {

	public QBSOButton() {
		this.setModuleName(Module.SO.getName());
	}

	@Override
	protected String getErrorMessage() {
		return "Select a SO first";
	}

	@Override
	protected String[] getItemIds(int soId) {
		Map<String, Serializable> map = new HashMap<>();
		map.put("id", soId);

		List<QueryRow> list = this.runQuery("SELECT postso.extTxnId FROM postso WHERE postso.soid = :id;", map);

		return list
				.stream()
				.map(row -> row.getInt("exttxnid"))
				.filter(Objects::nonNull)
				.map(Object::toString)
				.toArray(String[]::new);
	}

	@Override
	protected String getQBPath(int soId, String extId) {
		if (isCredit(soId, extId)) {
			return "creditmemo";
		}
		return "invoice";
	}

	private boolean isCredit(int soId, String extId) {
		Map<String, Serializable> map = new HashMap<>();
		map.put("id", soId);
		map.put("extId", extId);

		List<QueryRow> list = this.runQuery("""
				SELECT (SUM(postsoitem.totalPrice) + postso.totalTax) < 0 isCredit
				FROM postso
				JOIN postsoitem ON postsoitem.postsoid = postso.id
				WHERE postso.soid = :id
				AND postso.extTxnId = :extId;""", map);

		if (Util.isEmpty(list)) {
			return false;
		}

		return list.get(0).getInt("isCredit") == 1;
	}
}
