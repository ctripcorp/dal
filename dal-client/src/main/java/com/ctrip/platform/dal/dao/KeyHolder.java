package com.ctrip.platform.dal.dao;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import com.ctrip.platform.dal.dao.client.DalLogger;
import com.ctrip.platform.dal.dao.helper.EntityManager;
import com.ctrip.platform.dal.dao.task.DaoTask;
import com.ctrip.platform.dal.dao.task.KeyHolderAwaredTask;
import com.ctrip.platform.dal.exceptions.DalException;
import com.ctrip.platform.dal.exceptions.ErrorCode;

/**
 * A helper class that will collect all generated keys for insert operation
 * 
 * @author jhhe
 *
 */
public class KeyHolder {
    public static final String NOT_SET = "NOT SET!!!";
    
	private volatile boolean requireMerge = false;
	private AtomicInteger currentPos = new AtomicInteger();
	private AtomicInteger remainSize = new AtomicInteger();

	private final Map<Integer, Map<String, Object>> allKeys = new ConcurrentHashMap<>();
	
	private AtomicBoolean merged = new AtomicBoolean(false);

    private DalLogger logger;
    
	public KeyHolder() {
	    logger = DalClientFactory.getDalLogger();
	    currentPos.set(0);
	}
	
	/**
     * For internal use. Initialize all generated keys 
	 * @param size
	 */
	public void initialize(int size) {
		remainSize.set(size);
		
		if(allKeys.size() > 0)
		    logger.error("Reuse of KeyHolder detected!", new IllegalStateException("Reuse of KeyHolder detected!"));

		allKeys.clear();
		
        for(int i = 0; i < size; i++) {
            allKeys.put(i, createEmptyKeys());
        }
	}
	
	public int size() {
        return allKeys.size();
	}
	
	/**
	 * Indicate that a cross shard operation is under going, the generated keys need to be merged
	 */
	public void requireMerge() {
		this.requireMerge = true;
	}
	
	public boolean isRequireMerge() {
		return requireMerge;
	}
	
	public boolean isMerged() {
		return merged.get();
	}
	
	public void waitForMerge() throws InterruptedException {
		while(isMerged() == false)
			Thread.sleep(1);
	}
	
	public void waitForMerge(int timeout) throws InterruptedException {
		int i = 0;
		while(isMerged() == false && timeout > i++)
			Thread.sleep(1);
	}
	
	/**
	 * Get the generated Id. The type is of Number.
	 * @return id in number
	 * @throws SQLException if there is more than one generated key or the conversion is failed.
	 */
	public Number getKey() throws SQLException {
		return getId(getKeys());
	}

	/**
	 * Get the generated Id for given index. The type is of Number.
	 * @return key in number format
	 * @throws SQLException if the generated key is not number type.
	 */
	public Number getKey(int index) throws SQLException {
		if(size() != 0 && requireMerge && allKeys.containsKey(index))
			return getId(allKeys.get(index));
		
		try {
			return getId(getKeyList().get(index));
		} catch (Throwable e) {
			throw new DalException(ErrorCode.ValidateKeyHolderConvert, e);
		}
	}

	/**
	 * Get the first generated key in map.
	 * @return null if no key found, or the keys in a map
	 * @throws SQLException
	 */
	public Map<String, Object> getKeys() throws SQLException {
		if (size() != 1) {
			throw new DalException(ErrorCode.ValidateKeyHolderSize, getKeyList());
		}
		
        return allKeys.get(0);
	}

	/**
	 * Get all the generated keys for multiple insert.
	 * @return all the generated keys
	 * @throws DalException 
	 */
	public List<Map<String, Object>> getKeyList() throws DalException {
		if(requireMerge && merged.get() == false)
			throw new DalException(ErrorCode.KeyGenerationFailOrNotCompleted);

		List<Map<String, Object>> keyList = new ArrayList<>();
		
		int size = size();
		for(int i = 0; i < size; i++)
		    keyList.add(allKeys.get(i));
		
		return keyList;
	}
	
	/**
	 * Convert generated keys to list of number. 
	 * @return
	 * @throws SQLException if the conversion fails
	 */
	public List<Number> getIdList() throws SQLException {
		List<Number> idList = new ArrayList<Number>();
		
		try {
			for(Map<String, Object> key: getKeyList()) {
				idList.add(getId(key));
			}
			return idList;
		} catch (Throwable e) {
			e.printStackTrace();
			throw new DalException(ErrorCode.ValidateKeyHolderConvert, e);
		}
	}
	
	private Number getId(Map<String, Object> key) throws DalException {
		return (Number)key.values().iterator().next();
	}
	
	private static boolean isKeyHolderRequired(DaoTask<?> task, KeyHolder holder) {
        return task instanceof KeyHolderAwaredTask && holder != null;
    }
	
    /**
     * For internal use, add generated keys, for combined insert case
     * @param key
     */
    public void addKeys(List<Map<String, Object>> keys) {
        int i = 0;
        for(Map<String, Object> key: keys)
            allKeys.put(i++, key);
    }
    
	/**
	 * For internal use, add a generated key for single insert case
	 * @param key
	 */
	public void addKey(Map<String, Object> key) {
		allKeys.put(currentPos.get(), key);
		currentPos.incrementAndGet();
	}

	public static DalHints prepareLocalHints(DaoTask<?> task, DalHints hints) {
        // To avoid shard id being polluted by each pojos
        DalHints localHints = hints.clone();
        
        if(isKeyHolderRequired(task, hints.getKeyHolder()))
            localHints.setKeyHolder(new KeyHolder());
        
        return localHints;
    }

	public static void mergePartial(DaoTask<?> task, KeyHolder originalHolder, Integer[] indexList, KeyHolder localHolder, Throwable error) throws SQLException {
        if(!isKeyHolderRequired(task, originalHolder))
            return;
        
        if(error == null)
            originalHolder.addPatial(indexList, localHolder);
        else
            originalHolder.patialFailed(indexList.length);

    }

	public static void mergePartial(DaoTask<?> task, KeyHolder originalHolder, KeyHolder localHolder, Throwable error) throws SQLException {
	    if(!isKeyHolderRequired(task, originalHolder))
	        return;
	    
        if(error == null)
            originalHolder.addKey(localHolder.getKeys());
        else
            originalHolder.singleFail();
    }

	/**
     * For internal use, add a generated key 
     * @param key
     */
    private void singleFail() {
        addKey(createEmptyKeys());
    }
    
    private Map<String, Object> createEmptyKeys() {
        HashMap<String, Object> emptyKeys = new HashMap<>();
        emptyKeys.put(NOT_SET, null);

        return emptyKeys;
    }

    /**
     * For internal use. Indicate how many partial pojo insert failed 
     * @param indexList
     */
    private void patialFailed(int partialSize) {
        deduct(partialSize);
    }
	
	/**
	 * For internal use. Add partial generated keys, it will only be invoked for cross shard combine insert case 
	 * @param indexList
	 * @param tmpHolder
	 */
	public void addPatial(Integer[] indexList, KeyHolder tmpHolder) {
		int i = 0;
		for(Integer index: indexList) {
			allKeys.put(index, tmpHolder.allKeys.get(i++));
		}
		
		// All partial is added, start merge generated keys
		deduct(indexList.length);
	}

    private void deduct(int size) {
        if(remainSize.addAndGet(-size) == 0)
			merge();
    }
	
	private synchronized void merge() {
		if(merged.get())
			return;
		
//		keyList.clear();
//		for(int i = 0; i < allKeys.size(); i++)
//			keyList.add(allKeys.get(i));
//		
		merged.set(true);
	}
	
	public static void setGeneratedKeyBack(DaoTask<?> task, DalHints hints, List<?> rawPojos) throws SQLException {
	    if(!(task instanceof KeyHolderAwaredTask))
	        return;
	        
        KeyHolder keyHolder = hints.getKeyHolder();
        
        if(keyHolder == null || rawPojos == null || rawPojos.isEmpty())
            return;
        
        if(!(hints.is(DalHintEnum.setIdentityBack) && hints.isIdentityInsertDisabled()))
            return;
        
        EntityManager em = EntityManager.getEntityManager(rawPojos.get(0).getClass());
        if(em.getPrimaryKeyNames().length == 0)
            throw new IllegalArgumentException("insertIdentityBack only support JPA POJO. Please use code gen to regenerate your POJO");

        Field pkFlield = em.getFieldMap().get(em.getPrimaryKeyNames()[0]);
        
        if(pkFlield == null)
            throw new IllegalArgumentException("insertIdentityBack only support JPA POJO. Please use code gen to regenerate your POJO");
        
        for(int i = 0; i < rawPojos.size(); i++)
            setPrimaryKey(pkFlield, rawPojos.get(i), keyHolder.getKey(i));
    }
	
    /**
     * Only support number type and auto incremental id is one column
     * @throws SQLException
     */
    private static void setPrimaryKey(Field pkFlield, Object entity, Number val) throws SQLException {
        try {
            if(val == null) {
                pkFlield.set(entity, null);
                return;
            }
            if (pkFlield.getType().equals(Long.class) || pkFlield.getType().equals(long.class)) {
                pkFlield.set(entity, val.longValue());
                return;
            }
            if (pkFlield.getType().equals(Integer.class) || pkFlield.getType().equals(int.class)) {
                pkFlield.set(entity, val.intValue());
                return;
            }
            if (pkFlield.getType().equals(Byte.class) || pkFlield.getType().equals(byte.class)) {
                pkFlield.set(entity, val.byteValue());
                return;
            }
            if (pkFlield.getType().equals(Short.class) || pkFlield.getType().equals(short.class)) {
                pkFlield.set(entity, val.shortValue());
                return;
            }
        } catch (Throwable e) {
            throw new DalException(ErrorCode.SetPrimaryKeyFailed, entity.getClass().getName(), pkFlield.getName());
        }
    }	
}
