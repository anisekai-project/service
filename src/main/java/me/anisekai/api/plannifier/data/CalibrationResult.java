package me.anisekai.api.plannifier.data;

public class CalibrationResult {

    private final int updateCount;
    private final int deleteCount;

    public CalibrationResult(int updateCount, int deleteCount) {

        this.updateCount = updateCount;
        this.deleteCount = deleteCount;
    }

    public int getDeleteCount() {

        return this.deleteCount;
    }

    public int getUpdateCount() {

        return this.updateCount;
    }

}
