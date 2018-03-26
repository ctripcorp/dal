package test.com.ctrip.platform.dal.dao.helper;

import static org.junit.Assert.assertArrayEquals;

import java.util.Random;

import org.apache.commons.codec.binary.Base64;
import org.junit.Test;

import com.ctrip.platform.dal.dao.helper.DalBase64;

public class DalBase64Test {

    @Test
    public void test() {
        String a = "name=*";
        byte[] by = Base64.encodeBase64(a.getBytes());
        String en = new String(by);
        System.out.println(en);
        System.out.println(Base64.decodeBase64(by));
        System.out.println();

        a = "name=*";
        by = DalBase64.encodeBase64(a.getBytes());
        en = new String(by);
        System.out.println(en);
        System.out.println(DalBase64.decodeBase64(en.getBytes()));
    }

    @Test
    public void encodeBase64() throws Exception {
        String someSeeds = "123456abcdef,.!@#$";
        checkEncode(generateString(someSeeds, 5));
        checkEncode(generateString(someSeeds, 10));
        checkEncode(generateString(someSeeds, 50));
        checkEncode(generateString(someSeeds, 100));
        checkEncode(generateString(someSeeds, 1000));
    }

    @Test
    public void decodeBase64() throws Exception {
        String someSeeds = "123456abcdef,.!@#$";

        checkDecode(generateString(someSeeds, 5));
        checkDecode(generateString(someSeeds, 10));
        checkDecode(generateString(someSeeds, 50));
        checkDecode(generateString(someSeeds, 100));
        checkDecode(generateString(someSeeds, 1000));
    }

    private void checkEncode(String someString) {
        byte[] bytes = someString.getBytes();
        assertArrayEquals(Base64.encodeBase64(bytes), DalBase64.encodeBase64(bytes));
    }

    private void checkDecode(String someString) {
        byte[] bytes = Base64.encodeBase64(someString.getBytes());

        assertArrayEquals(Base64.decodeBase64(bytes), DalBase64.decodeBase64(bytes));
    }

    private String generateString(String characters, int length) {
        Random random = new Random();
        char[] text = new char[length];
        for (int i = 0; i < length; i++) {
            text[i] = characters.charAt(random.nextInt(characters.length()));
        }
        return new String(text);
    }
}
