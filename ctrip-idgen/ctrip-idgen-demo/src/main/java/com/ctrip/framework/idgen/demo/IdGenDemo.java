package com.ctrip.framework.idgen.demo;

import com.ctrip.framework.idgen.client.IdGeneratorFactory;
import com.ctrip.platform.dal.sharding.idgen.LongIdGenerator;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class IdGenDemo {

    public static void main(String[] args) {
        demo1();
    }

    private static void demo1() {
        String sequenceName = null;
        LongIdGenerator generator = null;
        Long id = null;

        while (true) {
            try {
                if (null == sequenceName) {
                    System.out.println("(Input a valid sequence name)");
                } else {
                    System.out.println("(Input a new sequence name, or press enter to use the old value)");
                }
                System.out.print("> ");
                String input = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8)).readLine().trim();
                if (!input.isEmpty()) {
                    if ("quit".equalsIgnoreCase(input)) {
                        break;
                    }
                    sequenceName = null;
                    generator = IdGeneratorFactory.getInstance().getOrCreateLongIdGenerator(input);
                    sequenceName = input;
                } else {
                    if (null == sequenceName) {
//                        System.out.println("Please input a valid sequence name");
                        continue;
                    } else {
                        int i = 0;
                    }
                }
                id = generator.nextId();
                System.out.println("==================================================");
                System.out.println("Generated id: " + id + " (sequence name = " + sequenceName + ")");
                System.out.println("==================================================");
            } catch (Exception e) {
                System.out.println(e.getMessage());
//                e.printStackTrace();
            }
        }

        System.exit(0);
    }

}
