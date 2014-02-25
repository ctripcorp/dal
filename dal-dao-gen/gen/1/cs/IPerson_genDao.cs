using System;
using System.Collections.Generic;
using com.ctrip.platform.tools.Entity.DataModel;

namespace com.ctrip.platform.tools.Interface.IDao
{
	public partial interface IPerson_genDao
	{

	       /// <summary>
        ///  ����Person_gen
        /// </summary>
        /// <param name="person_gen">Person_genʵ�����</param>
        /// <returns>����������</returns>
        int InsertPerson_gen(Person_gen person_gen);

        /// <summary>
        /// �޸�Person_gen
        /// </summary>
        /// <param name="person_gen">Person_genʵ�����</param>
        /// <returns>״̬����</returns>
        int UpdatePerson_gen(Person_gen person_gen);

        /// <summary>
        /// ɾ��Person_gen
        /// </summary>
        /// <param name="person_gen">Person_genʵ�����</param>
        /// <returns>״̬����</returns>
        int DeletePerson_gen(Person_gen person_gen);


        /// <summary>
        /// ����������ȡPerson_gen��Ϣ
        /// </summary>
        /// <param name="iD"></param>
        /// <returns>Person_gen��Ϣ</returns>
        Person_gen FindByPk(uint iD);

        /// <summary>
        /// ��ȡ����Person_gen��Ϣ
        /// </summary>
        /// <returns>Person_gen�б�</returns>
        IList<Person_gen> GetAll();

        /// <summary>
        ///  ��������Person_gen
        /// </summary>
        /// <param name="person_gen">Person_genʵ������б�</param>
        /// <returns>״̬����</returns>
        int BulkInsertPeople(IList<Person_gen> person_genList);

        /// <summary>
        ///  ��������Person_gen
        /// </summary>
        /// <param name="person_gen">Person_genʵ������б�</param>
        /// <returns>״̬����</returns>
        int BulkUpdatePeople(IList<Person_gen> person_genList);

        /// <summary>
        ///  ����ɾ��Person_gen
        /// </summary>
        /// <param name="person_gen">Person_genʵ������б�</param>
        /// <returns>״̬����</returns>
        int BulkDeletePeople(IList<Person_gen> person_genList);

        /// <summary>
        /// ȡ���ܼ�¼��
        /// </summary>
        /// <returns>��¼��</returns>
        long Count();

        /// <summary>
        ///  ����Person_gen������ҳ
        /// </summary>
        /// <param name="obj">Person_genʵ������������</param>
        /// <param name="pagesize">ÿҳ��¼��</param>
        /// <param name="pageNo">ҳ��</param>
        /// <returns>�������</returns>
        IList<Person_gen> GetListByPage(Person_gen obj, int pagesize, int pageNo);

        /// <summary>
        ///  GetNameByID
        /// </summary>
        /// <param name="iD"></param>
        /// <returns></returns>
        public IList<Person_gen> GetNameByID(uint iD);
        /// <summary>
        ///  deleteByName
        /// </summary>
        /// <param name="name"></param>
        /// <returns></returns>
        public int deleteByName(string name);
	}
}