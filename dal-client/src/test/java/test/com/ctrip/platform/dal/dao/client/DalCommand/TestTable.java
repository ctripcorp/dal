package test.com.ctrip.platform.dal.dao.client.DalCommand;

import com.ctrip.platform.dal.dao.DalPojo;
import com.ctrip.platform.dal.dao.annotation.Database;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Types;
import com.ctrip.platform.dal.dao.annotation.Type;

@Entity
@Database(name = "dao_test")
@Table(name = "test_table")
public class TestTable implements DalPojo {

    @Column(name = "ID")
    @Type(value = Types.INTEGER)
    private Integer iD;

    @Column(name = "Name")
    @Type(value = Types.VARCHAR)
    private String name;

    public Integer getID() {
        return iD;
    }

    public void setID(Integer iD) {
        this.iD = iD;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}