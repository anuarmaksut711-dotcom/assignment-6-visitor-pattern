package kz.aitu.visitor;

public abstract class FileSystemNode implements Element {
    private final String name;
    private DirectoryNode parent;

    FileSystemNode(String name) {
        if (name == null || name.isBlank() || name.contains("/") || name.contains("\\")
                || name.equals(".") || name.equals("..")) {
            throw new IllegalArgumentException("Name must be a non-empty single path component");
        }
        this.name = name;
    }

    @Override
    public final String getName() {
        return name;
    }

    final DirectoryNode getParent() {
        return parent;
    }

    final void attachTo(DirectoryNode directory) {
        if (parent != null) {
            throw new IllegalArgumentException("Node already has a parent");
        }
        parent = directory;
    }
}
