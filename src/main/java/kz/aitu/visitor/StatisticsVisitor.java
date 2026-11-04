package kz.aitu.visitor;

public final class StatisticsVisitor implements Visitor {
    private int fileCount;
    private int directoryCount;
    private int maximumDepth;
    private int currentDepth;

    @Override
    public void reset() {
        fileCount = 0;
        directoryCount = 0;
        maximumDepth = 0;
        currentDepth = 0;
    }

    @Override
    public void setContext(String relativePath, int depth) {
        currentDepth = depth;
    }

    @Override
    public void visit(FileNode file) {
        fileCount++;
        maximumDepth = Math.max(maximumDepth, currentDepth);
    }

    @Override
    public void visit(DirectoryNode directory) {
        directoryCount++;
        maximumDepth = Math.max(maximumDepth, currentDepth);
    }

    public int getFileCount() {
        return fileCount;
    }

    public int getDirectoryCount() {
        return directoryCount;
    }

    public int getMaximumDepth() {
        return maximumDepth;
    }
}
