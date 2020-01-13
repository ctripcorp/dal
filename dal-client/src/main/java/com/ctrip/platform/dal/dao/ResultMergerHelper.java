package com.ctrip.platform.dal.dao;

public class ResultMergerHelper {
    public static ResultMerger createResultMerger(ResultMerger merger) {
        if (merger instanceof ResultMerger.IntSummary) {
            return new ResultMerger.IntSummary();
        }

        if (merger instanceof ResultMerger.LongSummary) {
            return new ResultMerger.LongSummary();
        }

        if (merger instanceof ResultMerger.LongNumberSummary) {
            return new ResultMerger.LongNumberSummary();
        }

        if (merger instanceof ResultMerger.DoubleSummary) {
            return new ResultMerger.DoubleSummary();
        }

        if (merger instanceof ResultMerger.BigIntegerSummary) {
            return new ResultMerger.BigIntegerSummary();
        }

        if (merger instanceof ResultMerger.BigDecimalSummary) {
            return new ResultMerger.BigDecimalSummary();
        }

        return null;
    }

}
