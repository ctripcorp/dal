#if($host.hasPk() && $host.generateAPI(1,2,3,13,14,15,22,23,24,34,35,36))
#if($host.isIntegerPk())
	/**
	 * Query ${host.getPojoClassName()} by the specified ID
	 * The ID must be a number
	**/
	public ${host.getPojoClassName()} queryByPk(Number id, DalHints hints)
			throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		return client.queryByPk(id, hints);
	}
#else
	/**
	 * Query ${host.getPojoClassName()} by complex primary key
	**/
	public ${host.getPojoClassName()} queryByPk(${host.getPkParameterDeclaration()})
			throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		${host.getPojoClassName()} pk = new ${host.getPojoClassName()}();		
#foreach( $field in ${host.getPrimaryKeys()} )
		pk.set${field.getCapitalizedName()}(${field.getUncapitalizedName()});
#end
		return client.queryByPk(pk, hints);
	}
#end
    /**
	 * Query ${host.getPojoClassName()} by ${host.getPojoClassName()} instance which the primary key is set
	**/
	public ${host.getPojoClassName()} queryByPk(${host.getPojoClassName()} pk, DalHints hints)
			throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		return client.queryByPk(pk, hints);
	}
#end