#if($host.isHasSpt())
        /// <summary>
        ///  转换List为DataTable
        /// </summary>
        /// <param name="${WordUtils.uncapitalize($host.getClassName())}list">${host.getClassName()}实体对象列表</param>
        /// <param name="isInsert">${host.getClassName()}实体对象列表</param>
        /// <returns>DataTable</returns>
        private DataTable ToDataTable(IList<${host.getClassName()}> ${WordUtils.uncapitalize($host.getClassName())}List , bool insert)
        {
            DataTable dt = new DataTable();
#foreach($column in $host.getColumns())
            dt.Columns.Add("${WordUtils.capitalize($column.getName())}", typeof(${column.getType()}));
#end

            int i = 0;
            foreach (${host.getClassName()} ${WordUtils.uncapitalize($host.getClassName())}Info in ${WordUtils.uncapitalize($host.getClassName())}List)
            {
                DataRow row = dt.NewRow();
#foreach($column in $host.getColumns())
#if($column.isNullable() && $column.isValueType())
                if(${WordUtils.uncapitalize($host.getClassName())}Info.${WordUtils.capitalize($column.getName())} != null && ${WordUtils.uncapitalize($host.getClassName())}Info.${WordUtils.capitalize($column.getName())}.HasValue)
                {
#end
#if($column.isIdentity())
                    row["${WordUtils.capitalize($column.getName())}"] = insert ? ++i : ${WordUtils.uncapitalize($host.getClassName())}Info.${WordUtils.capitalize($column.getName())};
#else
                    row["${WordUtils.capitalize($column.getName())}"] = ${WordUtils.uncapitalize($host.getClassName())}Info.${WordUtils.capitalize($column.getName())};
#if($column.isNullable() && $column.isValueType())
                }
#end
#end
#end
                dt.Rows.Add(row);
            }
            return dt;
        }
#end

