package com.ctrip.framework.dal.dbconfig.plugin.entity;


public class KeyInfo {
   private String m_key;
   private String m_sslCode;

   public KeyInfo() {
   }


   //getter/setter
   public String getKey() {
      return m_key;
   }
   public KeyInfo setKey(String key) {
      m_key = key;
      return this;
   }

   public String getSslCode() {
      return m_sslCode;
   }
   public KeyInfo setSslCode(String sslCode) {
      m_sslCode = sslCode;
      return this;
   }


   @Override
   public boolean equals(Object obj) {
      if (obj instanceof KeyInfo) {
         KeyInfo _o = (KeyInfo) obj;
         if (!m_key.equals(_o.getKey())) {
            return false;
         }
         if (!m_sslCode.equals(_o.getSslCode())) {
            return false;
         }
         return true;
      }
      return false;
   }

   @Override
   public int hashCode() {
      int hash = 0;
      hash = hash * 31 + (m_key == null ? 0 : m_key.hashCode());
      hash = hash * 31 + (m_sslCode == null ? 0 : m_sslCode.hashCode());
      return hash;
   }



}
