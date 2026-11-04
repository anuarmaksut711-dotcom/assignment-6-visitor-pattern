package kz.aitu.visitor;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class VisitorOperationsTest {
    private final FileTreeWalker walker = new FileTreeWalker();

    private DirectoryNode createTree() {
        DirectoryNode root = new DirectoryNode("project");
        DirectoryNode documents = new DirectoryNode("docs");
        DirectoryNode nested = new DirectoryNode("nested");
        root.add(new FileNode("z.txt", 50));
        root.add(documents);
        root.add(new DirectoryNode("empty"));
        documents.add(new FileNode("a.txt", 40));
        documents.add(new FileNode("large.csv", 200));
        documents.add(nested);
        nested.add(new FileNode("b.txt", 70));
        return root;
    }

    @Test
    void totalSizeIncludesEachFileAcrossMultipleLevels() {
        TotalSizeVisitor visitor = new TotalSizeVisitor();
        walker.walk(createTree(), visitor);
        assertEquals(360, visitor.getTotalSize());
    }

    @Test
    void searchCombinesFilenameAndInclusiveSizeConditions() {
        SearchVisitor visitor = new SearchVisitor(name -> name.endsWith(".txt"), 50);
        walker.walk(createTree(), visitor);
        assertEquals(List.of("docs/nested/b.txt", "z.txt"), visitor.getMatches());
    }

    @Test
    void searchSortsPathsAndUsesTheChosenSubtreeAsRoot() {
        DirectoryNode outer = new DirectoryNode("outer");
        DirectoryNode subtree = createTree();
        outer.add(subtree);
        SearchVisitor visitor = new SearchVisitor(name -> name.endsWith(".txt"), 0);

        walker.walk(subtree, visitor);
        assertEquals(List.of("docs/a.txt", "docs/nested/b.txt", "z.txt"), visitor.getMatches());

        walker.walk(outer, visitor);
        assertEquals(List.of("project/docs/a.txt", "project/docs/nested/b.txt", "project/z.txt"),
                visitor.getMatches());
    }

    @Test
    void searchCriterionCanUseAnExactFilename() {
        SearchVisitor visitor = new SearchVisitor(name -> name.equals("large.csv"), 200);
        walker.walk(createTree(), visitor);
        assertEquals(List.of("docs/large.csv"), visitor.getMatches());
    }

    @Test
    void emptyDirectoryAndNoMatchesProduceEmptyResults() {
        DirectoryNode empty = new DirectoryNode("empty");
        TotalSizeVisitor size = new TotalSizeVisitor();
        SearchVisitor search = new SearchVisitor(name -> name.endsWith(".zip"), 0);

        walker.walk(empty, size);
        walker.walk(empty, search);
        assertEquals(0, size.getTotalSize());
        assertTrue(search.getMatches().isEmpty());

        walker.walk(createTree(), search);
        assertTrue(search.getMatches().isEmpty());
    }

    @Test
    void fileCanBeTheTraversalRoot() {
        FileNode file = new FileNode("single.txt", 19);
        TotalSizeVisitor size = new TotalSizeVisitor();
        SearchVisitor search = new SearchVisitor(name -> true, 19);
        walker.walk(file, size);
        walker.walk(file, search);
        assertEquals(19, size.getTotalSize());
        assertEquals(List.of("single.txt"), search.getMatches());
    }

    @Test
    void repeatedRunsResetStateAndObserveNewData() {
        DirectoryNode root = createTree();
        TotalSizeVisitor size = new TotalSizeVisitor();
        SearchVisitor search = new SearchVisitor(name -> name.endsWith(".txt"), 50);

        for (int run = 0; run < 2; run++) {
            walker.walk(root, search);
            walker.walk(root, size);
            assertEquals(360, size.getTotalSize());
            assertEquals(List.of("docs/nested/b.txt", "z.txt"), search.getMatches());
        }

        root.add(new FileNode("new.txt", 90));
        walker.walk(root, size);
        walker.walk(root, search);
        assertEquals(450, size.getTotalSize());
        assertEquals(List.of("docs/nested/b.txt", "new.txt", "z.txt"), search.getMatches());

        walker.walk(new DirectoryNode("empty"), search);
        walker.walk(new DirectoryNode("empty"), size);
        assertTrue(search.getMatches().isEmpty());
        assertEquals(0, size.getTotalSize());
    }

    @Test
    void searchReturnsAnImmutableSnapshot() {
        SearchVisitor visitor = new SearchVisitor(name -> true, 0);
        walker.walk(new FileNode("saved.txt", 1), visitor);
        List<String> saved = visitor.getMatches();

        assertThrows(UnsupportedOperationException.class, saved::clear);
        walker.walk(new DirectoryNode("empty"), visitor);
        assertEquals(List.of("saved.txt"), saved);
        assertTrue(visitor.getMatches().isEmpty());
    }

    @Test
    void rejectsInvalidSearchConfiguration() {
        assertThrows(IllegalArgumentException.class, () -> new SearchVisitor(name -> true, -1));
        assertThrows(NullPointerException.class, () -> new SearchVisitor(null, 0));
    }

    @Test
    void reportsSizeOverflowInsteadOfReturningANegativeTotal() {
        DirectoryNode root = new DirectoryNode("root");
        root.add(new FileNode("first.bin", Long.MAX_VALUE));
        root.add(new FileNode("second.bin", 1));
        TotalSizeVisitor visitor = new TotalSizeVisitor();

        assertThrows(ArithmeticException.class, () -> walker.walk(root, visitor));
        walker.walk(new FileNode("small.bin", 8), visitor);
        assertEquals(8, visitor.getTotalSize());
    }
}
