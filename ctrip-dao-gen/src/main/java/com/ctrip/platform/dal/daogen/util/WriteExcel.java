package com.ctrip.platform.dal.daogen.util;

import com.ctrip.platform.dal.daogen.entity.TitanKeyAPIInfo;
import com.dianping.cat.Cat;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import java.io.File;
import java.util.List;

public class WriteExcel {
    public static  String EXCEL_PATH = "/home/deploy/unUsedDynamicDSTitanKey.xls";

    public static boolean writeExcel(List<TitanKeyAPIInfo> titanKeyList) {
        File xlsFile = new File(EXCEL_PATH);
        WritableWorkbook writableWorkbook = null;
        try {
            writableWorkbook = Workbook.createWorkbook(xlsFile);
            WritableSheet sheet = writableWorkbook.createSheet("unUsedDynamicDSTitanKey", 0);

            sheet.addCell(new Label(0, 0, "ID"));
            sheet.addCell(new Label(1, 0, "TitanKey"));
            sheet.addCell(new Label(2, 0, "DBName"));

            for (int row = 1; row < titanKeyList.size(); row ++) {
                sheet.addCell(new Label(0, row, String.valueOf(row)));
                sheet.addCell(new Label(1, row, titanKeyList.get(row).getName()));
                sheet.addCell(new Label(2, row, titanKeyList.get(row).getConnectionInfo().getDbName()));
            }
            writableWorkbook.write();
        } catch (Exception e) {
            Cat.logError(e);
            return false;
        }
        finally {
            if (writableWorkbook != null) {
                try {
                    writableWorkbook.close();
                } catch (Exception e) {
                    //ignore
                }
            }
        }
        return true;
    }
}
