# Visitor File System

An in-memory file system built with Java 17, Maven and JUnit 5.

## Run

Run these commands from the project root:

```bash
mvn test
mvn compile
java -cp target/classes kz.aitu.visitor.Main
```

## Design

`FileNode` stores a name and a size in bytes. `DirectoryNode` stores child elements.
Each `accept` method calls the matching `visit` overload without checking the
element type.

`FileTreeWalker` owns the depth-first traversal. It resets the visitor before each
run and passes the current path and depth before calling `accept`. Visitors do
not traverse children. Each reachable node is processed once per operation.

- `TotalSizeVisitor` sums file sizes. Overflow throws `ArithmeticException`.
- `SearchVisitor` accepts a `Predicate<String>` for filenames and an inclusive minimum size.
- `StatisticsVisitor` counts files and directories and records the maximum depth. The root directory is included in the directory count.

Search paths are relative to the selected root, use `/` and are sorted
lexicographically. The root directory name is omitted. When the root is a file,
its path is its filename. The root depth is zero.

Visitors can be reused through `FileTreeWalker.walk`. Calling `accept` directly
processes one node without resetting visitor state. Traversal is sequential;
the tree must not be changed during a traversal.

## Tree constraints

File sizes must be non-negative. Names must not be blank, contain `/` or `\`,
or equal `.` or `..`. Sibling names are unique and case-sensitive. Each node has
at most one parent. Cycles and adding a directory to itself are rejected.
Invalid additions throw `IllegalArgumentException` without changing the tree.
Child collections and search results are returned as immutable snapshots.

## Extension

`StatisticsVisitor` was added in a separate commit without changing `FileNode`,
`DirectoryNode`, `Visitor` or the walker. A new operation implements both `visit`
methods and overrides `reset` and `setContext` when needed.
Adding a new element type requires a new `Visitor` overload and updates to all
existing visitors.

## Output

```text
Total size: 2798 bytes
Text files with size >= 100 bytes:
  documents/archive/old.txt
  documents/report.txt
  readme.txt
Files: 5
Directories: 5
Maximum depth: 3
```

## Tests

JUnit 5 tests cover double dispatch, nested directories, size accumulation, both
search conditions, relative paths, sorting, statistics, empty directories,
repeated runs and invalid tree changes.

Result: 25 tests passed.

Report: [docs/report.pdf](docs/report.pdf).

UML diagram: [SVG](docs/uml.svg), [PDF](docs/uml.pdf).
