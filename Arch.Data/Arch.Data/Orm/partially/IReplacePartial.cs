using System;
using System.Collections.Generic;
using System.Linq.Expressions;

namespace Arch.Data.Orm.partially
{
    public interface IReplacePartial<T>
    {
        IEnumerable<String> ReplaceFields { get; }

        IReplacePartial<T> Replace<TField>(Expression<Func<T, TField>> expression);

        IReplacePartial<T> Replace(String fieldName);

        IReplacePartial<T> Replace<TField>(IEnumerable<Expression<Func<T, TField>>> expressions);

        IReplacePartial<T> Replace(IEnumerable<String> fieldsName);

        Boolean Validate();
    }
}
