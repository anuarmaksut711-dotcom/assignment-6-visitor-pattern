package kz.aitu.visitor;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StatisticsVisitorTest {
    private final FileTreeWalker walker = new FileTreeWalker();

    @Test
    void countsNestedFilesAndDirectoriesIncludingRoot() {
        DirectoryNode root = new DirectoryNode("root");
        DirectoryNode first = new DirectoryNode("first");
        DirectoryNode second = new DirectoryNode("second");
        root.add(new FileNode("top.txt", 5));
        root.add(first);
        first.add(second);
        second.add(new FileNode("deep.txt", 9));
        first.add(new DirectoryNode("empty"));
        StatisticsVisitor visitor = new StatisticsVisitor();

        walker.walk(root, visitor);

        assertEquals(2, visitor.getFileCount());
        assertEquals(4, visitor.getDirectoryCount());
        assertEquals(3, visitor.getMaximumDepth());
    }

    @Test
    void emptyRootIsOneDirectoryAtDepthZero() {
        StatisticsVisitor visitor = new StatisticsVisitor();
        walker.walk(new DirectoryNode("empty"), visitor);
        assertEquals(0, visitor.getFileCount());
        assertEquals(1, visitor.getDirectoryCount());
        assertEquals(0, visitor.getMaximumDepth());
    }

    @Test
    void fileRootIsOneFileAtDepthZero() {
        StatisticsVisitor visitor = new StatisticsVisitor();
        walker.walk(new FileNode("only.txt", 0), visitor);
        assertEquals(1, visitor.getFileCount());
        assertEquals(0, visitor.getDirectoryCount());
        assertEquals(0, visitor.getMaximumDepth());
    }

    @Test
    void emptyDirectoriesContributeToMaximumDepth() {
        DirectoryNode root = new DirectoryNode("root");
        DirectoryNode first = new DirectoryNode("first");
        DirectoryNode second = new DirectoryNode("second");
        root.add(first);
        first.add(second);
        StatisticsVisitor visitor = new StatisticsVisitor();

        walker.walk(root, visitor);

        assertEquals(0, visitor.getFileCount());
        assertEquals(3, visitor.getDirectoryCount());
        assertEquals(2, visitor.getMaximumDepth());

        walker.walk(first, visitor);
        assertEquals(2, visitor.getDirectoryCount());
        assertEquals(1, visitor.getMaximumDepth());
    }

    @Test
    void repeatedRunsResetAllStatisticsAndAllowDifferentOperationOrder() {
        DirectoryNode root = new DirectoryNode("root");
        DirectoryNode folder = new DirectoryNode("folder");
        root.add(folder);
        folder.add(new FileNode("data.txt", 11));
        StatisticsVisitor statistics = new StatisticsVisitor();
        TotalSizeVisitor size = new TotalSizeVisitor();
        SearchVisitor search = new SearchVisitor(name -> name.endsWith(".txt"), 11);

        walker.walk(root, statistics);
        walker.walk(root, search);
        walker.walk(root, size);
        walker.walk(root, statistics);
        assertEquals(1, statistics.getFileCount());
        assertEquals(2, statistics.getDirectoryCount());
        assertEquals(2, statistics.getMaximumDepth());
        assertEquals(11, size.getTotalSize());
        assertEquals(List.of("folder/data.txt"), search.getMatches());

        root.add(new FileNode("added.txt", 4));
        walker.walk(root, statistics);
        assertEquals(2, statistics.getFileCount());

        walker.walk(new DirectoryNode("empty"), statistics);
        assertEquals(0, statistics.getFileCount());
        assertEquals(1, statistics.getDirectoryCount());
        assertEquals(0, statistics.getMaximumDepth());
    }
}
