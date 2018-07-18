package test.com.ctrip.platform.dal.dao.common;

import com.ctrip.platform.dal.dao.DalPojo;
import com.ctrip.platform.dal.dao.annotation.Database;
import com.ctrip.platform.dal.dao.annotation.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Types;


@Entity
@Database(name = "dao_test")
@Table(name = "test_table")
public class TestTable implements DalPojo {

    @Column(name = "ID")
    @Type(value = Types.INTEGER)
    private Integer iD;

    @Column(name = "Name")
    @Type(value = Types.NVARCHAR) // change Types to nvarchar to produce parameter error
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