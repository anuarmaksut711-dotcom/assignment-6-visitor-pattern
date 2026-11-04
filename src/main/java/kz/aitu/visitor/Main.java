package kz.aitu.visitor;

public final class Main {
    public static void main(String[] args) {
        DirectoryNode root = createSampleTree();
        FileTreeWalker walker = new FileTreeWalker();

        TotalSizeVisitor sizeVisitor = new TotalSizeVisitor();
        walker.walk(root, sizeVisitor);
        System.out.println("Total size: " + sizeVisitor.getTotalSize() + " bytes");

        SearchVisitor searchVisitor = new SearchVisitor(name -> name.endsWith(".txt"), 100);
        walker.walk(root, searchVisitor);
        System.out.println("Text files with size >= 100 bytes:");
        for (String path : searchVisitor.getMatches()) {
            System.out.println("  " + path);
        }
    }

    private static DirectoryNode createSampleTree() {
        DirectoryNode root = new DirectoryNode("workspace");
        DirectoryNode documents = new DirectoryNode("documents");
        DirectoryNode archive = new DirectoryNode("archive");
        DirectoryNode images = new DirectoryNode("images");

        root.add(new FileNode("readme.txt", 120));
        root.add(documents);
        root.add(images);
        root.add(new DirectoryNode("empty"));
        documents.add(new FileNode("notes.txt", 80));
        documents.add(new FileNode("report.txt", 450));
        documents.add(archive);
        archive.add(new FileNode("old.txt", 100));
        images.add(new FileNode("logo.png", 2048));
        return root;
    }
}
