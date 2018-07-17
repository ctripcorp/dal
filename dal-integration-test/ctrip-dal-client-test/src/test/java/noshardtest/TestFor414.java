package noshardtest;

import com.ctrip.datasource.configure.DalDataSourceFactory;
import com.ctrip.platform.dal.dao.DalClientFactory;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by lilj on 2017/11/28.
 */
public class TestFor414 {
    @Test
    public void testBigReuqest() throws Exception {
        DalClientFactory.shutdownFactory();
//        DalClientFactory.initClientFactory(this.getClass().getClassLoader().getResource(".").getPath()+"DalConfig/Dal.config");
        long start=System.currentTimeMillis();
        DalClientFactory.initClientFactory( ClassLoader.getSystemClassLoader().getResource(".").getPath()+"DalConfigFor414/Dal.config");
        long end=System.currentTimeMillis();
        System.out.println("cost: "+(end-start));
        DalClientFactory.shutdownFactory();
    }


    public void get1000Keys() throws Exception{
        BufferedReader in=new BufferedReader(new InputStreamReader(new FileInputStream("D:/titankeys.txt"),"UTF-8"));

        String line;
        Set<String> originaldbNames = new HashSet<>();
        Set<String> newdbNames = new HashSet<>();
        while((line=in.readLine())!=null){
            originaldbNames.add(line.trim());
        }
        Assert.assertEquals(1000,originaldbNames.size());

        System.out.println("filter keys start");
        DalDataSourceFactory dalDataSourceFactory=new DalDataSourceFactory();
        for(String dbname:originaldbNames){
            try {
                dalDataSourceFactory.createDataSource(dbname);
                newdbNames.add(dbname);
            }catch (Throwable e){
//                dbNames.remove(dbname);
//                e.printStackTrace();
            }
        }
//        System.out.println(newdbNames.size());
        System.out.println("filter keys done.");
        System.setOut(new PrintStream(new File("D:/titankeysOut.txt")));
        for (String dbname:newdbNames)
            System.out.println(dbname);
    }

//    @Test
//    public void test1000Keys() throws Exception{
////        get1000Keys();
//        BufferedReader in=new BufferedReader(new InputStreamReader(new FileInputStream("D:/titankeysOut.txt"),"UTF-8"));
//        String line;
//        Set<String> dbNames = new HashSet<>();
//        while((line=in.readLine())!=null){
//            dbNames.add(line.trim());
//        }
////        Assert.assertEquals(974,dbNames.size());
//        System.out.println("key number: "+ dbNames.size());
//        TitanProvider provider = new TitanProvider();
//        Map<String, String> settings = new HashMap<>();
//        provider.initialize(settings);
//        long start=System.currentTimeMillis();
//        provider.setup(dbNames);
//        long end=System.currentTimeMillis();
//        System.out.println("cost time: "+(end-start));
//        Thread.sleep(2000);
//        System.out.println("Done");
//    }


   /* public void check(int i) throws Exception{
        if(i==2)
            throw new Exception("i can't equals to 2!");
        else
            System.out.println(i);
    }

    @Test
    public  void  test() throws Exception{
        Set<Integer> original = new HashSet<>();
        Set<Integer> newSet = new HashSet<>();
        original.add(1);
        original.add(2);
        original.add(3);
        original.add(4);
//        System.setOut(new PrintStream(new File("out.txt")));
        int i=1;
        while(i<5){
            try {
                check(i);
                newSet.add(i);
            }catch (Exception e){

            }
            i++;
        }
        for(Integer j:newSet)
        System.out.println(j);
    }*/

}
