namespace Arch.Data.Orm
{
	[System.Flags]
	public enum Timing
	{
		None = 0,
		BeforeInsert = 1,
		AfterInsert = 2,
		BeforeUpdate = 4,
		AfterUpdate = 8,
		BeforeDelete = 16,
		AfterDelete = 32,
		AfterActivate = 64,
		All = 128
	}
}
