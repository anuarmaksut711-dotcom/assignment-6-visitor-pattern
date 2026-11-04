package kz.aitu.visitor;

import java.util.ArrayList;
import java.util.List;

public final class DirectoryNode extends FileSystemNode {
    private final List<Element> children = new ArrayList<>();

    public DirectoryNode(String name) {
        super(name);
    }

    public void add(FileSystemNode child) {
        if (child == null) {
            throw new IllegalArgumentException("Child must not be null");
        }
        for (FileSystemNode ancestor = this; ancestor != null; ancestor = ancestor.getParent()) {
            if (ancestor == child) {
                throw new IllegalArgumentException("Adding this node would create a cycle");
            }
        }
        for (Element sibling : children) {
            if (sibling.getName().equals(child.getName())) {
                throw new IllegalArgumentException("Duplicate child name: " + child.getName());
            }
        }
        child.attachTo(this);
        children.add(child);
    }

    @Override
    public List<Element> getChildren() {
        return List.copyOf(children);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
