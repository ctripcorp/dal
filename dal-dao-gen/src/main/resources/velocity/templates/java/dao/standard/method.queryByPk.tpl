#if($host.hasPk())
#if($host.isIntegerPk() && $host.generateAPI(1,13))
	/**
	 * Query ${host.getPojoClassName()} by the specified ID
	 * The ID must be a number
	**/
	public ${host.getPojoClassName()} queryByPk(Number id, DalHints hints)
			throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		return client.queryByPk(id, hints);
	}
#end
#if(!$host.isIntegerPk() && $host.generateAPI(2,14))	
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
#if($host.generateAPI(3,15))
    /**
	 * Query ${host.getPojoClassName()} by ${host.getPojoClassName()} instance which the primary key is set
	**/
	public ${host.getPojoClassName()} queryByPk(${host.getPojoClassName()} pk, DalHints hints)
			throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		return client.queryByPk(pk, hints);
	}
#end
#end