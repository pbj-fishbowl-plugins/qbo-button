package ca.cleaning_depot.qbo.button;

import com.evnt.common.Module;
import com.evnt.util.Util;
import com.fbi.fbo.impl.dataexport.QueryRow;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class QBPOButton extends QBButton {
	public QBPOButton() {
		this.setModuleName(Module.PO.getName());
	}

	@Override
	protected String getErrorMessage() {
		return "Select a PO first";
	}

	@Override
	protected String[] getItemIds(int soId) {
		Map<String, Serializable> map = new HashMap<>();
		map.put("id", soId);

		List<QueryRow> list = this.runQuery("SELECT postpo.extTxnId FROM postpo WHERE postpo.poid = :id;", map);

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
			return "vendorcredit";
		}
		return "bill";
	}

	private boolean isCredit(int soId, String extId) {
		Map<String, Serializable> map = new HashMap<>();
		map.put("id", soId);
		map.put("extId", extId);

		List<QueryRow> list = this.runQuery("""
				SELECT SUM(postpoitem.postedTotalCost) < 0 isCredit
				FROM postpo
				JOIN postpoitem ON postpoitem.postpoid = postpo.id
				WHERE postpo.poid = :id
				AND postpo.extTxnId = :extId;""", map);

		if (Util.isEmpty(list)) {
			return false;
		}

		return list.get(0).getInt("isCredit") == 1;
	}
}
