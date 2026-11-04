package kz.aitu.visitor;

public final class TotalSizeVisitor implements Visitor {
    private long totalSize;

    @Override
    public void reset() {
        totalSize = 0;
    }

    @Override
    public void visit(FileNode file) {
        totalSize = Math.addExact(totalSize, file.getSize());
    }

    @Override
    public void visit(DirectoryNode directory) {
    }

    public long getTotalSize() {
        return totalSize;
    }
}
