#if($host.hasPk())
#if($host.isIntegerPk() && $host.generateAPI(1,13))

	/**
	 * Query ${host.getPojoClassName()} by the specified ID
	 * The ID must be a number
	**/
	public ${host.getPojoClassName()} queryByPk(Number id)
			throws SQLException {
		return queryByPk(id, null);
	}

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
	public ${host.getPojoClassName()} queryByPk(${host.getPkParameterDeclarationWithoutHints()})
			throws SQLException {
		return queryByPk(${host.getPkParameters()}, null);
	}

	/**
	 * Query ${host.getPojoClassName()} by complex primary key
	**/
	public ${host.getPojoClassName()} queryByPk(${host.getPkParameterDeclaration()})
			throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		${host.getPojoClassName()} pk = new ${host.getPojoClassName()}();		
#foreach( $field in ${host.getPrimaryKeys()} )
		pk.set${field.getCamelCaseCapitalizedName()}(${field.getCamelCaseUncapitalizedName()});
#end
		return client.queryByPk(pk, hints);
	}
#end
#if($host.generateAPI(3,15))

	/**
	 * Query ${host.getPojoClassName()} by ${host.getPojoClassName()} instance which the primary key is set
	**/
	public ${host.getPojoClassName()} queryByPk(${host.getPojoClassName()} pk)
			throws SQLException {
		return queryByPk(pk, null);
	}

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

	/**
	 * Query against sample pojo. All not null attributes of the passed in pojo
	 * will be used as search criteria.
	**/
	public List<${host.getPojoClassName()}> queryLike(${host.getPojoClassName()} sample)
			throws SQLException {
		return queryLike(sample, null);
	}

	/**
	 * Query against sample pojo. All not null attributes of the passed in pojo
	 * will be used as search criteria.
	**/
	public List<${host.getPojoClassName()}> queryLike(${host.getPojoClassName()} sample, DalHints hints)
			throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		return client.queryLike(sample, hints);
	}
