package com.ctrip.platform.dal.daogen.sql.validate;

public class SqlServerExplain {
    private String StmtText;
    private Integer StmtId;
    private Integer NodeId;
    private Integer Parent;
    private String PhysicalOp;
    private String LogicalOp;
    private String Argument;
    private String DefinedValues;
    private Integer EstimateRows;
    private Double EstimateIO;
    private Double EstimateCPU;
    private Integer AvgRowSize;
    private Double TotalSubtreeCost;
    private String OutputList;
    private String Type;
    private Boolean Parallel;
    private Integer EstimateExecutions;

    public String getStmtText() {
        return StmtText;
    }

    public void setStmtText(String stmtText) {
        StmtText = stmtText;
    }

    public Integer getStmtId() {
        return StmtId;
    }

    public void setStmtId(Integer stmtId) {
        StmtId = stmtId;
    }

    public Integer getNodeId() {
        return NodeId;
    }

    public void setNodeId(Integer nodeId) {
        NodeId = nodeId;
    }

    public Integer getParent() {
        return Parent;
    }

    public void setParent(Integer parent) {
        Parent = parent;
    }

    public String getPhysicalOp() {
        return PhysicalOp;
    }

    public void setPhysicalOp(String physicalOp) {
        PhysicalOp = physicalOp;
    }

    public String getLogicalOp() {
        return LogicalOp;
    }

    public void setLogicalOp(String logicalOp) {
        LogicalOp = logicalOp;
    }

    public String getArgument() {
        return Argument;
    }

    public void setArgument(String argument) {
        Argument = argument;
    }

    public String getDefinedValues() {
        return DefinedValues;
    }

    public void setDefinedValues(String definedValues) {
        DefinedValues = definedValues;
    }

    public Integer getEstimateRows() {
        return EstimateRows;
    }

    public void setEstimateRows(Integer estimateRows) {
        EstimateRows = estimateRows;
    }

    public Double getEstimateIO() {
        return EstimateIO;
    }

    public void setEstimateIO(Double estimateIO) {
        EstimateIO = estimateIO;
    }

    public Double getEstimateCPU() {
        return EstimateCPU;
    }

    public void setEstimateCPU(Double estimateCPU) {
        EstimateCPU = estimateCPU;
    }

    public Integer getAvgRowSize() {
        return AvgRowSize;
    }

    public void setAvgRowSize(Integer avgRowSize) {
        AvgRowSize = avgRowSize;
    }

    public Double getTotalSubtreeCost() {
        return TotalSubtreeCost;
    }

    public void setTotalSubtreeCost(Double totalSubtreeCost) {
        TotalSubtreeCost = totalSubtreeCost;
    }

    public String getOutputList() {
        return OutputList;
    }

    public void setOutputList(String outputList) {
        OutputList = outputList;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public Boolean getParallel() {
        return Parallel;
    }

    public void setParallel(Boolean parallel) {
        Parallel = parallel;
    }

    public Integer getEstimateExecutions() {
        return EstimateExecutions;
    }

    public void setEstimateExecutions(Integer estimateExecutions) {
        EstimateExecutions = estimateExecutions;
    }
}
