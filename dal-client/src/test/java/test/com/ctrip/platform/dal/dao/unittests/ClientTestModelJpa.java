package test.com.ctrip.platform.dal.dao.unittests;

import java.sql.Timestamp;
import java.sql.Types;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.ctrip.platform.dal.dao.annotation.Database;
import com.ctrip.platform.dal.dao.annotation.Type;

@Database(name="123")
@Entity(name="dal_client_test")
public class ClientTestModelJpa {
    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Type(value=Types.INTEGER)
    private Integer id;
    
    @Column(name="quantity")
    @Type(value=Types.INTEGER)
    private Integer quan;
    
    @Column
    @Type(value=Types.SMALLINT)
    private Short type;
    
    @Column(length=50)
    @Type(value=Types.VARCHAR)
    private String address;
    
    @Column(nullable =false, insertable=false, name="last_changed")
    @Type(value=Types.TIMESTAMP)
    private Timestamp lastChanged;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getQuantity() {
        return quan;
    }

    public void setQuantity(Integer quantity) {
        this.quan = quantity;
    }

    public Short getType() {
        return type;
    }

    public void setType(Short type) {
        this.type = type;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Timestamp getLastChanged() {
        return lastChanged;
    }

    public void setLastChanged(Timestamp lastChanged) {
        this.lastChanged = lastChanged;
    }
}