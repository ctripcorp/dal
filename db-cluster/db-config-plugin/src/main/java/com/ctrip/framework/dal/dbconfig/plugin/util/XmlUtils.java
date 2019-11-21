package com.ctrip.framework.dal.dbconfig.plugin.util;

import com.sun.xml.internal.bind.marshaller.CharacterEscapeHandler;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Created by shenjie on 2019/4/30.
 */
public class XmlUtils {

    // ignore character escape
    private static final CharacterEscapeHandler characterNoEscapeHandler = new CharacterEscapeHandler() {
        @Override
        public void escape(char[] ch, int start,int length, boolean isAttVal, Writer writer) throws IOException {
            writer.write(ch, start, length);
        }
    };

    /**
     * 将对象直接转换成String类型的 XML输出
     */
    public static String toXml(Object obj) {
        try (StringWriter sw = new StringWriter()) {
            JAXBContext context = JAXBContext.newInstance(obj.getClass());
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
            marshaller.marshal(obj, sw);
            return sw.toString();
        } catch (Exception e) {
            throw new RuntimeException("object to xml exception", e);
        }
    }

    /**
     * 将String类型的xml转换成对象
     */
    public static Object fromXml(String xmlContent, Class clazz) {
        try (StringReader sr = new StringReader(xmlContent)) {
            JAXBContext context = JAXBContext.newInstance(clazz);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            return unmarshaller.unmarshal(sr);
        } catch (Exception e) {
            throw new RuntimeException("xml to object exception", e);
        }
    }

    public static String wrap(String parent, String content) {
        return String.format("<%s>%s</%s>", parent, content, parent);
    }

}
