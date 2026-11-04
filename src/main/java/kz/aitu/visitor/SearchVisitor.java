package kz.aitu.visitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public final class SearchVisitor implements Visitor {
    private final Predicate<String> filenameCriterion;
    private final long minimumSize;
    private final List<String> matches = new ArrayList<>();
    private String currentPath = "";

    public SearchVisitor(Predicate<String> filenameCriterion, long minimumSize) {
        this.filenameCriterion = Objects.requireNonNull(filenameCriterion, "Filename criterion is required");
        if (minimumSize < 0) {
            throw new IllegalArgumentException("Minimum size must not be negative");
        }
        this.minimumSize = minimumSize;
    }

    @Override
    public void reset() {
        matches.clear();
        currentPath = "";
    }

    @Override
    public void setContext(String relativePath, int depth) {
        currentPath = relativePath;
    }

    @Override
    public void visit(FileNode file) {
        if (filenameCriterion.test(file.getName()) && file.getSize() >= minimumSize) {
            matches.add(currentPath.isEmpty() ? file.getName() : currentPath);
        }
    }

    @Override
    public void visit(DirectoryNode directory) {
    }

    public List<String> getMatches() {
        List<String> sortedMatches = new ArrayList<>(matches);
        Collections.sort(sortedMatches);
        return List.copyOf(sortedMatches);
    }
}
