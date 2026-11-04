package kz.aitu.visitor;

import java.util.Objects;

public final class FileTreeWalker {
    public void walk(Element root, Visitor visitor) {
        Objects.requireNonNull(root, "Root must not be null");
        Objects.requireNonNull(visitor, "Visitor must not be null");
        visitor.reset();
        visitSubtree(root, visitor, "", 0);
    }

    private void visitSubtree(Element element, Visitor visitor, String path, int depth) {
        visitor.setContext(path, depth);
        element.accept(visitor);
        for (Element child : element.getChildren()) {
            String childPath = path.isEmpty() ? child.getName() : path + "/" + child.getName();
            visitSubtree(child, visitor, childPath, depth + 1);
        }
    }
}
