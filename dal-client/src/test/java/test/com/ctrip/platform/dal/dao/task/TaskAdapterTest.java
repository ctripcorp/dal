package test.com.ctrip.platform.dal.dao.task;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

import org.junit.Assert;
import org.junit.Test;

import com.ctrip.platform.dal.dao.DalParser;
import com.ctrip.platform.dal.dao.UpdatableEntity;
import com.ctrip.platform.dal.dao.annotation.Database;
import com.ctrip.platform.dal.dao.annotation.Type;
import com.ctrip.platform.dal.dao.helper.DalDefaultJpaParser;
import com.ctrip.platform.dal.dao.task.TaskAdapter;

public class TaskAdapterTest {
	
	@Test
	public void testGetPrimaryKeys() throws SQLException {
		TaskAdapter<MultipleKeyClientTestModel> test = new TaskAdapter();
		DalParser<MultipleKeyClientTestModel> parser = new DalDefaultJpaParser<>(MultipleKeyClientTestModel.class);
		
		test.initialize(parser);
		
		MultipleKeyClientTestModel pojo = new MultipleKeyClientTestModel();
		pojo.setId(1);
		pojo.setQuantity(10);
		pojo.setTableIndex(1000);
		pojo.setAddress("100000");
		
		Map<String, ?> v = test.getPrimaryKeys(parser.getFields(pojo));
		Iterator<String> i = v.keySet().iterator();
		Assert.assertEquals("id", i.next());
		Assert.assertEquals("quantity", i.next());
		Assert.assertEquals("tableIndex", i.next());
		Assert.assertEquals("address", i.next());
		
		Assert.assertEquals(1, v.get("id"));
		Assert.assertEquals(10, v.get("quantity"));
		Assert.assertEquals(1000, v.get("tableIndex"));
		Assert.assertEquals("100000", v.get("address"));
	}


	@Entity
	@Database(name=OracleTestInitializer.DATABASE_NAME)
	@Table(name="dal_client_test")
	private class MultipleKeyClientTestModel extends UpdatableEntity {
		@Id
		@Column(name="id")
		@Type(value=Types.INTEGER)
		private Integer id;
		
		@Id
		@Column(name="quantity")
		@Type(value=Types.INTEGER)
		private Integer quantity;
		
		@Column(name="dbIndex")
		@Type(value=Types.INTEGER)
		private Integer dbIndex;
		
		@Id
		@Column(name="tableIndex")
		@Type(value=Types.INTEGER)
		private Integer tableIndex;
		
		@Column(name="type")
		@Type(value=Types.SMALLINT)
		private Short type;
		
		@Id
		@Column(name="address")
		@Type(value=Types.VARCHAR)
		private String address;
		
		@Column(name="last_changed")
		@Type(value=Types.TIMESTAMP)
		@Version
		private Timestamp lastChanged;

		public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			update("id");
			this.id = id;
		}

		public Integer getQuantity() {
			return quantity;
		}

		public void setQuantity(Integer quantity) {
			update("quantity");
			this.quantity = quantity;
		}

		public Integer getDbIndex() {
			return dbIndex;
		}

		public void setDbIndex(Integer dbIndex) {
			update("dbIndex");
			this.dbIndex = dbIndex;
		}

		public Integer getTableIndex() {
			return tableIndex;
		}

		public void setTableIndex(Integer tableIndex) {
			update("tableIndex");
			this.tableIndex = tableIndex;
		}
		
		public Short getType() {
			return type;
		}

		public void setType(Short type) {
			update("type");
			this.type = type;
		}

		public String getAddress() {
			return address;
		}

		public void setAddress(String address) {
			update("address");
			this.address = address;
		}

		public Timestamp getLastChanged() {
			return lastChanged;
		}

		public void setLastChanged(Timestamp lastChanged) {
			update("last_changed");
			this.lastChanged = lastChanged;
		}
	}
}
