package com.ctrip.platform.dal.dao.helper;

import java.util.List;
import java.util.Set;

/**
 * @author c7ch23en
 */
public interface Sorter<T> {

    List<T> sort(Set<T> raw);

}
