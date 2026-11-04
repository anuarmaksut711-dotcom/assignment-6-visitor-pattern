package kz.aitu.visitor;

import java.util.List;

public interface Element {
    String getName();

    void accept(Visitor visitor);

    default List<Element> getChildren() {
        return List.of();
    }
}
