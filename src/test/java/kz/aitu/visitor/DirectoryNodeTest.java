package kz.aitu.visitor;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DirectoryNodeTest {
    @Test
    void rejectsNegativeSizeAndAcceptsZero() {
        assertThrows(IllegalArgumentException.class, () -> new FileNode("bad.bin", -1));
        assertEquals(0, new FileNode("empty.bin", 0).getSize());
    }

    @Test
    void rejectsInvalidNamesForFilesAndDirectories() {
        for (String name : new String[]{null, "", " ", ".", "..", "a/b", "a\\b"}) {
            assertThrows(IllegalArgumentException.class, () -> new FileNode(name, 1));
            assertThrows(IllegalArgumentException.class, () -> new DirectoryNode(name));
        }
    }

    @Test
    void rejectsDuplicateNamesAcrossElementTypesWithoutAttachingRejectedNode() {
        DirectoryNode root = new DirectoryNode("root");
        FileNode existing = new FileNode("shared", 7);
        DirectoryNode rejected = new DirectoryNode("shared");
        root.add(existing);

        assertThrows(IllegalArgumentException.class, () -> root.add(rejected));
        assertEquals(List.of(existing), root.getChildren());

        DirectoryNode another = new DirectoryNode("another");
        assertDoesNotThrow(() -> another.add(rejected));
    }

    @Test
    void rejectsMultipleParentsForFilesAndDirectories() {
        DirectoryNode first = new DirectoryNode("first");
        DirectoryNode second = new DirectoryNode("second");
        FileNode file = new FileNode("data.bin", 3);
        DirectoryNode folder = new DirectoryNode("folder");
        first.add(file);
        first.add(folder);

        assertThrows(IllegalArgumentException.class, () -> second.add(file));
        assertThrows(IllegalArgumentException.class, () -> second.add(folder));
        assertTrue(second.getChildren().isEmpty());
        assertEquals(List.of(file, folder), first.getChildren());
    }

    @Test
    void rejectsCyclesAndKeepsValidNesting() {
        DirectoryNode root = new DirectoryNode("root");
        DirectoryNode middle = new DirectoryNode("middle");
        DirectoryNode leaf = new DirectoryNode("leaf");
        root.add(middle);
        middle.add(leaf);

        assertThrows(IllegalArgumentException.class, () -> root.add(root));
        assertThrows(IllegalArgumentException.class, () -> leaf.add(root));
        assertThrows(IllegalArgumentException.class, () -> leaf.add(middle));
        assertEquals(List.of(middle), root.getChildren());
        assertEquals(List.of(leaf), middle.getChildren());
        assertTrue(leaf.getChildren().isEmpty());
        assertDoesNotThrow(() -> leaf.add(new FileNode("valid.txt", 2)));
    }

    @Test
    void rejectsNullChildAndRepeatedInsertion() {
        DirectoryNode root = new DirectoryNode("root");
        FileNode file = new FileNode("one.txt", 1);
        assertThrows(IllegalArgumentException.class, () -> root.add(null));
        root.add(file);
        assertThrows(IllegalArgumentException.class, () -> root.add(file));
        assertEquals(List.of(file), root.getChildren());
    }

    @Test
    void childCollectionsCannotBeModified() {
        DirectoryNode root = new DirectoryNode("root");
        FileNode file = new FileNode("one.txt", 1);
        root.add(file);

        assertThrows(UnsupportedOperationException.class, () -> root.getChildren().clear());
        assertThrows(UnsupportedOperationException.class,
                () -> file.getChildren().add(new FileNode("other.txt", 2)));
        assertEquals(List.of(file), root.getChildren());
    }
}
