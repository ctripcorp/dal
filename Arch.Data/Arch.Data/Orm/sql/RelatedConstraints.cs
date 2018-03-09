using System;

namespace Arch.Data.Orm.sql
{
    //InConstraint, NullConstraint,
    class BracketConstraint : SqlConstraint
    {
        private BracketConstraint(Boolean isLeft)
        {
            IsLeft = isLeft;
            Symbol = isLeft ? "(" : ")";
        }

        public override Boolean IsBracket()
        {
            return true;
        }

        public override Boolean IsClause()
        {
            return false;
        }

        public Boolean IsLeft { get; private set; }

        public static BracketConstraint LeftBracket()
        {
            return new BracketConstraint(true);
        }

        public static BracketConstraint RightBracket()
        {
            return new BracketConstraint(false);
        }

        public override String GetSql(SqlTable table, Char openQuote, Char closeQuote, ref Int32 offset)
        {
            return Symbol;
        }
    }

    class OperatorConstraint : SqlConstraint
    {
        private OperatorConstraint(String op)
        {
            Symbol = op;
        }

        public override Boolean IsOperator()
        {
            return true;
        }

        public override Boolean IsClause()
        {
            return false;
        }

        public static OperatorConstraint And()
        {
            return new OperatorConstraint("AND");
        }

        public static OperatorConstraint Or()
        {
            return new OperatorConstraint("OR");
        }

        public override String GetSql(SqlTable table, Char openQuote, Char closeQuote, ref Int32 offset)
        {
            return Symbol;
        }
    }

    class NullValueConstraint : SqlConstraint
    {
        public override Boolean IsNull()
        {
            return true;
        }

        public override Boolean IsClause()
        {
            return true;
        }

        public override String GetSql(SqlTable table, Char openQuote, Char closeQuote, ref Int32 offset)
        {
            return String.Empty;
        }
    }

    class NotConstraint : SqlConstraint
    {
        public NotConstraint()
        {
            Symbol = "NOT";
        }

        public override Boolean IsClause()
        {
            return true;
        }

        public override Boolean IsOperator()
        {
            return true;
        }

        public override String GetSql(SqlTable table, Char openQuote, Char closeQuote, ref Int32 offset)
        {
            return Symbol;
        }
    }

    class BetweenConstraint : SqlConstraint
    {
        public BetweenConstraint(String fieldName, Object paramValue1, Object paramValue2) : base(fieldName, "BETWEEN", paramValue1, paramValue2) { }

        public override Boolean IsClause()
        {
            return true;
        }

    }
}
