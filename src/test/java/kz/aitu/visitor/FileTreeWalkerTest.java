package kz.aitu.visitor;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileTreeWalkerTest {
    @Test
    void visitsEveryNodeOnceAndPassesPathAndDepthBeforeDispatch() {
        DirectoryNode root = new DirectoryNode("root");
        DirectoryNode folder = new DirectoryNode("folder");
        root.add(folder);
        folder.add(new FileNode("data.txt", 5));
        root.add(new DirectoryNode("empty"));
        List<String> visits = new ArrayList<>();
        Visitor visitor = new Visitor() {
            private String path;
            private int depth;

            @Override
            public void reset() {
                visits.clear();
            }

            @Override
            public void setContext(String relativePath, int currentDepth) {
                path = relativePath;
                depth = currentDepth;
            }

            @Override
            public void visit(FileNode file) {
                visits.add("file:" + path + ":" + depth);
            }

            @Override
            public void visit(DirectoryNode directory) {
                visits.add("directory:" + path + ":" + depth);
            }
        };

        FileTreeWalker walker = new FileTreeWalker();
        for (int run = 0; run < 2; run++) {
            walker.walk(root, visitor);
            assertEquals(List.of("directory::0", "directory:folder:1",
                    "file:folder/data.txt:2", "directory:empty:1"), visits);
        }
    }

    @Test
    void rejectsMissingRootOrVisitor() {
        FileTreeWalker walker = new FileTreeWalker();
        assertThrows(NullPointerException.class, () -> walker.walk(null, new TotalSizeVisitor()));
        assertThrows(NullPointerException.class, () -> walker.walk(new DirectoryNode("root"), null));
    }
}
