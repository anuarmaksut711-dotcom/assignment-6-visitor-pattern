package kz.aitu.visitor;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VisitorDispatchTest {
    @Test
    void eachElementCallsItsMatchingOverload() {
        List<String> calls = new ArrayList<>();
        Visitor visitor = new Visitor() {
            @Override
            public void visit(FileNode file) {
                calls.add("file:" + file.getName());
            }

            @Override
            public void visit(DirectoryNode directory) {
                calls.add("directory:" + directory.getName());
            }
        };
        Element file = new FileNode("data.bin", 14);
        Element directory = new DirectoryNode("folder");

        file.accept(visitor);
        directory.accept(visitor);

        assertEquals(List.of("file:data.bin", "directory:folder"), calls);
    }
}
